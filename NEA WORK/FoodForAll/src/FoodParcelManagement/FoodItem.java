package FoodParcelManagement;

public class FoodItem {
    private int foodItemId;
    private String foodItemName;
    private String description;
    private int quantity; // Added to track quantity

    // Constructor
    public FoodItem(int foodItemId, String foodItemName, String description) {
        this.foodItemId = foodItemId;
        this.foodItemName = foodItemName;
        this.description = description;
        this.quantity = 0; // Default quantity
    }

    // Getter and Setter methods
    public int getFoodItemId() {
        return foodItemId;
    }

    public void setFoodItemId(int foodItemId) {
        this.foodItemId = foodItemId;
    }

    public String getFoodItemName() {
        return foodItemName;
    }

    public void setFoodItemName(String foodItemName) {
        this.foodItemName = foodItemName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
