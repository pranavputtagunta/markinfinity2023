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
        ahrsGyro.reset();
        simulatedYaw = 0;
    }

    public double getAngle() {
        return ahrsGyro.getAngle();
    }

    public double getYaw() {
        return simulatedYaw!=0 ? simulatedYaw: ahrsGyro.getYaw();
    }

    public double getRoll() {
        return ahrsGyro.getRoll();
    }

    public double getPitch() {
        return ahrsGyro.getPitch();
    }

    public void simulationPeriodic(double rotSpeed) {
        simulatedYaw += rotSpeed;
        if (simulatedYaw>180)
            simulatedYaw=-180+(simulatedYaw-180);
        if (simulatedYaw<=-180)
            simulatedYaw=180+(simulatedYaw+180);
    }

    public Rotation2d getRotation2d() {
        return ahrsGyro.getRotation2d();
    }

    public void reset() {
        ahrsGyro.reset();
    }
}