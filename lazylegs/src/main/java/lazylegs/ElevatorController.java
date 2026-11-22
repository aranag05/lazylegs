package lazylegs;

public class ElevatorController {
    private final Elevator elevator;

    public ElevatorController(Elevator elevator) {
        this.elevator = elevator;
    }
    //Assign a destination floor
    public void requestFloor(int floor) {
        if (floor < elevator.getMinFloor() || floor > elevator.getMaxFloor()) {
            throw new IllegalArgumentException("Invalid destination floor: " + floor + ". Must be between " + elevator.getMinFloor() + " and " + elevator.getMaxFloor());

        }
        elevator.setDestinationFloor(floor);
        System.out.println("Elevator " + elevator.getId() + " assigned to floor " + floor);
    }
    public ElevatorDirection getDirection() {
        if (elevator.getCurrentFloor() < elevator.getDestinationFloor()) {
            return ElevatorDirection.UP;
        } else if (elevator.getCurrentFloor() > elevator.getDestinationFloor()) {
            return ElevatorDirection.DOWN;
        } else {
            return ElevatorDirection.IDLE;
        }
    }
    //Move elevator one floor
    public void move() {
        if (elevator.isIdle()) {
            System.out.println("Elevator " + elevator.getId() + " is already at the destination floor " + elevator.getCurrentFloor());
            return;
        }
    }   
    ElevatorDirection direction = getDirection();
    elevator.moveOneFloor();
    System.out.println("Elevator " + elevator.getId() + " moved " + direction + " to floor " + elevator.getCurrentFloor());
    if (elevator.isIdle()) {
        System.out.println("Elevator " + elevator.getId() + " has reached the destination floor " + elevator.getCurrentFloor());
    }
    public void displayStatus() {
        System.out.println("Elevator " + elevator.getId() + " is currently at floor " + elevator.getCurrentFloor() + " with destination floor " + elevator.getDestinationFloor());
        System.out.println("To get there the elevator is moving " + getDirection() + " and is " + (elevator.isIdle() ? "idle" : "not idle"));
    }
        
}
