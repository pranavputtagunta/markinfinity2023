package frc.robot.implementation;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;

public class IntakeSubsystem {
    private final CANSparkMax m_leftDrive1 = new CANSparkMax(3, MotorType.kBrushless);
    private final CANSparkMax m_rightDrive1 = new CANSparkMax(4, MotorType.kBrushless);
    private final MotorControllerGroup m_rightMotors = new MotorControllerGroup(m_rightDrive1);
    private final MotorControllerGroup m_leftMotors = new MotorControllerGroup(m_leftDrive1);

    private final DifferentialDrive intake = new DifferentialDrive(m_leftMotors, m_rightMotors);
    
    public void grab(double magnitude) {
        System.out.println("Grab:"+magnitude);
        intake.arcadeDrive(-magnitude, 0);
    }

    public void release(double magnitude) {
        System.out.println("Release:"+magnitude);
        intake.arcadeDrive(magnitude, 0);
    }

    

}
