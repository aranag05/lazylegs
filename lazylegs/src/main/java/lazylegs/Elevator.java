package lazylegs;
import java.util.Scanner;

public class Elevator {

    /*
    The current floor ID of the elevator
    */
    public int currentFloor(Scanner scanner) {
        int currentFloor;
        System.out.println("Enter current floor (3-200):");
        
        do { 
            currentFloor = scanner.nextInt();
            if (currentFloor < 3 || currentFloor > 200) { 
                System.err.println("Choose again! The building capacity is 3 to 200 floors");
            }
        } while (currentFloor < 3 || currentFloor > 200); 

        return currentFloor;
    }

    /*
    The destination floor ID of the elevator
    */
    public int destinationFloor(Scanner scanner) {
        System.out.println("Where do you want to go? (3-200):");
        int destinationFloor; 

        do { 
            destinationFloor = scanner.nextInt();
            if (destinationFloor < 3 || destinationFloor > 200) {
                System.err.println("Choose again! The building capacity is 3 to 200 floors");
            }
        } while (destinationFloor < 3 || destinationFloor > 200); 

        return destinationFloor;
    }

    public static void main(String[] args) {
        
        String elevatorId = (args.length > 0) ? args[0] : "A";
        System.out.println("Elevator: " + elevatorId);

        Scanner scanner = new Scanner(System.in);
        Elevator elevator = new Elevator();

        int current = elevator.currentFloor(scanner);
        int destination = elevator.destinationFloor(scanner);

        System.out.println("Elevator " + elevatorId + " moves from floor " + current + " to floor " + destination);
    }
}