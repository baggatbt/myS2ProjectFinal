package sample;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;


public class AppointmentsController implements Initializable {

    public TableView appointmentsTableView;
    public TableColumn appointmentIdColumn;
    public TableColumn titleColumn;
    public TableColumn descriptionColumn;
    public TableColumn locationColumn;
    public TableColumn contactColumn;
    public TableColumn typeColumn;
    public TableColumn startDateColumn;
    public TableColumn endDateColumn;
    public TableColumn customerIdColumn;
    public TableColumn userIdColumn;
    @FXML
    private TextField appointmentIdTextField;
    @FXML
    private TextField titleTextField;
    @FXML
    private TextField descriptionTextField;
    @FXML
    private TextField locationTextField;
    @FXML
    private ComboBox<String> contactComboBox;
    @FXML
    private ComboBox<String> typeComboBox;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private TextField customerIdTextField;
    @FXML
    private TextField userIdTextField;

    private ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();
    private Appointment selectedAppointment;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        appointmentIdTextField.setEditable(false);
        retrieveAppointmentsFromDB();
    }

    private void retrieveAppointmentsFromDB() {
        try {
            // Prepare the select statement
            PreparedStatement stmt = JDBC.getConnection().prepareStatement("SELECT Appointment_ID, Title, Description, Location, Contact_ID, Type, Start, End, Customer_ID, User_ID FROM appointments");
            // Execute the select statement
            ResultSet resultSet = stmt.executeQuery();
            // Clear the customer list
            appointmentList.clear();
            // Populate the customer list with data from the result set
            while (resultSet.next()) {
                appointmentList.add(new Appointment(
                        resultSet.getInt("Appointment_ID"),
                        resultSet.getString("Title"),
                        resultSet.getString("Description"),
                        resultSet.getString("Location"),
                        resultSet.getInt("Contact_ID"),
                        resultSet.getString("Type"),
                        resultSet.getTimestamp("Start").toLocalDateTime(),
                        resultSet.getTimestamp("End").toLocalDateTime(),
                        resultSet.getInt("Customer_ID"),
                        resultSet.getInt("User_ID")
                ));
            }
            // Close the connection
            // Set the items of the customer table to the customer list
            appointmentsTableView.setItems(appointmentList);
            // Set cell values for the table columns
            appointmentIdColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
            titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
            descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
            locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
            contactColumn.setCellValueFactory(new PropertyValueFactory<>("contactID"));
            typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
            startDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDateTime"));
            endDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDateTime"));
            customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
            userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




}



