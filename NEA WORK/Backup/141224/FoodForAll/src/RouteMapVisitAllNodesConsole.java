import DatabaseManagement.DatabaseService;
import FoodParcelManagement.FoodParcelRequest;
import RouteManagement.RouteMapVisitAllNodes;
import UserManagement.Address;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class RouteMapVisitAllNodesConsole
{
    public static void main(String[] args) {
        DatabaseService databaseService = new DatabaseService();
        RouteMapVisitAllNodes routeMapVisitAllNodes = new RouteMapVisitAllNodes();
        Scanner scanner = new Scanner(System.in);

        try {
            // Fetch the Food Bank address
            Address foodBankAddress = databaseService.GetFoodBankAddress();
            System.out.println("Food Bank Address: " + foodBankAddress.getFullAddress());

            // Fetch Volunteers from database
            System.out.println("\nFetching Volunteer Details...");
            Address volunteerA = databaseService.GetAddressById(7);
            Address volunteerB = databaseService.GetAddressById(8);

            System.out.println("\nVolunteer A Home: " + volunteerA.getFullAddress());
            System.out.println("Volunteer B Home: " + volunteerB.getFullAddress());

            // Ask user to enter the preferred delivery date
            System.out.println("\nEnter the Preferred Delivery Date (yyyy-MM-dd):");
            String preferredDeliveryDateInput = scanner.next();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date preferredDeliveryDate;

            try {
                preferredDeliveryDate = dateFormat.parse(preferredDeliveryDateInput);
            } catch (ParseException e) {
                System.err.println("Invalid date format. Please enter the date in yyyy-MM-dd format.");
                return;
            }

            // Fetch pending requests filtered by PreferredDeliveryDate
            List<FoodParcelRequest> filteredRequests = databaseService.GetPendingRequestsByPreferredDeliveryDate(preferredDeliveryDate);

            if (filteredRequests.isEmpty()) {
                System.out.println("No requests found for the specified delivery date.");
                return;
            }

            System.out.println("\nFiltered Requests:");
            for (FoodParcelRequest request : filteredRequests) {
                System.out.println(request.getRecipient().getAddress().getFullAddress());
            }

            // Display weights from the database
            System.out.println("\nWeights:");
            System.out.printf("%-50s %-50s %-10s\n", "From Address", "To Address", "Distance (Weight)");
            System.out.println("-----------------------------------------------------------------------------------------------------------------------");

            List<int[]> weightsFromDb = databaseService.GetWeights();
            for (int[] weight : weightsFromDb) {
                int fromId = weight[0];
                int toId = weight[1];
                int value = weight[2];

                Address fromAddress = databaseService.GetAddressById(fromId);
                Address toAddress = databaseService.GetAddressById(toId);

                if (fromAddress != null && toAddress != null) {
                    System.out.printf("%-50s %-50s %d\n",
                            fromAddress.getFullAddress(),
                            toAddress.getFullAddress(),
                            value
                    );
                }
            }

            // Select Start Node
            System.out.println("\nEnter the starting address ID (6 for Food Bank):");
            int startAddressId = scanner.nextInt();

            // Select End Node
            System.out.println("Enter the destination address ID (e.g., 7 for Volunteer A, or 8 for Volunteer B):");
            int endAddressId = scanner.nextInt();

            // Validate Addresses
            Address startNode = databaseService.GetAddressById(startAddressId);
            Address endNode = databaseService.GetAddressById(endAddressId);

            if (startNode == null || endNode == null) {
                System.err.println("Invalid Address IDs provided. Please try again.");
                return;
            }

            // Generate Route Map
            List<Address> route = routeMapVisitAllNodes.GenerateRouteMap(startNode, endNode, filteredRequests, weightsFromDb);
            System.out.println("\nOptimized Route Map:");
            for (Address addr : route) {
                System.out.println(addr.getFullAddress());
            }

            // Prompt volunteer to accept or reject the route
            System.out.println("\nDo you accept the route map? (yes/no):");
            String response = scanner.next();

            if (response.equalsIgnoreCase("yes")) {
                System.out.println("Enter Volunteer ID (e.g., 1 for Volunteer A):");
                int volunteerId = scanner.nextInt();

                // Add assignments and update statuses
                for (FoodParcelRequest request : filteredRequests) {
                    databaseService.addDeliveryAssignment(volunteerId, request.getParcelRequestId(), new Date());
                    databaseService.updateParcelRequestStatus(request.getParcelRequestId(), 2);
                }
                System.out.println("Route accepted. Assignments created, and request statuses updated.");
            } else {
                System.out.println("Route rejected. No changes were made.");
            }

        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
            databaseService.closeConnection();
        }
    }
}
