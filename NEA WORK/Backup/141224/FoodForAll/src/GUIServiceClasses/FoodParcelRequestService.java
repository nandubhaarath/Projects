//package GUIServiceClasses;
//
//import FoodParcelManagement.FoodParcelRequest;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.util.ArrayList;
//import java.util.List;
//
//public class FoodParcelRequestService implements IFoodParcelRequestService {
//    private DatabaseConnection dbConnection;
//
//    public FoodParcelRequestService() {
//        dbConnection = DatabaseConnection.getInstance();
//    }
//
//    @Override
//    public boolean submitFoodParcelRequest(FoodParcelRequest request) {
//        Connection connection = null;
//        try {
//            connection = dbConnection.connect();
//            String query = "INSERT INTO FoodParcelRequest (RecipientId, RequestDate, IsDeliveryRequired, PreferredDeliveryDate, StatusId) VALUES (?, ?, ?, ?, ?)";
//            PreparedStatement preparedStatement = connection.prepareStatement(query);
//            //preparedStatement.setInt(1, request.getRecipient().getRecipientId());
//            preparedStatement.setDate(2, new java.sql.Date(request.getRequestDate().getTime()));
//            preparedStatement.setBoolean(3, request.isDeliveryRequired());
//            preparedStatement.setDate(4, new java.sql.Date(request.getPreferredDeliveryDate().getTime()));
//            preparedStatement.setInt(5, request.getStatus().getStatusId());
//            return preparedStatement.executeUpdate() > 0;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        } finally {
//            dbConnection.closeConnection(connection);
//        }
//    }
//
//    @Override
//    public List<FoodParcelRequest> getFoodParcelRequestsForRecipient(int recipientId) {
//        List<FoodParcelRequest> requests = new ArrayList<>();
//        Connection connection = null;
//        try {
//            connection = dbConnection.connect();
//            String query = "SELECT * FROM FoodParcelRequest WHERE RecipientId = ?";
//            PreparedStatement preparedStatement = connection.prepareStatement(query);
//            preparedStatement.setInt(1, recipientId);
//            ResultSet resultSet = preparedStatement.executeQuery();
//
//            while (resultSet.next()) {
//                // Construct FoodParcelRequest and add to the list
//                FoodParcelRequest request = new FoodParcelRequest(resultSet.getInt("RequestId"), null,
//                        resultSet.getDate("RequestDate"), resultSet.getBoolean("IsDeliveryRequired"),
//                        resultSet.getDate("PreferredDeliveryDate"), null);
//                requests.add(request);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            dbConnection.closeConnection(connection);
//        }
//        return requests;
//    }
//}
