package uk.ac.Thematics;

import static spark.Spark.*;
import org.json.JSONObject;
import org.json.JSONArray;
import java.math.BigDecimal;
import java.util.List;

public class OrderItemWebService {

    public OrderItemWebService() {
        // Route to get all order items
        get("/api/orderitems", (req, res) -> {
            try {
                // Get all order items from the database
                List<OrderItem> orderItems = Database.getAllOrderItems();
                
                // Create a JSON array to hold the order items
                JSONArray orderItemsArray = new JSONArray();
                
                // Convert each order item to JSON and add to the array
                for (OrderItem orderItem : orderItems) {
                    JSONObject orderItemJson = new JSONObject();
                    orderItemJson.put("orderItemId", orderItem.getOrderItemId());
                    orderItemJson.put("orderId", orderItem.getOrderId());
                    orderItemJson.put("productId", orderItem.getProductId());
                    orderItemJson.put("quantity", orderItem.getQuantity());
                    orderItemJson.put("price", orderItem.getPrice());
                    orderItemJson.put("subtotal", orderItem.getSubtotal());
                    
                    // Try to get product name if available
                    Product product = Database.getProductByID(orderItem.getProductId());
                    if (product != null) {
                        orderItemJson.put("productName", product.getName());
                    }
                    
                    orderItemsArray.put(orderItemJson);
                }
                
                res.status(200);
                res.type("application/json");
                return orderItemsArray.toString();
                
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return "{\"error\": \"Server error: " + e.getMessage() + "\"}";
            }
        });

        // Route to get order items by order ID
        get("/api/order/:orderId/items", (req, res) -> {
            try {
                int orderId = Integer.parseInt(req.params(":orderId"));
                
                // Get order items for this specific order
                List<OrderItem> orderItems = Database.getOrderItemsByOrderID(orderId);
                
                // Create a JSON array to hold the order items
                JSONArray orderItemsArray = new JSONArray();
                
                // Convert each order item to JSON and add to the array
                for (OrderItem orderItem : orderItems) {
                    JSONObject orderItemJson = new JSONObject();
                    orderItemJson.put("orderItemId", orderItem.getOrderItemId());
                    orderItemJson.put("orderId", orderItem.getOrderId());
                    orderItemJson.put("productId", orderItem.getProductId());
                    orderItemJson.put("quantity", orderItem.getQuantity());
                    orderItemJson.put("price", orderItem.getPrice());
                    orderItemJson.put("subtotal", orderItem.getSubtotal());
                    
                    // Try to get product name if available
                    Product product = Database.getProductByID(orderItem.getProductId());
                    if (product != null) {
                        orderItemJson.put("productName", product.getName());
                    }
                    
                    orderItemsArray.put(orderItemJson);
                }
                
                // If no items found, return an empty array instead of null
                if (orderItems.isEmpty()) {
                    res.status(404);
                    return "{\"error\": \"No order items found for this order\"}";
                }
                
                res.status(200);
                res.type("application/json");
                return orderItemsArray.toString();
                
            } catch (NumberFormatException e) {
                res.status(400);
                return "{\"error\": \"Invalid order ID format\"}";
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return "{\"error\": \"Server error: " + e.getMessage() + "\"}";
            }
        });

        // Route to create a new order item
        post("/api/orderitem", (req, res) -> {
            try {
                // Parse the request body
                JSONObject requestBody = new JSONObject(req.body());
                
                // Create a new OrderItem
                OrderItem orderItem = new OrderItem(
                    0,  // OrderItemId will be set by the database
                    requestBody.getInt("orderId"),
                    requestBody.getInt("productId"),
                    requestBody.getInt("quantity"),
                    new BigDecimal(requestBody.get("price").toString()),
                    new BigDecimal(requestBody.get("subtotal").toString())
                );
                
                // Insert the order item into the database
                boolean success = Database.insertOrderItem(orderItem);
                if (!success) {
                    res.status(400);
                    return "{\"error\": \"Failed to create order item\"}";
                }
                
                // Prepare the response JSON
                JSONObject responseJson = new JSONObject();
                responseJson.put("orderItemId", orderItem.getOrderItemId());
                responseJson.put("orderId", orderItem.getOrderId());
                responseJson.put("productId", orderItem.getProductId());
                responseJson.put("quantity", orderItem.getQuantity());
                responseJson.put("price", orderItem.getPrice());
                responseJson.put("subtotal", orderItem.getSubtotal());
                
                res.status(201);
                res.type("application/json");
                return responseJson.toString();
                
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return "{\"error\": \"Server error: " + e.getMessage() + "\"}";
            }
        });

        // Route to get a specific order item by its ID
        get("/api/orderitem/:orderItemId", (req, res) -> {
            try {
                int orderItemId = Integer.parseInt(req.params(":orderItemId"));
                
                // Retrieve the specific order item
                OrderItem orderItem = Database.getOrderItemByID(orderItemId);
                
                if (orderItem == null) {
                    res.status(404);
                    return "{\"error\": \"Order item not found\"}";
                }
                
                // Prepare the response JSON
                JSONObject orderItemJson = new JSONObject();
                orderItemJson.put("orderItemId", orderItem.getOrderItemId());
                orderItemJson.put("orderId", orderItem.getOrderId());
                orderItemJson.put("productId", orderItem.getProductId());
                orderItemJson.put("quantity", orderItem.getQuantity());
                orderItemJson.put("price", orderItem.getPrice());
                orderItemJson.put("subtotal", orderItem.getSubtotal());
                
                // Try to get product name if available
                Product product = Database.getProductByID(orderItem.getProductId());
                if (product != null) {
                    orderItemJson.put("productName", product.getName());
                }
                
                res.status(200);
                res.type("application/json");
                return orderItemJson.toString();
                
            } catch (NumberFormatException e) {
                res.status(400);
                return "{\"error\": \"Invalid order item ID format\"}";
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return "{\"error\": \"Server error: " + e.getMessage() + "\"}";
            }
        });

        // Route to delete an order item by its ID
        delete("/api/orderitem/:orderItemId", (req, res) -> {
            try {
                int orderItemId = Integer.parseInt(req.params(":orderItemId"));
                
                // Delete the specific order item
                boolean success = Database.deleteOrderItemByID(orderItemId);
                
                if (!success) {
                    res.status(404);
                    return "{\"error\": \"Order item not found or deletion failed\"}";
                }
                
                res.status(200);
                res.type("application/json");
                return "{\"message\": \"Order item deleted successfully\"}";
                
            } catch (NumberFormatException e) {
                res.status(400);
                return "{\"error\": \"Invalid order item ID format\"}";
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return "{\"error\": \"Server error: " + e.getMessage() + "\"}";
            }
        });
    }
}
