package UserManagement;
public class DietaryPreference {
    private int preferenceId;
    private String preferenceName;
    private String description;
    private Recipient recipient;

    // Constructor
    public DietaryPreference(int preferenceId, String preferenceName, String description, Recipient recipient) {
        this.preferenceId = preferenceId;
        this.preferenceName = preferenceName;
        this.description = description;
        this.recipient = recipient;
    }

    // Getter and Setter methods
    public int getPreferenceId() {
        return preferenceId;
    }

    public void setPreferenceId(int preferenceId) {
        this.preferenceId = preferenceId;
    }

    public String getPreferenceName() {
        return preferenceName;
    }

    public void setPreferenceName(String preferenceName) {
        this.preferenceName = preferenceName;
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

