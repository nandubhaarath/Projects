package DatabaseManagement;

import FoodParcelManagement.FoodParcelRequest;
import UserManagement.Address;
import UserManagement.Recipient;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseService {
    private static final String DB_URL = "jdbc:sqlite:FoodForAll.db";
    private Connection conn;

    public DatabaseService() {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(DB_URL);
            conn.setAutoCommit(false);
            System.out.println("Database connection established successfully.");
        } catch (Exception e) {
            System.err.println("Database connection failed: " + e.getMessage());
        }
    }

    public Address GetFoodBankAddress() {
        String query = "SELECT fb.FoodBankId, a.AddressLine1, a.AddressLine2, a.City, a.PostCode " +
                "FROM FoodBank fb " +
                "JOIN Address a ON fb.AddressId = a.AddressId " +
                "WHERE fb.FoodBankId = 1";

        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return new Address(
                        rs.getInt("FoodBankId"),
                        rs.getString("AddressLine1"),
                        rs.getString("AddressLine2"),
                        rs.getString("City"),
                        rs.getString("PostCode")
                );
            }

        } catch (SQLException e) {
            System.err.println("Error fetching food bank address: " + e.getMessage());
        }

        return null;
    }

    public Address GetAddressById(int addressId) {
        String query = "SELECT * FROM Address WHERE AddressId = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, addressId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Address(
                            rs.getInt("AddressId"),
                            rs.getString("AddressLine1"),
                            rs.getString("AddressLine2"),
                            rs.getString("City"),
                            rs.getString("PostCode")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching address by ID: " + e.getMessage());
        }
        return null;
    }

    public List<Address> GetAllAddresses() {
        List<Address> allAddresses = new ArrayList<>();
        String query = "SELECT * FROM Address";

        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Address address = new Address(
                        rs.getInt("AddressId"),
                        rs.getString("AddressLine1"),
                        rs.getString("AddressLine2"),
                        rs.getString("City"),
                        rs.getString("PostCode")
                );
                allAddresses.add(address);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching addresses: " + e.getMessage());
        }

        return allAddresses;
    }

    public List<int[]> GetWeights() {
        List<int[]> weights = new ArrayList<>();
        String query = "SELECT FromNode, ToNode, Weight FROM Weights";

        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int fromId = rs.getInt("FromNode");
                int toId = rs.getInt("ToNode");
                int weight = rs.getInt("Weight");
                weights.add(new int[]{fromId, toId, weight});
            }

        } catch (SQLException e) {
            System.err.println("Error fetching weights: " + e.getMessage());
        }

        return weights;
    }

    public List<FoodParcelRequest> GetPendingRequestsByPreferredDeliveryDate(Date preferredDeliveryDate) {
        List<FoodParcelRequest> pendingRequests = new ArrayList<>();
        String query = "SELECT fpr.ParcelRequestId, fpr.RequestDate, fpr.PreferredDeliveryDate, " +
                "r.FirstName, r.LastName, " +
                "a.AddressId, a.AddressLine1, a.AddressLine2, a.City, a.PostCode " +
                "FROM FoodParcelRequest fpr " +
                "JOIN Recipient r ON fpr.RecipientId = r.RecipientId " +
                "JOIN Address a ON r.AddressId = a.AddressId " +
                "WHERE fpr.StatusId = 1 AND fpr.PreferredDeliveryDate = ?";

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, dateFormat.format(preferredDeliveryDate)); // Set date as a string in the format yyyy-MM-dd

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Address address = new Address(
                            rs.getInt("AddressId"),
                            rs.getString("AddressLine1"),
                            rs.getString("AddressLine2"),
                            rs.getString("City"),
                            rs.getString("PostCode")
                    );

                    Recipient recipient = new Recipient(
                            rs.getString("FirstName"),
                            rs.getString("LastName"),
                            address
                    );

                    // Parse dates explicitly from ResultSet as strings
                    Date requestDate = null;
                    Date deliveryDate = null;
                    try {
                        String requestDateString = rs.getString("RequestDate");
                        requestDate = requestDateString != null ? dateFormat.parse(requestDateString) : null;

                        String deliveryDateString = rs.getString("PreferredDeliveryDate");
                        deliveryDate = deliveryDateString != null ? dateFormat.parse(deliveryDateString) : null;
                    } catch (ParseException e) {
                        System.err.println("Error parsing date for request ID: " + rs.getInt("ParcelRequestId"));
                    }

                    FoodParcelRequest request = new FoodParcelRequest(
                            rs.getInt("ParcelRequestId"),
                            recipient,
                            requestDate,
                            deliveryDate != null,
                            deliveryDate
                    );

                    pendingRequests.add(request);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching pending requests by PreferredDeliveryDate: " + e.getMessage());
            e.printStackTrace();
        }
        return pendingRequests;
    }

    public List<Object[]> GetAssignmentsByVolunteerId(int volunteerId) {
        List<Object[]> assignments = new ArrayList<>();
        String query = "SELECT vda.AssignmentId, fpr.ParcelRequestId, r.FirstName, r.LastName, " +
                "a.AddressLine1, a.AddressLine2, a.City, a.PostCode, " +
                "sl.StatusName, vda.DeliveryDate " +
                "FROM VolunteerDeliveryAssignment vda " +
                "JOIN FoodParcelRequest fpr ON vda.ParcelRequestId = fpr.ParcelRequestId " +
                "JOIN Recipient r ON fpr.RecipientId = r.RecipientId " +
                "JOIN Address a ON r.AddressId = a.AddressId " +
                "JOIN StatusList sl ON fpr.StatusId = sl.StatusId " +
                "WHERE vda.VolunteerId = ?";

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // To parse date from SQLite format

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, volunteerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Date deliveryDate = null;
                    try {
                        String deliveryDateString = rs.getString("DeliveryDate"); // Retrieve as a string
                        if (deliveryDateString != null) {
                            deliveryDate = dateFormat.parse(deliveryDateString); // Parse string to Date
                        }
                    } catch (ParseException e) {
                        System.err.println("Error parsing delivery date: " + e.getMessage());
                    }

                    Object[] assignment = new Object[]{
                            rs.getInt("AssignmentId"),
                            rs.getInt("ParcelRequestId"),
                            rs.getString("FirstName") + " " + rs.getString("LastName"),
                            rs.getString("AddressLine1") + ", " + rs.getString("AddressLine2") + ", " + rs.getString("City") + ", " + rs.getString("PostCode"),
                            rs.getString("StatusName"),
                            deliveryDate // Pass parsed date
                    };
                    assignments.add(assignment);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching assignments for volunteer: " + e.getMessage());
            e.printStackTrace();
        }
        return assignments;
    }


    public void addDeliveryAssignment(int volunteerId, int parcelRequestId, Date deliveryDate) {
        String query = "INSERT INTO VolunteerDeliveryAssignment (VolunteerId, ParcelRequestId, DeliveryDate, StatusId) " +
                "VALUES (?, ?, ?, 2)";

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // Format for SQLite

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, volunteerId);
            stmt.setInt(2, parcelRequestId);
            stmt.setString(3, dateFormat.format(deliveryDate)); // Format the delivery date as 'YYYY-MM-DD'
            stmt.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            System.err.println("Error adding delivery assignment: " + e.getMessage());
        }
    }

    public void updateParcelRequestStatus(int parcelRequestId, int statusId) {
        String query = "UPDATE FoodParcelRequest SET StatusId = ? WHERE ParcelRequestId = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, statusId);
            stmt.setInt(2, parcelRequestId);
            stmt.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            System.err.println("Error updating parcel request status: " + e.getMessage());
        }
    }

    public void updateAssignmentStatus(int parcelRequestId, int statusId) {
        String query = "UPDATE FoodParcelRequest SET StatusId = ? WHERE ParcelRequestId = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, statusId);
            stmt.setInt(2, parcelRequestId);
            stmt.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            System.err.println("Error updating assignment status: " + e.getMessage());
        }
    }

    public void closeConnection() {
        try {
            if (conn != null) {
                conn.close();
                System.out.println("Database connection closed successfully.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }

    public List<FoodParcelRequest> GetPendingRequests() {
        List<FoodParcelRequest> pendingRequests = new ArrayList<>();
        String query = "SELECT fpr.ParcelRequestId, fpr.RequestDate, fpr.PreferredDeliveryDate, " +
                "r.FirstName, r.LastName, " +
                "a.AddressId, a.AddressLine1, a.AddressLine2, a.City, a.PostCode " +
                "FROM FoodParcelRequest fpr " +
                "JOIN Recipient r ON fpr.RecipientId = r.RecipientId " +
                "JOIN Address a ON r.AddressId = a.AddressId " +
                "WHERE fpr.StatusId = 1";

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Address address = new Address(
                        rs.getInt("AddressId"),
                        rs.getString("AddressLine1"),
                        rs.getString("AddressLine2"),
                        rs.getString("City"),
                        rs.getString("PostCode")
                );

                Recipient recipient = new Recipient(
                        rs.getString("FirstName"),
                        rs.getString("LastName"),
                        address
                );

                Date requestDate = null;
                Date preferredDate = null;

                try {
                    String requestDateString = rs.getString("RequestDate");
                    requestDate = requestDateString != null ? dateFormat.parse(requestDateString) : null;

                    String preferredDateString = rs.getString("PreferredDeliveryDate");
                    preferredDate = preferredDateString != null ? dateFormat.parse(preferredDateString) : null;
                } catch (ParseException e) {
                    System.err.println("Error parsing date for request ID: " + rs.getInt("ParcelRequestId"));
                }

                FoodParcelRequest request = new FoodParcelRequest(
                        rs.getInt("ParcelRequestId"),
                        recipient,
                        requestDate,
                        preferredDate != null,
                        preferredDate
                );

                pendingRequests.add(request);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching pending requests: " + e.getMessage());
            e.printStackTrace();
        }
        return pendingRequests;
    }



}
