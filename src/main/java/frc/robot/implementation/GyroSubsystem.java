package frc.robot.implementation;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.SerialPort;

public class GyroSubsystem {
    //private ADXRS450_Gyro gyro1 = new ADXRS450_Gyro();
    AHRS ahrsGyro = new AHRS(SerialPort.Port.kUSB);

    GyroSubsystem() {
        ahrsGyro.reset();
    }

    public double getYaw() {
        return ahrsGyro.getYaw();
    }

    public double getRoll() {
        return ahrsGyro.getRoll();
    }

    public double getPitch() {
        return ahrsGyro.getPitch();
    }
}