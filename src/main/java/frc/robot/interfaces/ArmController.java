package frc.robot.interfaces;

public interface ArmController {
    public final String LIFT_POSITION = "Lift Cur Pos";
    public final String LIFT_CONE_KEY = "Lift Cone Pos";
    public final String LIFT_CUBE_KEY = "Lift Cube Pos";
    public final String LIFT_STAB_KEY = "Lift Stable Pos";
    public final String LIFT_LOW_LIMIT = "Lift Low Limit";
    public final String LIFT_RANGE = "Lift Range";
    public final String ELEV_POSITION = "Elev Cur Pos";
    public final String ELEV_CONE_KEY = "Elev Cone Pos";
    public final String ELEV_CUBE_KEY = "Elev Cube Pos";
    public final String ELEV_STAB_KEY = "Elev Stable Pos";
    public final String ELEV_LOW_LIMIT = "Elev Low Limit";
    public final String ELEV_RANGE = "Elev Range";

    public void raiseArm(double speed);
    public void lowerArm(double speed);
    public void extendArm(double speed);
    public void retractArm(double speed);
    public boolean moveArmToTarget(String itemType);
    public void periodic();
    public void stop();
    public void stopElevator();
    public void stopLift();
    public void resetEncoderPos();
}