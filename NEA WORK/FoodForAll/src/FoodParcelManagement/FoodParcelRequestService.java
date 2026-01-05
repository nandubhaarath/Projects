package FoodParcelManagement;

import DatabaseManagement.DatabaseService;
import DatabaseManagement.IDatabaseService;
import UserManagement.Recipient;

import java.util.Date;
import java.util.List;

public class FoodParcelRequestService {
    private final IDatabaseService IDatabaseService;

    public FoodParcelRequestService() {
        this.IDatabaseService = new DatabaseService();
    }

    // Method to authenticate recipient with username and password
    public Recipient loginRecipient(String username, String password) {
        return IDatabaseService.getRecipientByUsernameAndPassword(username, password);
    }

    // Method to fetch parcels requested by a recipient
    public List<FoodParcelRequest> getRecipientParcels(int recipientId) {
        return IDatabaseService.getRecipientParcels(recipientId);
    }

    // Method to fetch all available food items
    public List<FoodItem> getAvailableFoodItems() {
        return IDatabaseService.getAllItems();
    }

    // Method to submit a new food parcel request
    public boolean submitFoodParcelRequest(int recipientId, List<FoodItem> items, Date preferredDeliveryDate) {
        return IDatabaseService.submitFoodParcelRequest(recipientId, items, preferredDeliveryDate);
    }

    // Method to close the database connection
    public void closeService() {
        IDatabaseService.closeConnection();
    }
}
