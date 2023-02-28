package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ResourceBundle;

/**
 * This class contains the logic for the login controller functions, such as handling logins and recording login activity
 */
public class LoginController implements Initializable {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private Button loginButton;

    /**
     * This intializes the connection to the database and handles user login's and records activity.
     * @param url a url as a parameter
     * @param resourceBundle a resource bundle as a parameter
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Handle login button click
        loginButton.setOnAction(event -> {
            try {
                JDBC.makeConnection();
                String sql = "SELECT * FROM users WHERE User_Name = ? AND Password = ?";
                JDBC.makePreparedStatement(sql, JDBC.getConnection());
                JDBC.getPreparedStatement().setString(1, usernameField.getText());
                JDBC.getPreparedStatement().setString(2, passwordField.getText());
                ResultSet result = JDBC.getPreparedStatement().executeQuery();
                if (result.next()) {
                    // login successful
                    System.out.println("Login Success!");
                    errorLabel.setText("");

                    // Record login activity to file
                    LocalDateTime now = LocalDateTime.now();
                    String loginStatus = "SUCCESS";
                    String activity = String.format("User %s logged in at %s on %s. Status: %s\n",
                            usernameField.getText(), now.toLocalTime().toString(), now.toLocalDate().toString(), loginStatus);
                    File loginFile = new File("login_activity.txt");
                    FileWriter writer = new FileWriter(loginFile, true);
                    writer.write(activity);
                    writer.close();

                    // check for appointments within 15 minutes
                    now = LocalDateTime.now();
                    LocalDateTime in15Min = now.plusMinutes(15);
                    Timestamp start = Timestamp.valueOf(now);
                    Timestamp end = Timestamp.valueOf(in15Min);
                    ResultSet appointments = getUpcomingAppointments(start, end);
                    if (appointments.next()) {
                        // alert the user about the upcoming appointment
                        String message = "Upcoming appointment within 15 minutes:\n\n" + "Appointment ID: " + appointments.getInt("Appointment_ID") + "\n" + "Date: " + appointments.getTimestamp("Start").toLocalDateTime().toLocalDate() + "\n" + "Time: " + appointments.getTimestamp("Start").toLocalDateTime().toLocalTime();
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
                        alert.showAndWait();
                    } else {
                        // no upcoming appointments
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "No upcoming appointments.");
                        alert.showAndWait();
                    }
                            // navigate to next screen
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("customerRecordForm.fxml"));
                    Parent customerForm = loader.load();
                    Scene customerScene = new Scene(customerForm);
                    // Get the primary stage of the application
                    Stage primaryStage = (Stage) loginButton.getScene().getWindow();
                    // Set the scene of the primary stage to the customerForm
                    primaryStage.setScene(customerScene);
                } else {
                    // login failed
                    errorLabel.setText("Incorrect username or password.");
                    // Record login activity to file
                    LocalDateTime now = LocalDateTime.now();
                    String loginStatus = "FAILED";
                    String activity = String.format("User %s failed login at %s on %s. Status: %s\n",
                            usernameField.getText(), now.toLocalTime().toString(), now.toLocalDate().toString(), loginStatus);
                    File loginFile = new File("login_activity.txt");
                    FileWriter writer = new FileWriter(loginFile, true);
                    writer.write(activity);
                    writer.close();
                }
            } catch (SQLException | IOException e) {
                errorLabel.setText("Error: " + e.getMessage());
            }
        });
    }

    /**
     * This method gets any upcoming appointments between the start and end times.
     * @param start a Timestamp as a parameter
     * @param end a Timestamp as a parameter
     * @return returns the preparedstatement
     * @throws SQLException throws an SQl exception
     */
    private ResultSet getUpcomingAppointments(Timestamp start, Timestamp end) throws SQLException {
        String sql = "SELECT * FROM appointments WHERE Start BETWEEN ? AND ?";
        JDBC.makePreparedStatement(sql, JDBC.getConnection());
        JDBC.getPreparedStatement().setTimestamp(1, start);
        JDBC.getPreparedStatement().setTimestamp(2, end);
        return JDBC.getPreparedStatement().executeQuery();
    }
}
