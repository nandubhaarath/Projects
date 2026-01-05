package FoodParcelManagement;

import DatabaseManagement.DatabaseService;

import java.util.Date;
import java.util.List;

public class VolunteerDeliveryAssignmentService {
    private final DatabaseService databaseService;

    public VolunteerDeliveryAssignmentService(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    // Fetch assignments for a specific volunteer
    public List<Object[]> getAssignments(int volunteerId) {
        return databaseService.GetAssignmentsByVolunteerId(volunteerId);
    }

    // Update assignment status
    public void updateAssignmentStatus(int parcelRequestId, int statusId) {
        databaseService.updateParcelRequestStatus(parcelRequestId, statusId);
    }
}
