package frc.robot.implementation;

import edu.wpi.first.wpilibj.PS4Controller;
import frc.robot.interfaces.TeleController;

/*
 * Uses PS4 controller to get speed and rotation based on button press and joy stick axis
 */
public class PSTeleController implements TeleController {
  private PS4Controller ps4c;
  
  public PSTeleController(int port) {
    ps4c = new PS4Controller(0);
  }

  @Override
  public String getControllerType() {
    return "PS4";
  }

  @Override
  public double getRotation() {
    double rotation = ps4c.getRawAxis(2);
    return rotation;
  }

  @Override
  public double getSpeed() {
    double speed = -ps4c.getRawAxis(1);
    return speed;
  }

  @Override
  public double getArmExtensionSpeed() {
    double val = ps4c.getRawAxis(5);
    return -val;
  }

  @Override
  public boolean shouldRoboMove() {
    return ps4c.getL1Button();
  }

  @Override
  public boolean shouldArmMove() {
    return ps4c.getR1Button();
  }

  public double getArmLiftSpeed() {
    double lift = ps4c.getRawAxis(1);
    return -lift;
  }

  @Override
  public boolean shouldGrabCone() {
    return ps4c.getTriangleButton();
  }

  @Override
  public boolean shouldGrabCube() {
    return ps4c.getSquareButton();
  }

  public boolean shouldReleaseCone() {
    return ps4c.getCrossButton();
  }

  @Override
  public boolean shouldReleaseCube() {
    return ps4c.getCircleButton();
  }

  @Override
  public boolean shouldArmMoveToConeTarget() {
    return ps4c.getL2Button();
  }

  @Override
  public boolean shouldArmMoveToCubeTarget() {
    return ps4c.getR2Button();
  }

  @Override
  public boolean shouldArmMoveToStablePos() {
    return ps4c.getPSButton();
  }
}
