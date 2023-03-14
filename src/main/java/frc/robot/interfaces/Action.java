package frc.robot.interfaces;

public class Action {
   public enum ActionType {Move, Turn, RCone, RCube, PCone, PCube, GCone, GCube, SArm, Stop};
   public Double speed; // Max Speed with with action should be done
   public Integer magnitude; // Duration or distance or angle associated with action
   public ActionType type; // Type of action to perform

   public Action(Double speed, Integer magnitude) {
    this.speed = speed;
    this.magnitude = magnitude;
   }

   public Action(Double speed, Integer magnitude, ActionType type) {
    this.speed = speed;
    this.magnitude = magnitude;
    this.type = type;
   }

   @Override
   public String toString() {
       return type+":"+speed+".."+magnitude;
   }
}
