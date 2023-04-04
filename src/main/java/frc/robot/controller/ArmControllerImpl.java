package frc.robot.controller;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.interfaces.ArmController;
import frc.robot.subsystem.ElevatorSubsystem;
import frc.robot.subsystem.GyroSubsystem;
import frc.robot.subsystem.PulleyLiftSubsystem;

public class ArmControllerImpl implements ArmController {
    private PulleyLiftSubsystem liftSubsystem = new PulleyLiftSubsystem();
    private ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem();
    private GyroSubsystem gyro = GyroSubsystem.getInstance();

    private String currentTarget = null;

    private int liftMinAngle = 5; // Cant go lower than 5deg
    private int liftMaxAngle = 82; // Cant go higher than 82deg
    private int maxArmHeight = 78; // 6'6 ft
    private int maxArmWidth = 48; // 4' ft
    private int maxExtensionInches = 0;

    public ArmControllerImpl() {
        SmartDashboard.putNumber(ArmController.ARM_MAX_HEIGHT,maxArmHeight); 
        SmartDashboard.putNumber(ArmController.ARM_MAX_WIDTH, maxArmWidth); 

        SmartDashboard.putNumber(LIFT_POSITION, 0);
        SmartDashboard.putNumber(LIFT_CONE_KEY, 60);
        SmartDashboard.putNumber(LIFT_CUBE_KEY, 65);
        SmartDashboard.putNumber(LIFT_STAB_KEY, 50);

        SmartDashboard.putNumber(ELEV_POSITION, 0);
        SmartDashboard.putNumber(ELEV_CONE_KEY, 50);
        SmartDashboard.putNumber(ELEV_CUBE_KEY, 55);
        SmartDashboard.putNumber(ELEV_STAB_KEY, 5);
    }

    public void init() {
        liftSubsystem.init();
        elevatorSubsystem.init();
        currentTarget = null;
        maxArmWidth = (int) SmartDashboard.getNumber(ARM_MAX_WIDTH, maxArmWidth);
        maxArmHeight = (int) SmartDashboard.getNumber(ARM_MAX_HEIGHT, maxArmHeight);
        liftMinAngle = (int) SmartDashboard.getNumber(LIFT_MIN_ANGLE, liftMinAngle);
        liftMaxAngle = (int) SmartDashboard.getNumber(LIFT_MAX_ANGLE, liftMaxAngle);
        updateMaxExtension(getArmAngle());
    }

    @Override
    public void periodic(long tickCount) {
        //if ((tickCount & 0x1111) == 0x1111) 
        {
            SmartDashboard.putNumber(LIFT_POSITION, liftSubsystem.getPosition());
            SmartDashboard.putNumber(ELEV_POSITION, elevatorSubsystem.getPosition());
        }
    }

    private Integer getArmAngle() {
        return gyro!=null? (int)gyro.getPitch(): null;
    }

    @Override
    public void raiseArm(double speed) {
        Integer currArmAngle = getArmAngle();
        if (currArmAngle==null || currArmAngle>liftMinAngle) {
            liftSubsystem.raiseArm(speed);
            updateMaxExtension(currArmAngle);
        } else
            System.out.println("Cant raise arm beyond "+currArmAngle);
    }

    @Override
    public void lowerArm(double speed) {
        Integer currArmAngle = getArmAngle();
        if (currArmAngle==null || currArmAngle<liftMaxAngle) {
            liftSubsystem.lowerArm(speed);
            updateMaxExtension(currArmAngle);
        } else
            System.out.println("Cant lower arm beyond "+currArmAngle);
    }

    @Override
    public String getCurrentTarget() {
        return currentTarget;
    }

    @Override
    public void setCurrentTarget(String currentTarget) {
        this.currentTarget = currentTarget;
    }

