package frc.robot.subsystem;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import frc.robot.interfaces.IntakeController.ItemType;
import frc.robot.main.Constants;

public class IntakeSubsystem {
    static final int INTAKE_CURRENT_LIMIT_A = 30; // How many amps the intake can use while picking up
    static final int INTAKE_HOLD_CURRENT_LIMIT_A = 5; // How many amps the intake can use while holding
    static final double INTAKE_HOLD_POWER = 0.07; // Percent output for holding
    private final CANSparkMax intake;

    public IntakeSubsystem() {
        intake = new CANSparkMax(Constants.DriveConstants.INTAKE, MotorType.kBrushed);
        intake.setIdleMode(IdleMode.kCoast);
    }

    public void grab(ItemType itemType, double speed) {
        double intakePower;
        int intakeAmps;

        if (itemType==ItemType.Cube) {       // cube in or cone out
            intakePower = speed;
            intakeAmps = INTAKE_CURRENT_LIMIT_A;
        } else {
            intakePower = -speed;
            intakeAmps = INTAKE_CURRENT_LIMIT_A;
        }
        setIntakeMotor(intakePower, intakeAmps);
    }

    public void release(ItemType itemType, double speed) {
        if (itemType==ItemType.Cube)
            grab(ItemType.Cone, speed);
        else
            grab(ItemType.Cube, speed);
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