package frc.robot.controller;

import java.util.Date;

import frc.robot.subsystem.AccelerometerSubsystem;

public class ClimbController {
    enum States {APPROACH_STATION, CLIMB_STATION, CENTER_STATION, ON_STATION};
    static private ClimbController self;
    private States state = States.APPROACH_STATION;
    private double maxSpeedClimbTimeInSec = 0.2, centerTimeInSec = 0.2, stableTimeInSec=0.2;
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
                return (elapsedTime>maxSpeedClimbTimeInSec)?  maxSpeed/2.0 : maxSpeed;

            case CENTER_STATION:  // Move at half speed on station to position robo at center
                if (Math.abs(tilt) <= levelTilt / 2) {
                    if (elapsedTime > centerTimeInSec) {
                        System.out.println("Centering done.. Holding..tilt:"+tilt);
                        state = States.ON_STATION;
                        startTime = new Date(); // starting on station
                    }
                }
                if (tilt >= levelTilt) {
                    return 0.1;
                } else if (tilt <= -levelTilt) {
                    return -0.1;
                } else
                    return 0.0;
            case ON_STATION:            
                if (elapsedTime > stableTimeInSec) {
                    return null;
                }
                if (tilt-levelTilt >= 0.1) {
                    return 0.1;
                } else if (tilt <= -levelTilt) {
                    return -0.1;
                }
                return 0.0;
        }
        return null;
    }
}
