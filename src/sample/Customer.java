package sample;

public class Customer {
    private int customer_id;
    private String name;
    private String address;
    private String postal_code;
    private String phone;
    private String first_level_division;
    private String country;
    public Customer(int customer_id, String name, String address, String postal_code, String phone, String first_level_division, String country) {
        this.customer_id = customer_id;
        this.name = name;
        this.address = address;
        this.postal_code = postal_code;
        this.phone = phone;
        this.first_level_division = first_level_division;
        this.country = country;
    }
    // Getters
    public int getCustomerId() {
        return this.customer_id;
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
    public String getCountry() {
        return this.country;
    }
    // Setters
    public void setCustomerId(int customer_id) {
        this.customer_id = customer_id;
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
    public void setCountry(String country) {
        this.country = country;
    }
}

