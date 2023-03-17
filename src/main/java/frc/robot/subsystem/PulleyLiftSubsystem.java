package frc.robot.subsystem;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.SparkMaxPIDController;

import edu.wpi.first.math.controller.ArmFeedforward;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.math.trajectory.TrapezoidProfile.State;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.interfaces.ArmController;
import frc.robot.main.Constants;

public class PulleyLiftSubsystem {
    private final CANSparkMax pulley = new CANSparkMax(Constants.DriveConstants.PULLEY, MotorType.kBrushless);
    private boolean stopped = true;
    private final int intakeAmps = 35;
    private RelativeEncoder m_encoder;
    private SparkMaxPIDController m_pidController;
    private double currSpeed = 0;
    private double stoppedPos;
    private double liftRange = 117; // Difference between high and low encode values

    private TrapezoidProfile m_profile;
    private Timer m_timer;
    private double targetPos;
    TrapezoidProfile.Constraints kArmMotionConstraint = new TrapezoidProfile.Constraints(2.0, 2.0);
    private double lowLimit = -117;

    public PulleyLiftSubsystem() {
        pulley.setIdleMode(IdleMode.kBrake);
        pulley.setSmartCurrentLimit(intakeAmps);
        m_encoder = pulley.getEncoder();
        m_pidController = pulley.getPIDController();
        m_pidController.setFeedbackDevice(m_encoder);
/*             
        m_pidController.setP(0.6);
        m_pidController.setI(0.0);
        m_pidController.setD(0.0);
*/
        stoppedPos = m_encoder.getPosition();
        SmartDashboard.putNumber(ArmController.LIFT_RANGE, liftRange);
    }

    public void init() {
        //lowLimit = SmartDashboard.getNumber(ArmController.LIFT_LOW_LIMIT, 0);
        liftRange = SmartDashboard.getNumber(ArmController.LIFT_RANGE, liftRange);
    }

    public void setPosition(double position) {
        //lowLimit = SmartDashboard.getNumber(ArmController.LIFT_LOW_LIMIT, 0);
        liftRange = SmartDashboard.getNumber(ArmController.LIFT_RANGE, liftRange);
        if (liftRange!=0 && (position<lowLimit || position>liftRange+lowLimit)) {
            //System.out.println("Ignoring lift pos outside limit");
            return;
        }
        m_encoder.setPosition(position);
    }

    public double getPosition() {
        return m_encoder.getPosition();
    }

    // Returns true if target reached
    public boolean moveToTarget(double target) {
        target += SmartDashboard.getNumber(ArmController.LIFT_LOW_LIMIT, 0);
        double curPos = m_encoder.getPosition();
        double diff = curPos-target;
        System.out.println("Moving lift to target:"+target+".. Diff:"+diff);
        double speed = Math.abs(diff)>10?0.75:Math.abs(diff)>2?0.25:0.1;
        if (diff>.5) { lowerArm(-speed); return false; }
        else if (diff<-0.5) { raiseArm(speed); return false; }
        else {stop(); return true;}
    }

    public void raiseArm(double speed) {
        //lowLimit = SmartDashboard.getNumber(ArmController.LIFT_LOW_LIMIT, 0);
        double highLimit = lowLimit+liftRange;
        // if (m_encoder.getPosition()>highLimit) {
        //     liftRange = SmartDashboard.getNumber(ArmController.LIFT_RANGE, liftRange); // re=read from dashboard
        //     highLimit = lowLimit+liftRange;
        //     if (liftRange>0 && m_encoder.getPosition()>highLimit) {
        //         System.out.println("Can't go higher than "+highLimit);
        //         stop();
        //         return;
        //     }
        // }
        System.out.println("raiseArm:"+speed);
        stopped = false;
        pulley.set(speed);
        currSpeed = speed;
        //pulley.setSmartCurrentLimit(intakeAmps);
    }

    public void lowerArm(double speed) {
        // if (m_encoder.getPosition()<lowLimit) {
        //     //lowLimit = SmartDashboard.getNumber(ArmController.LIFT_LOW_LIMIT, 0);
        //     liftRange = SmartDashboard.getNumber(ArmController.LIFT_RANGE, liftRange); // re=read from dashboard
        //     if (liftRange>0 && m_encoder.getPosition()<lowLimit) {
        //         System.out.println("Cant go lower!!!");
        //         stop();
        //         return;
        //     }
        // }
        System.out.println("lowerArm:"+speed);
        stopped = false;
        pulley.set(speed);
        currSpeed = speed;
        //pulley.setSmartCurrentLimit(intakeAmps);
    }

    public boolean isStopped() {
        return stopped;
    }

    private void updateMotionProfile(double targetPos) {
        if (this.targetPos == targetPos)
            return;
        this.targetPos = targetPos;
        TrapezoidProfile.State state = new TrapezoidProfile.State(m_encoder.getPosition(), m_encoder.getVelocity());
        TrapezoidProfile.State goal = new TrapezoidProfile.State(targetPos, 0.0);

        m_profile = new TrapezoidProfile(kArmMotionConstraint, goal, state);
        m_timer.reset();
    }

    private void adjustArmToTarget() {
        double elapsedTime = m_timer.get();
        State targetState;
        if (m_profile.isFinished(elapsedTime)) {
          targetState = new TrapezoidProfile.State(targetPos, 0.0);
        } else {
          targetState = m_profile.calculate(elapsedTime);
        }
        ArmFeedforward aff = new ArmFeedforward(0.0, 0.4, 4.0, 0.0);  
        double feedforward = aff.calculate(m_encoder.getPosition()-(Math.PI/6), targetState.velocity);
        m_pidController.setReference(targetState.position, CANSparkMax.ControlType.kPosition, 0, feedforward);
    }

    public void stop() {
        double currentPos = m_encoder.getPosition();
        if (!stopped) {
            currSpeed *= 0.5;
            System.out.println("Slowing lift motor..speed:"+currSpeed);
            pulley.set(currSpeed);
            stoppedPos = currentPos;
            if (currSpeed<0.05 && currSpeed>-0.05) {
                pulley.stopMotor();
                //pulley.set(0.01);
                stopped = true;
                System.out.println("Stopped Lift at pos:"+stoppedPos);
                //double feedforward = 0;
                //m_pidController.setReference(stoppedPos, CANSparkMax.ControlType.kPosition, 0, feedforward);
            }
        } /*else {
            double diff = currentPos-stoppedPos;
            double speed = Math.abs(diff)>10?0.75:Math.abs(diff)>2?0.25:0.05;
            if (diff>1.0) { lowerArm(-speed/4.0); }
            else if (diff<-.25) { raiseArm(speed); }
            else if (currSpeed!=0) {currSpeed = 0; pulley.set(currSpeed); }
            stopped = true; // reset stopped to true as we are only in holding mode
        }*/
    }

    public double getCurrentSpeed() {
        return currSpeed;
    }
}