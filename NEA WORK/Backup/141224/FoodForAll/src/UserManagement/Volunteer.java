package UserManagement;

import java.util.List;

public class Volunteer extends User {
    private String email;
    private String phoneNumber;

    // Constructor
    public Volunteer(String firstName, String lastName, String email, String phoneNumber, String userName, String password, Address address) {
        super(firstName, lastName, userName, password, address);
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    // Getter and Setter methods
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}

