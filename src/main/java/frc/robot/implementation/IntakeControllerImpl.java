package frc.robot.implementation;

import frc.robot.interfaces.IntakeController;

public class IntakeControllerImpl implements IntakeController {
    IntakeSubsystem intakeSubsytem = new IntakeSubsystem();
    boolean stopped = true;

    public void grabCone(double magnitude) {
        stopped = false;
        System.out.println("grabCone:"+magnitude);
        intakeSubsytem.grab(magnitude);
    }


    public void releaseCone(double magnitude) {
        stopped = false;
        System.out.println("releaseCone:"+magnitude);
        intakeSubsytem.release(magnitude);
    }

  
    public void grabCube(double magnitude) {
        stopped = false;
        System.out.println("grabCube:"+magnitude);
        intakeSubsytem.grab(magnitude);
    }


    public void releaseCube(double magnitude) {
        stopped = false;
        System.out.println("releaseCube:"+magnitude);
        intakeSubsytem.release(magnitude);
    }

    public void stop() {
        if (!stopped) {
            intakeSubsytem.stop();
            stopped = true;
            System.out.println("Stopping.....");
        }
    }
}
