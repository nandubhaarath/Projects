import FoodParcelManagement.FoodItem;
import FoodParcelManagement.FoodParcelRequest;
import FoodParcelManagement.FoodParcelRequestService;
import UserManagement.Recipient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class FoodParcelRequestConsoleApp {
    public static void main(String[] args) {
        FoodParcelRequestService foodParcelRequestService = new FoodParcelRequestService();
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.println("Welcome to the Food Parcel Request System!");

            while (true) {
                System.out.println("\nPlease enter your username to login:");
                String username = scanner.next();

                System.out.println("Please enter your password:");
                String password = scanner.next();

                // Authenticate the recipient
                Recipient recipient = foodParcelRequestService.loginRecipient(username, password);

                if (recipient == null) {
                    System.out.println("Invalid username or password. Please try again.");
                    continue;
                }

                System.out.println("\nLogin Successful!");
                System.out.println("Recipient Details:");
                System.out.println("Name: " + recipient.getFullName());
                System.out.println("Address: " + recipient.getAddress().getFullAddress());

                boolean loggedIn = true;
                while (loggedIn) {
                    System.out.println("\nFood Parcel Request Management :");
                    System.out.println("1. View Parcels Requested");
                    System.out.println("2. Request a New Parcel");
                    System.out.println("3. Logout");
                    System.out.println("Enter your choice:");

                    int choice = scanner.nextInt();

                    switch (choice) {
                        case 1:
                            // View existing requests
                            List<FoodParcelRequest> parcels = foodParcelRequestService.getRecipientParcels(recipient.getRecipientId());
                            if (parcels.isEmpty()) {
                                System.out.println("No parcel requests found.");
                            } else {
                                System.out.printf("%-15s %-30s %-30s\n", "Parcel ID", "Preferred Delivery Date", "Items Requested");
                                System.out.println("----------------------------------------------------------------");
                                for (FoodParcelRequest parcel : parcels) {
                                    System.out.printf("%-15d %-30s %-30s\n",
                                            parcel.getParcelRequestId(),
                                            new SimpleDateFormat("yyyy-MM-dd").format(parcel.getPreferredDeliveryDate()),
                                            parcel.getRequestedItems());
                                }
                            }
                            break;

                        case 2:
                            // Request a new parcel
                            System.out.println("\nEnter the preferred delivery date (yyyy-MM-dd):");
                            String dateInput = scanner.next();
                            Date preferredDate;
                            try {
                                preferredDate = new SimpleDateFormat("yyyy-MM-dd").parse(dateInput);
                            } catch (ParseException e) {
                                System.out.println("Invalid date format. Please try again.");
                                break;
                            }

                            List<FoodItem> availableItems = foodParcelRequestService.getAvailableFoodItems();
                            if (availableItems.isEmpty()) {
                                System.out.println("No food items are available.");
                                break;
                            }

                            System.out.printf("%-10s %-30s %-30s\n", "Item ID", "Item Name", "Description");
                            System.out.println("------------------------------------------------------------");
                            for (FoodItem item : availableItems) {
                                System.out.printf("%-10d %-30s %-30s\n",
                                        item.getFoodItemId(),
                                        item.getFoodItemName(),
                                        item.getDescription());
                            }

                            List<FoodItem> selectedItems = new ArrayList<>();
                            while (true) {
                                System.out.println("\nEnter an Item ID to add to your food parcel request (or 0 to finish):");
                                int itemId = scanner.nextInt();
                                if (itemId == 0) {
                                    break;
                                }

                                FoodItem selectedItem = availableItems.stream()
                                        .filter(item -> item.getFoodItemId() == itemId)
                                        .findFirst()
                                        .orElse(null);

                                if (selectedItem == null) {
                                    System.out.println("Invalid Item ID. Please try again.");
                                    continue;
                                }

                                System.out.println("Enter the quantity:");
                                int quantity = scanner.nextInt();
                                selectedItem.setQuantity(quantity);
                                selectedItems.add(selectedItem);
                            }

                            boolean requestSubmitted = foodParcelRequestService.submitFoodParcelRequest(
                                    recipient.getRecipientId(), selectedItems, preferredDate);

                            if (requestSubmitted) {
                                System.out.println("Parcel request submitted successfully!");
                            } else {
                                System.out.println("Failed to submit the parcel request. Please try again.");
                            }
                            break;

                        case 3:
                            // Logout
                            System.out.println("Logged out successfully.");
                            loggedIn = false;
                            return;  // Return to the home page
                        default:
                            System.out.println("Invalid choice. Please try again.");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        } finally {
            foodParcelRequestService.closeService();
            System.out.println("Application closed.");
        }
    }
}
