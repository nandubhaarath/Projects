package UserManagement;

public class Recipient {
    private String firstName;
    private String lastName;
    private Address address;

    public Recipient(String firstName, String lastName, Address address) {
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

    /**
     * Returns the full name of the recipient by combining first and last name.
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
