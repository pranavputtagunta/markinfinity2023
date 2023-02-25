package frc.robot.implementation;

import frc.robot.interfaces.ClawController;

public class ClawControllerImpl implements ClawController {
    IntakeSubsystem intakeSubsytem = new IntakeSubsystem();
    
    public void grabCone(double moveTime) {
        System.out.println("ConeClawForward:"+moveTime);
    }
    

    public void releaseCone(double moveTime) {
        System.out.println("ConeClawBackward:"+moveTime);
    }

  
    public void grabCube(double moveTime) {
        System.out.println("CubeClawForward:"+moveTime);
    }


    public void releaseCube(double moveTime) {
        System.out.println("CubeClawBackward:"+moveTime);
    }

}
