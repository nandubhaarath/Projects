import DatabaseManagement.DatabaseService;
import FoodParcelManagement.FoodParcelRequest;
import RouteManagement.Dijkstra;
import RouteManagement.RouteMapVisitAllNodes;
import UserManagement.Address;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class RouteMapConsoleApp {
    public static void main(String[] args) {
        DatabaseService databaseService = new DatabaseService();
        RouteMapVisitAllNodes routeMapVisitAllNodes = new RouteMapVisitAllNodes();
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

            // Display filtered requests in a table format
            System.out.println("\nFood Parcel Requests for the Day:");
            System.out.printf("%-15s %-30s %-50s\n", "Parcel ID", "Recipient Name", "Delivery Address");
            System.out.println("---------------------------------------------------------------------------------------------------");
            for (FoodParcelRequest request : filteredRequests) {
                System.out.printf(
                        "%-15d %-30s %-50s\n",
                        request.getParcelRequestId(),
                        request.getRecipient().getFullName(),
                        request.getRecipient().getAddress().getFullAddress()
                );
            }

            // Select Start Node
            System.out.println("\nEnter the starting address ID (6 for Food Bank):");
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

            // Ask the volunteer for their preference
            System.out.println("\nWould you like to:");
            System.out.println("1. Visit all nodes");
            System.out.println("2. Visit as many nodes as possible via the shortest path");
            int choice = scanner.nextInt();

            List<Address> route = new ArrayList<>();

            if (choice == 1) {
                // Visit all nodes using the finalized logic
                System.out.println("\nGenerating route to visit all nodes...");
                route = routeMapVisitAllNodes.GenerateRouteMap(startAddress, endAddress, filteredRequests, databaseService.GetWeights());

                System.out.println("\nOptimized Route Map:");
                for (Address addr : route) {
                    System.out.println(addr.getFullAddress());
                }

                // Filter requests based on the route
                List<FoodParcelRequest> requestsInRoute = new ArrayList<>();
                for (FoodParcelRequest request : filteredRequests) {
                    if (route.contains(request.getRecipient().getAddress())) {
                        requestsInRoute.add(request);
                    }
                }

                // Display filtered weights
                System.out.println("\nFiltered Weights:");
                System.out.printf("%-50s %-50s %-20s\n", "From Address", "To Address", "Weight (Distance)");
                System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------");
                for (int[] weight : databaseService.GetWeights()) {
                    Address fromAddress = databaseService.GetAddressById(weight[0]);
                    Address toAddress = databaseService.GetAddressById(weight[1]);
                    if (route.contains(fromAddress) && route.contains(toAddress)) {
                        System.out.printf("%-50s %-50s %-20d\n",
                                fromAddress.getFullAddress(),
                                toAddress.getFullAddress(),
                                weight[2]);
                    }
                }

                // Prompt for route acceptance
                System.out.println("\nDo you accept the route map? (yes/no):");
                String response = scanner.next();

                if (response.equalsIgnoreCase("yes")) {
                    System.out.println("Enter Volunteer ID (e.g., 1 for Volunteer A):");
                    int volunteerId = scanner.nextInt();

                    for (FoodParcelRequest request : requestsInRoute) {
                        databaseService.addDeliveryAssignment(volunteerId, request.getParcelRequestId(), preferredDeliveryDate);
                        databaseService.updateParcelRequestStatus(request.getParcelRequestId(), 2);
                    }
                    System.out.println("Route accepted. Assignments created, and request statuses updated.");
                } else {
                    System.out.println("Route rejected. No changes were made.");
                }
            } else if (choice == 2) {
                // Shortest path using Dijkstra
                System.out.println("\nCalculating the shortest path...");
                List<Address> allAddresses = databaseService.GetAllAddresses();
                ArrayList<String> nodeNames = new ArrayList<>();
                for (Address address : allAddresses) {
                    nodeNames.add(address.getFullAddress());
                }

                // Filter weights for relevant addresses
                List<int[]> filteredWeights = new ArrayList<>();
                for (int[] weight : databaseService.GetWeights()) {
                    int fromId = weight[0];
                    int toId = weight[1];

                    Address fromAddress = databaseService.GetAddressById(fromId);
                    Address toAddress = databaseService.GetAddressById(toId);

                    boolean fromRelevant = fromAddress.equals(startAddress) ||
                            fromAddress.equals(endAddress) ||
                            filteredRequests.stream()
                                    .anyMatch(request -> request.getRecipient().getAddress().equals(fromAddress));
                    boolean toRelevant = toAddress.equals(startAddress) ||
                            toAddress.equals(endAddress) ||
                            filteredRequests.stream()
                                    .anyMatch(request -> request.getRecipient().getAddress().equals(toAddress));

                    if (fromRelevant && toRelevant) {
                        filteredWeights.add(weight);
                    }
                }

                System.out.println("\nFiltered Weights:");
                System.out.printf("%-50s %-50s %-20s\n", "From Address", "To Address", "Weight (Distance)");
                System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------");
                for (int[] weight : filteredWeights) {
                    Address fromAddress = databaseService.GetAddressById(weight[0]);
                    Address toAddress = databaseService.GetAddressById(weight[1]);
                    System.out.printf("%-50s %-50s %-20d\n",
                            fromAddress.getFullAddress(),
                            toAddress.getFullAddress(),
                            weight[2]);
                }

                Dijkstra dijkstra = new Dijkstra(nodeNames);

                // Load filtered weights into Dijkstra
                for (int[] weight : filteredWeights) {
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
                System.out.println("\nShortest Path:");
                System.out.println(shortestPath);

                // Collect the addresses from the shortest path
                String[] pathNodes = shortestPath.split(" --> ");
                for (String nodeName : pathNodes) {
                    for (Address address : allAddresses) {
                        if (address.getFullAddress().equals(nodeName)) {
                            route.add(address);
                        }
                    }
                }

                // Map the addresses in the route to FoodParcelRequest objects
                List<FoodParcelRequest> requestsInRoute = new ArrayList<>();
                for (FoodParcelRequest request : filteredRequests) {
                    if (route.contains(request.getRecipient().getAddress())) {
                        requestsInRoute.add(request);
                    }
                }

                System.out.println("\nOptimized Route:");
                for (Address addr : route) {
                    System.out.println(addr.getFullAddress());
                }

                // Create records and update statuses
                System.out.println("\nDo you accept the route map? (yes/no):");
                String response = scanner.next();

                if (response.equalsIgnoreCase("yes")) {
                    System.out.println("Enter Volunteer ID (e.g., 1 for Volunteer A):");
                    int volunteerId = scanner.nextInt();

                    for (FoodParcelRequest request : requestsInRoute) {
                        databaseService.addDeliveryAssignment(volunteerId, request.getParcelRequestId(), preferredDeliveryDate);
                        databaseService.updateParcelRequestStatus(request.getParcelRequestId(), 2);
                    }
                    System.out.println("Route accepted. Assignments created, and request statuses updated.");
                } else {
                    System.out.println("Route rejected. No changes were made.");
                }
            } else {
                System.out.println("Invalid choice. Exiting.");
            }

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