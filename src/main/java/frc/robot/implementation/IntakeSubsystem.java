package frc.robot.implementation;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

public class IntakeSubsystem {
    private final CANSparkMax intake = new CANSparkMax(4, MotorType.kBrushed);
    
    IntakeSubsystem() {
        intake.setIdleMode(IdleMode.kBrake);
    }

    public void grab(double magnitude) {
        System.out.println("Grab:"+magnitude);
        double intakePower = 1.0;
        int intakeAmps = 25;

        intake.set(intakePower);
        intake.setSmartCurrentLimit(intakeAmps);
    }

    public void release(double magnitude) {
        System.out.println("Release:"+magnitude);
        double intakePower = -1.0;
        int intakeAmps = 25;

        intake.set(intakePower);
        intake.setSmartCurrentLimit(intakeAmps);
    }

    

}
