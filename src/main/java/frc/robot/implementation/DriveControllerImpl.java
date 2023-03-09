package frc.robot.implementation;

import com.revrobotics.RelativeEncoder;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.interfaces.DriveController;
import frc.robot.main.Constants.DriveConstants;

public class DriveControllerImpl implements DriveController {
    private double currentSpeed = 0.0;
    private double currentRotation = 0.0;
    private DriveSubsystem driveSubsystem = new DriveSubsystem();
    private double rotAccLimit = 0.1;

    public DriveControllerImpl() {
        SmartDashboard.putNumber(ROTATIONAL_ACC_LIMIT, rotAccLimit);
    }

    @Override
    public void stop() {
        if ((currentSpeed!=0) || (currentRotation!=0)) {
            currentSpeed = 0;
            currentRotation = 0;
            driveSubsystem.stopMotor();
            System.out.println("Not moving");
        }
        driveSubsystem.arcadeDrive(0, 0);
    }

    @Override
    public void move(double speed, double rotation) {
        boolean speedChange = currentSpeed != speed;
        boolean rotChange = rotation != currentRotation;
        if ((speedChange) || (rotChange)) {
            if (rotChange) {
                rotAccLimit = SmartDashboard.getNumber(ROTATIONAL_ACC_LIMIT, rotAccLimit);
                if (rotAccLimit!=0) {
                    double diff = rotation-currentRotation;
                    if ((rotation>0 && diff>rotAccLimit) || (rotation<0&&diff<-rotAccLimit)) {
                        System.out.println("Limiting rotational acceleration to "+rotAccLimit);
                        rotation = currentRotation + (diff>0?rotAccLimit:-rotAccLimit);
                    }
                }
            }
            System.out.println("Moving at speed:" + speed + ", rotation:" + rotation);
            currentSpeed = speed;
            currentRotation = rotation;
            // while(currentSpeed < speed){
            //     currentSpeed = 0;
            //     currentSpeed += 0.1;
            // }
            // while(currentRotation < rotation){
            //     currentRotation = 0;
            //     currentRotation += 0.1;
            // }
        }
        double adjustedSpeed = (speed>DriveConstants.maxSpeed) ? DriveConstants.maxSpeed: speed;
        driveSubsystem.arcadeDrive(adjustedSpeed, rotation);
    }










    @Override
    public void resetEncoders() {
        driveSubsystem.resetEncoders();
    }

    @Override
    public RelativeEncoder getRighttEncoder() {
        return driveSubsystem.getRightEncoder();
    }

    @Override
    public RelativeEncoder getLeftEncoder() {
        return driveSubsystem.getLeftEncoder();
    }

    @Override
    public void simulationPeriodic() {
        
    }
}