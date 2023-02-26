package frc.robot.implementation;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import frc.robot.interfaces.IntakeController.ItemType;

public class IntakeSubsystem {
    static final int INTAKE_CURRENT_LIMIT_A = 30; // How many amps the intake can use while picking up
    static final int INTAKE_HOLD_CURRENT_LIMIT_A = 5; // How many amps the intake can use while holding
    static final double INTAKE_OUTPUT_POWER = 1.0; // Percent output for intaking
    static final double INTAKE_HOLD_POWER = 0.07; // Percent output for holding
    private final CANSparkMax intake;

    IntakeSubsystem() {
        intake = new CANSparkMax(4, MotorType.kBrushed);
        intake.setIdleMode(IdleMode.kCoast);
    }

    public void grab(ItemType itemType) {
        double intakePower;
        int intakeAmps;

        if (itemType==ItemType.Cube) {       // cube in or cone out
            intakePower = INTAKE_OUTPUT_POWER;
            intakeAmps = INTAKE_CURRENT_LIMIT_A;
        } else {
            intakePower = -INTAKE_OUTPUT_POWER;
            intakeAmps = INTAKE_CURRENT_LIMIT_A;
        }
        setIntakeMotor(intakePower, intakeAmps);
    }

    public void release(ItemType itemType) {
        if (itemType==ItemType.Cube)
            grab(ItemType.Cone);
        else
            grab(ItemType.Cube);
    }

    public void hold(ItemType itemType) {
        double intakePower;
        int intakeAmps;
        if (itemType==ItemType.Cube) {
            intakePower = INTAKE_HOLD_POWER;
            intakeAmps = INTAKE_HOLD_CURRENT_LIMIT_A;
          } else {
            intakePower = -INTAKE_HOLD_POWER;
            intakeAmps = INTAKE_HOLD_CURRENT_LIMIT_A;
          }
          setIntakeMotor(intakePower, intakeAmps);
    }

    public void stop() {
        setIntakeMotor(0, 0);
        //intake.stopMotor();
    }

    /**
     * Set the arm output power.
     * 
     * @param percent desired speed
     * @param amps    current limit
     */
    private void setIntakeMotor(double speedPercent, int amps) {
        intake.set(speedPercent);
        intake.setSmartCurrentLimit(amps);
    }
}