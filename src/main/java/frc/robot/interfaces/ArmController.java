package frc.robot.interfaces;

public interface ArmController {
    public void raiseArm(double magnitude);
    public void lowerArm(double magnitude);
    public void extendArm(double magnitude);
    public void retractArm(double magnitude);
    public void stop();
}