package frc.robot.controller;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.interfaces.DriveController;
import frc.robot.subsystem.DriveSubsystem;
import frc.robot.subsystem.GyroSubsystem;

public class DriveControllerImpl implements DriveController {
    private GyroSubsystem gyro = new GyroSubsystem();
    private DriveSubsystem driveSubsystem = new DriveSubsystem();
    private double rotAccLimit = 0.1;

    public DriveControllerImpl() {
        SmartDashboard.putBoolean(RESET_ENCODER, false);
        SmartDashboard.putNumber(ROTATIONAL_ACC_LIMIT, rotAccLimit);
    }

    public void init() {
        driveSubsystem.resetEncoders();  
        gyro.init();
    }

    @Override
    public void stop() {
        if ((driveSubsystem.getCurrentSpeed()!=0) || (driveSubsystem.getCurrentRotation()!=0)) {
            driveSubsystem.stopMotor();
            System.out.println("Not moving");
        }
        driveSubsystem.arcadeDrive(0, 0);
    }

    @Override
    public void move(double speed, double rotation) {
        boolean speedChange = driveSubsystem.getCurrentSpeed() != speed;
        boolean rotChange = rotation != driveSubsystem.getCurrentRotation();
        if ((speedChange) || (rotChange)) {
            if (rotChange) {
                rotAccLimit = SmartDashboard.getNumber(ROTATIONAL_ACC_LIMIT, rotAccLimit);
                if (rotAccLimit!=0) {
                    double diff = rotation-driveSubsystem.getCurrentRotation();
                    if ((rotation>0 && diff>rotAccLimit) || (rotation<0&&diff<-rotAccLimit)) {
                        System.out.println("Limiting rotational acceleration to "+rotAccLimit);
                        rotation = driveSubsystem.getCurrentRotation() + (diff>0?rotAccLimit:-rotAccLimit);
                    }
                }
            }
            System.out.println("Moving at speed:" + speed + ", rotation:" + rotation);
        }
        driveSubsystem.arcadeDrive(speed, rotation);
    }

    @Override
    public void resetEncoders() {
        driveSubsystem.resetEncoders();
    }

    @Override
    public double getLeftEncoderPosition() {
        return driveSubsystem.getLeftEncoderPosition();
    }

    @Override
    public double getRightEncoderPosition() {
        return driveSubsystem.getRightEncoderPosition();
    }

    @Override
    public double getYaw() {
        return gyro.getYaw();
    }

    @Override
    public void periodic() {
        //System.out.println("gyroP:"+gyro.getPitch()+", gyroR:"+gyro.getDegrees());
        SmartDashboard.putNumber(DriveController.GYRO_PITCH, gyro.getPitch());
        SmartDashboard.putNumber(DriveController.GYRO_YAW, gyro.getYaw());
        //SmartDashboard.putNumber(DriveController.GYRO_ANGLE, gyro.getAngle());
        //SmartDashboard.putNumber(DriveController.GYRO_ROLL, gyro.getRoll());
        SmartDashboard.putNumber(DriveController.ENCODER_RT_POS, getRightEncoderPosition());
        SmartDashboard.putNumber(DriveController.ENCODER_LT_POS, getLeftEncoderPosition());
        //driveSubsystem.updateOdometry();
    }

    @Override
    public void simulationPeriodic() {
        driveSubsystem.simulationPeriodic();
        gyro.simulationPeriodic(driveSubsystem.getCurrentRotation());
    }
}