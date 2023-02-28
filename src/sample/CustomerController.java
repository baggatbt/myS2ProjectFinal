package sample;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
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
import javafx.scene.control.Button;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 * This class contains the logic for adding, deleting, updating and retriving customer information from the database.
 */
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

    /**
     * This method intializes the GUI combo boxes, as well as populates the table with customer data.
     * @param url takes a url parameter
     * @param resourceBundle takes a resource bundle parameter
     */
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


    /**
     * This method contains the logic for adding a new customer to the database.
     */
    @FXML
    public void addCustomer() {
        try {
            Connection connection = JDBC.getConnection();

            Object selectedItem = firstLevelDivisionComboBox.getSelectionModel().getSelectedItem();
            PreparedStatement statement = connection.prepareStatement("SELECT Division_ID FROM first_level_divisions WHERE Division = ?");
            statement.setString(1, (String) selectedItem);
            ResultSet result = statement.executeQuery();
            result.next();
            int divisionID = result.getInt("Division_ID");

            // Prepare the insert statement
            PreparedStatement stmt = JDBC.getConnection().prepareStatement("INSERT INTO customers (customer_name, address, postal_code, phone, Division_ID) VALUES (?, ?, ?, ?, ?)");

            // Set the values for each column in the table
            stmt.setString(1, nameField.getText());
            stmt.setString(2, addressField.getText());
            stmt.setString(3, postalCodeField.getText());
            stmt.setString(4, phoneField.getText());
            stmt.setInt(5, divisionID);

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




    /**
     * This method contains the logic to update an existing customer in the database
     */
    @FXML
    public void updateCustomer() {
        try {
            Connection connection = JDBC.getConnection();
            // Prepare the update statement
            PreparedStatement stmt = JDBC.getConnection().prepareStatement("UPDATE customers SET customer_name = ?, address = ?, postal_code = ?, phone = ?, Division_ID = ? WHERE customer_id = ?");
            // Set the values for each column in the table
            Customer selectedCustomer = customerTable.getSelectionModel().getSelectedItem();

            stmt.setString(1, nameField.getText());
            stmt.setString(2, addressField.getText());
            stmt.setString(3, postalCodeField.getText());
            stmt.setString(4, phoneField.getText());

            Object selectedItem = firstLevelDivisionComboBox.getSelectionModel().getSelectedItem();
            PreparedStatement statement = connection.prepareStatement("SELECT Division_ID FROM first_level_divisions WHERE Division = ?");
            statement.setString(1, (String) selectedItem);
            ResultSet result = statement.executeQuery();
            result.next();
            int divisionID = result.getInt("Division_ID");

            stmt.setInt(5, divisionID);
            stmt.setInt(6, selectedCustomer.getCustomerID());
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



    /**
     * This method contains the logic to delete a customer from the database
     */
    @FXML
    public void deleteCustomer() {
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

   public void showDeleteMessage() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Delete Customer");
        alert.setHeaderText(null);
        alert.setContentText("Customer record and appointments have been deleted.");
        alert.showAndWait();
    }


    public void populateCountryComboBox() {
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

    /**
     * This method contains the logic to clear the form of any input or text.
     */
    public void clearForm() {
        // Clear the input fields
        nameField.clear();
        addressField.clear();
        postalCodeField.clear();
        // Clear the selected items in the combo boxes
        countryComboBox.getSelectionModel().clearSelection();
        firstLevelDivisionComboBox.getSelectionModel().clearSelection();
    }


    /**
     * This method contains the logic to populate the division combo box with items to select.
     * @param selectedCountry takes in the selected country as a parameter
     */
    public void populateFirstLevelDivisionComboBox(String selectedCountry) {
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


    /**
     * This method populates the customer table with customer information from the database
     */
    public void populateCustomerTable() {
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


    /**
     * This method changes the current form to the appointments GUI
     * @param actionEvent takes in an actionEvent as a paramter
     * @throws IOException throws an IOexception
     */
    public void goToAppointments(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("appointmentsForm.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}

