package frc.robot.implementation;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class LiftSubsystem {
    private final CANSparkMax pulley = new CANSparkMax(5, MotorType.kBrushless);
    private boolean stopped = true;
    private final int intakeAmps = 35;
    private RelativeEncoder m_encoder;
    private double currSpeed = 0;

    LiftSubsystem() {
        pulley.setIdleMode(IdleMode.kCoast);
        pulley.setSmartCurrentLimit(intakeAmps);
        m_encoder = pulley.getEncoder();
        SmartDashboard.putNumber("Lift Position", m_encoder.getPosition());
    }

    public void raiseArm(double magnitude) {
        currSpeed = magnitude;
        System.out.println("raiseArm:"+currSpeed);
        stopped = false;
        pulley.set(currSpeed);
        //pulley.setSmartCurrentLimit(intakeAmps);
        SmartDashboard.putNumber("Lift Position", m_encoder.getPosition());
    }

    public void lowerArm(double magnitude) {
        currSpeed = magnitude;
        System.out.println("lowerArm:"+currSpeed);
        stopped = false;
        pulley.set(currSpeed);
        //pulley.setSmartCurrentLimit(intakeAmps);
        SmartDashboard.putNumber("Lift Position", m_encoder.getPosition());
    }

    public boolean isStopped() {
        return stopped;
    }

    public void stop() {
        if (!stopped) {
            currSpeed *= 0.5;
            System.out.println("Slowing lift motor..speed:"+currSpeed);
            pulley.set(currSpeed);
            if (currSpeed<0.05) {
                pulley.stopMotor();
                stopped = true;
                System.out.println("Stopped Lift");
            }
        }
    }
}