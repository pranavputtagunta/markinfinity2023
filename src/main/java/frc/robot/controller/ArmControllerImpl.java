package frc.robot.controller;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.interfaces.ArmController;
import frc.robot.subsystem.ElevatorSubsystem;
import frc.robot.subsystem.LiftSubsystem;
import frc.robot.subsystem.PulleyLiftSubsystem;

public class ArmControllerImpl implements ArmController {
    PulleyLiftSubsystem liftSubsystem = new PulleyLiftSubsystem();
    ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem();
    String currentTarget = null;
    
    public ArmControllerImpl() {
        SmartDashboard.putNumber(LIFT_POSITION, 0);
        SmartDashboard.putNumber(LIFT_LOW_LIMIT, 0);
        SmartDashboard.putNumber(LIFT_CONE_KEY, 60);
        SmartDashboard.putNumber(LIFT_CUBE_KEY, 65);
        SmartDashboard.putNumber(LIFT_STAB_KEY, 50);

        SmartDashboard.putNumber(ELEV_POSITION, 0);
        SmartDashboard.putNumber(ELEV_LOW_LIMIT, 0);
        SmartDashboard.putNumber(ELEV_CONE_KEY, 50);
        SmartDashboard.putNumber(ELEV_CUBE_KEY, 55);
        SmartDashboard.putNumber(ELEV_STAB_KEY, 5);
    }

    public void init() {
        liftSubsystem.init();
        elevatorSubsystem.init();
        currentTarget = null;
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

    @Override
    public String getCurrentTarget() {
        return currentTarget;
    }

    @Override
    public void setCurrentTarget(String currentTarget) {
        this.currentTarget = currentTarget;
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
                lTarget= SmartDashboard.getNumber(LIFT_STAB_KEY, 50);
                eTarget = SmartDashboard.getNumber(ELEV_STAB_KEY, 5);
                break;
            default:
                return status;
        }
        currentTarget = itemType;
        boolean status1 = liftSubsystem.moveToTarget(lTarget);
        boolean status2 = elevatorSubsystem.moveToTarget(eTarget);
        status = status1 && status2;
        if (status) {
            System.out.println("Completed move to target:"+itemType);
            currentTarget = null;
        }
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
        double lowLimit = SmartDashboard.getNumber(LIFT_LOW_LIMIT, 0);
        if (lift_change!=0)
            liftSubsystem.setPosition(liftSubsystem.getPosition()+lift_change);
        if (liftSubsystem.getPosition()>lowLimit)
            liftSubsystem.setPosition(liftSubsystem.getPosition()-0.001); // simulate the pull of gravity
        if (elev_change!=0)
            elevatorSubsystem.setPosition(elevatorSubsystem.getPosition()+elev_change);
    }
}
