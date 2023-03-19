package frc.robot.interfaces;

public class Action {
   public enum ActionType {
    Move("in"), Turn("deg"), Cruise("sec"), 
    RCone("sec"), RCube("sec"),  GCone("sec"), GCube("sec"), 
    PCone("level"), PCube("level"), SArm("level"),  Hold("sec"), Stop("sec");
    String unit;
    ActionType(String magnitudeUnit) {
      this.unit = magnitudeUnit;
    }
  };
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

   public boolean equals(Action obj) {
     if (obj==null) return false;
     if (obj.type!=type) return false;
     if (obj.speed!=speed) return false;
     if (obj.magnitude!=magnitude) return false;
    return true;
   }

   @Override
   public String toString() {
       return type+":"+magnitude+ type.unit+"@"+speed;
   }
}
