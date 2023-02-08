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
        try {
            Connection connection = JDBC.getConnection();

            Object selectedItem = typeComboBox.getSelectionModel().getSelectedItem();
            PreparedStatement statement = connection.prepareStatement("SELECT Type FROM appointments WHERE Type = ?");
            statement.setString(1, (String) selectedItem);
            ResultSet result = statement.executeQuery();
            String type = result.getString("Type");

            Object selectedItem2 = contactComboBox.getSelectionModel().getSelectedItem();
            PreparedStatement statement2 = connection.prepareStatement("SELECT Contact_Name FROM contacts WHERE Contact_Name = ?");
            statement.setString(1, (String) selectedItem);
            ResultSet result2 = statement.executeQuery();
            String contactName = result.getString("Contact");

            java.sql.Date sqlStartDate = java.sql.Date.valueOf(String.valueOf(startDatePicker));
            java.sql.Date sqlEndDate = java.sql.Date.valueOf(String.valueOf(endDatePicker));


            // Prepare the insert statement
            PreparedStatement stmt = JDBC.getConnection().prepareStatement("INSERT INTO appointments(Title, Description, Location, Type, Start, End, Customer_ID, User_ID) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");

            // Set the values for each column in the table
            stmt.setString(1, titleTextField.getText());
            stmt.setString(2, descriptionTextField.getText());
            stmt.setString(3, locationTextField.getText());
            stmt.setString(4, type);
            stmt.setDate(5, sqlStartDate);
            stmt.setDate(6, sqlEndDate);
            stmt.setString(7, customerIdTextField.getText());
            stmt.setString( 8, userIdTextField.getText());

            // Execute the insert statement
            stmt.executeUpdate();

            clearForm();


            // Refresh the table
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