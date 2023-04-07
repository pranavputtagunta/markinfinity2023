
package frc.robot.autonoperations;

import frc.robot.interfaces.ArmController;
import frc.robot.interfaces.DriveController;
import frc.robot.interfaces.IntakeController;
import frc.robot.subsystem.AccelerometerSubsystem;

public class AutonBalanceSimple {

    private DriveController driveController;
    private ArmController armController;
    private IntakeController intakeController;
    private AccelerometerSubsystem accelerometer;
    
    public AutonBalanceSimple(DriveController driveController, ArmController armController, 
        IntakeController intakeController,AccelerometerSubsystem accelerometerSubsystem) 
    {
        this.driveController = driveController;
        this.armController = armController;
        this.intakeController = intakeController;
        this.accelerometer  = accelerometerSubsystem;
    }

    
    public boolean autonBalance(long timeInAuto) {
        armController.stop();
        if(timeInAuto < 500){
          intakeController.releaseCone(.7);
        }
        if(timeInAuto > 500){
          intakeController.releaseCone(0);
        }
        armController.stop();

        if(timeInAuto < 800){
          return true;
        }
        
        if (timeInAuto < 4400) {
          driveController.move(0.4, 0);
          return true;
        } else {
          if (accelerometer.getTilt() < -10.5) {
            driveController.move(0.3, 0);
            return true;
          } else if (accelerometer.getTilt() > 10.5) {
            driveController.move(-0.3, 0);
            return true;
          } else {
            driveController.stop();
            return true; //finish
          }
        }
      }
}
