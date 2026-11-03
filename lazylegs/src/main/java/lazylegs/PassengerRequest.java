package lazylegs;

/*
Passenger request: source floor and destinaton floor
*/
public class PassengerRequest {

    private final int sourceFloor;
    private final int destinationFloor;

    public PassengerRequest(int sourceFloor, int destinationFloor) {
        if (sourceFloor == destinationFloor) {
            throw new IllegalArgumentException(
                    "Source and destination floor must differ: " + sourceFloor);
        }
        this.sourceFloor = sourceFloor;
        this.destinationFloor = destinationFloor;
    }

    /** Gets source floor */
    public int getSourceFloor() {
        return sourceFloor;
    }

    /* Gets destination floor */
    public int getDestinationFloor() {
        return destinationFloor;
    }

    /** true if passenger wants to go to higher levels in the buildng  */
    public boolean isGoingUp() {
        return destinationFloor > sourceFloor;
    }

    @Override
    public String toString() {
        return "PassengerRequest{" + sourceFloor + " -> " + destinationFloor + "}";
    }
}