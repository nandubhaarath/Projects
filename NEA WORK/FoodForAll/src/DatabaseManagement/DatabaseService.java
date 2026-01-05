package DatabaseManagement;

import FoodParcelManagement.FoodItem;
import FoodParcelManagement.FoodParcelRequest;
import FoodParcelManagement.FoodParcelItem;
import UserManagement.Address;
import UserManagement.Recipient;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseService implements IDatabaseService {
    private static final String DB_URL = "jdbc:sqlite:FoodForAll.db";
    private Connection conn;

    public DatabaseService() {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(DB_URL);
            conn.setAutoCommit(false);
        } catch (Exception e) {

        }
    }

    @Override
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

    @Override
    public List<FoodParcelRequest> GetPendingRequests() {
        List<FoodParcelRequest> pendingRequests = new ArrayList<>();
        String query = "SELECT fpr.ParcelRequestId, fpr.RequestDate, fpr.PreferredDeliveryDate, " +
                "r.RecipientId, r.FirstName, r.LastName, " +
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
                        rs.getInt("RecipientId"), // Added recipientId
                        rs.getString("FirstName"),
                        rs.getString("LastName"),
                        address
                );

                Date requestDate = null;
                Date preferredDate = null;

                try {
                    requestDate = rs.getString("RequestDate") != null ? dateFormat.parse(rs.getString("RequestDate")) : null;
                    preferredDate = rs.getString("PreferredDeliveryDate") != null ? dateFormat.parse(rs.getString("PreferredDeliveryDate")) : null;
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


    @Override
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

    @Override
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


    @Override
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

    @Override
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

    @Override
    public List<FoodParcelRequest> GetPendingRequestsByPreferredDeliveryDate(Date preferredDeliveryDate) {
        List<FoodParcelRequest> pendingRequests = new ArrayList<>();
        String query = "SELECT fpr.ParcelRequestId, fpr.RequestDate, fpr.PreferredDeliveryDate, " +
                "r.RecipientId, r.FirstName, r.LastName, " +
                "a.AddressId, a.AddressLine1, a.AddressLine2, a.City, a.PostCode " +
                "FROM FoodParcelRequest fpr " +
                "JOIN Recipient r ON fpr.RecipientId = r.RecipientId " +
                "JOIN Address a ON r.AddressId = a.AddressId " +
                "WHERE fpr.StatusId = 1 AND fpr.PreferredDeliveryDate = ?";

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, dateFormat.format(preferredDeliveryDate));

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
                            rs.getInt("RecipientId"), // Added recipientId
                            rs.getString("FirstName"),
                            rs.getString("LastName"),
                            address
                    );

                    Date requestDate = null;
                    Date deliveryDate = null;
                    try {
                        requestDate = rs.getString("RequestDate") != null ? dateFormat.parse(rs.getString("RequestDate")) : null;
                        deliveryDate = rs.getString("PreferredDeliveryDate") != null ? dateFormat.parse(rs.getString("PreferredDeliveryDate")) : null;
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


    @Override
    public void addDeliveryAssignment(int volunteerId, int parcelRequestId, Date deliveryDate) {
        String query = "INSERT INTO VolunteerDeliveryAssignment (VolunteerId, ParcelRequestId, DeliveryDate, StatusId) " +
                "VALUES (?, ?, ?, 2)";

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, volunteerId);
            stmt.setInt(2, parcelRequestId);
            stmt.setString(3, dateFormat.format(deliveryDate));
            stmt.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            System.err.println("Error adding delivery assignment: " + e.getMessage());
        }
    }

    @Override
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

    @Override
    public List<FoodItem> getAllItems() {
        String query = "SELECT FoodItemId, FoodItemName, Description FROM FoodItemList";
        List<FoodItem> items = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                items.add(new FoodItem(
                        rs.getInt("FoodItemId"),        // Map FoodItemId to foodItemId
                        rs.getString("FoodItemName"),   // Map FoodItemName to foodItemName
                        rs.getString("Description")     // Map Description to description
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching food items: " + e.getMessage());
        }

        return items;
    }

    @Override
    public Recipient getRecipientByUsername(String username) {
        String query = "SELECT * FROM Recipient WHERE UserName = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Recipient(
                            rs.getInt("RecipientId"), // Added recipientId
                            rs.getString("FirstName"),
                            rs.getString("LastName"),
                            GetAddressById(rs.getInt("AddressId"))
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching recipient by username: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<FoodParcelRequest> getRecipientParcels(int recipientId) {
        List<FoodParcelRequest> parcels = new ArrayList<>();
        String query = "SELECT fpr.ParcelRequestId, fpr.RequestDate, fpr.PreferredDeliveryDate, " +
                "fpr.StatusId, r.FirstName, r.LastName, " +
                "a.AddressId, a.AddressLine1, a.AddressLine2, a.City, a.PostCode " +
                "FROM FoodParcelRequest fpr " +
                "JOIN Recipient r ON fpr.RecipientId = r.RecipientId " +
                "JOIN Address a ON r.AddressId = a.AddressId " +
                "WHERE fpr.RecipientId = ?";

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, recipientId);
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
                            recipientId,
                            rs.getString("FirstName"),
                            rs.getString("LastName"),
                            address
                    );

                    Date requestDate = null;
                    Date preferredDate = null;
                    try {
                        requestDate = rs.getString("RequestDate") != null ? dateFormat.parse(rs.getString("RequestDate")) : null;
                        preferredDate = rs.getString("PreferredDeliveryDate") != null ? dateFormat.parse(rs.getString("PreferredDeliveryDate")) : null;
                    } catch (ParseException e) {
                        System.err.println("Error parsing dates for parcel requests: " + e.getMessage());
                    }

                    int parcelRequestId = rs.getInt("ParcelRequestId");
                    List<FoodParcelItem> requestedItems = getItemsForParcel(parcelRequestId);

                    FoodParcelRequest request = new FoodParcelRequest(
                            parcelRequestId,
                            recipient,
                            requestDate,
                            preferredDate != null,
                            preferredDate,
                            requestedItems
                    );

                    parcels.add(request);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching recipient parcels: " + e.getMessage());
        }
        return parcels;
    }

    @Override
    public List<FoodParcelItem> getItemsForParcel(int parcelRequestId) {
        List<FoodParcelItem> items = new ArrayList<>();
        String query = "SELECT fpi.FoodItemId, fi.FoodItemName, fi.Description, fpi.Quantity " +
                "FROM foodparcelitem fpi " +
                "JOIN fooditemlist fi ON fpi.FoodItemId = fi.FoodItemId " +
                "WHERE fpi.ParcelRequestId = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, parcelRequestId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    FoodItem foodItem = new FoodItem(
                            rs.getInt("FoodItemId"),
                            rs.getString("FoodItemName"),
                            rs.getString("Description")
                    );

                    FoodParcelItem parcelItem = new FoodParcelItem(
                            rs.getInt("FoodItemId"),
                            foodItem,
                            rs.getInt("Quantity")
                    );

                    items.add(parcelItem);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching items for parcel ID " + parcelRequestId + ": " + e.getMessage());
        }

        return items;
    }

    @Override
    public boolean submitFoodParcelRequest(int recipientId, List<FoodItem> items, Date preferredDate) {
        String insertParcelQuery = "INSERT INTO FoodParcelRequest (RecipientId, FoodBankId, PreferredDeliveryDate, StatusId, RequestDate) VALUES (?, ?, ?, 1, ?)";
        String insertItemQuery = "INSERT INTO FoodParcelItem (ParcelRequestId, FoodItemId, Quantity) VALUES (?, ?, ?)";

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try (PreparedStatement parcelStmt = conn.prepareStatement(insertParcelQuery, Statement.RETURN_GENERATED_KEYS)) {
            // Insert parcel request with hardcoded FoodBankId = 1
            parcelStmt.setInt(1, recipientId);
            parcelStmt.setInt(2, 1); // Hardcoded FoodBankId
            parcelStmt.setString(3, dateFormat.format(preferredDate));
            parcelStmt.setString(4, dateFormat.format(new Date())); // Set current date as the request date
            parcelStmt.executeUpdate();

            // Retrieve generated ParcelRequestId
            try (ResultSet generatedKeys = parcelStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int parcelRequestId = generatedKeys.getInt(1);

                    // Insert each item into FoodParcelItem
                    try (PreparedStatement itemStmt = conn.prepareStatement(insertItemQuery)) {
                        for (FoodItem item : items) {
                            itemStmt.setInt(1, parcelRequestId);
                            itemStmt.setInt(2, item.getFoodItemId());
                            itemStmt.setInt(3, item.getQuantity());
                            itemStmt.addBatch();
                        }
                        itemStmt.executeBatch();
                    }
                } else {
                    throw new SQLException("Creating parcel request failed, no ID obtained.");
                }
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Error submitting food parcel request: " + e.getMessage());
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("Error during rollback: " + rollbackEx.getMessage());
            }
        }
        return false;
    }


    @Override
    public Recipient getRecipientByUsernameAndPassword(String username, String password) {
        String query = "SELECT * FROM Recipient WHERE UserName = ? AND Password = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Recipient(
                            rs.getInt("RecipientId"),
                            rs.getString("FirstName"),
                            rs.getString("LastName"),
                            GetAddressById(rs.getInt("AddressId"))
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching recipient by username and password: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void updateAssignmentStatusByAssignmentId(int assignmentId, int statusId) {
        String query = "UPDATE FoodParcelRequest " +
                "SET StatusId = ? " +
                "WHERE ParcelRequestId = (SELECT ParcelRequestId FROM VolunteerDeliveryAssignment WHERE AssignmentId = ?)";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, statusId);
            stmt.setInt(2, assignmentId);
            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                conn.commit();
                System.out.println("Assignment status updated successfully for AssignmentId: " + assignmentId);
            } else {
                System.out.println("No assignment found with AssignmentId: " + assignmentId);
            }
        } catch (SQLException e) {
            System.err.println("Error updating assignment status: " + e.getMessage());
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("Error during rollback: " + rollbackEx.getMessage());
            }
        }
    }


    @Override
    public void closeConnection() {
        try {
            if (conn != null) {
                conn.close();
                // System.out.println("Database connection closed successfully.");
            }
        } catch (SQLException e) {
            //System.err.println("Error closing database connection: " + e.getMessage());
        }
    }


}
