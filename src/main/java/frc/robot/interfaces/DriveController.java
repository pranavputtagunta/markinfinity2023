package frc.robot.interfaces;

import com.revrobotics.RelativeEncoder;

public interface DriveController {
    public final String ROTATIONAL_ACC_LIMIT="Rot Acc Limt";
    public void move(double speed, double rotation);
    public void stop();
    public RelativeEncoder getRighttEncoder();
    public RelativeEncoder getLeftEncoder();
    public void resetEncoders();
}
