package frc.robot.implementation;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.SparkMaxPIDController;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.interfaces.ArmController;
import frc.robot.main.Constants;

public class LiftSubsystem {
    private final CANSparkMax pulley = new CANSparkMax(Constants.DriveConstants.PULLEY, MotorType.kBrushless);
    private boolean stopped = true;
    private final int intakeAmps = 35;
    private RelativeEncoder m_encoder;
    private SparkMaxPIDController m_pidController;
    private double currSpeed = 0;
    private double stoppedPos;
    private double liftRange = 135; // Difference between high and low encode values

    LiftSubsystem() {
        pulley.setIdleMode(IdleMode.kBrake);
        pulley.setSmartCurrentLimit(intakeAmps);
        m_encoder = pulley.getEncoder();
        m_pidController = pulley.getPIDController();
        m_pidController.setFeedbackDevice(m_encoder);
        stoppedPos = m_encoder.getPosition();
    }

    public double getPosition() {
        return m_encoder.getPosition();
    }

    public boolean moveToTarget(double target) {
        target += SmartDashboard.getNumber(ArmController.LIFT_LOW_LIMIT, 0);
        double curPos = m_encoder.getPosition();
        int diff = (int) (curPos-target);
        double speed = Math.abs(diff)>10?0.75:0.25;
        if (diff>1) { lowerArm(speed); return false; }
        else if (diff<1) { raiseArm(speed); return false; }
        return true;
    }

    public void raiseArm(double magnitude) {
        double high_limit = SmartDashboard.getNumber(ArmController.LIFT_LOW_LIMIT, 80)+liftRange;
        if (m_encoder.getPosition()>high_limit) {
            System.out.println("Cant go higher than "+high_limit);
            stop();
            return;
        }
        currSpeed = magnitude;
        System.out.println("raiseArm:"+currSpeed);
        stopped = false;
        pulley.set(currSpeed);
        //pulley.setSmartCurrentLimit(intakeAmps);
    }

    public void lowerArm(double magnitude) {
        double low_limit = SmartDashboard.getNumber(ArmController.LIFT_LOW_LIMIT, -55);
        if (m_encoder.getPosition()<low_limit) {
            System.out.println("Cant go lower!!!");
            stop();
            return;
        }
        currSpeed = magnitude;
        System.out.println("lowerArm:"+currSpeed);
        stopped = false;
        pulley.set(currSpeed);
        //pulley.setSmartCurrentLimit(intakeAmps);
    }

    public boolean isStopped() {
        return stopped;
    }

    public void stop() {
        double currentPos = m_encoder.getPosition();
        if (!stopped) {
            currSpeed *= 0.5;
            System.out.println("Slowing lift motor..speed:"+currSpeed);
            pulley.set(currSpeed);
            stoppedPos = currentPos;
            if (currSpeed<0.05 && currSpeed>-0.05) {
                //pulley.stopMotor();
                pulley.set(0.01);
                stopped = true;
                System.out.println("Stopped Lift");
            }
        } else {
            if (currentPos-stoppedPos>2)
                lowerArm(-0.01);
            else if (currentPos-stoppedPos<-2)
                raiseArm(0.01);
        }
    }
}