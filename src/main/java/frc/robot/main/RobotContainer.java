package frc.robot.main;

import frc.robot.implementation.ArmControllerImpl;
import frc.robot.implementation.ClawControllerImpl;
import frc.robot.implementation.AutonomousControllerImpl;
import frc.robot.implementation.DriveControllerImpl;
import frc.robot.implementation.PSTeleController;
import frc.robot.implementation.XboxTeleController;

import frc.robot.interfaces.ArmController;
import frc.robot.interfaces.ClawController;
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
  private ClawController clawController = new ClawControllerImpl();
  private AutonomousController autonomousController = new AutonomousControllerImpl();
  Pair lastAction = null;

  String[] autoOp = { "Move -48", "Lift -24", "Turn 90" };
  String[] autoOp2 = { "Move 48", "Lift 24", "Xtnd 6", "Grab 100", "Grab -100", "Xtnd -6", "Lift -24", "Turn 90",
      "Move -6", "Turn -90", "Move -6" }; // Move 4ft

  /**
   * The container for the robot. Contains subsystems, OI devices, and commands.
   */
  public RobotContainer() {
    if ("PS4".equalsIgnoreCase(IOConstants.teleControllerType))
      teleController = new PSTeleController(IOConstants.psDriverControllerPort);
    else
      teleController = new XboxTeleController(IOConstants.xbDriverControllerPort);
    System.out.println("Using " + teleController.getControllerType() + " telecontroller");
  }

  public void autonomousInit() {
    autonomousController.autonomousInit(autoOp);
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
      case "MoveConeForward":
        clawController.grabCone(chosenAction.p1);
        break;
      case "MoveConeBackward":
        clawController.releaseCone(chosenAction.p1);
        break;
      case "MoveCubeForward":
        clawController.grabCube(chosenAction.p1);
        break;
      case "MoveCubeBackward":
        clawController.releaseCube(chosenAction.p1);
        break;
      default:
        System.out.print("Skipping " + chosenAction.type);
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

  public void calibrationInit() {
    autonomousController.calibrationInit();
  }

  public boolean calibrate(int testCount, long timeInTest) {
    Pair chosenAction = autonomousController.calibrate(testCount, timeInTest);
    if (chosenAction != null) {
      if (chosenAction.type != null)
        performAction(chosenAction);
      return true;
    } else
      return false;
  }

  public void teleOp() {
    if (teleController.shouldRoboMove()) {
      double speed = teleController.getSpeed();
      double rotation = teleController.getRotation();
      if ((Math.abs(speed) > 0.05) || (Math.abs(rotation) > 0.05))
        driveController.move(speed, rotation);
      else
        driveController.stop();
    } else {
      driveController.stop();
    }

    if (teleController.shouldArmMove()) {
      double extendMagnitude = teleController.getArmExtensionMagnitude();
      double liftMagnitude = teleController.getArmLiftMagnitude();
      if (extendMagnitude > 0.05)
        armController.extendArm(extendMagnitude);
      else if (extendMagnitude < -0.05)
        armController.retractArm(-extendMagnitude);

      if (liftMagnitude > 0.05)
        armController.raiseArm(liftMagnitude);
      else if (liftMagnitude < -0.05) {
        armController.lowerArm(-liftMagnitude);
      }
    }

    if (teleController.shouldGrabCone()) {
      clawController.grabCone(0);
    } else if (teleController.shouldGrabCube()) {
      clawController.grabCube(0);
    } else if (teleController.shouldReleaseCone()) {
      clawController.releaseCube(0);
    } else if (teleController.shouldReleaseCube()) {
      clawController.releaseCube(0);
    }
  }
}