package lazylegs;

/**
 * Ένας ανελκυστήρας, με αναγνωριστικό γράμμα (A έως T).
 * Στην πράξη ο τρέχων όροφος έρχεται από αισθητήρες· εδώ τον κρατάμε σε μεταβλητή.
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

    /** Το αναγνωριστικό γράμμα του ανελκυστήρα. */
    public char getId() {
        return id;
    }

    /** Ο όροφος στον οποίο βρίσκεται τώρα ο ανελκυστήρας. */
    public int getCurrentFloor() {
        return currentFloor;
    }

    /** Ο όροφος προς τον οποίο κινείται ο ανελκυστήρας. */
    public int getDestinationFloor() {
        return destinationFloor;
    }

    public void setDestinationFloor(int destinationFloor) {
        this.destinationFloor = destinationFloor;
    }

    /** true αν ο ανελκυστήρας δεν έχει κάπου να πάει. */
    public boolean isIdle() {
        return currentFloor == destinationFloor;
    }
}