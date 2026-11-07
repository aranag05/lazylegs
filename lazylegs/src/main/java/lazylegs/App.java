package lazylegs;
import java.util.Scanner;
/**
 This is the main method of the app
 */
public class App 
{
    public static void main( String[] args )
    {
        //Interface from the user to enter source and destination floor//
        System.out.println("Enter your source floor");
        Scanner scanner = new Scanner(System.in);
        int passengerCurrentFloor = scanner.nextInt();
        System.out.println("Enter your destination floor");
        Scanner scanner2 = new Scanner(System.in);
        int passengerRequestFloor = scanner2.nextInt();
        
        //Method constructor of PassengerRequest checks if source floor and Request floor is the same//
       
        PassengerRequest passengerRequest = new PassengerRequest(passengerCurrentFloor, passengerRequestFloor);
        System.out.println(passengerRequest.toString());

    }
}
