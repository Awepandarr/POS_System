package uk.ac.Thematics;

import static spark.Spark.*;
import org.json.JSONObject;
import org.json.JSONArray;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OrderWebService {

    public OrderWebService() {
        get("/api/orders", (req, res) -> {
            try {
                // Get all orders from the database
                List<Order> orders = Database.getAllOrders();
                
                // Create a JSON array to hold the orders
                JSONArray ordersArray = new JSONArray();
                
                // Convert each order to JSON and add to the array
                for (Order order : orders) {
                    JSONObject orderJson = order.toJSON();
                    ordersArray.put(orderJson);
                }
                
                res.status(200);
                res.type("application/json");
                return ordersArray.toString();
                
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return "{\"error\": \"Server error: " + e.getMessage() + "\"}";
            
            }
        }
        );
    
        post("/api/order", (req, res) -> {
            try {
                // Log the full request body for debugging
                String requestBodyStr = req.body();
                System.out.println("Full Order Request Body: " + requestBodyStr);
                
                JSONObject requestBody = new JSONObject(requestBodyStr);
                
                // Validate required fields
                if (!requestBody.has("customerId") || 
                    !requestBody.has("totalAmount") || 
                    !requestBody.has("orderItems")) {
                    res.status(400);
                    return "{\"error\": \"Missing required order fields\"}";
                }
                java.sql.Date orderDate = null;
                if (requestBody.has("order_date")) {
                    try {
                        orderDate = java.sql.Date.valueOf(requestBody.getString("order_date"));
                    } catch (Exception e) {
                        orderDate = new java.sql.Date(System.currentTimeMillis()); // Fallback to current date
                    }
                } else {
                    orderDate = new java.sql.Date(System.currentTimeMillis()); // Default to current date
                }
                
                // Create order first
                Order order = new Order(
                    0,  // Order ID will be set by the database
                    requestBody.getInt("customerId"),
                    requestBody.getBigDecimal("totalAmount"),
                    orderDate,
                    PaymentStatus.PENDING,
                    requestBody.optString("orderType", "IN_STORE"),
                    requestBody.optBigDecimal("taxAmount", BigDecimal.ZERO),
                    requestBody.getBigDecimal("finalAmount"),
                    requestBody.optBigDecimal("discountAmount", BigDecimal.ZERO),
                    requestBody.optString("deliveryType", ""), // Allow delivery type
                    "PROCESSING"
                );
                
                // Insert the order first
                boolean orderInserted = Database.insertOrder(order);
                if (!orderInserted) {
                    res.status(400);
                    return "{\"error\": \"Failed to create order\"}";
                }
                
                // Process order items
                JSONArray orderItemsArray = requestBody.getJSONArray("orderItems");
                List<OrderItem> orderItems = new ArrayList<>();
                
                for (int i = 0; i < orderItemsArray.length(); i++) {
                    JSONObject itemJson = orderItemsArray.getJSONObject(i);
                    
                    // Validate each order item
                    if (!itemJson.has("productId") || 
                        !itemJson.has("quantity") || 
                        !itemJson.has("price") || 
                        !itemJson.has("subtotal")) {
                        System.err.println("Skipping invalid order item: " + itemJson);
                        continue;
                    }
                    
                    OrderItem orderItem = new OrderItem(
                        0,  // OrderItemId will be set by the database
                        order.getOrderId(),  // Use the newly created order's ID
                        itemJson.getInt("productId"),
                        itemJson.getInt("quantity"),
                        new BigDecimal(itemJson.get("price").toString()),
                        new BigDecimal(itemJson.get("subtotal").toString())
                    );
                    
                    orderItems.add(orderItem);
                }
                
                // Validate that we have order items
                if (orderItems.isEmpty()) {
                    res.status(400);
                    return "{\"error\": \"No valid order items\"}";
                }
                
                // Insert order items
                boolean itemsInserted = Database.insertOrderItems(orderItems);
                
                if (!itemsInserted) {
                    res.status(400);
                    return "{\"error\": \"Failed to create order items\"}";
                }
                
                // Return the created order
                JSONObject responseJson = order.toJSON();
                responseJson.put("orderItemCount", orderItems.size());
                
                res.status(201);
                res.type("application/json");
                return responseJson.toString();
                
            } catch (Exception e) {
                System.err.println("Detailed Order Creation Error:");
                e.printStackTrace();
                res.status(500);
                return "{\"error\": \"Server error: " + e.getMessage() + "\"}";
            }
        });
        
        // Route to get an order by ID
        get("/api/order/:orderId", (req, res) -> {
            try {
                int orderId = Integer.parseInt(req.params(":orderId"));
                Order order = Database.getOrderByID(orderId);
                
                if (order == null) {
                    res.status(404);
                    return "{\"error\": \"Order not found\"}";
                }
                
                // Get order items for this order
                JSONObject orderJson = order.toJSON();
                
                // Add order items to the response
                JSONArray itemsArray = new JSONArray();
                for (OrderItem item : Database.getOrderItemsByOrderID(orderId)) {
                    JSONObject itemJson = new JSONObject();
                    itemJson.put("orderItemId", item.getOrderItemId());
                    itemJson.put("productId", item.getProductId());
                    itemJson.put("quantity", item.getQuantity());
                    itemJson.put("price", item.getPrice());
                    itemJson.put("subtotal", item.getSubtotal());
                    
                    // Try to get product name if available
                    Product product = Database.getProductByID(item.getProductId());
                    if (product != null) {
                        itemJson.put("productName", product.getName());
                    }
                    
                    itemsArray.put(itemJson);
                }
                
                orderJson.put("orderItems", itemsArray);
                
                res.status(200);
                res.type("application/json");
                return orderJson.toString();
                
            } catch (NumberFormatException e) {
                res.status(400);
                return "{\"error\": \"Invalid order ID format\"}";
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return "{\"error\": \"Server error: " + e.getMessage() + "\"}";
            }
        });
        
    }


    // Helper method to create an OrderItem from JSON
    private OrderItem createOrderItem(int orderId, JSONObject itemJson) {
        try {
            int productId = itemJson.getInt("productId");
            int quantity = itemJson.getInt("quantity");
            BigDecimal price = new BigDecimal(itemJson.get("price").toString());
            BigDecimal subtotal = price.multiply(new BigDecimal(quantity));
            
            return new OrderItem(0, orderId, productId, quantity, price, subtotal);
        } catch (Exception e) {
            System.err.println("Error creating order item: " + e.getMessage());
            return null;
        }
    }
}