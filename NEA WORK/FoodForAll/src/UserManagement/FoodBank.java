package UserManagement;

public class FoodBank {
    private int foodBankId;
    private String foodBankName;
    private Address address;

    // Constructor
    public FoodBank(int foodBankId, String foodBankName, Address address) {
        this.foodBankId = foodBankId;
        this.foodBankName = foodBankName;
        this.address = address;
    }

    // Getter and Setter methods
    public int getFoodBankId() {
        return foodBankId;
    }

    public void setFoodBankId(int foodBankId) {
        this.foodBankId = foodBankId;
    }

    public String getFoodBankName() {
        return foodBankName;
    }

    public void setFoodBankName(String foodBankName) {
        this.foodBankName = foodBankName;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
