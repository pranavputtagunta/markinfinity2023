package frc.robot.main;

public class Pair {
   public Double p1;
   public Integer p2; 
   String type;

   public Pair(Double p1, Integer p2) {
    this.p1 = p1;
    this.p2 = p2;
   }

   public Pair(Double p1, Integer p2, String type) {
    this.p1 = p1;
    this.p2 = p2;
    this.type = type;
   }

   @Override
   public String toString() {
       return type+":"+p1+"--"+p2;
   }
}
