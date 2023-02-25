package frc.robot.implementation;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;

public class LiftSubsystem {
    private final CANSparkMax m_leftDrive1 = new CANSparkMax(1, MotorType.kBrushless);
    private final CANSparkMax m_rightDrive1 = new CANSparkMax(2, MotorType.kBrushless);
    private final MotorControllerGroup m_rightMotors = new MotorControllerGroup(m_rightDrive1);
    private final MotorControllerGroup m_leftMotors = new MotorControllerGroup(m_leftDrive1);

    private final DifferentialDrive lift = new DifferentialDrive(m_leftMotors, m_rightMotors);

    public void raiseArm(double magnitude) {
        System.out.println("raiseArm:"+magnitude);
        lift.arcadeDrive(magnitude, 0);
    }

    public void lowerArm(double magnitude) {
        System.out.println("lowerArm:"+magnitude);
        lift.arcadeDrive(magnitude, 0);
    }

    public void extendArm(double magnitude) {
        System.out.println("extendArm:"+magnitude);
    }

    public void retractArm(double magnitude) {
        System.out.println("retractArm:"+magnitude);
    }
}
