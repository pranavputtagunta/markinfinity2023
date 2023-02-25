package frc.robot.implementation;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.main.Constants.*;

public class DriveSubsystem extends SubsystemBase {
    private final WPI_TalonSRX m_rightDrive2 = new WPI_TalonSRX(DriveConstants.TalonDevNumLt); //ok
    private final WPI_VictorSPX m_leftDrive2 = new WPI_VictorSPX(DriveConstants.VictorDevNumLt);
    private final WPI_TalonSRX m_rightDrive1 = new WPI_TalonSRX(DriveConstants.TalonDevNumRt); // OK
    private final WPI_VictorSPX m_leftDrive1 = new WPI_VictorSPX(DriveConstants.VictorDevNumRt); //Ok

    private final MotorControllerGroup m_rightMotors = new MotorControllerGroup(m_rightDrive1, m_rightDrive2);
    private final MotorControllerGroup m_leftMotors = new MotorControllerGroup(m_leftDrive1, m_leftDrive2);

    private final DifferentialDrive robotDrive = new DifferentialDrive(m_leftMotors, m_rightMotors);

    // The left-side drive encoder
    private final Encoder m_leftEncoder = new Encoder(
            DriveConstants.kLeftEncoderPorts[0],
            DriveConstants.kLeftEncoderPorts[1],
            DriveConstants.kLeftEncoderReversed);

    // The right-side drive encoder
    private final Encoder m_rightEncoder = new Encoder(
            DriveConstants.kRightEncoderPorts[0],
            DriveConstants.kRightEncoderPorts[1],
            DriveConstants.kRightEncoderReversed);

    public DriveSubsystem() {
        // We need to invert one side of the drivetrain so that positive voltages
        // result in both sides moving forward. Depending on how your robot's
        // gearbox is constructed, you might have to invert the left side instead.
        m_rightMotors.setInverted(true);
        
        // Sets the distance per pulse for the encoders
        m_leftEncoder.setDistancePerPulse(DriveConstants.kEncoderDistancePerPulse);
        m_rightEncoder.setDistancePerPulse(DriveConstants.kEncoderDistancePerPulse);

        //robotDrive.setExpiration(1.0);

        resetEncoders();
    }

    public DifferentialDrive getRoboDrive() {
        return robotDrive;
    }

    public void resetEncoders() {
        m_leftEncoder.reset();
        m_rightEncoder.reset();
    }

    public void stopMotor() {
        robotDrive.stopMotor();
    }

    /**
     * Drives the robot using arcade controls.
     *
     * @param fwd the commanded forward movement
     * @param rot the commanded rotation
     */
    public void arcadeDrive(double speed, double rotation) {
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

    @Override
    public void periodic() {
      // Update the odometry in the periodic block
      //m_odometry.update(m_gyro.getRotation2d(), m_leftEncoder.getDistance(), m_rightEncoder.getDistance());
    }
  
    /**
     * Returns the currently-estimated pose of the robot.
     *
     * @return The pose.
     */
    public Pose2d getPose() {
      return new Pose2d(); //m_odometry.getPoseMeters();
    }
  
    /**
     * Returns the current wheel speeds of the robot.
     *
     * @return The current wheel speeds.
     */
    public DifferentialDriveWheelSpeeds getWheelSpeeds() {
      return new DifferentialDriveWheelSpeeds(m_leftEncoder.getRate(), m_rightEncoder.getRate());
    }
}