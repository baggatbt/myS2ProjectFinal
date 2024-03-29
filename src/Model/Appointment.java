package Model;

import java.sql.Timestamp;

/**
 * This class contains the parameters and model for the appointments, as well as the necessary getters and setters.
 */
public class Appointment {
    private int appointmentID;
    private String title;
    private String description;
    private String location;
    private int contactID;
    private String type;
    private Timestamp startDateTime;
    private Timestamp endDateTime;
    private int customerID;
    private int userID;

    public Appointment(int appointmentID,
                       String title, String description, String location,
                       int contact, String type, Timestamp start,
                       Timestamp end, int customerID, int userID) {
    this.appointmentID = appointmentID;
    this.title = title;
    this.description = description;
    this.location = location;
    this.contactID = contact;
    this.type = type;
    this.startDateTime = start;
    this.endDateTime = end;
    this.customerID = customerID;
    this.userID = userID;
    }

    // Getter methods
    public int getAppointmentID() {
        return appointmentID;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public int getContactID() {
        return contactID;
    }

    public String getType() {
        return type;
    }

    public Timestamp getStartDateTime() {
        return startDateTime;
    }

    public Timestamp getEndDateTime() {
        return endDateTime;
    }

    public int getCustomerID() {
        return customerID;
    }

    public int getUserID() {
        return userID;
    }

    // Setter methods
    public void setAppointmentID(int appointmentID) {
        this.appointmentID = appointmentID;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setContactID(int contact) {
        this.contactID = contact;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setStartDateTime(Timestamp startDateTime) {
        this.startDateTime = startDateTime;
    }

    public void setEndDateTime(Timestamp endDateTime) {
        this.endDateTime = endDateTime;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }
}
