package frc.robot.main;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.controller.ArmControllerImpl;
import frc.robot.controller.AutonWithEncoder;
import frc.robot.controller.ClimbController;
import frc.robot.controller.DriveControllerImpl;
import frc.robot.controller.IntakeControllerImpl;
import frc.robot.controller.PSTeleController;
import frc.robot.controller.XboxTeleController;
import frc.robot.interfaces.Action;
import frc.robot.interfaces.ArmController;
import frc.robot.interfaces.IntakeController;
import frc.robot.interfaces.AutonomousController;
import frc.robot.interfaces.DriveController;
import frc.robot.interfaces.TeleController;
import frc.robot.main.Constants.IOConstants;
import frc.robot.subsystem.AccelerometerSubsystem;

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
  private TeleController driveteleController;
  private TeleController armTeleController;
  private DriveController driveController = new DriveControllerImpl();
  private ArmController armController = new ArmControllerImpl();
  private IntakeController intakeController = new IntakeControllerImpl();
  private AutonomousController autonomousController = new AutonWithEncoder(driveController);
  private AutoBalance mAutoBalance;

  //another auto balance
  private AccelerometerSubsystem accelerometer = AccelerometerSubsystem.getInstance();
  private ClimbController climbController = ClimbController.getInstance();


  Action lastAction = null;
  int calibrationCycle = 0;
  int cycle = 0;
  // Instructions for auton operation
  // Move 4ft, then take 2sec to move arm in place for cone, 1 sec to release
  // cone, 2 sec to secure arm, then move back 4ft, turn 90 deg,...
 // "Move 48, PCone 2, RCone 1, SArm 2, Move -48, Turn -90, Move -20";

  final String[] ritOps = {"Move 30"};
  final String[] lftOps = {"RCube 1", "Stop", "Move -80"};
  final String[] defOps = {"Move 50"}; // Balance 0
  final String[] midOps = {"Move 98"};

  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  /**
   * The container for the robot. Contains subsystems, OI devices, and commands.
   */
  public RobotContainer() {
    if ("PS4".equalsIgnoreCase(IOConstants.teleControllerType))
      driveteleController = new PSTeleController(IOConstants.psDriverControllerPort);
    else
      driveteleController = new XboxTeleController(IOConstants.xbDriverControllerPort);
    System.out.println("Using " + driveteleController.getControllerType() + " telecontroller");

    if ("PS4".equals(IOConstants.teleControllerType2))
      armTeleController = new PSTeleController(IOConstants.psDriverControllerPort2);
    else if (IOConstants.teleControllerType2 != null)
      armTeleController = new XboxTeleController(IOConstants.xbDriverControllerPort2);
    else {
      System.out.println("Using single controller for arm and drive");
      armTeleController = driveteleController;
    }
    m_chooser.setDefaultOption("DefaultSeq", "Default");
    m_chooser.addOption("RightSeq", "Right");
    m_chooser.addOption("LeftSeq", "Left");
    m_chooser.addOption("MiddleSeq", "Middle");

    SmartDashboard.putData("Auto choices", m_chooser);
    SmartDashboard.putString("Auton Commands", "");
  }

  public void simulationPeriodic() {
    driveController.simulationPeriodic();
    armController.simulationPeriodic();
  }

  public void autonomousInit() {
    driveController.init();
    armController.init();
    String autoOpr = SmartDashboard.getString("Auton Commands", "");
    mAutoBalance = new AutoBalance();
  
    if (autoOpr != null && autoOpr.length() > 0)
      autonomousController.autonomousInit(autoOpr.split(","));
    else {
      String[] autoOps;
      m_autoSelected = m_chooser.getSelected();
      System.out.println("Auto selected: " + m_autoSelected);    
      switch (m_autoSelected) {
        case "Right": autoOps = ritOps; break;
        case "Middle": autoOps = midOps; break;
        case "Left":autoOps = lftOps; break;
        default: autoOps = defOps; break;
      }
      autonomousController.autonomousInit(autoOps);
    }
  }

  void autonomousExit() {
    driveController.stop();
    armController.stop();
    intakeController.stop();
  }

  private void performAction(Action chosenAction) {
    switch (chosenAction.type) {
      case Balance:
        double speed = mAutoBalance.scoreAndBalance();
        driveController.move(speed/4, 0);
        break;
      case Turn:
        if (chosenAction.speed==0) { driveController.stop(); autonomousController.actionComplete(chosenAction); }
        else driveController.move(0, chosenAction.speed);
        break;
      case Move:
      if (chosenAction.speed==0) { driveController.stop(); autonomousController.actionComplete(chosenAction); }
      else driveController.move(chosenAction.speed, 0);
        break;
      case Stop:
        driveController.stop();
        autonomousController.actionComplete(chosenAction);
        break;
      case SArm:
        if (armController.moveArmToTarget("Stable"))
          autonomousController.actionComplete(chosenAction);
        break;
      case PCone:
        if (armController.moveArmToTarget("Cone"))
          autonomousController.actionComplete(chosenAction);
        break;
      case PCube:
        if (armController.moveArmToTarget("Cube"))
          autonomousController.actionComplete(chosenAction);
        break;
      case GCone:
        intakeController.grabCone(1.0);
        break;
      case RCone:
      if (chosenAction.speed==0) { intakeController.stop(); } else
        intakeController.releaseCone(1.0);
        break;
      case GCube:
        intakeController.grabCube(0.9);
        break;
      case RCube:
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
    // Get the next operation to perform and magnitude 
    Action chosenAction = autonomousController.getNextAction(timeInAutonomous);

    if (chosenAction != null) {
      if (lastAction != chosenAction) {
        System.out.println("Chosen action at time:" + timeInAutonomous + " is " + chosenAction);
        lastAction = chosenAction;
      }
      performAction(chosenAction);
      return true;
    } else {
      driveController.stop();
      armController.stop();
      return false;
    }
  }

  public void teleOpInit() {
    driveController.init();
    armController.init();
    boolean resetEncoderPos = SmartDashboard.getBoolean("Reset Encoder", false);
    if (resetEncoderPos) {
      System.out.println("Resetting arm encoder pos");
      armController.resetEncoderPos();
      SmartDashboard.putBoolean("Reset Encoder", false);
    }
  }

  public void calibrationInit() {
    driveController.init();
    calibrationCycle = (int) SmartDashboard.getNumber(DashboardItem.Calibrate_Cycle.name(), calibrationCycle);
    calibrationCycle++;
    SmartDashboard.putNumber(DashboardItem.Calibrate_Cycle.name(), calibrationCycle);
    System.out.println("Calibrate Cycle:" + calibrationCycle);
    autonomousController.calibrationInit(calibrationCycle);
  }

  public boolean calibrate(long timeInTest) {
    Action chosenAction = autonomousController.calibrate(calibrationCycle, timeInTest);
    if (chosenAction != null) {
      if (chosenAction.type != null)
        performAction(chosenAction);
      return true;
    } else {
      System.out.println("Calibrate complete for:" + calibrationCycle);
      return false;
    }
  }

  void periodic() {
    armController.periodic();
    driveController.periodic();
    SmartDashboard.putNumber("Tilt", accelerometer.getTilt());
  }

  private double limit(double orig, double limit) {
    //if (orig > -0.04 && orig < 0.04)
      //return 0.0;
    return orig * limit;
  }

  public void teleOp() {
    if (driveteleController.shouldRoboMove()) {
      double speed = limit(driveteleController.getSpeed(), 0.8);
      double rotation = limit(driveteleController.getRotation(), 0.5);
      if ((speed != 0) || (rotation != 0))
        driveController.move(speed, rotation);
      else
        driveController.stop();
    } else {
      driveController.stop();
    }

    if (armTeleController.shouldArmMove()) {
      armController.setCurrentTarget(null); // Stop automated move to target if user start manually adjusting arm
      double extendSpeed = limit(armTeleController.getArmExtensionSpeed(), 0.8);
      double liftSpeed = limit(armTeleController.getArmLiftSpeed(), 0.8);

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
    } else if ("Cone".equals(armController.getCurrentTarget()) || armTeleController.shouldArmMoveToConeTarget()) {
      armController.moveArmToTarget("Cone");
    } else if ("Cube".equals(armController.getCurrentTarget()) || armTeleController.shouldArmMoveToCubeTarget()) {
      armController.moveArmToTarget("Cube");
    } else if ("Stable".equals(armController.getCurrentTarget()) || armTeleController.shouldArmMoveToStablePos()) {
      armController.moveArmToTarget("Stable");
    } else {
      armController.stop();
    }

    if (armTeleController.shouldGrabCone()) {
      intakeController.grabCone(1.0);
    } else if (armTeleController.shouldGrabCube()) {
      intakeController.grabCube(1.0);
    } else if (armTeleController.shouldReleaseCone()) {
      intakeController.releaseCone(1.0);
    } else if (armTeleController.shouldReleaseCube()) {
      intakeController.releaseCube(1.0);
    } else {
      intakeController.stop();
    }
  }

  public void autonCommand(int bigNum) {
    if (cycle++ < bigNum)
      driveController.move(-.5, 0);
    else
      driveController.stop();
  }

}