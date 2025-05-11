package uk.ac.Thematics;

import static spark.Spark.*;
import org.json.JSONObject;

public class LoyaltyPointsWebService {

    public LoyaltyPointsWebService() {
        // Route to get loyalty points for a customer
        get("/api/loyalty-points/:customerId", (req, res) -> {
            try {
                int customerId = Integer.parseInt(req.params(":customerId"));
                Customer customer = Database.getCustomerByID(customerId);
                
                if (customer == null) {
                    res.status(404);
                    return "Customer not found";
                }
                
                JSONObject response = new JSONObject();
                response.put("customerId", customer.getCustomerId());
                response.put("loyaltyPoints", customer.getLoyaltyPoints());
                
                res.type("application/json");
                return response.toString();
            } catch (NumberFormatException e) {
                res.status(400);
                return "Invalid customer ID format";
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return "Internal server error: " + e.getMessage();
            }
        });

        // Route to add loyalty points to a customer
        post("/api/loyalty-points/:customerId/add", (req, res) -> {
            try {
                JSONObject requestBody = new JSONObject(req.body());
                int customerId = Integer.parseInt(req.params(":customerId"));
                
                // Validate the request
                if (!requestBody.has("pointsToAdd")) {
                    res.status(400);
                    return "Points to add must be specified";
                }
                
                int pointsToAdd = requestBody.getInt("pointsToAdd");
                if (pointsToAdd < 0) {
                    res.status(400);
                    return "Points to add must be a positive number";
                }
                
                // Get the customer
                Customer customer = Database.getCustomerByID(customerId);
                if (customer == null) {
                    res.status(404);
                    return "Customer not found";
                }
                
                // Add the points
                int currentPoints = customer.getLoyaltyPoints();
                int newPoints = currentPoints + pointsToAdd;
                customer.setLoyaltyPoints(newPoints);
                
                // Update the customer in the database
                boolean updated = Database.updateCustomer(customer);
                if (!updated) {
                    res.status(500);
                    return "Failed to update customer";
                }
                
                // Return the updated loyalty points
                JSONObject response = new JSONObject();
                response.put("customerId", customer.getCustomerId());
                response.put("previousPoints", currentPoints);
                response.put("pointsAdded", pointsToAdd);
                response.put("newPoints", newPoints);
                
                res.type("application/json");
                return response.toString();
            } catch (NumberFormatException e) {
                res.status(400);
                return "Invalid number format";
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return "Internal server error: " + e.getMessage();
            }
        });

        // Route to redeem loyalty points
        post("/api/loyalty-points/:customerId/redeem", (req, res) -> {
            try {
                JSONObject requestBody = new JSONObject(req.body());
                int customerId = Integer.parseInt(req.params(":customerId"));
                
                // Validate the request
                if (!requestBody.has("pointsToRedeem")) {
                    res.status(400);
                    return "Points to redeem must be specified";
                }
                
                int pointsToRedeem = requestBody.getInt("pointsToRedeem");
                if (pointsToRedeem <= 0) {
                    res.status(400);
                    return "Points to redeem must be greater than zero";
                }
                
                // Get the customer
                Customer customer = Database.getCustomerByID(customerId);
                if (customer == null) {
                    res.status(404);
                    return "Customer not found";
                }
                
                // Check if customer has enough points
                int currentPoints = customer.getLoyaltyPoints();
                if (currentPoints < pointsToRedeem) {
                    res.status(400);
                    return "Customer does not have enough points";
                }
                
                // Redeem the points
                int newPoints = currentPoints - pointsToRedeem;
                customer.setLoyaltyPoints(newPoints);
                
                // Update the customer in the database
                boolean updated = Database.updateCustomer(customer);
                if (!updated) {
                    res.status(500);
                    return "Failed to update customer";
                }
                
                // Return the updated loyalty points
                JSONObject response = new JSONObject();
                response.put("customerId", customer.getCustomerId());
                response.put("previousPoints", currentPoints);
                response.put("pointsRedeemed", pointsToRedeem);
                response.put("newPoints", newPoints);
                response.put("discountAmount", pointsToRedeem * 0.01); // $0.01 per point
                
                res.type("application/json");
                return response.toString();
            } catch (NumberFormatException e) {
                res.status(400);
                return "Invalid number format";
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return "Internal server error: " + e.getMessage();
            }
        });
    }
}
