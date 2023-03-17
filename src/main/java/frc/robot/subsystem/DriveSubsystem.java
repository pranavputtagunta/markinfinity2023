package frc.robot.subsystem;

//import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
//import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

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

    RelativeEncoder rtEncoder1 = m_rightDrive1.getEncoder();
    RelativeEncoder rtEncoder2 = m_rightDrive2.getEncoder();

    RelativeEncoder ltEncoder1 = m_leftDrive1.getEncoder();
    RelativeEncoder ltEncoder2 = m_leftDrive2.getEncoder();

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

        m_rightDrive1.setIdleMode(IdleMode.kBrake);
        m_rightDrive2.setIdleMode(IdleMode.kBrake);
        m_leftDrive1.setIdleMode(IdleMode.kBrake);
        m_leftDrive2.setIdleMode(IdleMode.kBrake);

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
        return  rtEncoder1!=null? rtEncoder1.getPosition(): 0;
        //return  rtEncoder1!=null? (rtEncoder1.getPosition()+rtEncoder2.getPosition())/2.0 : 0;
    }

    public double getLeftEncoderPosition() {
        return  ltEncoder1!=null? -ltEncoder1.getPosition(): 0;
        //return  ltEncoder1!=null? -(ltEncoder1.getPosition()+ltEncoder2.getPosition())/2.0 : 0;
    }

    public DifferentialDrive getRoboDrive() {
        return robotDrive;
    }

    public void resetEncoders() {
        System.out.println("Resetting encoders");
        if (rtEncoder1!=null) rtEncoder1.setPosition(0);
        if (rtEncoder2!=null) rtEncoder2.setPosition(0);
        if (ltEncoder1!=null) ltEncoder1.setPosition(0);
        if (ltEncoder2!=null) ltEncoder2.setPosition(0);
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

    @Override
    public void simulationPeriodic() {
        double rotToEncVal = currentRotation/2.0;
        double speedToEncVal = currentSpeed/2.0;
        if (rtEncoder1!=null) rtEncoder1.setPosition(rtEncoder1.getPosition()+speedToEncVal+rotToEncVal);
        if (rtEncoder2!=null) rtEncoder2.setPosition(rtEncoder2.getPosition()+speedToEncVal+rotToEncVal);
        if (ltEncoder1!=null) ltEncoder1.setPosition(ltEncoder1.getPosition()-speedToEncVal+rotToEncVal);
        if (ltEncoder2!=null) ltEncoder2.setPosition(ltEncoder2.getPosition()-speedToEncVal+rotToEncVal);
    }
}