import java.util.Scanner;

public class FoodForAllConsoleApp {
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean exit = false;

        while (!exit) {
            // Ensure the scanner is not closed
            if (scanner == null) {
                scanner = new Scanner(System.in);
            }

            System.out.println("\n=========================================================");
            System.out.println("     Welcome to the \"Food For All\" System");
            System.out.println("=========================================================");
            System.out.println("Please select an option:");
            System.out.println("1. Food Parcel Request form");
            System.out.println("2. Volunteer Route Generator form");
            System.out.println("3. Volunteer assignments form");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    FoodParcelRequestConsoleApp.main(new String[]{});
                    break;
                case 2:
                    RouteMapConsoleApp.main(new String[]{});
                    break;
                case 3:
                    VolunteerAssignmentConsoleApp.main(new String[]{});
                    break;
                case 4:
                    System.out.println("Thank you for using the Food For All System. Goodbye!");
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 4.");
            }
        }

        scanner.close();
    }
}
