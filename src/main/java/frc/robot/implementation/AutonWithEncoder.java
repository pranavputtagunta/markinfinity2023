package frc.robot.implementation;

import java.util.ArrayList;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.interfaces.AutonomousController;
import frc.robot.interfaces.DriveController;
import frc.robot.main.Pair;

public class AutonWithEncoder implements AutonomousController {
    ArrayList<Pair> actionMap = new ArrayList<Pair>(25);
    DriveController driveSubsystem;
    int curOp = 0;
    Double startLtPos, startRtPos;
    double autonMaxSpeed = 0.75;

    public AutonWithEncoder(DriveController driveSubsystem) {
        this.driveSubsystem = driveSubsystem;
        SmartDashboard.putNumber("Auton MAX Speed", autonMaxSpeed);
        SmartDashboard.putString("Auton Cur Action", "");
    }

    @Override
    public void autonomousInit(String[] autoOp) {
        System.out.println("Received " + autoOp.length + " autoOp items");
        autonMaxSpeed = SmartDashboard.getNumber("Auton MAX Speed", autonMaxSpeed);

        for (int i = 0; i < autoOp.length; i++) {
            System.out.println("Processing:" + autoOp[i]);
            int spaceIndx = autoOp[i].indexOf(' ');
            Integer distangl = Integer.parseInt(autoOp[i].substring(spaceIndx+1));
            String action = autoOp[i].substring(0, spaceIndx);
            Pair p = new Pair(autonMaxSpeed, distangl, action);
            System.out.println("Adding:" + p);
            actionMap.add(p);
        }
    }

    double convertToDistance(double encodeLtDelta, double encoderRtDelta) {
        return 2.355 * ((encodeLtDelta+encoderRtDelta)/2.0);
    }

    double convertToAngle(double encodeLtDelta, double encoderRtDelta) {
        return 1.5 * ((encodeLtDelta-encoderRtDelta)/2.0);
    }

    private double distanceRemainingToSpeed(double remaining) {
        boolean reverse = remaining<0? true: false;
        double factor = 1.0;
        if (Math.abs(remaining)<10) factor = 0.5;
        return factor*(reverse?-autonMaxSpeed:autonMaxSpeed);
    }

    private double angleRemainingToSpeed(double remaining) {
        boolean reverse = remaining<0? true: false;
        double factor = 1.0;
        if (Math.abs(remaining)<10) factor = 0.5;
        return factor*(reverse?-autonMaxSpeed:autonMaxSpeed);
    }

    @Override
    public Pair getNextAction(long timeInAutonomous) {
        while(curOp<actionMap.size()) {
            if (startLtPos==null) startLtPos =  driveSubsystem.getLeftEncoderPosition();
            if (startRtPos==null) startRtPos =  driveSubsystem.getRightEncoderPosition();
            Pair p =  actionMap.get(curOp);
            System.out.println("Currnet action:"+p);
            double currentLtPos = driveSubsystem.getLeftEncoderPosition();
            double currentRtPos = driveSubsystem.getRightEncoderPosition();
            double remaining = 0;
            if (p.type.equals("Move")) {
                double distMoved = convertToDistance(currentLtPos-startLtPos, currentRtPos-startRtPos);
                remaining = p.p2-distMoved;
                System.out.println("Distance Moved:"+distMoved+". Remaining:"+remaining);
                if (Math.abs(remaining)<=0.1) {
                    System.out.println("Completed action:"+p);
                    startLtPos = startRtPos = null;
                    curOp++;
                } else {
                    p.p1 = distanceRemainingToSpeed(remaining);
                    return p;
                }
            } else if (p.type.equals("Turn")) {
                double distTurned = convertToAngle(currentLtPos-startLtPos, currentRtPos-startRtPos);
                remaining = p.p2-distTurned;
                System.out.println("Distance Turned:"+distTurned+". Remaining:"+remaining);
                if (Math.abs(remaining)<=0.1) {
                    System.out.println("Completed action:"+p);
                    startLtPos = startRtPos = null;
                    curOp++;
                } else {
                    p.p1 = angleRemainingToSpeed(remaining);
                    return p;
                }
            }
            SmartDashboard.putString("Auton Cur Action", p.type+" "+remaining+"/"+p.p2);;
        }
        return null;
    }

    @Override
    public Pair calibrate(int calibrationCount, long timeInTest) {
        return null;
    }

    @Override
    public void calibrationInit(int calibrationCycle) {
    }    
}
