package GUIServiceClasses;

import FoodParcelManagement.FoodParcelRequest;

import java.util.List;

public interface IFoodParcelRequestService {
    boolean submitFoodParcelRequest(FoodParcelRequest request);
    List<FoodParcelRequest> getFoodParcelRequestsForRecipient(int recipientId);
}

