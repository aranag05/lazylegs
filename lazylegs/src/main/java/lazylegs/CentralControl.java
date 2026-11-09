package lazylegs;

/* Knows all the current elevator positions and passenger requests*/
public class CentralControl {
    boolean[][] elevatorPositions = new boolean[200][20]; /* 200 floors , 20 Elevators */ 

    //Insert of an enum for the direction of the elevator//
    enum  Direction {
    UP,
    DOWN }
    
    Direction up = Direction.UP;
    Direction down = Direction.DOWN;

}
