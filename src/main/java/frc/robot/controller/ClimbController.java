package frc.robot.controller;

import java.util.Date;

import frc.robot.subsystem.AccelerometerSubsystem;

public class ClimbController {
    public enum States {APPROACH_STATION, CLIMB_STATION, CENTER_STATION, HOLD_POSITION};
    static private ClimbController self;
    private States state = States.APPROACH_STATION;
    private int maxSpeedClimbTime = 2000, centerTime = 2000; // In milisec
    private double onChargeStationTilt = 13.0;
    private Date startTime;
    private double levelTilt = 6.0;
    AccelerometerSubsystem accelerometer = AccelerometerSubsystem.getInstance();

    private ClimbController() {
        self = this;
    }

    public static ClimbController getInstance() {
        return self==null?new ClimbController():self;
    }

    public States getState() {
        return state;
    }

    public Double getClimbSpeed(double maxSpeed) {
        double tilt = accelerometer.getTilt();
        Date currentTime = new Date();
        long elapsedTime = startTime!=null?currentTime.getTime() - startTime.getTime(): 0;
        switch (state) {
            case APPROACH_STATION: // Approch at maxSpeed and climb at that speed for climbTimeInSec.. Go to next state when tilt detected
                if (tilt > onChargeStationTilt) {
                    state = States.CLIMB_STATION;
                    System.out.println("Stating climb..tilt:"+tilt);
                    startTime = currentTime; // starting climb
                }
                return maxSpeed;
            
            case CLIMB_STATION: // Climb at maxSpeed and then at half speed.. Go to next state once level is detected
                if (tilt < levelTilt) {
                    state = States.CENTER_STATION;
                    System.out.println("Stating centering on station..tilt:"+tilt);
                    startTime = new Date(); // starting centering
                }
                return (elapsedTime>maxSpeedClimbTime)?  maxSpeed/2.0 : maxSpeed;

            case CENTER_STATION:  // Move at half speed on station to position robo at center
                if (Math.abs(tilt) <= levelTilt / 2) {
                    if (elapsedTime > centerTime) {
                        System.out.println("Centering done.. Holding..tilt:"+tilt);
                        state = States.HOLD_POSITION;
                        startTime = new Date(); // starting on station
                    }
                }
                if (tilt >= levelTilt) {
                    System.out.println("Adjusting..tilt:"+tilt);
                    return 0.1;
                } else if (tilt <= -levelTilt) {
                    System.out.println("Adjusting..tilt:"+tilt);
                    return -0.1;
                } else
                    return 0.005;
            case HOLD_POSITION:
                if (tilt-levelTilt >= 0.1) {
                    System.out.println("Adjusting..tilt:"+tilt);
                    return 0.1;
                } else if (tilt <= -levelTilt) {
                    System.out.println("Adjusting..tilt:"+tilt);
                    return -0.1;
                }
                return 0.0;
        }
        return null;
    }

    public void simulationPeriodic(long tickCount) {
        double tilt;
        if (tickCount<100) tilt=0;
        else if (tickCount<150) tilt=20;
        else tilt=0;
        accelerometer.setTilt(tilt);
    }
}