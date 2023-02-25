package frc.robot.implementation;

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
    double rotation = xbc.getRawAxis(5);
    
    return rotation;
  }

  @Override
  public double getSpeed() {
    double speed = -xbc.getRawAxis(1);
    return speed;
  }

  @Override
  public double getArmExtensionMagnitude() {
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

  public double getArmLiftMagnitude() {
    double lift = xbc.getRawAxis(5);
    return -lift;
  }

  @Override
  public boolean shouldGrab() {
    return xbc.getBButton();
  }

  @Override
  public boolean shouldRelease() {
    return false;
  }

  @Override
  public boolean shouldGrabCone() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean shouldGrabCube() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean shouldReleaseCone() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean shouldReleaseCube() {
    // TODO Auto-generated method stub
    return false;
  }
}