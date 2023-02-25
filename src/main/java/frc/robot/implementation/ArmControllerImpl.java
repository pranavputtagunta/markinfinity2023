package frc.robot.implementation;

import frc.robot.interfaces.ArmController;

public class ArmControllerImpl implements ArmController {
    LiftSubsystem liftSubsystem = new LiftSubsystem();
    ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem();
    boolean stopped = true;
    @Override

    public void raiseArm(double magnitude) {
        stopped = false;
        liftSubsystem.raiseArm(magnitude);
    }

    @Override
    public void lowerArm(double magnitude) {
        stopped = false;
        liftSubsystem.lowerArm(magnitude);
    }

    @Override
    public void extendArm(double magnitude) {
        stopped = false;
        elevatorSubsystem.extendArm(magnitude);
    }

    @Override
    public void retractArm(double magnitude) {
        stopped = false;
        elevatorSubsystem.retractArm(magnitude);
    }

    public void stop() {
        liftSubsystem.stop();
        elevatorSubsystem.stop();
        if (!stopped) {
            System.out.println("Stopping......");
            stopped = true;
        }
       
    }
}
