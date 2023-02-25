package frc.robot.implementation;

import frc.robot.interfaces.IntakeController;

public class IntakeControllerImpl implements IntakeController {
    IntakeSubsystem intakeSubsytem = new IntakeSubsystem();
    boolean stopped = true;

    public void grabCone(double moveTime) {
        stopped = false;
        System.out.println("grabCone:"+moveTime);
        intakeSubsytem.grab(1.0);
    }


    public void releaseCone(double moveTime) {
        stopped = false;
        System.out.println("releaseCone:"+moveTime);
        intakeSubsytem.release(1.0);
    }

  
    public void grabCube(double moveTime) {
        stopped = false;
        System.out.println("grabCube:"+moveTime);
        intakeSubsytem.grab(1.0);
    }


    public void releaseCube(double moveTime) {
        stopped = false;
        System.out.println("releaseCube:"+moveTime);
        intakeSubsytem.release(1.0);
    }

    public void stop() {
        if (!stopped) {
            intakeSubsytem.stop();
            stopped = true;
            System.out.println("Stopping.....");
        }
    }
}
