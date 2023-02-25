package frc.robot.implementation;

import frc.robot.interfaces.ClawController;

public class ClawControllerImpl implements ClawController {
    IntakeSubsystem intakeSubsytem = new IntakeSubsystem();
    
    public void grabCone(double moveTime) {
        System.out.println("grabCone:"+moveTime);
        intakeSubsytem.grab(1.0);
    }
    

    public void releaseCone(double moveTime) {
        System.out.println("releaseCone:"+moveTime);
        intakeSubsytem.release(1.0);
    }

  
    public void grabCube(double moveTime) {
        System.out.println("grabCube:"+moveTime);
        intakeSubsytem.grab(1.0);
    }


    public void releaseCube(double moveTime) {
        System.out.println("releaseCube:"+moveTime);
        intakeSubsytem.release(1.0);
    }
}
