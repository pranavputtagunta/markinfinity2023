package frc.robot.interfaces;

public interface DriveController {
    public final String RESET_ENCODER="Reset Encoder";
    public final String ROTATIONAL_ACC_LIMIT="Rot Acc Limt";
    public final String ENCODER_RT_POS="Drive Rt Pos";
    public final String ENCODER_LT_POS="Drive Lt Pos";
    public final String GYRO_ANGLE="Gyro Angle";
    public final String GYRO_RATE="Gyro Rate";

    public void move(double speed, double rotation);
    public void stop();
    public void resetEncoders();
    public double getLeftEncoderPosition();
    public double getRightEncoderPosition();
    public void simulationPeriodic();
    public void periodic();
}
