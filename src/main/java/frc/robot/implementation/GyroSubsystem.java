package frc.robot.implementation;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.SPI;

public class GyroSubsystem {
    private ADXRS450_Gyro gyro1 = new ADXRS450_Gyro();
    AHRS ahrsGyro = new AHRS(SPI.Port.kMXP);

    GyroSubsystem() {
        ahrsGyro.reset();
    }

    public double getAngle() {
        return ahrsGyro.getAngle();
    }

    public double getRate() {
        return ahrsGyro.getRate();
    }

    public double getDegrees() {
        return ahrsGyro.getRotation2d().getDegrees();
    }  
}
