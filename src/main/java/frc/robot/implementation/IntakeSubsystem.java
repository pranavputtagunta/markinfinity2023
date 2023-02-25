package frc.robot.implementation;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;

public class IntakeSubsystem {
    private final CANSparkMax intakeDrive = new CANSparkMax(4, MotorType.kBrushless);
    private final MotorControllerGroup m_rightMotors = new MotorControllerGroup(intakeDrive);

    private final DifferentialDrive intake = new DifferentialDrive(m_rightMotors,null);
    
    IntakeSubsystem() {
        intakeDrive.setIdleMode(IdleMode.kBrake);
    }

    public void grab(double magnitude) {
        System.out.println("Grab:"+magnitude);
        intake.arcadeDrive(-magnitude, 0);
    }

    public void release(double magnitude) {
        System.out.println("Release:"+magnitude);
        intake.arcadeDrive(magnitude, 0);
    }

    

}
