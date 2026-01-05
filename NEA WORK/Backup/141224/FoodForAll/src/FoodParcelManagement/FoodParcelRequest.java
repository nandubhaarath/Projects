package FoodParcelManagement;

import UserManagement.Recipient;

import java.util.Date;

public class FoodParcelRequest {
    private int parcelRequestId; // Updated variable name for clarity
    private Recipient recipient;
    private Date requestDate;
    private boolean isDeliveryRequired;
    private Date preferredDeliveryDate;

    // Constructor
    public FoodParcelRequest(int parcelRequestId, Recipient recipient, Date requestDate,
                             boolean isDeliveryRequired, Date preferredDeliveryDate) {
        this.parcelRequestId = parcelRequestId;
        this.recipient = recipient;
        this.requestDate = requestDate;
        this.isDeliveryRequired = isDeliveryRequired;
        this.preferredDeliveryDate = preferredDeliveryDate;
    }

    // Getter and Setter methods
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

    public void setDeliveryRequired(boolean isDeliveryRequired) {
        this.isDeliveryRequired = isDeliveryRequired;
    }

    public Date getPreferredDeliveryDate() {
        return preferredDeliveryDate;
    }

    public void setPreferredDeliveryDate(Date preferredDeliveryDate) {
        this.preferredDeliveryDate = preferredDeliveryDate;
    }
}
