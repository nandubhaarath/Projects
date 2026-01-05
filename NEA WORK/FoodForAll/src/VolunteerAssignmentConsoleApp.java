import DatabaseManagement.DatabaseService;
import DatabaseManagement.IDatabaseService;
import FoodParcelManagement.VolunteerDeliveryAssignmentService;

import java.util.List;
import java.util.Scanner;

public class VolunteerAssignmentConsoleApp {
    public static void main(String[] args) {
        IDatabaseService IDatabaseService = new DatabaseService();
        VolunteerDeliveryAssignmentService assignmentService = new VolunteerDeliveryAssignmentService(IDatabaseService);
        Scanner scanner = new Scanner(System.in);

        try {
           // System.out.println("Enter Volunteer ID:");
            int volunteerId ;


            while (true) {
                System.out.println("Enter Volunteer ID:");
                volunteerId = scanner.nextInt();

                if (volunteerId == 1 || volunteerId == 2) {
                    break;
                } else {
                    System.out.println("Invalid Volunteer ID. Please enter valid Volunteer ID.");
                }
            }


            while (true) {
                System.out.println("\nVolunteer Assignment Management");
                System.out.println("1. View Assignments");
                System.out.println("2. Update Assignment Status");
                System.out.println("3. Exit");
                System.out.print("Choose an option: ");
                int choice = scanner.nextInt();

                if (choice == 1) {
                    // View assignments
                    List<Object[]> assignments = assignmentService.getAssignments(volunteerId);
                    if (assignments.isEmpty()) {
                        System.out.println("No assignments found.");
                    } else {
                        System.out.printf("%-15s %-20s %-30s %-50s %-15s %-15s\n",
                                "Assignment ID", "Parcel Request ID", "Recipient Name", "Delivery Address", "Status", "Delivery Date");
                        for (Object[] assignment : assignments) {
                            System.out.printf("%-15d %-20d %-30s %-50s %-15s %-15s\n",
                                    assignment[0], assignment[1], assignment[2], assignment[3], assignment[4], assignment[5]);
                        }
                    }
                } else if (choice == 2) {
                    // Update assignment status
                    System.out.println("Enter Assignment ID to update:");
                    int assignmentId = scanner.nextInt();
                    System.out.println("Enter new status (1: Pending, 3: Delivered):");
                    int statusId = scanner.nextInt();

                    // Update the status
                    assignmentService.updateAssignmentStatusByAssignmentId(assignmentId, statusId);
                    System.out.println("Status updated successfully.");
                } else if (choice == 3) {
                    System.out.println("Exiting...");
                    break;
                } else {
                    System.out.println("Invalid option. Please try again.");
                }
            }
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        } finally {
            IDatabaseService.closeConnection();
           // scanner.close();
        }
    }
}
