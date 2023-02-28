package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.scene.control.ScrollPane;

public class ReportController implements Initializable {

    @FXML
    private TextArea reportTextArea;

    @FXML
    private ScrollPane scrollPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public void goToAppointments(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("appointmentsForm.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    public void contactSchedulesReport(ActionEvent actionEvent) throws IOException {
        try {
            // Prepare the select statement
            PreparedStatement stmt = JDBC.getConnection().prepareStatement(
                    "SELECT Appointment_ID, Title, Type, Description, Start, End, appointments.Customer_ID, appointments.Contact_ID " +
                            "FROM appointments "
            );


            // Execute the select statement
            ResultSet resultSet = stmt.executeQuery();

            // Append results to reportTextArea
            StringBuilder sb = new StringBuilder();
            sb.append("Contact Schedules Report:\n\n");
            sb.append(String.format("%-15s %-40s %-15s %-40s %-30s %-30s %-30s\n",
                    "Appointment ID", "Title", "Type", "Description", "Start", "End", "Customer ID"));
            while (resultSet.next()) {
                sb.append(String.format("%-25s %-30s %-30s %-30s %-30s %-30s %-30s\n",
                        resultSet.getInt("Appointment_ID"), resultSet.getString("Title"),
                        resultSet.getString("Type"), resultSet.getString("Description"),
                        resultSet.getTimestamp("Start"), resultSet.getTimestamp("End"),
                        resultSet.getInt("Customer_ID")));
            }
            reportTextArea.setText(sb.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void totalCustomerReport(ActionEvent actionEvent) throws IOException {
        try {
            // Prepare the select statement
            PreparedStatement stmt = JDBC.getConnection().prepareStatement(
                    "SELECT DISTINCT Type, MONTHNAME(Start) AS Month, COUNT(*) AS Total " +
                            "FROM appointments " +
                            "GROUP BY Type, MONTH(Start)"
            );
            // Execute the select statement
            ResultSet resultSet = stmt.executeQuery();

            // Append results to reportTextArea
            StringBuilder sb = new StringBuilder();
            sb.append("Total Customer Report:\n\n");
            sb.append(String.format("%-30s %-25s %-15s\n", "Type", "Month", "Total"));
            while (resultSet.next()) {
                String month = resultSet.getString("Month");
                String monthName = month.substring(0,1).toUpperCase() + month.substring(1).toLowerCase(); // capitalize the first letter of the month name
                sb.append(String.format("%-30s %-25s %-15s\n", resultSet.getString("Type"), monthName, resultSet.getInt("Total")));
            }

            reportTextArea.setText(sb.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void allCurrentCustomersReport(ActionEvent actionEvent) throws IOException {
        try {
            // Prepare the select statement
            PreparedStatement stmt = JDBC.getConnection().prepareStatement(
                    "SELECT Customer_ID, Customer_Name FROM customers"
            );
            // Execute the select statement
            ResultSet resultSet = stmt.executeQuery();
            // Append results to reportTextArea
            StringBuilder sb = new StringBuilder();
            sb.append("All Customers Report:\n\n");
            while (resultSet.next()) {
                sb.append(String.format("%-10s %s\n", resultSet.getString("Customer_ID"), resultSet.getString("Customer_Name")));
            }
            reportTextArea.setText(sb.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

