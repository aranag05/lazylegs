package lazylegs;

/**
 * Αίτημα μετακίνησης ενός επιβάτη: από ποιον όροφο ξεκινά και πού θέλει να πάει.
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

    /** Ο όροφος από τον οποίο έγινε το αίτημα. */
    public int getSourceFloor() {
        return sourceFloor;
    }

    /** Ο όροφος στον οποίο θέλει να πάει ο επιβάτης. */
    public int getDestinationFloor() {
        return destinationFloor;
    }

    /** true αν ο επιβάτης θέλει να ανέβει. */
    public boolean isGoingUp() {
        return destinationFloor > sourceFloor;
    }

    @Override
    public String toString() {
        return "PassengerRequest{" + sourceFloor + " -> " + destinationFloor + "}";
    }
}