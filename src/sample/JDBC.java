package sample;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * This class contains the connection information for the database used in the project
 */
public class JDBC {
    private static final String protocol = "jdbc";
    private static final String vendor = ":mysql:";
    private static final String location = "//localhost/";
    private static final String databaseName = "client_schedule";
    private static final String jdbcUrl = protocol + vendor + location + databaseName + "?connectionTimeZone = SERVER"; // LOCAL
    private static final String driver = "com.mysql.cj.jdbc.Driver"; // Driver reference
    private static final String userName = "sqlUser"; // Username
    private static String password = "Passw0rd!"; // Password
    private static Connection connection = null;  // Connection Interface
    private static PreparedStatement preparedStatement;

    /**
     * This method gets a connection to the database
     */
    public static void makeConnection() {

        try {
            Class.forName(driver); // Locate Driver
            //password = Details.getPassword(); // Assign password
            connection = DriverManager.getConnection(jdbcUrl, userName, password); // reference Connection object
            System.out.println("Connection successful!");
        }
        catch(ClassNotFoundException e) {
            System.out.println("Error:" + e.getMessage());
        }
        catch(SQLException e) {
            System.out.println("Error:" + e.getMessage());
        }
    }

    public static Connection getConnection() {
        return connection;
    }
    public static void closeConnection() {
        try {
            connection.close();
            System.out.println("Connection closed!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * This method creates a prepared statement to interact with the database
     * @param sqlStatement an SQL statement as a parameter
     * @param conn a connection as a parameter
     * @throws SQLException an SQLException as a parameter
     */
    public static void makePreparedStatement(String sqlStatement, Connection conn) throws SQLException {
        if (conn != null)
            preparedStatement = conn.prepareStatement(sqlStatement);
        else
            System.out.println("Prepared Statement Creation Failed!");
    }

    /**
     * This methods gets a prepared statement
     * @return the prepared statement
     * @throws SQLException throws an SQLException
     */
    public static PreparedStatement getPreparedStatement() throws SQLException {
        if (preparedStatement != null)
            return preparedStatement;
        else System.out.println("Null reference to Prepared Statement");
        return null;
    }



}