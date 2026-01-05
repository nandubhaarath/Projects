package UserManagement;

public class Address {
    private int addressId;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String postalCode;

    // Constructor with all fields
    public Address(int addressId, String addressLine1, String addressLine2, String city, String postalCode) {
        this.addressId = addressId;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.city = city;
        this.postalCode = postalCode;
    }

    // Constructor with only postalCode
    public Address(String postalCode) {
        this.postalCode = postalCode;
    }

    // Getter and Setter methods
    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getFullAddress() {
        return addressLine1 + ", " + addressLine2 + ", " + city + ", " + postalCode;
    }

    // Simpler comparison for use within code logic
    public boolean hasSamePostCode(String postCode) {
        return this.postalCode != null && this.postalCode.equals(postCode);
    }

    // Equality comparison based on AddressId
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; // Check if same instance
        if (obj == null || getClass() != obj.getClass()) return false; // Type check

        Address other = (Address) obj;
        return this.addressId == other.addressId; // Compare by AddressId
    }
}
