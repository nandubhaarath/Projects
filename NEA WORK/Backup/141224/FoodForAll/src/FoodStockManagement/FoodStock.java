package FoodStockManagement;

import FoodParcelManagement.FoodItem;

public class FoodStock {
    private int stockId;
    private FoodItem foodItem;
    private int quantity;

    // Constructor
    public FoodStock(int stockId, FoodItem foodItem, int quantity) {
        this.stockId = stockId;
        this.foodItem = foodItem;
        this.quantity = quantity;
    }

    // Getter and Setter methods
    public int getStockId() {
        return stockId;
    }

    public void setStockId(int stockId) {
        this.stockId = stockId;
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
