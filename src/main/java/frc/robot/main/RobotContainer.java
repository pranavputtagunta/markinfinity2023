package frc.robot.main;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.implementation.ArmControllerImpl;
import frc.robot.implementation.IntakeControllerImpl;
import frc.robot.implementation.AutonomousControllerImpl;
import frc.robot.implementation.DriveControllerImpl;
import frc.robot.implementation.PSTeleController;
import frc.robot.implementation.XboxTeleController;

import frc.robot.interfaces.ArmController;
import frc.robot.interfaces.IntakeController;
import frc.robot.interfaces.AutonomousController;
import frc.robot.interfaces.DriveController;
import frc.robot.interfaces.TeleController;
import frc.robot.main.Constants.IOConstants;

/**
 * This class is where the bulk of the robot should be declared. Since
 * Command-based is a "declarative" paradigm, very little robot logic should
 * actually be handled in
 * the {@link Robot} periodic methods (other than the scheduler calls). Instead,
 * the structure of
 * the robot (including subsystems, commands, and trigger mappings) should be
 * declared here.
 */
public class RobotContainer {
  private TeleController teleController;
  private DriveController driveController = new DriveControllerImpl();
  private ArmController armController = new ArmControllerImpl();
  private IntakeController intakeController = new IntakeControllerImpl();
  private AutonomousController autonomousController = new AutonomousControllerImpl();
  String armMoveToTargetInProgress = null;
  Pair lastAction = null;
  int calibrationCycle = 0;

  // Instructions for auton operation
  // Move 4ft, then take 2sec to move arm in place for cone, 1 sec to release cone, 2 sec to secure arm, then move back 4ft, turn 90 deg,...
  String autoOp = null;// "Move 48, PCone 2, RCone 1, SArm 2, Move -48, Turn -90, Move -20";  

  /**
   * The container for the robot. Contains subsystems, OI devices, and commands.
   */
  public RobotContainer() {
    if ("PS4".equalsIgnoreCase(IOConstants.teleControllerType))
      teleController = new PSTeleController(IOConstants.psDriverControllerPort);
    else
      teleController = new XboxTeleController(IOConstants.xbDriverControllerPort);
    System.out.println("Using " + teleController.getControllerType() + " telecontroller");
    initDashboard();
  }

  public void simulationPeriodic() {
    driveController.simulationPeriodic();
    armController.simulationPeriodic();
  }

  public void autonomousInit() {
    String autoOpr = SmartDashboard.getString("Auton Commands", "");
    armMoveToTargetInProgress = null;
    if (autoOpr!=null && autoOpr.length()>0) 
      autonomousController.autonomousInit(autoOpr.split(","));
    else if (autoOp!=null)
      autonomousController.autonomousInit(autoOp.split(","));
  }

  private void performAction(Pair chosenAction) {
    switch (chosenAction.type) {
      case "Lift":
        armController.raiseArm(chosenAction.p1);
        break;
      case "Xtnd":
        armController.extendArm(chosenAction.p1);
      case "Turn":
        driveController.move(0, chosenAction.p1);
        break;
      case "Move":
        driveController.move(chosenAction.p1, 0);
        break;
      case "Stop":
        driveController.stop();
        break;
      case "SArm":
        armController.moveArmToTarget("Stable");
        break;
      case "PCone":
        armController.moveArmToTarget("Cone");
        break;
      case "PCube":
        armController.moveArmToTarget("Cube");
        break;
      case "GCone":
        intakeController.grabCone(1.0);
        break;
      case "RCone":
        intakeController.releaseCone(1.0);
        break;
      case "GCube":
        intakeController.grabCube(0.9);
        break;
      case "RCube":
        intakeController.releaseCube(1.0);
        break;
      default:
        System.out.print("Skipping UNKNOWN action:" + chosenAction.type);
    }
  }

  /*
   * Returns true if more autonomous operations are left.. false if all operations
   * have been completed
   */
  public boolean autonomousOp(long timeInAutonomous) {
    // Get the next operation to perform and magnitude (e.g <Move, 0.5> - meaning
    // move at 50% speed)
    Pair chosenAction = autonomousController.getNextAction(timeInAutonomous);

    if (chosenAction != null) {
      if (lastAction != chosenAction) {
        System.out.println("Chosen action at time:" + timeInAutonomous + " is " + chosenAction);
        lastAction = chosenAction;
      }
      performAction(chosenAction);
      return true;
    } else {
      return false;
    }
  }

