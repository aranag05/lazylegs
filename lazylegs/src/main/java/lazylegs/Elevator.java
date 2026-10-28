package lazylegs;
import java.util.Scanner;
public class Elevator {

    /*
    The current floor ID of the elevator
    Normaly works with sensors
    */
    public int currentFloor() {

            return 0;   
    }

    /*
    The destination floor ID of the elevator
    Normaly works with sensors 
    */
    public int destinationFloor() {
        System.out.println("Where do you want to go?"); /* Question where to go */
        Scanner scanner = new Scanner(System.in); /* Insert object scanner to input Destination Floor from user */

        int destinationFloor ; 
        do { 
            destinationFloor = scanner.nextInt(); /* Call method next.Int  to assign destinationFloor a value */
            if ( destinationFloor < 3 || destinationFloor > 200 ) { // Checking for the correct value of destinationFloor//
            System.err.println("Choose again! The building capacity is 3 to 200 floors");
            destinationFloor = scanner.nextInt();
            }
        } while ( destinationFloor < 3 || destinationFloor > 200);
        scanner.close();
        return destinationFloor;
    }
}
