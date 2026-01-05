package FoodParcelManagement;

import DatabaseManagement.IDatabaseService;

import java.util.List;

public class VolunteerDeliveryAssignmentService {
    private final IDatabaseService IDatabaseService;

    public VolunteerDeliveryAssignmentService(IDatabaseService IDatabaseService) {
        this.IDatabaseService = IDatabaseService;
    }

    // Fetch assignments for a specific volunteer
    public List<Object[]> getAssignments(int volunteerId) {
        return IDatabaseService.GetAssignmentsByVolunteerId(volunteerId);
    }

    // Update assignment status
    public void updateAssignmentStatus(int parcelRequestId, int statusId) {
        IDatabaseService.updateParcelRequestStatus(parcelRequestId, statusId);
    }

    // Update assignment status using assignmentId
    public void updateAssignmentStatusByAssignmentId(int assignmentId, int statusId) {
        IDatabaseService.updateAssignmentStatusByAssignmentId(assignmentId, statusId);
    }

}
