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
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.cell.PropertyValueFactory;


public class CustomerController implements Initializable {


    public Button deleteCustomerButton;
    public Button updateCustomerButton;
    public Button addCustomerButton;
    public TextField phoneField;
    public TableColumn customerIDColumn;
    public TableColumn addressColumn;
    public TableColumn postalCodeColumn;
    public TableColumn phoneColumn;
    public TableColumn countryColumn;
    public TableColumn firstLevelDivisionColumn;
    public TableColumn customerNameColumn;
    @FXML
    private TextField nameField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField postalCodeField;
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
            System.out.println(selectedCountry);
        });
        // Populate the customer table with data from the database
        populateCustomerTable();
    }




    // Method to add a new customer to the database
    @FXML
    private void addCustomer() {
        try {

            // Prepare the insert statement
            PreparedStatement stmt = JDBC.getConnection().prepareStatement("INSERT INTO customers (customer_name, address, postal_code, phone, Division_ID) VALUES (?, ?, ?, ?, ?)");
            stmt.setString(1, nameField.getText());
            stmt.setString(2, addressField.getText());
            stmt.setString(3, postalCodeField.getText());
            stmt.setString(4, phoneField.getText());
            stmt.setString(5, firstLevelDivisionComboBox.getSelectionModel().getSelectedItem());
            // Execute the insert statement
            stmt.executeUpdate();

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

            // Prepare the update statement
            PreparedStatement stmt = JDBC.getConnection().prepareStatement("UPDATE customers SET customer_name = ?, address = ?, postal_code = ?, phone = ?, country = ?, Division_ID = ? WHERE customer_id = ?");
            stmt.setString(1, nameField.getText());
            stmt.setString(2, addressField.getText());
            stmt.setString(3, postalCodeField.getText());
            stmt.setString(4, phoneField.getText());
            stmt.setString(5, countryComboBox.getSelectionModel().getSelectedItem());
            stmt.setString(6, firstLevelDivisionComboBox.getSelectionModel().getSelectedItem());
            stmt.setInt(7, customerTable.getSelectionModel().getSelectedItem().getCustomerID());
            // Execute the update statement
            stmt.executeUpdate();


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

            // Get the selected customer from the table
            Customer selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
            // Prepare the delete statement for the customer's appointments
            PreparedStatement stmt1 = JDBC.getConnection().prepareStatement("DELETE FROM appointments WHERE customer_id = ?");
            stmt1.setInt(1, selectedCustomer.getCustomerID());
            // Execute the delete statement for the customer's appointments
            stmt1.executeUpdate();
            // Prepare the delete statement for the customer
            PreparedStatement stmt2 = JDBC.getConnection().prepareStatement("DELETE FROM customers WHERE customer_id = ?");
            stmt2.setInt(1, selectedCustomer.getCustomerID());
            // Execute the delete statement for the customer
            stmt2.executeUpdate();
            populateCustomerTable();




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

            // Prepare a statement to select all the countries from the database
            PreparedStatement stmt = JDBC.getConnection().prepareStatement("SELECT country FROM countries");
            // Execute the query and store the results in a ResultSet
            ResultSet rs = stmt.executeQuery();
            // Iterate through the results and add each country to the combo box
            while (rs.next()) {
                countryComboBox.getItems().add(rs.getString("country"));
            }

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
        Connection connection = JDBC.getConnection();
        try {
            // Connect to the database
            // Prepare the SQL statement to select the country_ID based on the selected country
            PreparedStatement statementCountryName = connection.prepareStatement("SELECT Country_ID FROM countries WHERE Country = ?");
            statementCountryName.setString(1, selectedCountry);
            ResultSet resultSet1 = statementCountryName.executeQuery();
            resultSet1.next();
            int countryID = resultSet1.getInt("Country_ID");

            // Prepare the SQL statement to select the first-level divisions based on the selected country_ID
            PreparedStatement statement = connection.prepareStatement("SELECT Division FROM first_level_divisions WHERE country_id = ?");
            statement.setInt(1, countryID);

            // Execute the query and store the results in a ResultSet
            ResultSet resultSet = statement.executeQuery();

            // Clear the contents of the first-level division combo box
            firstLevelDivisionComboBox.getItems().clear();

            // Iterate through the ResultSet and add the first-level divisions to the combo box
            while (resultSet.next()) {
                firstLevelDivisionComboBox.getItems().add(resultSet.getString("Division"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }






    private void populateCustomerTable() {
        try {
            // Prepare the select statement
            PreparedStatement stmt = JDBC.getConnection().prepareStatement("SELECT Customer_ID, Customer_Name, Address, Postal_Code, Phone, Division_ID FROM customers");
            // Execute the select statement
            ResultSet resultSet = stmt.executeQuery();
            // Clear the customer list
            customerList.clear();
            // Populate the customer list with data from the result set
            while (resultSet.next()) {
                customerList.add(new Customer(
                        resultSet.getInt("Customer_ID"),
                        resultSet.getString("Customer_Name"),
                        resultSet.getString("Address"),
                        resultSet.getString("Postal_Code"),
                        resultSet.getString("Phone"),
                        resultSet.getString("Division_ID")
                ));
            }
            // Close the connection
            // Set the items of the customer table to the customer list
            customerTable.setItems(customerList);

            // Set cell values for the table columns
            customerIDColumn.setCellValueFactory(new PropertyValueFactory<>("customerID"));
            customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("Name"));
            addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
            postalCodeColumn.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
            phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
            firstLevelDivisionColumn.setCellValueFactory(new PropertyValueFactory<>("FirstLevelDivision"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



}
