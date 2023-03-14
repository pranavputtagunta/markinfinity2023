package frc.robot.controller;

import java.util.ArrayList;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.interfaces.Action;
import frc.robot.interfaces.AutonomousController;
import frc.robot.interfaces.DriveController;
import frc.robot.interfaces.Action.ActionType;

public class AutonWithEncoder implements AutonomousController {
    ArrayList<Action> actionMap = new ArrayList<Action>(25);
    DriveController driveController;
    Action prevAction = null;
    int curOp = 0;
    Double startLtPos, startRtPos;
    double autonMaxSpeed = 0.75;
    double encoderToDistanceConversion = 2.355;
    double encoderToAngleConversion = 10;
    long actionStartTime = 0;
    double startAngle;

    public AutonWithEncoder(DriveController driveController) {
        this.driveController = driveController;
        SmartDashboard.putNumber("Auton MAX Speed", autonMaxSpeed);
        SmartDashboard.putNumber("Encoder Dist Factor", encoderToDistanceConversion);
        SmartDashboard.putNumber("Encoder Angle Factor", encoderToAngleConversion);
        SmartDashboard.putString("Auton Cur Action", "");
    }

    @Override
    public void autonomousInit(String[] autoOp) {
        curOp = 0;
        prevAction = null;
        actionMap.clear();
        System.out.println("Received " + autoOp.length + " autoOp items");
        startAngle = driveController.getAngle();
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
            ActionType actionType;
            try {
                Integer distangl = 0;
                if (spaceIndx>=0) {
                    distangl = Integer.parseInt(autoOpr.substring(spaceIndx+1));
                    actionType = ActionType.valueOf(autoOpr.substring(0, spaceIndx));
                    if (distangl>=0)
                        actionType = ActionType.valueOf(autoOpr.substring(0, spaceIndx));
                } else
                    actionType = ActionType.valueOf(autoOpr);
                Action p = new Action(autonMaxSpeed, distangl, actionType);
                System.out.println("Adding:" + p);
                actionMap.add(p);
            } catch (Exception e) {
                System.out.println("Ignoring"+e.getMessage());
            }
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
        double speed = remaining>0? autonMaxSpeed: -autonMaxSpeed; //positive speed turns clockwise
        double factor = 1.0;
        double absVal = Math.abs(remaining);
        if (absVal<10) factor = 0.5;
        if (absVal<5) factor = 0.25;
        return factor*speed;
    }

    @Override
    public void actionComplete(Action action) {
        System.out.println("Completed action:"+action);
        startLtPos = startRtPos = null;
        actionStartTime = 0;
        prevAction = action;
        curOp++;
    }

    private double getAngleTurned() {
        double angleTurned = driveController.getAngle()-startAngle;
        return angleTurned;
    }

    @Override
    public Action getNextAction(long timeInAutonomous) {
        Action chosenAction = null;
        double remaining = 0;
        while(curOp<actionMap.size()) {
            if (startLtPos==null) startLtPos =  driveController.getLeftEncoderPosition();
            if (startRtPos==null) startRtPos =  driveController.getRightEncoderPosition();
            chosenAction = actionMap.get(curOp);
            if (prevAction==null || !prevAction.equals(chosenAction)) {
                System.out.println("Current action:"+chosenAction);
                prevAction = chosenAction;
            }
            if (actionStartTime==0) actionStartTime = timeInAutonomous;
            double currentLtPos = driveController.getLeftEncoderPosition();
            double currentRtPos = driveController.getRightEncoderPosition();
            if (chosenAction.type == ActionType.Move) {
                double distMoved = convertToDistance(currentLtPos-startLtPos, currentRtPos-startRtPos);
                remaining = chosenAction.magnitude-distMoved;
                System.out.println("Distance Moved:"+distMoved+". Remaining:"+remaining);
                if (Math.abs(remaining)<=1) {
                    actionComplete(chosenAction);
                } else {
                    chosenAction.speed = distanceRemainingToSpeed(remaining);
                    break;
                }
            } else if (chosenAction.type == ActionType.Turn) {
                //double angleTurned = convertToAngle(currentLtPos-startLtPos, currentRtPos-startRtPos);
                double angleTurned = getAngleTurned();
                remaining = chosenAction.magnitude-angleTurned;
                System.out.println("Angle Turned:"+angleTurned+". Remaining:"+remaining);
                if (Math.abs(remaining)<=1) {
                    actionComplete(chosenAction);
                } else {
                    chosenAction.speed = angleRemainingToSpeed(remaining);
                    break;
                }
            } else if (chosenAction.type == ActionType.PCone || chosenAction.type== ActionType.PCube || chosenAction.type== ActionType.SArm ) {
                break;
            } else if (chosenAction.type == ActionType.RCone || chosenAction.type== ActionType.RCube ) {
                remaining = chosenAction.magnitude*1000-(timeInAutonomous-actionStartTime);
                System.out.println(chosenAction.type+".. time:"+chosenAction.magnitude*1000+". Remaining:"+remaining);
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
            SmartDashboard.putString("Auton Cur Action", chosenAction.type+" "+remaining+"/"+chosenAction.magnitude);
        else
            SmartDashboard.putString("Auton Cur Action", "None");
        return chosenAction;
    }

    @Override
    public Action calibrate(int calibrationCount, long timeInTest) {
        return null;
    }

    @Override
    public void calibrationInit(int calibrationCycle) {
    }    
}
