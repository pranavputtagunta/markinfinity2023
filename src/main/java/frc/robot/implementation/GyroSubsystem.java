package frc.robot.implementation;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.SPI;

public class GyroSubsystem {
    
    private ADXRS450_Gyro gyro = new ADXRS450_Gyro();
    //AHRS gyro = new AHRS(SPI.Port.kMXP);

    public double getAngle() {
        return gyro.getAngle();
    }

    public double getRate() {
        return gyro.getRate();
    }

    public double getDegrees() {
        return gyro.getRotation2d().getDegrees();
    }  
}
