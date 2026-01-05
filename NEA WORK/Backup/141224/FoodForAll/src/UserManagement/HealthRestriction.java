package UserManagement;

public class HealthRestriction {
    private int restrictionId;
    private String restrictionName;
    private String description;
    private Recipient recipient;

    // Constructor
    public HealthRestriction(int restrictionId, String restrictionName, String description, Recipient recipient) {
        this.restrictionId = restrictionId;
        this.restrictionName = restrictionName;
        this.description = description;
        this.recipient = recipient;
    }

    // Getter and Setter methods
    public int getRestrictionId() {
        return restrictionId;
    }

    public void setRestrictionId(int restrictionId) {
        this.restrictionId = restrictionId;
    }

    public String getRestrictionName() {
        return restrictionName;
    }

    public void setRestrictionName(String restrictionName) {
        this.restrictionName = restrictionName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Recipient getRecipient() {
        return recipient;
    }

    public void setRecipient(Recipient recipient) {
        this.recipient = recipient;
    }
}

