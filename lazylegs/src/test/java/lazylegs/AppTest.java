package lazylegs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Unit tests. Τρέχουν με: mvn test
 */
class AppTest {

    @Test
    void passengerRequestKeepsFloors() {
        PassengerRequest request = new PassengerRequest(0, 12);
        assertEquals(0, request.getSourceFloor());
        assertEquals(12, request.getDestinationFloor());
        assertTrue(request.isGoingUp());
    }

    @Test
    void passengerRequestRejectsSameFloor() {
        assertThrows(IllegalArgumentException.class, () -> new PassengerRequest(5, 5));
    }

    @Test
    void newElevatorIsIdle() {
        Elevator elevator = new Elevator('C', 0);
        assertEquals('C', elevator.getId());
        assertTrue(elevator.isIdle());

        elevator.setDestinationFloor(7);
        assertFalse(elevator.isIdle());
    }

    @Test
    void elevatorRejectsInvalidLetter() {
        assertThrows(IllegalArgumentException.class, () -> new Elevator('Z', 0));
    }
}