package frc.robot.interfaces;

public interface IntakeController {
    public enum ItemType {Cone, Cube};
    public void grabCone();
    public void releaseCone();
    public void grabCube();
    public void releaseCube();
    public void stop();
}
