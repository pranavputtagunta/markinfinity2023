package frc.robot.implementation;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class ElevatorSubsystem {
    private final CANSparkMax m_left = new CANSparkMax(6, MotorType.kBrushless);
    private final CANSparkMax m_right = new CANSparkMax(7, MotorType.kBrushless);
    private final RelativeEncoder m_encoder;
    private final MotorControllerGroup m_rightMotors = new MotorControllerGroup(m_right);
    private final MotorControllerGroup m_leftMotors = new MotorControllerGroup(m_left);

    private final DifferentialDrive elev = new DifferentialDrive(m_leftMotors, m_rightMotors);

    ElevatorSubsystem() {
        m_left.setIdleMode(IdleMode.kBrake);
        m_left.setInverted(true);
        m_right.setIdleMode(IdleMode.kBrake);
        m_encoder = m_left.getEncoder();
        SmartDashboard.putNumber("Elev Position", m_encoder.getPosition());
    }

    public void extendArm(double magnitude) {
        System.out.println("extendArm:"+magnitude);
        elev.arcadeDrive(magnitude, 0);
        SmartDashboard.putNumber("Elev Position", m_encoder.getPosition());
    }

    public void retractArm(double magnitude) {
        System.out.println("retractArm:"+magnitude);
        elev.arcadeDrive(-magnitude, 0);
        SmartDashboard.putNumber("Elev Position", m_encoder.getPosition());
    }

    public void stop() {
        elev.arcadeDrive(0, 0);
    }
}
