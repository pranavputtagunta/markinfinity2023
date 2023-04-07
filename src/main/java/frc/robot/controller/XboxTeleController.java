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
    double rotation = xbc.getRawAxis(2); //4
    // if (xbc.getRawAxis(3) > 0.5){
    //   rotation *= 0.75;
    // }
    return rotation;
  }

  @Override
  public double getSpeed() {
    double speed = xbc.getRawAxis(1);
    if (xbc.getRawAxis(3) > 0.5){
      speed *= 0.75;
    }
    return speed;
  }

  @Override
  public double getArmExtensionSpeed() {
    double val = xbc.getRawAxis(1);
    return -val;
  }

  @Override
  public boolean shouldRoboMove() {
    //return xbc.getLeftBumper();
    return true;
  }

  @Override
  public boolean shouldArmMove() {
    //return xbc.getRightBumper();
    //left bumper is reserved for preset values, so do not control arm manually when this is pressed
    return !xbc.getLeftBumper();
  }

  public double getArmLiftSpeed() {
    double lift = xbc.getRawAxis(5);
    if(Math.abs(lift) < 0.1 && Math.abs(lift) > 0){
      return 0;
    }
    return lift;
  }

  @Override
  public boolean shouldGrabCone() {
     return xbc.getRawAxis(3) > 0.5; //get trigger
    
  }

  @Override
  public boolean shouldGrabCube() {
    return xbc.getRawAxis(4)> 0.5; //get trigger
  }

  @Override
  public boolean shouldReleaseCone() {
    return xbc.getAButton();
  }

  @Override
  public boolean shouldReleaseCube() {
    return xbc.getBButton();
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