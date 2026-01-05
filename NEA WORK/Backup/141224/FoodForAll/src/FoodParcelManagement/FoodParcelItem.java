package FoodParcelManagement;

public class FoodParcelItem {
    private int itemId;
    private FoodItem foodItem;
    private int quantity;

    // Constructor
    public FoodParcelItem(int itemId, FoodItem foodItem, int quantity) {
        this.itemId = itemId;
        this.foodItem = foodItem;
        this.quantity = quantity;
    }

    // Getter and Setter methods
    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public FoodItem getFoodItem() {
        return foodItem;
    }

    public void setFoodItem(FoodItem foodItem) {
        this.foodItem = foodItem;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}

