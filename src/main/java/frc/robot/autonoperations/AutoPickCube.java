package frc.robot.autonoperations;

import frc.robot.interfaces.ArmController;
import frc.robot.interfaces.DriveController;
import frc.robot.interfaces.IntakeController;
import frc.robot.subsystem.AccelerometerSubsystem;

public class AutoPickCube {
    private DriveController driveController;
    private ArmController armController;
    private IntakeController intakeController;
    private AccelerometerSubsystem accelerometer;

    enum State {INITIAL, MOVEFORWARD, LOWERARM, RUNINTAKE, RAISEARM, TURN, DONE}
    State state ;

    long actionStartTime;
    
    public AutoPickCube(DriveController driveController, ArmController armController, 
                IntakeController intakeController,AccelerometerSubsystem accelerometerSubsystem) 
    {
        this.driveController = driveController;
        this.armController = armController;
        this.intakeController = intakeController;
        this.accelerometer  = accelerometerSubsystem;
        this.state = State.INITIAL;
        this.actionStartTime = 0;
    }

    public boolean executePickUpCubeAction(long timeInAuto) {
        switch(state) {
            case INITIAL:
                break;
            case MOVEFORWARD:
                break;
            case LOWERARM:
                break;
            case RUNINTAKE:
                break;
            case RAISEARM:
                break;
            case TURN:
                break;
            case DONE:
                break;
        }

        return false; //todo
    }

}
