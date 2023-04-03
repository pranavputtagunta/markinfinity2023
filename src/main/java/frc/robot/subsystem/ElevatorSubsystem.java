package frc.robot.subsystem;

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
    private double elevRange = 70; // Difference between high and low encode values
    private double distanceToEncoderConversion = 0.75;  // 4 in = 3 enc
    double lowLimit = 0;
    boolean stopped = true;
    double currSpeed = 0;
    double speedLimitPoint = 2.0;
    double maxExtension = 0;

    private final DifferentialDrive elev = new DifferentialDrive(m_leftMotors, m_rightMotors);

    public ElevatorSubsystem() {
        m_left.setSmartCurrentLimit(35);
        m_right.setSmartCurrentLimit(35);
        m_left.setIdleMode(IdleMode.kBrake);
        m_left.setInverted(false);
        m_right.setInverted(true);
        m_right.setIdleMode(IdleMode.kBrake);
        m_encoder = m_left.getEncoder();
        SmartDashboard.putNumber(ArmController.ELEV_RANGE, elevRange);
        SmartDashboard.putNumber(ArmController.ELEV_MAX_EXTN, maxExtension);
        SmartDashboard.putNumber(ArmController.ELEV_LOW_LIMIT, lowLimit);
        SmartDashboard.putNumber(ArmController.ELEV_CONV_FACTOR, distanceToEncoderConversion);
    }

    public void init() {
        lowLimit = SmartDashboard.getNumber(ArmController.ELEV_LOW_LIMIT, lowLimit);
        elevRange = SmartDashboard.getNumber(ArmController.ELEV_RANGE, elevRange);
        speedLimitPoint = SmartDashboard.getNumber(ArmController.SPEED_LIMIT_POINT, speedLimitPoint);
        distanceToEncoderConversion = SmartDashboard.getNumber(ArmController.ELEV_CONV_FACTOR, distanceToEncoderConversion);
    }

    public double getMaxExtension() {
        return maxExtension;
    }

    public double getPosition() {
        return m_encoder.getPosition()-lowLimit;
    }

    public double getCurrentSpeed() {
        return currSpeed;
    }

    public boolean isStopped() {
        return stopped;
    }

    public void setPosition(double position) {
        lowLimit = SmartDashboard.getNumber(ArmController.ELEV_LOW_LIMIT, lowLimit);
        elevRange = SmartDashboard.getNumber(ArmController.ELEV_RANGE, elevRange);
        if (elevRange>0 && (position<lowLimit || position>elevRange+lowLimit)) {
            System.out.println("Elev pos:"+position+" outside limit");
            return;
        }
        m_encoder.setPosition(position); 
    }

    public void setMaxExtension(int maxExtensionInInches) {
        if (maxExtensionInInches==0) return;
        double maxExtensionRange = 2.0 + maxExtensionInInches * distanceToEncoderConversion;
        if (elevRange>0 && maxExtensionRange>elevRange)
            maxExtensionRange = elevRange;
        if (this.maxExtension != maxExtensionRange) {
            System.out.println("maxExtensionInInches:"+maxExtensionInInches+"...maxExtensionRange:"+maxExtension);
            this.maxExtension = maxExtensionRange;
        }
        SmartDashboard.putNumber(ArmController.ELEV_MAX_EXTN, maxExtension);
    }

    public void adjustArmExtension() {
        double currentPos = (double) m_encoder.getPosition();
        if (currentPos-maxExtension>1) {
            System.out.println("Adjusting arm pos("+currentPos+") to maxExtension:"+ maxExtension);
            moveToTarget(maxExtension);
        }
    }

    public void extendArm(double speed) {
        double currentPos = m_encoder.getPosition();
        System.out.println("Extend Arm speed:"+speed+", currentPos:"+currentPos+", maxExtension:"+maxExtension);
        if (maxExtension>0 && currentPos>=maxExtension) {        
            double amtLeft = maxExtension-currentPos;
             if (elevRange>0 && amtLeft<=0) {
                System.out.println("Cant go further than "+maxExtension);
                stop();
                return;
            } else if (speed>0.25 && elevRange>0 && speedLimitPoint>0 && amtLeft<speedLimitPoint) {
                double newSpeed = 0.25+(speed-0.25)*amtLeft/speedLimitPoint;
                System.out.println("Near limit:"+maxExtension+". Restricting speed from "+speed+" to "+newSpeed);
                speed = newSpeed;
            }
        }
        stopped = false;
        currSpeed = speed;
        elev.arcadeDrive(currSpeed, 0);
    }

    // Returns true if target reached
    public boolean moveToTarget(double target) {
        double curPos = m_encoder.getPosition();
        int diff = (int) (curPos-target);
        System.out.println("Moving elev to target:"+target+".. Diff:"+diff);
        double speed = Math.abs(diff)>10?0.75:Math.abs(diff)>2?0.5:0.25;
        if (diff>=1) { retractArm(-speed); return false; }
        else if (diff<=-1) { extendArm(speed); return false; }
        else { stop(); return true;}
    }

    public void retractArm(double speed) {
        double currentPos = m_encoder.getPosition();
        System.out.println("Retract Arm speed:"+speed+", currentPos:"+currentPos);
        if (elevRange>0 && currentPos<=0) {
            System.out.println("Cant go lower than "+lowLimit);
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
            elevRange = SmartDashboard.getNumber(ArmController.ELEV_RANGE, elevRange); // refresh from dashboard just incase it has been updated
            lowLimit = SmartDashboard.getNumber(ArmController.ELEV_LOW_LIMIT, lowLimit);
            currSpeed = 0;
            stopped = true;
        }
        elev.arcadeDrive(0, 0);
    }
}