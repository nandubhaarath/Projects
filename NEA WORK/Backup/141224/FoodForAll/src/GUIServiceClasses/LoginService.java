package GUIServiceClasses;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginService implements ILoginService {
    private DatabaseConnection dbConnection;

    public LoginService() {
        dbConnection = DatabaseConnection.getInstance();
    }

    @Override
    public boolean validateLogin(String username, String password) {
        Connection connection = null;
        try {
            connection = dbConnection.connect();
            String query = "SELECT * FROM Users WHERE username = ? AND password = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            dbConnection.closeConnection(connection);
        }
    }
}
