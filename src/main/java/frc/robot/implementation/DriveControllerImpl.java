package frc.robot.implementation;

import com.revrobotics.RelativeEncoder;

import frc.robot.interfaces.DriveController;
import frc.robot.main.Constants.DriveConstants;

public class DriveControllerImpl implements DriveController {
    private double currentSpeed = 0.0;
    private double currentRotation = 0.0;
    private DriveSubsystem driveSubsystem = new DriveSubsystem();

    @Override
    public void stop() {
        if ((currentSpeed!=0) || (currentRotation!=0)) {
            currentSpeed = 0;
            currentRotation = 0;
            driveSubsystem.stopMotor();
            System.out.println("Not moving");
        }
        driveSubsystem.arcadeDrive(0, 0);
    }

    @Override
    public void move(double speed, double rotation) {
        if ((currentSpeed != speed) || (rotation!=currentRotation)) {
            System.out.println("Moving at speed:" + speed + ", rotation:" + rotation);
            currentSpeed = speed;
            currentRotation = rotation;
        }
        double adjustedSpeed = (speed>DriveConstants.maxSpeed) ? DriveConstants.maxSpeed: speed;
        driveSubsystem.arcadeDrive(adjustedSpeed, rotation);
    }

    @Override
    public void resetEncoders() {
        driveSubsystem.resetEncoders();
    }

    @Override
    public RelativeEncoder getRighttEncoder() {
        return driveSubsystem.getRightEncoder();
    }

    @Override
    public RelativeEncoder getLeftEncoder() {
        return driveSubsystem.getLeftEncoder();
    }
}