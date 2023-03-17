package frc.robot.subsystem;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.SerialPort;

public class GyroSubsystem {
    //private ADXRS450_Gyro gyro1 = new ADXRS450_Gyro();
    final AHRS ahrsGyro = new AHRS(SerialPort.Port.kUSB);
    double simulatedYaw = 0;

    public GyroSubsystem() {
        init();
    }

    public void init() {
        if (ahrsGyro!=null) ahrsGyro.reset();
        simulatedYaw = 0;
    }

    public double getAngle() {
        return ahrsGyro==null ? 0 : ahrsGyro.getAngle();
    }

    public double getYaw() {
        return simulatedYaw!=0 ? simulatedYaw: ahrsGyro==null ? 0 : ahrsGyro.getYaw();
    }

    public double getRoll() {
        return ahrsGyro==null ? 0 : ahrsGyro.getRoll();
    }

    public double getPitch() {
        return ahrsGyro==null ? 0 : ahrsGyro.getPitch();
    }

    public void simulationPeriodic(double rotSpeed) {
        simulatedYaw += rotSpeed;
        if (simulatedYaw>180)
            simulatedYaw=-180+(simulatedYaw-180);
        if (simulatedYaw<=-180)
            simulatedYaw=180+(simulatedYaw+180);
    }

    public Rotation2d getRotation2d() {
        return ahrsGyro==null ? new Rotation2d() : ahrsGyro.getRotation2d();
    }

    public void reset() {
        if (ahrsGyro!=null) ahrsGyro.reset();
    }
}