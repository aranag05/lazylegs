package lazylegs;

public class ElevatorTest {
    Elevator elevator = new Elevator('A', 2, 0, 10);
    System.out.println("Elevator ID: " + elevator.getId());
    System.out.println("Current Floor: " + elevator.getCurrentFloor()); 
    System.out.println("Min Floor: " + elevator.getMinFloor());
    System.out.println("Max Floor: " + elevator.getMaxFloor());
    system.out.println("Destination Floor: " + elevator.getDestinationFloor());
    elevator.setDestinationFloor(5);
    System.out.println("is idle: " + elevator.isIdle());
    System.out.println("Destination Floor: " + elevator.getDestinationFloor());
    while (!elevator.isIdle()) {
       elevator.moveOneFloor();
       System.out.println("Current Floor: " + elevator.getCurrentFloor());
    }
}
