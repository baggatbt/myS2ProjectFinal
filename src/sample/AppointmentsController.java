package sample;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 * This class contains all the methods for adding/deleting/updating and retrieving database information, as well as the behavior the the GUI
 */
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
    public TabPane appointmentsTabPane;
    public Tab allAppointmentsTab;
    public Tab appointmentsMonthTab;
    public Tab appointmentsWeekTab;
    public Button reportsButton;
    public Button backToCustomersButton;


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


    @Override
    /**
     * This initializes the combo boxes, as well as the appointment tables.
     * I used lambda expressions for the reportsButton and the backToCustomersButton in order to set listeners to my FXML buttons in a clean legible way, while also allowing me to remove the onAction parameter from my FXML so all the code is handled inside my class
     *
     */
    public void initialize(URL url, ResourceBundle resourceBundle) {
        appointmentIdTextField.setEditable(false);
        populateTypeComboBox();
        populateContactComboBox();
        populateTimePicker(startTimePicker);
        populateTimePicker(endTimePicker);
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
        int currentWeek = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
        // two lambda expressions
        reportsButton.setOnAction(actionEvent -> goToReports(actionEvent));
        backToCustomersButton.setOnAction(actionEvent -> backToCustomers(actionEvent));
        retrieveAppointmentsFromDB();

        appointmentsTabPane.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldTab, newTab) -> {
                    if (newTab == appointmentsMonthTab) {
                        retrieveAppointmentsByMonthFromDB(currentMonth);
                    } else if (newTab == appointmentsWeekTab) {
                        retrieveAppointmentsByWeekFromDB(currentWeek);
                    } else {
                        retrieveAppointmentsFromDB();
                    }
                }
        );
    }


    /**
     * This method gets all of the appointments from the database and adds them to the table view.
     */
    public void retrieveAppointmentsFromDB() {
        try {
            // Prepare the select statement
            PreparedStatement stmt = JDBC.getConnection().prepareStatement(
                    "SELECT Appointment_ID, Title, Description, Location, Contact_ID, Type, Start, End, Customer_ID, User_ID FROM appointments"
            );
            // Execute the select statement
            ResultSet resultSet = stmt.executeQuery();
            // Clear the appointment list
            appointmentList.clear();
            // Populate the appointment list with data from the result set
            while (resultSet.next()) {
                // Get the start date and time in the local timezone of the user's computer
                Timestamp startTimestamp = resultSet.getTimestamp("Start");
                LocalDateTime startLocalDateTime = startTimestamp.toLocalDateTime();
                ZonedDateTime startZonedDateTime = startLocalDateTime.atZone(ZoneId.systemDefault());
                ZonedDateTime localStartZonedDateTime = startZonedDateTime.withZoneSameInstant(ZoneId.systemDefault());
                Timestamp localStartTimestamp = Timestamp.valueOf(localStartZonedDateTime.toLocalDateTime());

                // Get the end date and time in the local timezone of the user's computer
                Timestamp endTimestamp = resultSet.getTimestamp("End");
                LocalDateTime endLocalDateTime = endTimestamp.toLocalDateTime();
                ZonedDateTime endZonedDateTime = endLocalDateTime.atZone(ZoneId.systemDefault());
                ZonedDateTime localEndZonedDateTime = endZonedDateTime.withZoneSameInstant(ZoneId.systemDefault());
                Timestamp localEndTimestamp = Timestamp.valueOf(localEndZonedDateTime.toLocalDateTime());

                appointmentList.add(new Appointment(
                        resultSet.getInt("Appointment_ID"),
                        resultSet.getString("Title"),
                        resultSet.getString("Description"),
                        resultSet.getString("Location"),
                        resultSet.getInt("Contact_ID"),
                        resultSet.getString("Type"),
                        localStartTimestamp, // use the local start timestamp
                        localEndTimestamp, // use the local end timestamp
                        resultSet.getInt("Customer_ID"),
                        resultSet.getInt("User_ID")
                ));
            }

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

    /**
     * This method gets all appointments by month from the database,
     * @param month is the parameter taken to specify which month the table should display data from
     */
    public void retrieveAppointmentsByMonthFromDB(int month) {
        try {
            // Prepare the select statement with a WHERE clause to filter by month
            PreparedStatement stmt = JDBC.getConnection().prepareStatement(
                    "SELECT Appointment_ID, Title, Description, Location, Contact_ID, Type, Start, End, Customer_ID, User_ID " +
                            "FROM appointments " +
                            "WHERE MONTH(Start) = ?"
            );
            stmt.setInt(1, month);

            // Execute the select statement
            ResultSet resultSet = stmt.executeQuery();

            // Clear the appointment list
            appointmentList.clear();

            // Populate the appointment list with data from the result set
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

            // Set the items of the appointment table to the appointment list
            appointmentsMonthTableView.setItems(appointmentList);

            // Set cell values for the table columns
            appointmentIdColumn1.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
            titleColumn1.setCellValueFactory(new PropertyValueFactory<>("title"));
            descriptionColumn1.setCellValueFactory(new PropertyValueFactory<>("description"));
            locationColumn1.setCellValueFactory(new PropertyValueFactory<>("location"));
            contactColumn1.setCellValueFactory(new PropertyValueFactory<>("contactID"));
            typeColumn1.setCellValueFactory(new PropertyValueFactory<>("type"));
            startDateColumn1.setCellValueFactory(new PropertyValueFactory<>("startDateTime"));
            endDateColumn1.setCellValueFactory(new PropertyValueFactory<>("endDateTime"));
            customerIdColumn1.setCellValueFactory(new PropertyValueFactory<>("customerID"));
            userIdColumn1.setCellValueFactory(new PropertyValueFactory<>("userID"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method gets all appointments by week from the database,
     * @param week is the parameter taken to specify which week the table should display data from
     */
    public void retrieveAppointmentsByWeekFromDB(int week) {
        try {
            // Prepare the select statement with a WHERE clause to filter by week
            PreparedStatement stmt = JDBC.getConnection().prepareStatement(
                    "SELECT Appointment_ID, Title, Description, Location, Contact_ID, Type, Start, End, Customer_ID, User_ID "
                            + "FROM appointments "
                            + "WHERE WEEK(Start, 1) = ?"
            );
            stmt.setInt(1, week);
            // Execute the select statement
            ResultSet resultSet = stmt.executeQuery();
            // Clear the appointment list
            appointmentList.clear();
            // Populate the appointment list with data from the result set
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
            // Set the items of the appointment table to the appointment list
            appointmentsWeekTableView.setItems(appointmentList);
            // Set cell values for the table columns
            appointmentIdColumn2.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
            titleColumn2.setCellValueFactory(new PropertyValueFactory<>("title"));
            descriptionColumn2.setCellValueFactory(new PropertyValueFactory<>("description"));
            locationColumn2.setCellValueFactory(new PropertyValueFactory<>("location"));
            contactColumn2.setCellValueFactory(new PropertyValueFactory<>("contactID"));
            typeColumn2.setCellValueFactory(new PropertyValueFactory<>("type"));
            startDateColumn2.setCellValueFactory(new PropertyValueFactory<>("startDateTime"));
            endDateColumn2.setCellValueFactory(new PropertyValueFactory<>("endDateTime"));
            customerIdColumn2.setCellValueFactory(new PropertyValueFactory<>("customerID"));
            userIdColumn2.setCellValueFactory(new PropertyValueFactory<>("userID"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method populates the Type combo box with all types found in the database.
     */
    public void populateTypeComboBox() {
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

    /**
     * This method populates the contact combo box with all contact's found in the database
     */
    public void populateContactComboBox() {
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
    /**
     * This method allows the user to add a new appointment to the database, as well as the table view.
     */
    @FXML
    public void addAppointment() {
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
            LocalTime businessHoursStart = LocalTime.of(8, 0);
            LocalTime businessHoursEnd = LocalTime.of(22, 0);
            ZoneId est = ZoneId.of("America/New_York");

            startDateTime = LocalDateTime.of(startDate, startTime);
            endDateTime = LocalDateTime.of(endDate, endTime);

            ZonedDateTime startZoned = ZonedDateTime.of(startDateTime, est);
            ZonedDateTime endZoned = ZonedDateTime.of(endDateTime, est);

            if (startZoned.toLocalTime().isBefore(businessHoursStart) || endZoned.toLocalTime().isAfter(businessHoursEnd)) {
                // Display an error message to the user
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Invalid appointment time");
                alert.setContentText("Appointments can only be scheduled during business hours (8:00 a.m. to 10:00 p.m. EST, including weekends).");
                alert.showAndWait();
                return;
            }
            try (
                    PreparedStatement stmt3 = connection.prepareStatement(
                            "SELECT * FROM appointments WHERE Customer_ID = ? AND ((Start >= ? AND Start < ?) OR (End > ? AND End <= ?) OR (Start <= ? AND End >= ?))")) {

                // Set the parameters for the query
                stmt3.setInt(1, customerID);
                stmt3.setTimestamp(2, Timestamp.valueOf(startDateTime));
                stmt3.setTimestamp(3, Timestamp.valueOf(endDateTime));
                stmt3.setTimestamp(4, Timestamp.valueOf(startDateTime));
                stmt3.setTimestamp(5, Timestamp.valueOf(endDateTime));
                stmt3.setTimestamp(6, Timestamp.valueOf(startDateTime));
                stmt3.setTimestamp(7, Timestamp.valueOf(endDateTime));

                ResultSet rs = stmt3.executeQuery();


                if (rs.next()) {
                    // Display an error message to the user
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Overlapping appointment");
                    alert.setContentText("The customer already has an appointment scheduled during the requested time.");
                    alert.showAndWait();
                    return;
                }

            } catch (SQLException e) {
                e.printStackTrace();
                // Display an error message to the user
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Database error");
                alert.setContentText("An error occurred while checking for overlapping appointments. Please try again later.");
                alert.showAndWait();
                return;
            }


            stmt.executeUpdate();
            clearForm();

            // Refresh the appointment table view
            retrieveAppointmentsFromDB();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method clears the form of any inputs or text
     */
    public void clearForm() {
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

    /**
     * This method is responsible for populating the timepicker with times for the user to select
     * @param timePicker takes in the timePicker as a parameter
     */
    public static void populateTimePicker(ComboBox<String> timePicker) {
        timePicker.getItems().addAll(
                "12:00 AM", "01:00 AM", "02:00 AM", "03:00 AM", "04:00 AM", "05:00 AM", "06:00 AM", "07:00 AM", "08:00 AM", "09:00 AM", "10:00 AM", "11:00 AM",
                "12:00 PM", "01:00 PM", "02:00 PM", "03:00 PM", "04:00 PM", "05:00 PM", "06:00 PM", "07:00 PM", "08:00 PM", "09:00 PM", "10:00 PM", "11:00 PM"
        );
    }

    /**
     * This method allows the user to delete an appointment from the database.
     */
    @FXML
    public void deleteAppointment() {
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

    /**
     * This method allows the user to update appointment information in the database.
     */
    @FXML
    public void updateAppointment() {
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

            startTime = LocalTime.parse((String) startTimePicker.getSelectionModel().getSelectedItem(), DateTimeFormatter.ofPattern("hh:mm a"));
            endTime = LocalTime.parse((String) endTimePicker.getSelectionModel().getSelectedItem(), DateTimeFormatter.ofPattern("hh:mm a"));

            if (startTime.isBefore(LocalTime.of(8, 0)) || endTime.isAfter(LocalTime.of(22, 0))) {
                // Display error message
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Scheduling Error");
                alert.setContentText("The appointment time must be between 8:00 a.m. and 10:00 p.m. EST, including weekends.");
                alert.showAndWait();
                return;
            }

            try (
                    PreparedStatement stmt4 = connection.prepareStatement(
                            "SELECT * FROM appointments WHERE Customer_ID = ? AND ((Start >= ? AND Start < ?) OR (End > ? AND End <= ?) OR (Start <= ? AND End >= ?))")) {

                // Set the parameters for the query
                stmt4.setInt(1, customerID);
                stmt4.setTimestamp(2, Timestamp.valueOf(startDateTime));
                stmt4.setTimestamp(3, Timestamp.valueOf(endDateTime));
                stmt4.setTimestamp(4, Timestamp.valueOf(startDateTime));
                stmt4.setTimestamp(5, Timestamp.valueOf(endDateTime));
                stmt4.setTimestamp(6, Timestamp.valueOf(startDateTime));
                stmt4.setTimestamp(7, Timestamp.valueOf(endDateTime));

                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    // Display an error message to the user
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Overlapping appointment");
                    alert.setContentText("The customer already has an appointment scheduled during the requested time.");
                    alert.showAndWait();
                    return;
                }

            } catch (SQLException e) {
                e.printStackTrace();
                // Display an error message to the user
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Database error");
                alert.setContentText("An error occurred while checking for overlapping appointments. Please try again later.");
                alert.showAndWait();
                return;
            }


            // Execute the update statement
            stmt.executeUpdate();
            clearForm();
            // Refresh the appointment table view
            retrieveAppointmentsFromDB();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method switches the current form to the reportForm
     * @param actionEvent takes in an actionEvent as a parameter
     */
    public void goToReports(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("reportForm.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This allows the user to change the form back to the customerRecordForm
     * @param actionEvent takes in an actionEvent as a parameter.
     */
    public void backToCustomers(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("customerRecordForm.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


