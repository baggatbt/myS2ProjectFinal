package sample;

import java.net.URL;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
        populateTypeComboBox();
        populateContactComboBox();
        retrieveAppointmentsFromDB();

    }

    private void retrieveAppointmentsFromDB() {
        try {
            // Prepare the select statement
            PreparedStatement stmt = JDBC.getConnection().prepareStatement("SELECT Appointment_ID, Title, Description, Location, Contact_ID, Type, Start, End, Customer_ID, User_ID FROM appointments");
            // Execute the select statement
            ResultSet resultSet = stmt.executeQuery();

            PreparedStatement stmt2 = JDBC.getConnection().prepareStatement("SELECT Start FROM appointments");
            ResultSet resultSet2 = stmt2.executeQuery();
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
                        resultSet.getTimestamp("Start"),
                        resultSet.getTimestamp("End"),
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
            customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerID"));
            userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userID"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void populateTypeComboBox() {
        try {


            // Prepare a statement to select all the countries from the database
            PreparedStatement stmt = JDBC.getConnection().prepareStatement("SELECT Type FROM appointments");
            // Execute the query and store the results in a ResultSet
            ResultSet rs = stmt.executeQuery();
            // Iterate through the results and add each country to the combo box
            while (rs.next()) {
                typeComboBox.getItems().add(rs.getString("Type"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void populateContactComboBox() {
        try {


            // Prepare a statement to select all the countries from the database
            PreparedStatement stmt = JDBC.getConnection().prepareStatement("SELECT Contact_Name FROM contacts");
            // Execute the query and store the results in a ResultSet
            ResultSet rs = stmt.executeQuery();
            // Iterate through the results and add each country to the combo box
            while (rs.next()) {
                contactComboBox.getItems().add(rs.getString("Contact_Name"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void addAppointment() {
        try (Connection connection = JDBC.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT Type FROM appointments WHERE Type = ?");

             PreparedStatement stmt = JDBC.getConnection().prepareStatement("INSERT INTO appointments(Title, Description, Location, Type, Start, End, Customer_ID, User_ID, Contact_ID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")) {

            Object selectedItem = typeComboBox.getSelectionModel().getSelectedItem();
            statement.setString(1, (String) selectedItem);
            ResultSet result = statement.executeQuery();
            result.next();
            String type = result.getString("Type");

            Object selectedItem2 = contactComboBox.getSelectionModel().getSelectedItem();
            PreparedStatement statement2 = connection.prepareStatement("SELECT Contact_ID FROM contacts WHERE Contact_Name = ?");
            statement2.setString(1, (String) selectedItem2);
            ResultSet result2 = statement2.executeQuery();
            result2.next();
            int contactID = result2.getInt("Contact_ID");

            LocalDate selectedStartDate = startDatePicker.getValue();
            LocalDate selectedEndDate = endDatePicker.getValue();
            LocalDateTime startDateTime = LocalDateTime.of(selectedStartDate, LocalTime.MIN);
            LocalDateTime endDateTime = LocalDateTime.of(selectedEndDate, LocalTime.MAX);

            stmt.setString(1, titleTextField.getText());
            stmt.setString(2, descriptionTextField.getText());
            stmt.setString(3, locationTextField.getText());
            stmt.setString(4, type);
            stmt.setTimestamp(5, Timestamp.valueOf(startDateTime));
            stmt.setTimestamp(6, Timestamp.valueOf(endDateTime));
            stmt.setString(7, customerIdTextField.getText());
            stmt.setString( 8, userIdTextField.getText());
            stmt.setInt(9, contactID);


            stmt.executeUpdate();
            clearForm();
            retrieveAppointmentsFromDB();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearForm() {
        titleTextField.clear();
        descriptionTextField.clear();
        locationTextField.clear();
        typeComboBox.getSelectionModel().clearSelection();
        contactComboBox.getSelectionModel().clearSelection();
        customerIdTextField.clear();
        userIdTextField.clear();
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
    }




}