  public void teleOpInit() {
    boolean resetEncoderPos = SmartDashboard.getBoolean("Reset Encoder", false);
    if (resetEncoderPos) {
      System.out.println("Resetting encoder pos");
      armController.resetEncoderPos();
      SmartDashboard.putBoolean("Reset Encoder", false);
    }
  }

  public void calibrationInit() {
    calibrationCycle = (int) SmartDashboard.getNumber(DashboardItem.Calibrate_Cycle.name(), calibrationCycle);
    calibrationCycle++;
    SmartDashboard.putNumber(DashboardItem.Calibrate_Cycle.name(), calibrationCycle);
    System.out.println("Calibrate Cycle:"+calibrationCycle);
    autonomousController.calibrationInit(calibrationCycle);
  }

  public boolean calibrate(long timeInTest) {
    Pair chosenAction = autonomousController.calibrate(calibrationCycle, timeInTest);
    if (chosenAction != null) {
      if (chosenAction.type != null)
        performAction(chosenAction);
      return true;
    } else {
      System.out.println("Calibrate complete for:"+calibrationCycle);
      return false;
    }
  }

  void periodic() {
    armController.periodic();
  }

  private double limit(double orig, double limit) {
    if (orig>-0.04 && orig<0.04) return 0.0;
    if (orig>limit) return limit;
    if (orig<-limit) return -limit;
    return orig;
  }

  public void teleOp() {
    if (teleController.shouldRoboMove()) {
      double speed = limit(teleController.getSpeed(), 1.0);
      double rotation = limit(teleController.getRotation(), 1.0);
      if ((speed!=0) || (rotation!= 0))
        driveController.move(speed, rotation);
      else
        driveController.stop();
    } else {
      driveController.stop();
    }

    if (teleController.shouldArmMove()) {
      armMoveToTargetInProgress = null; // Stop automated move to target if user start manually adjusting arm
      double extendSpeed = limit(teleController.getArmExtensionSpeed(), 0.75);
      double liftSpeed = limit(teleController.getArmLiftSpeed(), 0.66);

      if (extendSpeed > 0)
        armController.extendArm(extendSpeed);
      else if (extendSpeed < 0)
        armController.retractArm(extendSpeed);
      else
        armController.stopElevator();

      if (liftSpeed > 0)
        armController.raiseArm(liftSpeed);
      else if (liftSpeed < 0)
        armController.lowerArm(liftSpeed);
      else
        armController.stopLift();
    } else if ("Cone".equals(armMoveToTargetInProgress) || teleController.shouldArmMoveToConeTarget()) {
      armMoveToTargetInProgress = armController.moveArmToTarget("Cone")?null:"Cone";
    } else if ("Cube".equals(armMoveToTargetInProgress) || teleController.shouldArmMoveToCubeTarget()) {
      armMoveToTargetInProgress = armController.moveArmToTarget("Cube")?null:"Cube";
    } else if ("Stable".equals(armMoveToTargetInProgress) || teleController.shouldArmMoveToStablePos()) {
      armMoveToTargetInProgress = armController.moveArmToTarget("Stable")?null:"Stable";
    } else {
      armController.stop();
    }

    if (teleController.shouldGrabCone()) {
      intakeController.grabCone(1.0);
    } else if (teleController.shouldGrabCube()) {
      intakeController.grabCube(0.9);
    } else if (teleController.shouldReleaseCone()) {
      intakeController.releaseCone(1.0);
    } else if (teleController.shouldReleaseCube()) {
      intakeController.releaseCube(1.0);
    } else {
      intakeController.stop();
    }
  }

  private void initDashboard() {
    System.out.println("Initializing dashboard");
    for(DashboardItem m : DashboardItem.values()) { 
      System.out.println("Adding "+m.getKey() + " with "+m.getDefaultValue());
      SmartDashboard.putNumber(m.getKey(), m.getDefaultValue());
   }
   SmartDashboard.putBoolean("Reset Encoder", false);
   SmartDashboard.putString("Auton Commands", "");

  }
}