package frc.robot.implementation;

import frc.robot.interfaces.ArmController;

public class ArmControllerImpl implements ArmController {
    LiftSubsystem liftSubsystem = new LiftSubsystem();

    @Override
    public void raiseArm(double magnitude) {
        liftSubsystem.raiseArm(magnitude);
    }

    @Override
    public void lowerArm(double magnitude) {
        liftSubsystem.lowerArm(magnitude);
    }

    @Override
    public void extendArm(double magnitude) {
        liftSubsystem.extendArm(magnitude);
    }

    @Override
    public void retractArm(double magnitude) {
        liftSubsystem.retractArm(magnitude);
    }
}
