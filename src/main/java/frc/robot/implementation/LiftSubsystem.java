package frc.robot.implementation;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

public class LiftSubsystem {
    private final CANSparkMax pulley = new CANSparkMax(5, MotorType.kBrushless);

    LiftSubsystem() {
        pulley.setIdleMode(IdleMode.kBrake);
    }

    public void raiseArm(double magnitude) {
        System.out.println("raiseArm:"+magnitude);
        double intakePower = 1.0;
        int intakeAmps = 25;

        pulley.set(intakePower);
        pulley.setSmartCurrentLimit(intakeAmps);
    }

    public void lowerArm(double magnitude) {
        System.out.println("lowerArm:"+magnitude);
        double intakePower = -1.0;
        int intakeAmps = 25;

        pulley.set(intakePower);
        pulley.setSmartCurrentLimit(intakeAmps);
    }

    public void stop() {
        pulley.stopMotor();
    }
}