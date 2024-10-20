import javax.swing.*;
import java.math.BigDecimal;
import java.sql.*;

public class DatabaseCreation {
    public static void createTableIfNotExists() {

        DB db = new DB();
        try (Connection connection = db.setDB();
             Statement statement = connection.createStatement()
        ) {


            // Create LOGS table
            String createTableQuery_logs = "CREATE TABLE IF NOT EXISTS logs (" +
                    "id INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                    "moneyBefore DOUBLE(10,2) DEFAULT NULL, " +
                    "amount DOUBLE(10,2) DEFAULT NULL, " +
                    "type VARCHAR(20) DEFAULT NULL, " +
                    "moneyAfter DOUBLE(10,2) DEFAULT NULL, " +
                    "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "notes VARCHAR(255) DEFAULT NULL)";
            statement.executeUpdate(createTableQuery_logs);

            // Create USER table
            String createTableQuery_user = "CREATE TABLE IF NOT EXISTS user (" +
                    "money DECIMAL(13,2) DEFAULT NULL, " +  // Total precision of 13 digits with 2 after the comma
                    "money_limit DECIMAL(13,2) DEFAULT NULL)";
            statement.executeUpdate(createTableQuery_user);

            // Check if any rows exist in the user table
            String checkQuery = "SELECT COUNT(*) FROM user";
            try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery);
                 ResultSet resultSet = checkStmt.executeQuery()) {
                if (resultSet.next()) {
                    int rowCount = resultSet.getInt(1);
                    // Insert initial value if no rows exist
                    if (rowCount == 0) {
                        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO user(money) VALUES (?)");
                        BigDecimal zeroValue = BigDecimal.ZERO;  // Insert BigDecimal.ZERO (0) to keep the possibility to use large values
                        preparedStatement.setBigDecimal(1, zeroValue);
                        preparedStatement.executeUpdate();
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error occurred: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        createTableIfNotExists();
    }
}
