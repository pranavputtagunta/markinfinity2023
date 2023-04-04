package frc.robot.controller;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.interfaces.IntakeController;
import frc.robot.subsystem.IntakeSubsystem;

public class IntakeControllerImpl implements IntakeController {
    IntakeSubsystem intakeSubsytem = new IntakeSubsystem();
    boolean stopped = true;
    ItemType currentPiece = null;
    private final String CURRENT_PIECE = "Current Piece";

    public IntakeControllerImpl() {
        SmartDashboard.putString(CURRENT_PIECE, "None");
    }
    
    public void grabCone(double speed) {
        stopped = false;
        currentPiece = ItemType.Cone;
        SmartDashboard.putString(CURRENT_PIECE, currentPiece.name());
        System.out.println("Grab:" + currentPiece);
        intakeSubsytem.grab(currentPiece, speed);
    }

    public void releaseCone(double speed) {
        stopped = false;
        System.out.println("Release Cone"); 
        intakeSubsytem.release(ItemType.Cone, speed);
        currentPiece = null;
        SmartDashboard.putString(CURRENT_PIECE, "None");
    }

    public void grabCube(double speed) {
        stopped = false;
        currentPiece = ItemType.Cube;
        SmartDashboard.putString(CURRENT_PIECE, currentPiece.name());
        System.out.println("Grab:" + currentPiece);
        intakeSubsytem.grab(currentPiece, speed);
    }

    public void releaseCube(double speed) {
        stopped = false;
        System.out.println("Release Cube"); 
        intakeSubsytem.release(ItemType.Cube, speed);
        currentPiece = null;
        SmartDashboard.putString(CURRENT_PIECE, "None");
    }

    public void stop() {
        if (currentPiece!=null) {
            System.out.println("Holding "+currentPiece);
            intakeSubsytem.hold(currentPiece);
        } else if (!stopped) {
            System.out.println("Stopping intake.....");
            intakeSubsytem.stop();
            stopped = true;
        }
    }
}