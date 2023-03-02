package frc.robot.implementation;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.interfaces.ArmController;
import frc.robot.main.Constants;

public class ElevatorSubsystem {
    private final CANSparkMax m_left = new CANSparkMax(Constants.DriveConstants.ELEV_LT, MotorType.kBrushless);
    private final CANSparkMax m_right = new CANSparkMax(Constants.DriveConstants.ELEV_RT, MotorType.kBrushless);
    private final RelativeEncoder m_encoder;
    private final MotorControllerGroup m_rightMotors = new MotorControllerGroup(m_right);
    private final MotorControllerGroup m_leftMotors = new MotorControllerGroup(m_left);
    private double elevRange = 180; // Difference between high and low encode values
    boolean stopped = true;
    double currSpeed = 0;
    private final DifferentialDrive elev = new DifferentialDrive(m_leftMotors, m_rightMotors);

    ElevatorSubsystem() {
        m_left.setIdleMode(IdleMode.kBrake);
        m_left.setInverted(false);
        m_right.setInverted(true);
        m_right.setIdleMode(IdleMode.kBrake);
        m_encoder = m_left.getEncoder();
        SmartDashboard.putNumber(ArmController.ELEV_RANGE, elevRange);
    }

    public double getPosition() {
        return m_encoder.getPosition();
    }

    public double getCurrentSpeed() {
        return currSpeed;
    }

    public boolean isStopped() {
        return stopped;
    }

    public void setPosition(double position) {
        double lowLimit = SmartDashboard.getNumber(ArmController.ELEV_LOW_LIMIT, 0);
        elevRange = SmartDashboard.getNumber(ArmController.ELEV_RANGE, elevRange);
        if (position<lowLimit || position>elevRange+lowLimit) {
            System.out.println("Lift pos outside limit");
            return;
        }
        m_encoder.setPosition(position); 
    }

    public void extendArm(double speed) {
        System.out.println("extendArm:"+speed);
        double low_limit = SmartDashboard.getNumber(ArmController.ELEV_LOW_LIMIT, 0);
        double xtnd_limit = low_limit+elevRange;
        if (m_encoder.getPosition()>=xtnd_limit) {
            elevRange = SmartDashboard.getNumber(ArmController.ELEV_RANGE, elevRange); // Reread it from dashboard
            xtnd_limit = low_limit+elevRange;
            if (m_encoder.getPosition()>=xtnd_limit) {
                System.out.println("Cant go further than "+xtnd_limit);
                elevRange = SmartDashboard.getNumber(ArmController.ELEV_RANGE, elevRange);
                stop();
                return;
            }
        }
        stopped = false;
        currSpeed = speed;
        elev.arcadeDrive(speed, 0);
    }

    // Returns true if target reached
    boolean moveToTarget(double target) {
        target += SmartDashboard.getNumber(ArmController.ELEV_LOW_LIMIT, 0);
        double curPos = m_encoder.getPosition();
        int diff = (int) (curPos-target);
        System.out.println("Moving elev  to target:"+target+".. Diff:"+diff);
        double speed = Math.abs(diff)>10?0.75:Math.abs(diff)>2?0.25:0.1;
        if (diff>1) { retractArm(-speed); return false; }
        else if (diff<1) { extendArm(speed); return false; }
        else { stop(); return true;}
    }

    public void retractArm(double speed) {
        System.out.println("retractArm:"+speed);
        double rtrt_limit = SmartDashboard.getNumber(ArmController.ELEV_LOW_LIMIT, 0);
        if (m_encoder.getPosition()<=rtrt_limit) {
            System.out.println("Cant go further than "+rtrt_limit);
            stop();
            return;
        }
        stopped = false;
        currSpeed = speed;
        elev.arcadeDrive(speed, 0);
    }

    public void stop() {
        if (!stopped) {
            System.out.println("Stopping elevator......");
            currSpeed = 0;
            stopped = true;
        }
        elev.arcadeDrive(0, 0);
    }
}