// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.main;

import java.util.Date;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;


/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to each mode, 
 * as described in the TimedRobot documentation. If you change the name of this class or the package after creating this project,
 * you must also update the manifest file in the resource directory.
 */


public class RobotWithAutoBalance extends TimedRobot {
  private Command m_autonomousCommand;

  private CANSparkMax mLF;
  private CANSparkMax mLB;
  private CANSparkMax mRF;
  private CANSparkMax mRB;
  private CANSparkMax pulley;
  private AutoBalance mAutoBalance;

  @Override
  public void robotInit() {
    mLF = new CANSparkMax(17, MotorType.kBrushless);
    mLB = new CANSparkMax(18, MotorType.kBrushless);
    mRF = new CANSparkMax(19, MotorType.kBrushless);
    mRB = new CANSparkMax(20, MotorType.kBrushless);
    pulley = new CANSparkMax(1, MotorType.kBrushless);
    mAutoBalance = new AutoBalance();
  }
  @Override
  public void robotPeriodic() {
    CommandScheduler.getInstance().run();
  }

  @Override
  public void disabledInit() {
    mLB.setIdleMode(IdleMode.kBrake);
    mLF.setIdleMode(IdleMode.kBrake);
    mRB.setIdleMode(IdleMode.kBrake);
    mRF.setIdleMode(IdleMode.kBrake);
    pulley.setIdleMode(IdleMode.kBrake);
  }

  @Override
  public void disabledPeriodic() {}

  @Override
  public void autonomousInit() {
    mLB.setIdleMode(IdleMode.kBrake);
    mLF.setIdleMode(IdleMode.kBrake);
    mRB.setIdleMode(IdleMode.kBrake);
    mRF.setIdleMode(IdleMode.kBrake);
    pulley.setIdleMode(IdleMode.kBrake);
    if (m_autonomousCommand != null) {
      m_autonomousCommand.schedule();
    }
  }

  /***********************************************************************************
   * To implement the auto balance auto, just run the command in autonomous periodic *
   * and set your left and right motor speeds to the output like this                *
   ***********************************************************************************/
  @Override
  public void autonomousPeriodic() {
    double speed = mAutoBalance.scoreAndBalance();
    setSpeed(speed/4, -speed/4);
  }

  @Override
  public void teleopInit() {
    if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
    }
  }

  @Override
  public void teleopPeriodic() {}

  @Override
  public void testInit() {
    CommandScheduler.getInstance().cancelAll();
  }

  @Override
  public void testPeriodic() {}

  @Override
  public void simulationInit() {}

  @Override
  public void simulationPeriodic() {}

  //helper function for setting motor controller speeds
  private void setSpeed(double left, double right){
    mLF.set(left);
    mLB.set(left);
    mRF.set(right);
    mRB.set(right);
  }
}
