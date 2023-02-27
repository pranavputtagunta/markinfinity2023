package frc.robot.interfaces;

public interface IntakeController {
    public enum ItemType {Cone, Cube};
    public void grabCone(double speed);
    public void releaseCone(double speed);
    public void grabCube(double speed);
    public void releaseCube(double speed);
    public void stop();
}
