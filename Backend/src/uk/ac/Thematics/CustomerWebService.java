package uk.ac.Thematics;

import org.json.JSONObject;
import static spark.Spark.*;

import java.util.List;
import java.util.stream.Collectors;

public class CustomerWebService {

    public CustomerWebService() {
    	
        get("/api/customers", (req, res) -> {
            // Fetch all customers from the database
            List<Customer> customers = Database.getAllCustomers();  // Ensure this method returns a List<Customer>
            if (customers == null || customers.isEmpty()) {
                res.status(404);
                return "No customers found";
            }

            // Return the list of customers as a JSON array
            res.type("application/json");
            return customers.stream()
                            .map(customer -> customer.toJSON().toString())  // Convert each customer to JSON
                            .collect(Collectors.joining(",", "[", "]"));  // Join them into a JSON array
        });
        // Route to get customer by ID
        get("/api/customer/:customerId", (req, res) -> {
            int customerId = Integer.parseInt(req.params(":customerId"));
            Customer customer = Database.getCustomerByID(customerId);
            if (customer == null) {
                res.status(404);
                return "Customer not found";
            }
            res.type("application/json");
            return customer.toJSON().toString();
        });

        // Route to create a new customer
        post("/api/customer", (req, res) -> {
            JSONObject requestBody = new JSONObject(req.body());
            Customer customer = new Customer(
                0, // Will be set by the database
                requestBody.getString("firstName") + " " + requestBody.getString("lastName"),  // Combining firstName and lastName
                requestBody.getString("email"),
                requestBody.getString("phoneNumber"),
                requestBody.getString("address"),
                0 // Default loyaltyPoints to 0
            );

            boolean success = Database.insertCustomer(customer);
            if (success) {
                res.status(201);
                res.type("application/json");
                return customer.toJSON().toString();
            } else {
                res.status(400);
                return "Failed to create customer";
            }
        });


        // Route to update an existing customer
        put("/api/customer/:customerId", (req, res) -> {
            JSONObject requestBody = new JSONObject(req.body());
            int customerId = Integer.parseInt(req.params(":customerId"));
            
            // Create a customer object with the updated fields
            Customer customer = new Customer(
                customerId, // Customer ID from the URL parameter
                requestBody.getString("firstName") + " " + requestBody.getString("lastName"), // Combine first and last name
                requestBody.getString("email"),
                requestBody.getString("phoneNumber"),
                requestBody.getString("address"),
                requestBody.optInt("loyaltyPoints", 0) // Use loyaltyPoints from the request body or default to 0
            );

            boolean success = Database.updateCustomer(customer);
            if (success) {
                res.status(200); // 200 OK
                res.type("application/json");
                return customer.toJSON().toString(); // Return the updated customer in JSON format
            } else {
                res.status(404); // 404 Not Found
                return "Customer not found"; // Return an error message if customer is not found
            }
        });


        // Route to delete a customer by ID
        delete("/api/customer/:customerId", (req, res) -> {
            int customerId = Integer.parseInt(req.params(":customerId"));
            boolean success = Database.deleteCustomerByID(customerId);
            if (success) {
                res.status(200);
                return "Customer deleted successfully";
            } else {
                res.status(404);
                return "Customer not found";
            }
        });
    }
}
