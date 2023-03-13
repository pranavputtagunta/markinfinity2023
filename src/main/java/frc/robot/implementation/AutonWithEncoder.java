package frc.robot.implementation;

import java.util.ArrayList;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.interfaces.AutonomousController;
import frc.robot.interfaces.DriveController;
import frc.robot.main.Pair;

public class AutonWithEncoder implements AutonomousController {
    ArrayList<Pair> actionMap = new ArrayList<Pair>(25);
    DriveController driveController;
    int curOp = 0;
    Double startLtPos, startRtPos;
    double autonMaxSpeed = 0.75;
    double encoderToDistanceConversion = 2.355;
    double encoderToAngleConversion = 10;
    long actionStartTime = 0;

    public AutonWithEncoder(DriveController driveController) {
        this.driveController = driveController;
        SmartDashboard.putNumber("Auton MAX Speed", autonMaxSpeed);
        SmartDashboard.putNumber("Encoder Dist Factor", encoderToDistanceConversion);
        SmartDashboard.putNumber("Encoder Angle Factor", encoderToAngleConversion);
        SmartDashboard.putString("Auton Cur Action", "");
    }

    @Override
    public void autonomousInit(String[] autoOp) {
        System.out.println("Received " + autoOp.length + " autoOp items");
        autonMaxSpeed = SmartDashboard.getNumber("Auton MAX Speed", autonMaxSpeed);
        encoderToDistanceConversion = SmartDashboard.getNumber("Encoder Dist Factor", encoderToDistanceConversion);
        encoderToAngleConversion = SmartDashboard.getNumber("Encoder Angle Factor", encoderToAngleConversion);
        actionStartTime = 0;
        startLtPos =  startRtPos = null;
        curOp = 0;
        for (int i = 0; i < autoOp.length; i++) {
            String autoOpr = autoOp[i].trim();
            System.out.println("Processing:" + autoOpr);
            int spaceIndx = autoOpr.indexOf(' ');
            Integer distangl = Integer.parseInt(autoOpr.substring(spaceIndx+1));
            String action = autoOpr.substring(0, spaceIndx);
            Pair p = new Pair(autonMaxSpeed, distangl, action);
            System.out.println("Adding:" + p);
            actionMap.add(p);
        }
    }

    double convertToDistance(double encodeLtDelta, double encoderRtDelta) {
        return encoderToDistanceConversion * ((encodeLtDelta+encoderRtDelta)/2.0);
    }

    double convertToAngle(double encodeLtDelta, double encoderRtDelta) {
        return encoderToAngleConversion * ((encodeLtDelta-encoderRtDelta)/2.0);
    }

    private double distanceRemainingToSpeed(double remaining) {
        boolean reverse = remaining<0? true: false;
        double factor = 1.0;
        if (Math.abs(remaining)<10) factor = 0.5;
        if (Math.abs(remaining)<5) factor = 0.25;
        return factor*(reverse?-autonMaxSpeed:autonMaxSpeed);
    }

    private double angleRemainingToSpeed(double remaining) {
        boolean reverse = remaining<0? true: false;
        double factor = 1.0;
        if (Math.abs(remaining)<10) factor = 0.5;
        if (Math.abs(remaining)<5) factor = 0.25;
        return factor*(reverse?autonMaxSpeed:-autonMaxSpeed);
    }

    @Override
    public void actionComplete(Pair action) {
        System.out.println("Completed action:"+action);
        startLtPos = startRtPos = null;
        actionStartTime = 0;
        curOp++;
    }

    @Override
    public Pair getNextAction(long timeInAutonomous) {
        Pair chosenAction = null;
        double remaining = 0;
        while(curOp<actionMap.size()) {
            if (startLtPos==null) startLtPos =  driveController.getLeftEncoderPosition();
            if (startRtPos==null) startRtPos =  driveController.getRightEncoderPosition();
            chosenAction = actionMap.get(curOp);
            System.out.println("Current action:"+chosenAction);
            if (actionStartTime==0) actionStartTime = timeInAutonomous;
            double currentLtPos = driveController.getLeftEncoderPosition();
            double currentRtPos = driveController.getRightEncoderPosition();
            if (chosenAction.type.equals("Move")) {
                double distMoved = convertToDistance(currentLtPos-startLtPos, currentRtPos-startRtPos);
                remaining = chosenAction.p2-distMoved;
                System.out.println("Distance Moved:"+distMoved+". Remaining:"+remaining);
                if (Math.abs(remaining)<=1) {
                    actionComplete(chosenAction);
                } else {
                    chosenAction.p1 = distanceRemainingToSpeed(remaining);
                    break;
                }
            } else if (chosenAction.type.equals("Turn")) {
                double distTurned = convertToAngle(currentLtPos-startLtPos, currentRtPos-startRtPos);
                remaining = chosenAction.p2-distTurned;
                System.out.println("Angle Turned:"+distTurned+". Remaining:"+remaining);
                if (Math.abs(remaining)<=1) {
                    actionComplete(chosenAction);
                } else {
                    chosenAction.p1 = angleRemainingToSpeed(remaining);
                    break;
                }
            } else if (chosenAction.type.equals("PCone") || chosenAction.type.equals("PCube") || chosenAction.type.equals("Stable") ) {
                break;
            } else if (chosenAction.type.equals("RCone") || chosenAction.type.equals("RCube") ) {
                remaining = chosenAction.p2*1000-(timeInAutonomous-actionStartTime);
                System.out.println("Action time:"+chosenAction.p2*1000+". Remaining:"+remaining);
                if (remaining<=10)
                    actionComplete(chosenAction);
                else
                    break;
            } else {
                System.out.println("Ignoring "+chosenAction);
                actionComplete(chosenAction);
            }
        }
        if (chosenAction!=null)
            SmartDashboard.putString("Auton Cur Action", chosenAction.type+" "+remaining+"/"+chosenAction.p2);
        else
            SmartDashboard.putString("Auton Cur Action", "None");
        return chosenAction;
    }

    @Override
    public Pair calibrate(int calibrationCount, long timeInTest) {
        return null;
    }

    @Override
    public void calibrationInit(int calibrationCycle) {
    }    
}
