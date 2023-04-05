package frc.robot.autonoperations;

import frc.robot.interfaces.ArmController;
import frc.robot.interfaces.DriveController;
import frc.robot.interfaces.IntakeController;
import frc.robot.subsystem.AccelerometerSubsystem;

public class AutoScoreCone {
    private DriveController driveController;
    private ArmController armController;
    private IntakeController intakeController;
    private AccelerometerSubsystem accelerometer;

    enum State {INITIAL, MOVEFORWARD, RAISEARMFORCONE, ADJUSTFORCONERELEASE, RELEASECONE, STABILIZEARM, PARK, DONE}
    State state ;

    long actionStartTime;
    
    public AutoScoreCone(DriveController driveController, ArmController armController, 
                IntakeController intakeController,AccelerometerSubsystem accelerometerSubsystem) 
    {
        this.driveController = driveController;
        this.armController = armController;
        this.intakeController = intakeController;
        this.accelerometer  = accelerometerSubsystem;
        this.state = State.INITIAL;
        this.actionStartTime = 0;
    }

    public boolean executeScoreAction(long timeInAuto) {
        switch (state) {
            case INITIAL:
                state = State.MOVEFORWARD;
                actionStartTime = timeInAuto;
                break;
            case MOVEFORWARD:
                System.out.println("AutoScore:  moving forward timeInAuto="+timeInAuto);
                //milliseconds to move for going near dropping point
                if (timeInAuto-actionStartTime < 2500 ) {
                    driveController.move(0.4, 0);
                } else {
                    driveController.stop();
                    state = State.RAISEARMFORCONE;
                    actionStartTime = timeInAuto;
                }
                break;
            case RAISEARMFORCONE:
                System.out.println("AutoScore:  raising elevator and adjusting pulley for scoring timeInAuto="+timeInAuto);
                if (armController.moveArmToTarget("Cone")) {
                    state = State.ADJUSTFORCONERELEASE;
                    actionStartTime = timeInAuto;
                }
                break;
            case ADJUSTFORCONERELEASE:
                System.out.println("AutoScore:  final drive or arm adjustments timeInAuto="+timeInAuto);
                //todo any final drive or arm adjustments
                actionStartTime = timeInAuto;
                state = State.RELEASECONE;
                break;
            case RELEASECONE:
                //release cone for millisecons
                if (timeInAuto-actionStartTime < 500) {
                    intakeController.releaseCone(1.0);
                } else {
                    intakeController.releaseCone(0);
                    actionStartTime = timeInAuto;
                    state = State.STABILIZEARM;
                }
                break;
            case STABILIZEARM:
                if (armController.moveArmToTarget("Stable")) {
                    actionStartTime = timeInAuto;
                    state = State.PARK;
                }
                break;
            case PARK:
                //todo
                actionStartTime = timeInAuto;
                state = State.DONE;
                break;
            case DONE:
                return false; //end of actions
            
        }
        return true; 
    }

}
