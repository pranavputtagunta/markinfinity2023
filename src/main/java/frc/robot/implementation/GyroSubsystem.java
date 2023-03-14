package frc.robot.implementation;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.SerialPort;

public class GyroSubsystem {
    //private ADXRS450_Gyro gyro1 = new ADXRS450_Gyro();
    final AHRS ahrsGyro = new AHRS(SerialPort.Port.kUSB);
    double simulatedAngle = 0;

    GyroSubsystem() {
        init();
    }

    public void init() {
        ahrsGyro.reset();
        simulatedAngle = 0;
    }

    public double getAngle() {
        return simulatedAngle!=0 ? simulatedAngle: ahrsGyro.getAngle();
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

    void simulationPeriodic(double rotSpeed) {
        simulatedAngle += rotSpeed;
        if (simulatedAngle>180)
            simulatedAngle=-180+(simulatedAngle-180);
        if (simulatedAngle<=-180)
            simulatedAngle=180+(simulatedAngle+180);
    }
}