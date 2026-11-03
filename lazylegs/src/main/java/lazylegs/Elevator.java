package lazylegs;

/**
An elevator, with a letter as an elevator ID (A to T)
Inside actual elevators are used sensors, during the development of the code we are only using varables 
*/

public class Elevator {

    private final char id;
    private int currentFloor;
    private int destinationFloor;

    public Elevator(char id, int startFloor) {
        if (id < 'A' || id > 'T') {
            throw new IllegalArgumentException("Elevator id must be A-T, got: " + id);
        }
        this.id = id;
        this.currentFloor = startFloor;
        this.destinationFloor = startFloor;
    }

    /** Elevator ID (letter) */
    public char getId() {
        return id;
    }

    /** Current elevator position */
    public int getCurrentFloor() {
        return currentFloor;
    }

    /** Destination floor assigned to the elevator */
    public int getDestinationFloor() {
        return destinationFloor;
    }

    public void setDestinationFloor(int destinationFloor) {
        this.destinationFloor = destinationFloor;
    }

    /** true if elevator doesn't have a destinaton floor */
    public boolean isIdle() {
        return currentFloor == destinationFloor;
    }
}