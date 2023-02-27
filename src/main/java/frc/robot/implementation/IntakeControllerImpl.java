package frc.robot.implementation;

import frc.robot.interfaces.IntakeController;

public class IntakeControllerImpl implements IntakeController {
    IntakeSubsystem intakeSubsytem = new IntakeSubsystem();
    boolean stopped = true;
    ItemType currentPiece = null;
    
    public void grabCone() {
        stopped = false;
        currentPiece = ItemType.Cone;
        System.out.println("Grab:" + currentPiece);
        intakeSubsytem.grab(currentPiece);
    }

    public void releaseCone() {
        stopped = false;
        System.out.println("Release:" + currentPiece); 
        intakeSubsytem.release(currentPiece);
        currentPiece = null;
    }

    public void grabCube() {
        stopped = false;
        currentPiece = ItemType.Cone;
        System.out.println("Grab:" + currentPiece);
        intakeSubsytem.grab(currentPiece);
    }

    public void releaseCube() {
        stopped = false;
        System.out.println("Release:" + currentPiece); 
        intakeSubsytem.release(currentPiece);
        currentPiece = null;
    }

    public void stop() {
        if (currentPiece!=null) {
            System.out.println("Holding "+currentPiece);
            intakeSubsytem.hold(currentPiece);
        } else if (!stopped) {
            System.out.println("Stopping.....");
            intakeSubsytem.stop();
            stopped = true;
        }
    }
}
