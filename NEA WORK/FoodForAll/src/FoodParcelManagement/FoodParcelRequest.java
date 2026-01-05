package FoodParcelManagement;

import UserManagement.Recipient;
import java.util.Date;
import java.util.List;

public class FoodParcelRequest {
    private int parcelRequestId;
    private Recipient recipient;
    private Date requestDate;
    private boolean isDeliveryRequired;
    private Date preferredDeliveryDate;
    private List<FoodParcelItem> requestedItems; // Field for requested items

    // Constructor with requested items
    public FoodParcelRequest(int parcelRequestId, Recipient recipient, Date requestDate,
                             boolean isDeliveryRequired, Date preferredDeliveryDate,
                             List<FoodParcelItem> requestedItems) {
        this.parcelRequestId = parcelRequestId;
        this.recipient = recipient;
        this.requestDate = requestDate;
        this.isDeliveryRequired = isDeliveryRequired;
        this.preferredDeliveryDate = preferredDeliveryDate;
        this.requestedItems = requestedItems;
    }

    // Constructor without requested items (for backward compatibility)
    public FoodParcelRequest(int parcelRequestId, Recipient recipient, Date requestDate,
                             boolean isDeliveryRequired, Date preferredDeliveryDate) {
        this.parcelRequestId = parcelRequestId;
        this.recipient = recipient;
        this.requestDate = requestDate;
        this.isDeliveryRequired = isDeliveryRequired;
        this.preferredDeliveryDate = preferredDeliveryDate;
    }

    // Getter for requested items
    public List<FoodParcelItem> getRequestedItems() {
        return requestedItems;
    }

    // Setter for requested items
    public void setRequestedItems(List<FoodParcelItem> requestedItems) {
        this.requestedItems = requestedItems;
    }

    public int getParcelRequestId() {
        return parcelRequestId;
    }

    public void setParcelRequestId(int parcelRequestId) {
        this.parcelRequestId = parcelRequestId;
    }

    public Recipient getRecipient() {
        return recipient;
    }

    public void setRecipient(Recipient recipient) {
        this.recipient = recipient;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public boolean isDeliveryRequired() {
        return isDeliveryRequired;
    }

    public void setDeliveryRequired(boolean deliveryRequired) {
        isDeliveryRequired = deliveryRequired;
    }

    public Date getPreferredDeliveryDate() {
        return preferredDeliveryDate;
    }

    public void setPreferredDeliveryDate(Date preferredDeliveryDate) {
        this.preferredDeliveryDate = preferredDeliveryDate;
    }
}
