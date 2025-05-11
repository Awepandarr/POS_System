
package uk.ac.Thematics;

import org.json.JSONObject;

public class Customer {
    private int customerId;
    private String customerName;
    private String customerEmail;
    private String contact;
    private String address;  // Add address field
    private int loyaltyPoints;

    // Constructor with all fields
    public Customer(int customerId, String customerName, String customerEmail, String contact, String address, int loyaltyPoints) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.contact = contact;
        this.address = address;  // Initialize address
        this.loyaltyPoints = loyaltyPoints;
    }

    // Getters and Setters
    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void setLoyaltyPoints(int loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }

    // Method to convert Customer to JSON format for API responses
    public JSONObject toJSON() {
        JSONObject customerJson = new JSONObject();
        customerJson.put("customerId", this.customerId);
        customerJson.put("customerName", this.customerName);
        customerJson.put("customerEmail", this.customerEmail);
        customerJson.put("contact", this.contact);
        customerJson.put("address", this.address);  // Include address in the JSON response
        customerJson.put("loyaltyPoints", this.loyaltyPoints);
        return customerJson;
    }
}
