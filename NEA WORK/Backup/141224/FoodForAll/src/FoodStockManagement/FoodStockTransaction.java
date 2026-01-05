package FoodStockManagement;

import java.util.Date;

public class FoodStockTransaction {
    private int transactionId;
    private FoodStock foodStock;
    private String transactionType; // e.g., "Added", "Removed"
    private int quantity;
    private Date transactionDate;

    // Constructor
    public FoodStockTransaction(int transactionId, FoodStock foodStock, String transactionType, int quantity, Date transactionDate) {
        this.transactionId = transactionId;
        this.foodStock = foodStock;
        this.transactionType = transactionType;
        this.quantity = quantity;
        this.transactionDate = transactionDate;
    }

    // Getter and Setter methods
    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public FoodStock getFoodStock() {
        return foodStock;
    }

    public void setFoodStock(FoodStock foodStock) {
        this.foodStock = foodStock;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }
}
