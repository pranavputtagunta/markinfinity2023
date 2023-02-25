package frc.robot.implementation;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

public class IntakeSubsystem {
    private final CANSparkMax intake = new CANSparkMax(4, MotorType.kBrushed);
    final int intakeAmps = 30;

    IntakeSubsystem() {
        intake.setIdleMode(IdleMode.kCoast);
    }

    public void grab(double magnitude) {
        System.out.println("Grab:"+magnitude);
        intake.set(magnitude);
        intake.setSmartCurrentLimit(intakeAmps);
    }

    public void release(double magnitude) {
        System.out.println("Release:"+magnitude);
        intake.set(magnitude);
        intake.setSmartCurrentLimit(intakeAmps);
    }

    public void stop() {
        intake.stopMotor();
    }
}
