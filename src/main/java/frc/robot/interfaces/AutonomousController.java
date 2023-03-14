package frc.robot.interfaces;

public interface AutonomousController {
    public Action getNextAction(long timeInAutonomous);
    public void actionComplete(Action action);
    public Action calibrate(int calibrationCount, long timeInTest);
    public void autonomousInit(String[] autoOp);
    public void calibrationInit(int calibrationCycle);
}
