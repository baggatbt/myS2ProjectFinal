package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorLabel;
    @FXML
    private Button loginButton;

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
                    // navigate to next screen
                } else {
                    // login failed
                    errorLabel.setText("Incorrect username or password.");
                }
            } catch (SQLException e) {
                errorLabel.setText("Error: " + e.getMessage());
            } finally {
                JDBC.closeConnection();
            }
        });
    }
}
