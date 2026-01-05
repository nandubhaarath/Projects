package UserManagement;

public class Recipient {
    private int recipientId; // Added recipient ID
    private final String firstName;
    private final String lastName;
    private final Address address;

    public Recipient(int recipientId, String firstName, String lastName, Address address) {
        this.recipientId = recipientId; // Initialize recipient ID
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
    }

    public Address getAddress() {
        return address;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(int recipientId) {
        this.recipientId = recipientId;
    }

    /**
     * Returns the full name of the recipient by combining first and last name.
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
