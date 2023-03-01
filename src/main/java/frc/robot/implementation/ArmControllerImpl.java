package frc.robot.implementation;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.interfaces.ArmController;

public class ArmControllerImpl implements ArmController {
    LiftSubsystem liftSubsystem = new LiftSubsystem();
    ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem();
    boolean stopped = true;

    public ArmControllerImpl() {
        SmartDashboard.putNumber(LIFT_POSITION, 0);
        SmartDashboard.putNumber(LIFT_LOW_LIMIT, 0);
        SmartDashboard.putNumber(LIFT_CONE_KEY, 60);

        SmartDashboard.putNumber(ELEV_POSITION, 0);
        SmartDashboard.putNumber(ELEV_LOW_LIMIT, -20);
        SmartDashboard.putNumber(ELEV_CONE_KEY, 50);
    }

    @Override
    public void periodic() {
        SmartDashboard.putNumber(LIFT_POSITION, liftSubsystem.getPosition());
        SmartDashboard.putNumber(ELEV_POSITION, elevatorSubsystem.getPosition());
    }

    @Override
    public void raiseArm(double speed) {
        stopped = false;
        liftSubsystem.raiseArm(speed);
    }

    @Override
    public void lowerArm(double speed) {
        stopped = false;
        liftSubsystem.lowerArm(speed);
    }

    /**
     * @return true if moved to target, false if move arm has not yet reached target
     */
    @Override
    public boolean moveArmToTarget(String itemType) {
        double lTarget;
        double eTarget;
        boolean status = false;
        switch (itemType) {
            case "Cone": 
                lTarget= SmartDashboard.getNumber(LIFT_CONE_KEY, 60);
                eTarget = SmartDashboard.getNumber(ELEV_CONE_KEY, 50);
                break;
            case "Cube": 
                lTarget= SmartDashboard.getNumber(LIFT_CUBE_KEY, 60);
                eTarget = SmartDashboard.getNumber(ELEV_CUBE_KEY, 50);
                break;
            case "Stable": 
                lTarget= SmartDashboard.getNumber(LIFT_STAB_KEY, 60);
                eTarget = SmartDashboard.getNumber(ELEV_STAB_KEY, 50);
                break;
            default:
                return status;
        }
        status  = liftSubsystem.moveToTarget(lTarget);
        status &= elevatorSubsystem.moveToTarget(eTarget);
        return status;
    }

    @Override
    public void extendArm(double speed) {
        stopped = false;
        elevatorSubsystem.extendArm(speed);
    }

    @Override
    public void retractArm(double speed) {
        stopped = false;
        elevatorSubsystem.retractArm(speed);
    }

    public void stopElevator() {
        elevatorSubsystem.stop();
    }

    public void stopLift() {
        liftSubsystem.stop();
    }

    public void stop() {
        liftSubsystem.stop();
        elevatorSubsystem.stop();
        if (!stopped) {
            System.out.println("Stopping arm.....");
            stopped = true;
        }       
    }

    @Override
    public void resetEncoderPos() {
        liftSubsystem.resetEncoderPos();
        elevatorSubsystem.resetEncoderPos();        
    }
}
