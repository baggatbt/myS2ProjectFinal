package sample;

/**
 * This class contains the logic, parameters and methods for the Customer object.
 */
public class Customer {
    private int customerID;
    private String name;
    private String address;
    private String postal_code;
    private String phone;
    private String first_level_division;


    public Customer(int customerID, String name, String address, String postal_code, String phone, String first_level_division) {
        this.customerID = customerID;
        this.name = name;
        this.address = address;
        this.postal_code = postal_code;
        this.phone = phone;
        this.first_level_division = first_level_division;
    }

    // Getters
    public int getCustomerID() {
        return this.customerID;
    }

    public String getName() {
        return this.name;
    }

    public String getAddress() {
        return this.address;
    }

    public String getPostalCode() {
        return this.postal_code;
    }

    public String getPhone() {
        return this.phone;
    }

    public String getFirstLevelDivision() {
        return this.first_level_division;
    }

    // Setters
    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPostalCode(String postal_code) {
        this.postal_code = postal_code;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setFirstLevelDivision(String first_level_division) {
        this.first_level_division = first_level_division;
    }
}

