package frc.robot.implementation;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.interfaces.ArmController;
import frc.robot.main.Constants;

public class ElevatorSubsystem {
    private final CANSparkMax m_left = new CANSparkMax(Constants.DriveConstants.ELEV_LT, MotorType.kBrushless);
    private final CANSparkMax m_right = new CANSparkMax(Constants.DriveConstants.ELEV_RT, MotorType.kBrushless);
    private final RelativeEncoder m_encoder;
    private final MotorControllerGroup m_rightMotors = new MotorControllerGroup(m_right);
    private final MotorControllerGroup m_leftMotors = new MotorControllerGroup(m_left);
    private double elevRange = 180; // Difference between high and low encode values
    boolean stopped = true;
    private final DifferentialDrive elev = new DifferentialDrive(m_leftMotors, m_rightMotors);

    ElevatorSubsystem() {
        m_left.setIdleMode(IdleMode.kBrake);
        m_left.setInverted(false);
        m_right.setInverted(true);
        m_right.setIdleMode(IdleMode.kBrake);
        m_encoder = m_left.getEncoder();
    }

    public double getPosition() {
        return m_encoder.getPosition();
    }

    public void extendArm(double magnitude) {
        System.out.println("extendArm:"+magnitude);
        double xtnd_limit = SmartDashboard.getNumber(ArmController.ELEV_LOW_LIMIT, 0)+elevRange;
        if (m_encoder.getPosition()>=xtnd_limit) {
            System.out.println("Cant go further than "+xtnd_limit);
            stop();
            return;
        }
        stopped = false;
        elev.arcadeDrive(magnitude, 0);
    }

    boolean moveToTarget(double target) {
        target += SmartDashboard.getNumber(ArmController.ELEV_LOW_LIMIT, -20);
        double curPos = m_encoder.getPosition();
        int diff = (int) (curPos-target);
        double speed = Math.abs(diff)>10?0.75:0.25;
        if (diff>1) { extendArm(speed); return false; }
        else if (diff<1) { retractArm(speed); return false; }
        return true;
    }

    public void retractArm(double magnitude) {
        System.out.println("retractArm:"+magnitude);
        double rtrt_limit = SmartDashboard.getNumber(ArmController.ELEV_LOW_LIMIT, -20);
        if (m_encoder.getPosition()<=rtrt_limit) {
            System.out.println("Cant go further than "+rtrt_limit);
            stop();
            return;
        }
        stopped = false;
        elev.arcadeDrive(magnitude, 0);
    }

    public void stop() {
        if (!stopped) {
            System.out.println("Stopping elevator......");
            stopped = true;
        }
        elev.arcadeDrive(0, 0);
    }
}