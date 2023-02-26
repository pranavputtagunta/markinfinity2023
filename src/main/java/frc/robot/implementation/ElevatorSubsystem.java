package frc.robot.implementation;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.main.Constants;

public class ElevatorSubsystem {
    private final CANSparkMax m_left = new CANSparkMax(Constants.DriveConstants.ELEV_LT, MotorType.kBrushless);
    private final CANSparkMax m_right = new CANSparkMax(Constants.DriveConstants.ELEV_RT, MotorType.kBrushless);
    private final RelativeEncoder m_encoder;
    private final MotorControllerGroup m_rightMotors = new MotorControllerGroup(m_right);
    private final MotorControllerGroup m_leftMotors = new MotorControllerGroup(m_left);

    private final DifferentialDrive elev = new DifferentialDrive(m_leftMotors, m_rightMotors);

    ElevatorSubsystem() {
        m_left.setIdleMode(IdleMode.kBrake);
        m_left.setInverted(false);
        m_right.setInverted(true);
        m_right.setIdleMode(IdleMode.kBrake);
        m_encoder = m_left.getEncoder();
        SmartDashboard.putNumber("Elev Position", m_encoder.getPosition());
        SmartDashboard.putNumber("Elev Rtrt Limit",-20);
        SmartDashboard.putNumber("Elev Xtnd Limit", 135);
    }

    public void extendArm(double magnitude) {
        System.out.println("extendArm:"+magnitude);
        double xtnd_limit = SmartDashboard.getNumber("Elev Xtnd Limit", 135);
        if (m_encoder.getPosition()>=xtnd_limit) {
            System.out.println("Cant go further than "+xtnd_limit);
            stop();
            return;
        }
        elev.arcadeDrive(magnitude, 0);
        SmartDashboard.putNumber("Elev Position", m_encoder.getPosition());
    }

    public void retractArm(double magnitude) {
        System.out.println("retractArm:"+magnitude);
        double rtrt_limit = SmartDashboard.getNumber("Elev rtrt Limit", -20);
        if (m_encoder.getPosition()<=rtrt_limit) 
        {
            System.out.println("Cant go further than "+rtrt_limit);
            stop();
            return;
        }
        elev.arcadeDrive(magnitude, 0);
        SmartDashboard.putNumber("Elev Position", m_encoder.getPosition());
    }

    public void stop() {
        elev.arcadeDrive(0, 0);
    }
}