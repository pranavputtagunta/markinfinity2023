package frc.robot.interfaces;

public interface ClawController {
    public void grabCone(double MoveTime);
    public void releaseCone(double moveTime);
    public void grabCube(double moveTime);
    public void releaseCube(double moveTime);
}
