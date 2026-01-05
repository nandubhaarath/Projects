package FoodParcelManagement;

public class StatusList {
    private int statusId;
    private String statusName;

    // Constructor
    public StatusList(int statusId, String statusName) {
        this.statusId = statusId;
        this.statusName = statusName;
    }

    // Getter and Setter methods
    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }
}