    /**
     * @return true if moved to target, false if move arm has not yet reached target
     */
    @Override
    public boolean moveArmToTarget(String itemType) {
        double lTarget;
        double eTarget;
        boolean status = false;
        switch (itemType) {
            case "Cone": 
                lTarget= SmartDashboard.getNumber(LIFT_CONE_KEY, 60);
                eTarget = SmartDashboard.getNumber(ELEV_CONE_KEY, 50);
                break;
            case "Cube": 
                lTarget= SmartDashboard.getNumber(LIFT_CUBE_KEY, 65);
                eTarget = SmartDashboard.getNumber(ELEV_CUBE_KEY, 55);
                break;
            case "Stable": 
                lTarget= SmartDashboard.getNumber(LIFT_STAB_KEY, 88);
                eTarget = SmartDashboard.getNumber(ELEV_STAB_KEY, 5);
                break;
            default:
                return status;
        }
        currentTarget = itemType;
        boolean status1 = liftSubsystem.moveToTarget(lTarget);
        boolean status2 = elevatorSubsystem.moveToTarget(eTarget);
        status = status1 && status2;
        if (status) {
            System.out.println("Completed move to target:"+itemType);
            currentTarget = null;
        }
        return status;
    }

    @Override
    public void extendArm(double speed) {
        elevatorSubsystem.extendArm(speed);
    }

    @Override
    public void retractArm(double speed) {
        elevatorSubsystem.retractArm(speed);
    }

    public void stopElevator() {
        elevatorSubsystem.stop();
        // Keep calling so that elevator can adjust..Called only when user is not adjusting evelvator
        if (maxExtensionInches>0) 
            elevatorSubsystem.adjustArmExtension();
    }

    public void stopLift() {
        liftSubsystem.stop();
    }

    public void stop() {
        setCurrentTarget(null);
        if (!(liftSubsystem.isStopped() && elevatorSubsystem.isStopped()))
            System.out.println("Stopped arm.....");
        liftSubsystem.stop(); // Call this even if already stopped to maintain position
        elevatorSubsystem.stop();
    }

    @Override
    public void resetEncoderPos() {
        liftSubsystem.setPosition(0);
        elevatorSubsystem.setPosition(0);        
    }

    private void updateMaxExtension(Integer armAngle) {
        if (armAngle==null) return;
        int widthLimit = armAngle>=30 && maxArmWidth>0 ?(int)(maxArmWidth/Math.cos(Math.toRadians(90-armAngle))):-1;
        int heightLimit = armAngle<=60 && maxArmHeight>0 ?(int)(maxArmHeight/Math.cos(Math.toRadians(armAngle))):-1;
        if (heightLimit>0 && widthLimit>0)
            maxExtensionInches = widthLimit<=heightLimit ? widthLimit : heightLimit;
        else 
            maxExtensionInches= widthLimit>0?widthLimit:heightLimit;
        System.out.println("MaxExtn:"+maxExtensionInches+"(WidthLimit:"+widthLimit+", HeightLimit:"+heightLimit+") for ArmAngle:"+armAngle);
        elevatorSubsystem.setMaxExtension(maxExtensionInches);
    }

    @Override
    public void simulationPeriodic(long tickCount) {
        double elevChange = elevatorSubsystem.getCurrentSpeed();
        double liftChange = liftSubsystem.getCurrentSpeed();
        boolean angleChanged = false;
        if (liftChange!=0) {
            gyro.simulationPeriodic(null, -liftChange);
            liftSubsystem.setPosition(liftSubsystem.getPosition()+liftChange);
            angleChanged = true;
        }
        /*double lowLimit = SmartDashboard.getNumber(LIFT_LOW_LIMIT, 0);
        if (liftSubsystem.getPosition()>lowLimit) {
            gyro.simulationPeriodic(null,-0.001);
            liftSubsystem.setPosition(liftSubsystem.getPosition()-0.001); // simulate the pull of gravity
            angleChanged = true;
        }*/
        if (angleChanged) {
            double liftRange = SmartDashboard.getNumber(ArmController.LIFT_RANGE, 0);
            if (liftRange!=0)
                liftSubsystem.setPositionByPitch(gyro.getPitch());
        }

        if (elevChange!=0)
            elevatorSubsystem.setPosition(elevatorSubsystem.getPosition()+elevChange);
    }
}
