import javax.swing.*;
import java.math.BigDecimal;
import java.sql.*;

public class DatabaseCreation {
    private static final String DB_URL = "jdbc:mariadb://localhost:3307/brokenomore";

    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "zaf71sint"; //set the password related to your MariaDB
    public static void createTableIfNotExists(){
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        Statement statement = connection.createStatement()
        ){
            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS brokenomore"); //create database BROKENOMORE if not exists
            statement.executeUpdate("USE brokenomore"); //enter in the database

            //create LOGS table
            String createTableQuery_logs = "CREATE TABLE IF NOT EXISTS logs ("+
                    "id INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY, "+
                    "moneyBefore DOUBLE(10,2) DEFAULT NULL, "+
                    "amount DOUBLE(10,2) DEFAULT NULL, "+
                    "type VARCHAR(20) DEFAULT NULL, "+
                    "moneyAfter DOUBLE(10,2) DEFAULT NULL, "+
                    "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "+
                    "notes VARCHAR(255) DEFAULT NULL)";
            statement.executeUpdate(createTableQuery_logs);

            //create USER table
            String createTableQuery_user = "CREATE TABLE IF NOT EXISTS user ("+
                    "money DECIMAL(13,2) DEFAULT NULL, "+  //total precision of 13 digits with 2 after the comma
                                                        //Use DECIMAL for large values in the future
                    "money_limit DECIMAL(13,2) DEFAULT NULL)";
            statement.executeUpdate(createTableQuery_user);

            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO user(money) VALUES (?)");
            BigDecimal zeroValue = BigDecimal.ZERO;  // Use BigDecimal.ZERO to insert 0 to keep the possibility to use big decimal in the future
            preparedStatement.setBigDecimal(1, zeroValue);

            preparedStatement.executeUpdate();


        }catch (SQLException e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error occured!" + e.getMessage());
        }
    }

    public static void main(String[] args){
        createTableIfNotExists();
    }

}
