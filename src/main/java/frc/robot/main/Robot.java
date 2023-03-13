// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.main;

import java.util.Date;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.CommandScheduler;


/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to each mode, 
 * as described in the TimedRobot documentation. If you change the name of this class or the package after creating this project,
 * you must also update the manifest file in the resource directory.
 */
public class Robot extends TimedRobot {
  private RobotContainer robotContainer;
  Date autonomousStartTime;
  boolean autonomousComplete;
  boolean calibrateInProgress;
  Date testStartTime = null;
  boolean moving = false;
  boolean rotating = false;

  /**
   * This function is run when the robot is first started up and should be used
   * for any initialization code.
   */
  @Override
  public void robotInit() {
    CameraServer.startAutomaticCapture();
    robotContainer = new RobotContainer();
  }

  /**
   * This function is called every 20 ms, no matter the mode. Use this for items
   * like diagnostics that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>
   * This runs after the mode specific periodic functions, but before LiveWindow
   * and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
    // Runs the Scheduler. This is responsible for polling buttons, adding
    // newly-scheduled commands, running already-scheduled commands, removing finished or
    // interrupted commands, and running subsystem periodic() methods. This must be called from the
    // robot's periodic block in order for anything in the Command-based framework to work.
    CommandScheduler.getInstance().run();
    robotContainer.periodic();
  }

  @Override
  public void simulationPeriodic() {
    // This method will be called once per scheduler run during simulation
    robotContainer.simulationPeriodic();
  }

  /** This function is run once each time the robot enters autonomous mode. */
  @Override
  public void autonomousInit() {
    autonomousComplete = false;
    autonomousStartTime = null;
    System.out.println("AutonomousInit.");
    robotContainer.autonomousInit();
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    // Return immediately if robo has completed all operations in autonomous mode.
    if (autonomousComplete) return;
    // Otherwise calculate the time the Robo has been in autonomous mode and invoke autonomousOp() in roboContainer
    Date currentTime = new Date();
    long difference = 0;
    if (autonomousStartTime==null) 
      autonomousStartTime = currentTime;
    else
      difference = currentTime.getTime()-autonomousStartTime.getTime();
    //System.out.println("Elapsed time: "+difference);
    // If autonomousOp returns false, then all operations are done and autonomous mode is complete
    if (!robotContainer.autonomousOp(difference)) {
      System.out.println("Autonomous completed in "+difference+" millisec!");
      autonomousComplete = true;
    }
    //robotContainer.autonCommand(30);
  }

  /**
   * This function is called once each time the robot enters teleoperated mode.
   */
  @Override
  public void teleopInit() {
    System.out.println("Teleop initialized");
    calibrateInProgress = false;
    robotContainer.teleOpInit();
  }

  /** This function is called periodically during teleoperated mode. */
  @Override
  public void teleopPeriodic() {
    robotContainer.teleOp();
  }

  /** This function is called once each time the robot enters test mode. */

  @Override
  public void testInit() {
    robotContainer.calibrationInit();
    testStartTime = null;
    calibrateInProgress = true;
  }

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {
    if (!calibrateInProgress) return;
    Date currentTime = new Date();
    long difference = 0;
    if (testStartTime==null) {
      testStartTime = currentTime;
    } else
      difference = currentTime.getTime()-testStartTime.getTime();
    //System.out.println("Elapsed time in test: "+difference);
    if (!robotContainer.calibrate(difference)) {
      testStartTime = null;
      calibrateInProgress = false;
    }
  }
}
