package lazylegs;

/**
An elevator, with a letter as an elevator ID (A to T)
Inside actual elevators are used sensors, during the development of the code we are only using varables 
*/

public class Elevator {

    private final char id;
    private int currentFloor;
    private int destinationFloor;
    private int minFloor;
    private int maxFloor;

    public Elevator(char id, int startFloor) {
        if (id < 'A' || id > 'T') {
            throw new IllegalArgumentException("Elevator id must be A-T, got: " + id);
        }
        this.id = id;
        this.currentFloor = startFloor;
        this.destinationFloor = startFloor;
    }

    public Elevator(char id, int startFloor, int minFloor, int maxFloor) {
        if (id < 'A' || id > 'T') {
            throw new IllegalArgumentException("Elevator id must be A-T, got: " + id);
        }
        if (minFloor >= maxFloor) {
            throw new IllegalArgumentException("Invalid floor range: minFloor must be less than maxFloor");
        }
        if (startFloor < minFloor || startFloor > maxFloor) {
            throw new IllegalArgumentException("Start floor must be within the range of " + minFloor + " and " + maxFloor);
        }
        this.id = id;
        this.currentFloor = startFloor;
        this.destinationFloor = startFloor;
        this.minFloor = minFloor;
        this.maxFloor = maxFloor;
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

    public int getMinFloor() {
        return minFloor;
    }

    public int getMaxFloor() {
        return maxFloor;
    }

    public void setDestinationFloor(int destinationFloor) {
        if (destinationFloor < minFloor || destinationFloor > maxFloor) {
            throw new IllegalArgumentException("Destination floor must be within the range of " + minFloor + " and " + maxFloor);
        }
        this.destinationFloor = destinationFloor;
    }

    /** true if elevator doesn't have a destinaton floor */
    public boolean isIdle() {
        return currentFloor == destinationFloor;
    }

    public void moveOneFloor() {
    if (isIdle()) {
            return; // Elevator is already at the destination floor
        }

        if (currentFloor < destinationFloor) {
            currentFloor++;
        } else {
            currentFloor--;
        }
    }
}
