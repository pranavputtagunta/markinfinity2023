package frc.robot.interfaces;

public interface IntakeController {
    public void grabCone(double MoveTime);
    public void releaseCone(double moveTime);
    public void grabCube(double moveTime);
    public void releaseCube(double moveTime);
    public void stop();
}
