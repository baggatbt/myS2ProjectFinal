package sample;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class CustomerController implements Initializable {

    @FXML
    private TextField nameField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField postalCodeField;
    @FXML
    private TextField phoneNumberField;
    @FXML
    private ComboBox<String> countryComboBox;
    @FXML
    private ComboBox<String> firstLevelDivisionComboBox;
    @FXML
    private TableView<Customer> customerTable;

    private ObservableList<Customer> customerList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Populate the country combo box with data from the database
        populateCountryComboBox();
        // Populate the first level division combo box with data filtered by the user's selection of a country
        countryComboBox.setOnAction(event -> {
            String selectedCountry = countryComboBox.getSelectionModel().getSelectedItem();
            populateFirstLevelDivisionComboBox(selectedCountry);
        });
        // Populate the customer table with data from the database
        populateCustomerTable();
    }

    // Method to add a new customer to the database
    @FXML
    private void addCustomer() {
        try {
            JDBC.makeConnection();
            // Prepare the insert statement
            PreparedStatement stmt = JDBC.getConnection().prepareStatement("INSERT INTO customers (name, address, postal_code, phone_number, country, first_level_division) VALUES (?, ?, ?, ?, ?, ?)");
            stmt.setString(1, nameField.getText());
            stmt.setString(2, addressField.getText());
            stmt.setString(3, postalCodeField.getText());
            stmt.setString(4, phoneNumberField.getText());
            stmt.setString(5, countryComboBox.getSelectionModel().getSelectedItem());
            stmt.setString(6, firstLevelDivisionComboBox.getSelectionModel().getSelectedItem());
            // Execute the insert statement
            stmt.executeUpdate();
            // Close the connection
            JDBC.closeConnection();
            // Clear the form fields
            clearForm();
            // Refresh the customer table
            populateCustomerTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to update an existing customer in the database
    @FXML
    private void updateCustomer() {
        try {
            JDBC.makeConnection();
            // Prepare the update statement
            PreparedStatement stmt = JDBC.getConnection().prepareStatement("UPDATE customers SET name = ?, address = ?, postal_code = ?, phone_number = ?, country = ?, first_level_division = ? WHERE customer_id = ?");
            stmt.setString(1, nameField.getText());
            stmt.setString(2, addressField.getText());
            stmt.setString(3, postalCodeField.getText());
            stmt.setString(4, phoneNumberField.getText());
            stmt.setString(5, countryComboBox.getSelectionModel().getSelectedItem());
            stmt.setString(6, firstLevelDivisionComboBox.getSelectionModel().getSelectedItem());
            stmt.setInt(7, customerTable.getSelectionModel().getSelectedItem().getCustomerId());
            // Execute the update statement
            stmt.executeUpdate();
            // Close the connection
            JDBC.closeConnection();
            // Clear the form fields
            clearForm();
            // Refresh the customer table
            populateCustomerTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to delete a customer from the database
    @FXML
    private void deleteCustomer() {
        try {
            JDBC.makeConnection();
            // Get the selected customer from the table
            Customer selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
            // Prepare the delete statement for the customer's appointments
            PreparedStatement stmt1 = JDBC.getConnection().prepareStatement("DELETE FROM appointments WHERE customer_id = ?");
            stmt1.setInt(1, selectedCustomer.getCustomerId());
            // Execute the delete statement for the customer's appointments
            stmt1.executeUpdate();
            // Prepare the delete statement for the customer
            PreparedStatement stmt2 = JDBC.getConnection().prepareStatement("DELETE FROM customers WHERE customer_id = ?");
            stmt2.setInt(1, selectedCustomer.getCustomerId());
            // Execute the delete statement for the customer
            stmt2.executeUpdate();
            // Close the connection
            JDBC.closeConnection();
            // Update the customer table
            populateCustomerTable();
            // Show a message to the user
            showDeleteMessage();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showDeleteMessage() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Delete Customer");
        alert.setHeaderText(null);
        alert.setContentText("Customer record and appointments have been deleted.");
        alert.showAndWait();
    }


    private void populateCountryComboBox() {
        try {
            // Make a connection to the database
            JDBC.makeConnection();
            // Prepare a statement to select all the countries from the database
            PreparedStatement stmt = JDBC.getConnection().prepareStatement("SELECT country_name FROM countries");
            // Execute the query and store the results in a ResultSet
            ResultSet rs = stmt.executeQuery();
            // Iterate through the results and add each country to the combo box
            while (rs.next()) {
                countryComboBox.getItems().add(rs.getString("country_name"));
            }
            // Close the connection
            JDBC.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearForm() {
        // Clear the input fields
        nameField.clear();
        addressField.clear();
        postalCodeField.clear();
        // Clear the selected items in the combo boxes
        countryComboBox.getSelectionModel().clearSelection();
        firstLevelDivisionComboBox.getSelectionModel().clearSelection();
    }


    private void populateFirstLevelDivisionComboBox(String selectedCountry) {
        // Clear the current items in the combo box
        firstLevelDivisionComboBox.getItems().clear();
        try {
            // Connect to the database
            Connection connection = JDBC.getConnection();
            // Prepare the SQL statement to select the first-level divisions based on the selected country
            PreparedStatement statement = connection.prepareStatement("SELECT first_level_division FROM divisions WHERE country = ?");
            statement.setString(1, selectedCountry);
            // Execute the query and store the results in a ResultSet
            ResultSet resultSet = statement.executeQuery();
            // Iterate through the ResultSet and add the first-level divisions to the combo box
            while (resultSet.next()) {
                firstLevelDivisionComboBox.getItems().add(resultSet.getString("first_level_division"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void populateCustomerTable() {
        try {
            // Make a connection to the database
            JDBC.makeConnection();
            // Prepare a statement to select all the customers from the database
            PreparedStatement stmt = JDBC.getConnection().prepareStatement("SELECT * FROM customers");
            // Execute the query and store the results in a ResultSet
            ResultSet rs = stmt.executeQuery();
            // Clear the customer list
            customerList.clear();
            // Iterate through the results and create a new Customer object for each row
            while (rs.next()) {
                customerList.add(new Customer(rs.getInt("customer_id"), rs.getString("name"), rs.getString("address"),
                        rs.getString("postal_code"), rs.getString("phone"), rs.getString("first_level_division"),
                        rs.getString("country")));
            }
            // Close the connection
            JDBC.closeConnection();
            // Set the items of the customer table to the customer list
            customerTable.setItems(customerList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
