package frc.robot.implementation;

//import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
//import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.math.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.main.Constants.*;

public class DriveSubsystem extends SubsystemBase {
    /*private final WPI_TalonSRX m_rightDrive2 = new WPI_TalonSRX(DriveConstants.TalonDevNumLt);
    private final WPI_VictorSPX m_leftDrive2 = new WPI_VictorSPX(DriveConstants.VictorDevNumLt);
    private final WPI_TalonSRX m_rightDrive1 = new WPI_TalonSRX(DriveConstants.TalonDevNumRt);
    private final WPI_VictorSPX m_leftDrive1 = new WPI_VictorSPX(DriveConstants.VictorDevNumRt);
    */

    private final CANSparkMax m_rightDrive1 = new CANSparkMax(DriveConstants.SparkDevNumRight1, MotorType.kBrushless); 
    private final CANSparkMax m_rightDrive2 = new CANSparkMax(DriveConstants.SparkDevNumRight2, MotorType.kBrushless);
    private final CANSparkMax m_leftDrive1 = new CANSparkMax(DriveConstants.SparkDevNumLeft1, MotorType.kBrushless);
    private final CANSparkMax m_leftDrive2 = new CANSparkMax(DriveConstants.SparkDevNumLeft2, MotorType.kBrushless);

    RelativeEncoder rtEncoder = m_rightDrive1.getEncoder();
    RelativeEncoder ltEncoder = m_leftDrive1.getEncoder();

    private final MotorControllerGroup m_rightMotors = new MotorControllerGroup(m_rightDrive1, m_rightDrive2);
    private final MotorControllerGroup m_leftMotors = new MotorControllerGroup(m_leftDrive1, m_leftDrive2);

    private final DifferentialDrive robotDrive = new DifferentialDrive(m_leftMotors, m_rightMotors);
    private double currentSpeed = 0;
    private double currentRotation = 0;

    public DriveSubsystem() {
        // We need to invert one side of the drivetrain so that positive voltages
        // result in both sides moving forward. Depending on how your robot's
        // gearbox is constructed, you might have to invert the left side instead.
        m_leftMotors.setInverted(true);
        
        m_rightDrive1.setSmartCurrentLimit(35);
        m_rightDrive2.setSmartCurrentLimit(35);
        m_leftDrive1.setSmartCurrentLimit(35);
        m_leftDrive2.setSmartCurrentLimit(35);
    
        //robotDrive.setExpiration(1.0);
        robotDrive.setSafetyEnabled(false);

        resetEncoders();
    }

    public double getCurrentSpeed() {
        return currentSpeed;
    }

    public double getCurrentRotation() {
        return currentRotation;
    }

    public double getRightEncoderPosition() {
        return  rtEncoder!=null? rtEncoder.getPosition() : 0;
    }

    public double getLeftEncoderPosition() {
        return  ltEncoder!=null? ltEncoder.getPosition() : 0;
    }

    public DifferentialDrive getRoboDrive() {
        return robotDrive;
    }

    public void resetEncoders() {
        if (rtEncoder!=null) rtEncoder.setPosition(0);
        if (ltEncoder!=null) ltEncoder.setPosition(0);
    }

    public void stopMotor() {
        currentSpeed = 0;
        currentRotation = 0;
        robotDrive.stopMotor();
    }

    /**
     * Drives the robot using arcade controls.
     *
     * @param fwd the commanded forward movement
     * @param rot the commanded rotation
     */
    public void arcadeDrive(double speed, double rotation) {
        currentSpeed = speed;
        currentRotation = rotation;
        robotDrive.arcadeDrive(speed, rotation);
    }

    /**
     * Controls the left and right sides of the drive directly with voltages.
     *
     * @param leftVolts  the commanded left output
     * @param rightVolts the commanded right output
     */
    public void tankDriveVolts(double leftVolts, double rightVolts) {
        m_leftMotors.setVoltage(leftVolts);
        m_rightMotors.setVoltage(rightVolts);
        robotDrive.feed();
    }
  
    /**
     * Returns the current wheel speeds of the robot.
     *
     * @return The current wheel speeds.
     */
    public DifferentialDriveWheelSpeeds getWheelSpeeds() {
      return null; //new DifferentialDriveWheelSpeeds(m_leftEncoder.getRate(), m_rightEncoder.getRate());
    }

    @Override
    public void simulationPeriodic() {
        ltEncoder.setPosition(ltEncoder.getPosition()+currentSpeed+currentRotation);
        rtEncoder.setPosition(rtEncoder.getPosition()+currentSpeed-currentRotation);
    }
}