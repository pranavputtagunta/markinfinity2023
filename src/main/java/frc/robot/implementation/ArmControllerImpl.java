package frc.robot.implementation;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.interfaces.ArmController;

public class ArmControllerImpl implements ArmController {
    LiftSubsystem liftSubsystem = new LiftSubsystem();
    ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem();
    
    public ArmControllerImpl() {
        SmartDashboard.putNumber(LIFT_POSITION, 0);
        SmartDashboard.putNumber(LIFT_LOW_LIMIT, -110);
        SmartDashboard.putNumber(LIFT_CONE_KEY, 60);
        SmartDashboard.putNumber(LIFT_CUBE_KEY, 65);
        SmartDashboard.putNumber(LIFT_STAB_KEY, 88);

        SmartDashboard.putNumber(ELEV_POSITION, 0);
        SmartDashboard.putNumber(ELEV_LOW_LIMIT, 0);
        SmartDashboard.putNumber(ELEV_CONE_KEY, 50);
        SmartDashboard.putNumber(ELEV_CUBE_KEY, 55);
        SmartDashboard.putNumber(ELEV_STAB_KEY, 5);
    }

    @Override
    public void periodic() {
        SmartDashboard.putNumber(LIFT_POSITION, liftSubsystem.getPosition());
        SmartDashboard.putNumber(ELEV_POSITION, elevatorSubsystem.getPosition());
    }

    @Override
    public void raiseArm(double speed) {
        liftSubsystem.raiseArm(speed);
    }

    @Override
    public void lowerArm(double speed) {
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
                lTarget= SmartDashboard.getNumber(LIFT_CUBE_KEY, 65);
                eTarget = SmartDashboard.getNumber(ELEV_CUBE_KEY, 55);
                break;
            case "Stable": 
                lTarget= SmartDashboard.getNumber(LIFT_STAB_KEY, 88);
                eTarget = SmartDashboard.getNumber(ELEV_STAB_KEY, 5);
                break;
            default:
                return status;
        }
        boolean status1 = liftSubsystem.moveToTarget(lTarget);
        boolean status2 = elevatorSubsystem.moveToTarget(eTarget);
        status = status1 && status2;
        if (status) System.out.println("Completed move to target:"+itemType);
        return status;
    }

    @Override
    public void extendArm(double speed) {
        elevatorSubsystem.extendArm(speed);
    }

    @Override
    public void retractArm(double speed) {
        elevatorSubsystem.retractArm(speed);
    }

    public void stopElevator() {
        elevatorSubsystem.stop();
    }

    public void stopLift() {
        liftSubsystem.stop();
    }

    public void stop() {
        if (!(liftSubsystem.isStopped() && elevatorSubsystem.isStopped()))
            System.out.println("Stopped arm.....");
        liftSubsystem.stop(); // Call this even if already stopped to maintain position
        elevatorSubsystem.stop();
    }

    @Override
    public void resetEncoderPos() {
        liftSubsystem.setPosition(0);
        elevatorSubsystem.setPosition(0);        
    }

    @Override
    public void simulationPeriodic() {
        double elev_change = elevatorSubsystem.getCurrentSpeed();
        double lift_change = liftSubsystem.getCurrentSpeed();
        if (lift_change!=0)
            liftSubsystem.setPosition(liftSubsystem.getPosition()+lift_change); 
        liftSubsystem.setPosition(liftSubsystem.getPosition()-0.001); // simulate the pull of gravity
        if (elev_change!=0)
            elevatorSubsystem.setPosition(elevatorSubsystem.getPosition()+elev_change);
    }
}
