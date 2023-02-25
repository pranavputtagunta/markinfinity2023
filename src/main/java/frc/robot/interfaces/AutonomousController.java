package frc.robot.interfaces;

import frc.robot.main.Pair;

public interface AutonomousController {
    public Pair getNextAction(long timeInAutonomous);
    public Pair calibrate(int calibrationCount, long timeInTest);
    public void autonomousInit(String[] autoOp);
    public void calibrationInit(int calibrationCycle);
}
