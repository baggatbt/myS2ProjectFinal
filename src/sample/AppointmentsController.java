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
import java.util.Optional;
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
    public ComboBox startTimePicker;
    public ComboBox endTimePicker;
    public Button updateButton;
    public TableView appointmentsWeekTableView;
    public TableView appointmentsMonthTableView;
    public TableColumn appointmentIdColumn1;
    public TableColumn descriptionColumn1;
    public TableColumn titleColumn1;
    public TableColumn locationColumn1;
    public TableColumn contactColumn1;
    public TableColumn typeColumn1;
    public TableColumn startDateColumn1;
    public TableColumn endDateColumn1;
    public TableColumn customerIdColumn1;
    public TableColumn userIdColumn1;
    public TableColumn appointmentIdColumn2;
    public TableColumn titleColumn2;
    public TableColumn descriptionColumn2;
    public TableColumn locationColumn2;
    public TableColumn contactColumn2;
    public TableColumn typeColumn2;
    public TableColumn startDateColumn2;
    public TableColumn endDateColumn2;
    public TableColumn customerIdColumn2;
    public TableColumn userIdColumn2;

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
        populateTimePicker(startTimePicker);
        populateTimePicker(endTimePicker);
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
             PreparedStatement stmt = connection.prepareStatement("INSERT INTO appointments(Title, Description, Location, Type, Start, End, Customer_ID, User_ID, Contact_ID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            String title = titleTextField.getText();
            String description = descriptionTextField.getText();
            String location = locationTextField.getText();
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
            int customerID = Integer.parseInt(customerIdTextField.getText());
            int userID = Integer.parseInt(userIdTextField.getText());

            // Concatenate start date and time into one variable
            LocalDate startDate = startDatePicker.getValue();
            LocalTime startTime = LocalTime.parse((String) startTimePicker.getSelectionModel().getSelectedItem(), DateTimeFormatter.ofPattern("hh:mm a"));
            LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);

            LocalDate endDate = endDatePicker.getValue();
            LocalTime endTime = LocalTime.parse((String) endTimePicker.getSelectionModel().getSelectedItem(), DateTimeFormatter.ofPattern("hh:mm a"));
            LocalDateTime endDateTime = LocalDateTime.of(endDate, endTime);

            // Set parameter values for the insert statement
            stmt.setString(1, title);
            stmt.setString(2, description);
            stmt.setString(3, location);
            stmt.setString(4, type);
            stmt.setTimestamp(5, Timestamp.valueOf(startDateTime));
            stmt.setTimestamp(6, Timestamp.valueOf(endDateTime));
            stmt.setInt(7, customerID);
            stmt.setInt(8, userID);
            stmt.setInt(9, contactID);

            // Execute the insert statement
            stmt.executeUpdate();
            clearForm();

            // Refresh the appointment table view
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

    public static void populateTimePicker(ComboBox<String> timePicker) {
        timePicker.getItems().addAll(
                "12:00 AM", "01:00 AM", "02:00 AM", "03:00 AM", "04:00 AM", "05:00 AM", "06:00 AM", "07:00 AM", "08:00 AM", "09:00 AM", "10:00 AM", "11:00 AM",
                "12:00 PM", "01:00 PM", "02:00 PM", "03:00 PM", "04:00 PM", "05:00 PM", "06:00 PM", "07:00 PM", "08:00 PM", "09:00 PM", "10:00 PM", "11:00 PM"
        );
    }

    @FXML
    private void deleteAppointment() {
        try {
// Get the selected appointment from the table
            Appointment selectedAppointment = (Appointment) appointmentsTableView.getSelectionModel().getSelectedItem();
            if (selectedAppointment != null) {
// Ask the user for confirmation
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation Dialog");
                alert.setHeaderText("Delete Appointment");
                alert.setContentText(String.format("Are you sure you want to cancel the appointment with ID %d (%s)?", selectedAppointment.getAppointmentID(), selectedAppointment.getType()));
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    // Prepare the delete statement for the appointment
                    PreparedStatement stmt1 = JDBC.getConnection().prepareStatement("DELETE FROM appointments WHERE appointment_id = ?");
                    stmt1.setInt(1, selectedAppointment.getAppointmentID());
                    // Execute the delete statement for the appointment
                    stmt1.executeUpdate();
                    retrieveAppointmentsFromDB();
                }
            } else {
                // No appointment selected
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning Dialog");
                alert.setHeaderText("No Appointment Selected");
                alert.setContentText("Please select an appointment to delete.");
                alert.showAndWait();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void updateAppointment () {
        try (Connection connection = JDBC.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT Type FROM appointments WHERE Type = ?");
             PreparedStatement stmt = connection.prepareStatement("UPDATE appointments SET Title = ?, Description = ?, Location = ?, Type = ?, Start = ?, End = ?, Customer_ID = ?, User_ID = ?, Contact_ID = ? WHERE Appointment_ID = ?")) {
            // Get the selected appointment from the table
            Appointment selectedAppointment = (Appointment) appointmentsTableView.getSelectionModel().getSelectedItem();
            // Get the new values from the input fields
            String title = titleTextField.getText();
            String description = descriptionTextField.getText();
            String location = locationTextField.getText();
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
            int customerID = Integer.parseInt(customerIdTextField.getText());
            int userID = Integer.parseInt(userIdTextField.getText());
            // Concatenate start date and time into one variable
            LocalDate startDate = startDatePicker.getValue();
            LocalTime startTime = LocalTime.parse((String) startTimePicker.getSelectionModel().getSelectedItem(), DateTimeFormatter.ofPattern("hh:mm a"));
            LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);
            LocalDate endDate = endDatePicker.getValue();
            LocalTime endTime = LocalTime.parse((String) endTimePicker.getSelectionModel().getSelectedItem(), DateTimeFormatter.ofPattern("hh:mm a"));
            LocalDateTime endDateTime = LocalDateTime.of(endDate, endTime);
            // Set parameter values for the update statement
            stmt.setString(1, title);
            stmt.setString(2, description);
            stmt.setString(3, location);
            stmt.setString(4, type);
            stmt.setTimestamp(5, Timestamp.valueOf(startDateTime));
            stmt.setTimestamp(6, Timestamp.valueOf(endDateTime));
            stmt.setInt(7, customerID);
            stmt.setInt(8, userID);
            stmt.setInt(9, contactID);
            stmt.setInt(10, selectedAppointment.getAppointmentID());
            // Execute the update statement
            stmt.executeUpdate();
            clearForm();
            // Refresh the appointment table view
            retrieveAppointmentsFromDB();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}

