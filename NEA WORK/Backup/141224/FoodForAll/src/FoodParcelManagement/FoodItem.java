package FoodParcelManagement;

public class FoodItem {
    private int foodItemId;
    private String foodItemName;
    private String description;

    // Constructor
    public FoodItem(int foodItemId, String foodItemName, String description) {
        this.foodItemId = foodItemId;
        this.foodItemName = foodItemName;
        this.description = description;
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
}
