package frc.robot.main;

import javax.lang.model.util.ElementScanner14;

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
import frc.robot.main.Constants.DashboardItem;
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
  private IntakeController clawController = new IntakeControllerImpl();
  private AutonomousController autonomousController = new AutonomousControllerImpl();
  Pair lastAction = null;
  int calibrationCycle = 0;

  String[][] autoOp = { { "Move 48", "Lift -24", "Turn 90" },  // Move 4ft, then lower arm, then turn 90 deg
                        { "Move 48", "Lift 24", "Xtnd 6", "Grab 100", "Grab -100", "Xtnd -6", "Lift -24", "Turn 90",
                          "Move -6", "Turn -90", "Move -6"}
                      };

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

  public void autonomousInit() {
    int autoSeq = (int) SmartDashboard.getNumber(DashboardItem.Auto_Sequence.name(), 0);
    if (autoSeq>=autoOp.length) autoSeq = 0;
    autonomousController.autonomousInit(autoOp[autoSeq]);
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
      if (extendMagnitude>0.7) extendMagnitude = 0.7;
      if (liftMagnitude>0.7) liftMagnitude = 0.7;

      if (extendMagnitude > 0.05)
        armController.extendArm(extendMagnitude);
      else if (extendMagnitude < -0.05)
        armController.retractArm(-extendMagnitude);
      else {
        armController.stop();
      }

      if (liftMagnitude > 0.05)
        armController.raiseArm(liftMagnitude);
      else if (liftMagnitude < -0.05) {
        armController.lowerArm(-liftMagnitude);
      }
      else {
        armController.stop();
      }
    } else {
      armController.stop();
    }

    double grabMagnitude = 0.3;
    if (teleController.shouldGrabCone()) {
      clawController.grabCone(grabMagnitude);
    } else if (teleController.shouldGrabCube()) {
      clawController.grabCube(grabMagnitude);
    } else if (teleController.shouldReleaseCone()) {
      clawController.releaseCone(grabMagnitude);
    } else if (teleController.shouldReleaseCube()) {
      clawController.releaseCube(grabMagnitude);
    } else {
      clawController.stop();
    } 

  }


  private void initDashboard() {
    System.out.println("Processing dashboard items");
    SmartDashboard.putNumber(DashboardItem.Calibrate_Cycle.name(), calibrationCycle);
    for(DashboardItem m : DashboardItem.values()) { 
      System.out.println("Adding "+m + " with "+m.getDefaultValue());
      SmartDashboard.putNumber(m.name(), m.getDefaultValue());
   }
   SmartDashboard.putNumber(DashboardItem.Auto_Sequence.name(), 0);
  }
}