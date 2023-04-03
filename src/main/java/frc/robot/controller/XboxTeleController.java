package frc.robot.controller;

import edu.wpi.first.wpilibj.XboxController;
import frc.robot.interfaces.TeleController;
import  frc.robot.main.Constants;

/*
 * Uses PS4 controller to get speed and rotation based on button press and joy stick axis
 */
public class XboxTeleController implements TeleController {
  private XboxController xbc;
  
  public XboxTeleController(int port) {
    xbc = new XboxController(port);
  }

  @Override
  public String getControllerType() {
    return "Xbox";
  }

  @Override
  public double getRotation() {
    double rotation = xbc.getRawAxis(4);
    return rotation;
  }

  @Override
  public double getSpeed() {
    double speed = xbc.getRawAxis(1);
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
    //return true;
  }

  @Override
  public boolean shouldArmMove() {
    //return xbc.getRightBumper();
    return true;
  }

  public double getArmLiftSpeed() {
    double lift = xbc.getRawAxis(5);
    return lift;
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
    //return xbc.getLeftStickButton();
    return xbc.getLeftBumper() && xbc.getPOV()== Constants.XBoxConstants.kDPadRight;
  }

  @Override
  public boolean shouldArmMoveToCubeTarget() {
    //return xbc.getRightStickButton();
    return xbc.getLeftBumper() && xbc.getPOV()== Constants.XBoxConstants.kDPadLeft;
  }

  @Override
  public boolean shouldArmMoveToStablePos() {
    //return xbc.getStartButton();
    //System.out.println("Dpad value: " + xbc.getPOV());
    return xbc.getLeftBumper() && xbc.getPOV()== Constants.XBoxConstants.kDPadDown;
  }
}