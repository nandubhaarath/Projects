import DatabaseManagement.DatabaseService;
import FoodParcelManagement.FoodParcelRequest;
import RouteManagement.Dijkstra;
import UserManagement.Address;
import UserManagement.Recipient;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class RouteMapDijkstraConsoleApp
{
    public static void main(String[] args) {
        DatabaseService databaseService = new DatabaseService();
        Scanner scanner = new Scanner(System.in);

        try {
            // Fetch the Food Bank address
            Address foodBankAddress = databaseService.GetFoodBankAddress();
            System.out.println("Food Bank Address: " + foodBankAddress.getFullAddress());

            // Fetch Volunteer Addresses
            Address volunteerA = databaseService.GetAddressById(7);
            Address volunteerB = databaseService.GetAddressById(8);

            System.out.println("\nVolunteer A Home: " + volunteerA.getFullAddress());
            System.out.println("Volunteer B Home: " + volunteerB.getFullAddress());

            // Fetch all pending requests and display their addresses
            System.out.println("\nPending Requests:");
            List<FoodParcelRequest> getPendingRequests = databaseService.GetPendingRequests();

            for (int i = 0; i < getPendingRequests.size(); i++)
            {
                FoodParcelRequest request = getPendingRequests.get(i);

                Recipient recipient = request.getRecipient();
                Address address = recipient.getAddress();
                String fullAddress = address.getFullAddress();

                System.out.println(fullAddress);
            }

            // Fetch and display weights in the desired format
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
            System.out.println("\nEnter the starting address ID (6 for Food Bank)");
            int startAddressId = scanner.nextInt();

            // Select End Node
            System.out.println("Enter the destination address ID (e.g., 7 for Volunteer A, or 8 for Volunteer B):");
            int endAddressId = scanner.nextInt();

            // Validate Addresses
            Address startAddress = databaseService.GetAddressById(startAddressId);
            Address endAddress = databaseService.GetAddressById(endAddressId);

            if (startAddress == null || endAddress == null) {
                System.err.println("Invalid Address IDs provided. Please try again.");
                return;
            }

            System.out.println("\nCalculating the shortest path from:");
            System.out.println("Start: " + startAddress.getFullAddress());
            System.out.println("Destination: " + endAddress.getFullAddress());

            // Fetch all addresses and convert to node names
            List<Address> allAddresses = databaseService.GetAllAddresses();
            ArrayList<String> nodeNames = new ArrayList<>();
            for (Address address : allAddresses) {
                nodeNames.add(address.getFullAddress());
            }

            // Initialize the Dijkstra class
            Dijkstra dijkstra = new Dijkstra(nodeNames);

            // Load weights into Dijkstra
            for (int[] weight : weightsFromDb) {
                int fromId = weight[0];
                int toId = weight[1];
                int value = weight[2];

                Address fromAddress = databaseService.GetAddressById(fromId);
                Address toAddress = databaseService.GetAddressById(toId);

                if (fromAddress != null && toAddress != null) {
                    dijkstra.SetWeightBetweenNodes(fromAddress.getFullAddress(), toAddress.getFullAddress(), value);
                }
            }

            // Calculate the shortest path
            String shortestPath = dijkstra.GetShortestPath(startAddress.getFullAddress(), endAddress.getFullAddress());
            int pathLength = dijkstra.GetShortestPathLength(startAddress.getFullAddress(), endAddress.getFullAddress());

            // Print the results
            System.out.println("\nShortest Path:");
            System.out.println(shortestPath);
            System.out.println("Total Path Length: " + pathLength);

        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Close the database connection
            databaseService.closeConnection();
            scanner.close();
        }
    }
}
