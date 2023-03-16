package frc.robot.controller;

import edu.wpi.first.wpilibj.XboxController;
import frc.robot.interfaces.TeleController;

/*
 * Uses PS4 controller to get speed and rotation based on button press and joy stick axis
 */
public class XboxTeleController implements TeleController {
  private XboxController xbc;
  
  public XboxTeleController(int port) {
    xbc = new XboxController(0);
  }

  @Override
  public String getControllerType() {
    return "Xbox";
  }

  @Override
  public double getRotation() {
    double rotation = -xbc.getRawAxis(4);
    return rotation;
  }

  @Override
  public double getSpeed() {
    double speed = -xbc.getRawAxis(1);
    return speed;
  }

  @Override
  public double getArmExtensionSpeed() {
    double val = xbc.getRawAxis(1);
    return -val;
  }

  @Override
  public boolean shouldRoboMove() {
    return xbc.getLeftBumper();
  }

  @Override
  public boolean shouldArmMove() {
    return xbc.getRightBumper();
  }

  public double getArmLiftSpeed() {
    double lift = xbc.getRawAxis(5);
    return -lift;
  }

  @Override
  public boolean shouldGrabCone() {
     return xbc.getXButton();
    
  }

  @Override
  public boolean shouldGrabCube() {
    return xbc.getYButton();
  }

  @Override
  public boolean shouldReleaseCone() {
    return xbc.getBButton();
  }

  @Override
  public boolean shouldReleaseCube() {
    return xbc.getAButton();
  }

  @Override
  public boolean shouldArmMoveToConeTarget() {
    return xbc.getLeftStickButton();
  }

  @Override
  public boolean shouldArmMoveToCubeTarget() {
    return xbc.getRightStickButton();
  }

  @Override
  public boolean shouldArmMoveToStablePos() {
    return xbc.getStartButton();
  }
}