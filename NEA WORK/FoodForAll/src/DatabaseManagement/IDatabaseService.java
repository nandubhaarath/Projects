package DatabaseManagement;

import FoodParcelManagement.FoodItem;
import FoodParcelManagement.FoodParcelItem;
import FoodParcelManagement.FoodParcelRequest;
import UserManagement.Address;
import UserManagement.Recipient;

import java.util.Date;
import java.util.List;

public interface IDatabaseService {
    Address GetFoodBankAddress();

    List<FoodParcelRequest> GetPendingRequests();

    Address GetAddressById(int addressId);

    List<Object[]> GetAssignmentsByVolunteerId(int volunteerId);

    List<Address> GetAllAddresses();

    List<int[]> GetWeights();

    List<FoodParcelRequest> GetPendingRequestsByPreferredDeliveryDate(Date preferredDeliveryDate);

    void addDeliveryAssignment(int volunteerId, int parcelRequestId, Date deliveryDate);

    void updateParcelRequestStatus(int parcelRequestId, int statusId);

    List<FoodItem> getAllItems();

    Recipient getRecipientByUsername(String username);

    List<FoodParcelRequest> getRecipientParcels(int recipientId);

    List<FoodParcelItem> getItemsForParcel(int parcelRequestId);

    boolean submitFoodParcelRequest(int recipientId, List<FoodItem> items, Date preferredDate);

    Recipient getRecipientByUsernameAndPassword(String username, String password);

    void updateAssignmentStatusByAssignmentId(int assignmentId, int statusId);

    void closeConnection();
}
