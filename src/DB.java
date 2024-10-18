import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DB {

    public String[] GetDbInfo(){
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream("./config.properties")) { // get file
            props.load(fis);
        } catch (IOException e) { // check errors
            e.printStackTrace();
        }

        String dbUrl = props.getProperty("db.url");
        String dbUser = props.getProperty("db.user");
        String dbPass = props.getProperty("db.password");

        String[] dbInfo = {dbUrl, dbUser, dbPass}; // return all info in a string

        return dbInfo;
    }

    public Connection setDB() {
        String[] auth = GetDbInfo();
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(auth[0], auth[1], auth[2]);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public double getMoney() throws SQLException {
        try {
            double money = 0;
            Connection connection = setDB();
            Statement statement = connection.createStatement();

            String query = "SELECT money FROM user";

            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                money = resultSet.getFloat("money");
            }
            return money;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public double getMoneyLimit() throws SQLException {
        try {
            double money_limit = 0;
            Connection connection = setDB();
            Statement statement = connection.createStatement();

            String query = "SELECT money_limit FROM user";

            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                money_limit = resultSet.getFloat("money_limit");
            }
            return money_limit;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void addMoney(double moneyAmount) throws SQLException {
        try {
            Connection connection = setDB();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE user SET money = money + ?"
            );
            preparedStatement.setDouble(1, moneyAmount);

            preparedStatement.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addLogs(double moneyBefore, double amount, String type, double moneyAfter, String notes) throws SQLException {
        try{
            Connection connection = setDB();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO logs(moneyBefore, amount, type, moneyAfter, notes) VALUES (?, ?, ?, ?, ?)"
            );
            preparedStatement.setDouble(1, moneyBefore);
            preparedStatement.setDouble(2, amount);
            preparedStatement.setString(3, type);
            preparedStatement.setDouble(4, moneyAfter);
            preparedStatement.setString(5, notes);

            preparedStatement.executeUpdate();
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }

    public List<List<String>> getHistory() throws SQLException {

        try {
            List<String> line; // list of String
            List<List<String>> allData = new ArrayList<>(); // list of a list of String

            Connection connection = setDB();

            Statement statement = connection.createStatement();

            String query = "SELECT * FROM logs";

            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                line = new ArrayList<>();

                String id = resultSet.getString("id"); // get query responses
                String moneyBefore = resultSet.getString("moneyBefore");
                String amount = resultSet.getString("amount");
                String type = resultSet.getString("type");
                String moneyAfter = resultSet.getString("moneyAfter");
                String time = resultSet.getString("timestamp");
                String notes = resultSet.getString("notes");

                line.add(id); // add all response to query in the list that represent the row
                line.add(moneyBefore);
                line.add(amount);
                line.add(type);
                line.add(moneyAfter);
                line.add(time);
                line.add(notes);

                allData.add(line); // add it in the list of a list, that represent all the rows and the column of our table logs
            }
            return allData;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>(); // if there is a problem, return an empty list of list
    }

    public void setMoneyLimit(double limit) throws SQLException {
        try{
            Connection connection = setDB();

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE user SET money_limit = ?"
            );
            preparedStatement.setDouble(1, limit);

            preparedStatement.executeUpdate();

            connection.close();
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }

}
