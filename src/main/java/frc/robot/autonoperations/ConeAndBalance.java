
package frc.robot.autonoperations;

import frc.robot.interfaces.ArmController;
import frc.robot.interfaces.DriveController;
import frc.robot.interfaces.IntakeController;
import frc.robot.subsystem.AccelerometerSubsystem;

public class ConeAndBalance {

    private DriveController driveController;
    private ArmController armController;
    private IntakeController intakeController;
    private AccelerometerSubsystem accelerometer;
    
    public ConeAndBalance(DriveController driveController, ArmController armController, 
        IntakeController intakeController,AccelerometerSubsystem accelerometerSubsystem) 
    {
        this.driveController = driveController;
        this.armController = armController;
        this.intakeController = intakeController;
        this.accelerometer  = accelerometerSubsystem;
    }

    
    public boolean autonBalance(long timeInAuto) {
        
        if(timeInAuto < 700){
          armController.lowerArm(0.7);
          armController.extendArm(1);
        }else{
          armController.stop();
        }

        if(timeInAuto < 900){
          return true;
        }

        if(timeInAuto < 1200 ){
          intakeController.releaseCone(.7);
        }else {
          intakeController.stop();
        }

        if(timeInAuto < 1400){
          return true;
        }

        if(timeInAuto < 2190){
          armController.raiseArm(-0.7);
          armController.retractArm(-1);
        }else{
          armController.stop();
        }

        if(timeInAuto < 2300){
          return true;
        }

        
        


        
        
        if (timeInAuto < 6600) {
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
        //return true; 
        
      
      }
}
