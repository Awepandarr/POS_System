package uk.ac.Thematics;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Map;
import java.util.HashMap;
public class Database {

    private static final String JDBC_CONNECTION_STRING = "jdbc:sqlite:POS.db";
    private static Connection connection = null;

    private Database() {} // Private constructor to prevent instantiation
    private static boolean isValidConnection(Connection conn) {
        try {
            return conn != null && !conn.isClosed() && conn.isValid(5);
        } catch (SQLException e) {
            return false;
        }
    }
    public static Connection getConnection() {
        try {
            if (!isValidConnection(connection)) {
                closeConnection(); // Ensure old connection is closed
                connection = DriverManager.getConnection(JDBC_CONNECTION_STRING);
                System.out.println("Created new database connection.");
            }
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException("Database connection failed!", e);
        }
    }
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the total discounts for the current day from the EndOfDayReport table
     * @return the total discounts amount as BigDecimal
     * @throws SQLException if a database error occurs
     */
    public static BigDecimal getTotalDiscountsFromEndOfDayReport() throws SQLException {
        // Get today's date in the format SQLite expects
        LocalDate today = LocalDate.now();
        String dateStr = today.toString(); // Format: YYYY-MM-DD
        
        String query = "SELECT total_discounts FROM EndOfDayReport WHERE date = ?";
        System.out.println("Looking for EndOfDayReport for date: " + dateStr);
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, dateStr);
            
            // Debug SQL query
            System.out.println("Executing SQL: " + query.replace("?", "'" + dateStr + "'"));
            
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                BigDecimal result = rs.getBigDecimal("total_discounts");
                System.out.println("Found discount value in EndOfDayReport: " + result);
                
                // Ensure value is properly formatted for currency
                if (result != null) {
                    result = result.setScale(2, java.math.RoundingMode.HALF_UP);
                    return result;
                } else {
                    System.out.println("Discount value is null, returning zero");
                    return BigDecimal.ZERO;
                }
            } else {
                System.out.println("No record found in EndOfDayReport for date: " + dateStr);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving discounts from EndOfDayReport: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("No EndOfDayReport found for today, returning zero");
        return BigDecimal.ZERO;
    }

    /**
     * Debug helper method to check what's in the EndOfDayReport table
     */
    public static void debugEndOfDayReportTable() {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT report_id, date, total_discounts FROM EndOfDayReport");
             ResultSet rs = ps.executeQuery()) {
            
            System.out.println("==== Current EndOfDayReport Records ====");
            boolean hasData = false;
            
            while (rs.next()) {
                hasData = true;
                System.out.println("Report ID: " + rs.getInt("report_id") + 
                                  ", Date: " + rs.getString("date") + 
                                  ", Discounts: " + rs.getBigDecimal("total_discounts"));
            }
            
            if (!hasData) {
                System.out.println("No records found in EndOfDayReport table");
            }
            System.out.println("=======================================");
        } catch (SQLException e) {
            System.err.println("Error debugging EndOfDayReport table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Fixes database locking issues by closing existing connections
     */
    public static void fixDatabaseLock() {
        try {
            closeConnection(); // Close any existing connections
            System.out.println("Closed all database connections to fix locking issues");
        } catch (Exception e) {
            System.err.println("Error trying to fix database lock: " + e.getMessage());
            e.printStackTrace();
        }
    }
    // Insert Product
    public static boolean insertProduct(Product product) {
        String query = "INSERT INTO Products (name, price, category_id, stock_quantity, barcode, image_url) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, product.getName());
            ps.setBigDecimal(2, product.getPrice());
            ps.setInt(3, product.getCategoryId());
            ps.setInt(4, product.getStockQuantity());
            ps.setString(5, product.getBarcode());
            ps.setString(6, product.getImageUrl()); // Add image_url parameter

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        product.setProductId(rs.getInt(1));  // Set the generated product ID
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Get Product by Barcode
 // Get Product by Barcode
    public static Product getProductDetailsByBarcode(String barcode) {
        String query = "SELECT * FROM Products WHERE barcode = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, barcode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Mapping result to Product object with image_url
                    return new Product(
                        rs.getInt("product_id"),
                        rs.getString("name"),
                        rs.getBigDecimal("price"),
                        rs.getInt("category_id"),
                        rs.getInt("stock_quantity"),
                        rs.getString("barcode"),
                        rs.getString("image_url")  // Add image_url field
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
		return null;
    }

    // Get Product by ID
        public static Product getProductByID(int productID) {
            String query = "SELECT * FROM Products WHERE product_id = ?";
            try (Connection conn = getConnection();
                 PreparedStatement ps = conn.prepareStatement(query)) {

                ps.setInt(1, productID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new Product(
                            rs.getInt("product_id"),
                            rs.getString("name"),
                            rs.getBigDecimal("price"),
                            rs.getInt("category_id"),
                            rs.getInt("stock_quantity"),
                            rs.getString("barcode"),
                            rs.getString("image_url")  // Add image_url field
                        );
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

/**
 * Retrieves end of day reports within a specified date range
 * @param startDate The start date of the report range (inclusive)
 * @param endDate The end date of the report range (inclusive)
 * @return JSONObject containing start date, end date, and an array of reports
 */
        public static JSONObject getEndOfDayReportHistory(LocalDate startDate, LocalDate endDate) {
            JSONObject responseObj = new JSONObject();
            JSONArray reportsArray = new JSONArray();
            
            // Debug logging for input dates
            System.out.println("DEBUG: getEndOfDayReportHistory - Start Date: " + startDate);
            System.out.println("DEBUG: getEndOfDayReportHistory - End Date: " + endDate);
            
            // Validate input dates
            if (startDate == null || endDate == null) {
                startDate = LocalDate.now().minusDays(7);
                endDate = LocalDate.now();
                System.out.println("DEBUG: Adjusted dates - Start: " + startDate + ", End: " + endDate);
            }
            
            // Ensure start date is not after end date
            if (startDate.isAfter(endDate)) {
                LocalDate temp = startDate;
                startDate = endDate;
                endDate = temp;
            }
            
            // Modify the query to use DATE function for SQLite date comparison
            String query = "SELECT * FROM EndOfDayReport " +
                           "WHERE date(date) BETWEEN date(?) AND date(?) " +
                           "ORDER BY date DESC";
            
            try (Connection conn = getConnection();
                 PreparedStatement ps = conn.prepareStatement(query)) {
                
                // Set date parameters using string representation
                ps.setString(1, startDate.toString());
                ps.setString(2, endDate.toString());
                
                // Debug logging for the SQL query
                System.out.println("DEBUG: Executing SQL: " + query);
                System.out.println("DEBUG: With parameters: Start Date = " + startDate + ", End Date = " + endDate);
                
                ResultSet rs = ps.executeQuery();
                
                // Debug logging for result set
                System.out.println("DEBUG: Iterating through result set...");
                
                int reportCount = 0;
                while (rs.next()) {
                    reportCount++;
                    JSONObject report = new JSONObject();
                    report.put("report_id", rs.getInt("report_id"));
                    report.put("date", rs.getString("date"));
                    report.put("total_sales", rs.getBigDecimal("total_sales"));
                    report.put("total_payments", rs.getBigDecimal("total_payments"));
                    report.put("total_discounts", rs.getBigDecimal("total_discounts"));
                    report.put("payment_breakdown", rs.getString("payment_breakdown"));
                    
                    reportsArray.put(report);
                    
                    // Additional debug for each report
                    System.out.println("DEBUG: Found Report - Date: " + report.getString("date"));
                }
                
                System.out.println("DEBUG: Total reports found: " + reportCount);
                
                // Prepare response object
                responseObj.put("start_date", startDate.toString());
                responseObj.put("end_date", endDate.toString());
                responseObj.put("reports", reportsArray);
                
                // Debug logging
                System.out.println("DEBUG: Generated response: " + responseObj.toString(2));
                
                return responseObj;
                
            } catch (SQLException e) {
                System.err.println("ERROR: Retrieving report history: " + e.getMessage());
                e.printStackTrace();
                
                // Error handling in the response
                responseObj.put("error", "Failed to retrieve report history");
                return responseObj;
            }
        }
        
     // Add this method to the Database class in your existing Database.java file
        public static List<OrderItem> getAllOrderItems() {
            List<OrderItem> allOrderItems = new ArrayList<>();
            String query = "SELECT * FROM Order_Items";
            
            try (Connection conn = getConnection();
                 PreparedStatement ps = conn.prepareStatement(query);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    OrderItem orderItem = new OrderItem(
                        rs.getInt("order_item_id"),
                        rs.getInt("order_id"),
                        rs.getInt("product_id"),
                        rs.getInt("quantity"),
                        rs.getBigDecimal("price"),
                        rs.getBigDecimal("subtotal")
                    );
                    allOrderItems.add(orderItem);
                }
            } catch (SQLException e) {
                System.err.println("Error retrieving order items: " + e.getMessage());
                e.printStackTrace();
            }
            
            return allOrderItems;
        }
        public static List<Order> getAllOrders() {
            List<Order> orders = new ArrayList<>();
            String query = "SELECT * FROM Orders";
            
            try (Connection conn = getConnection();
                 PreparedStatement ps = conn.prepareStatement(query);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    // Convert payment_status string from DB to PaymentStatus enum
                    PaymentStatus paymentStatus;
                    try {
                        paymentStatus = PaymentStatus.fromDbValue(rs.getString("payment_status"));
                    } catch (IllegalArgumentException e) {
                        // Default to PENDING if the value can't be converted
                        paymentStatus = PaymentStatus.PENDING;
                    }
                    
                    Order order = new Order(
                        rs.getInt("order_id"),
                        rs.getInt("customer_id"),
                        rs.getBigDecimal("total_amount"),
                        rs.getDate("order_date"),
                        paymentStatus,  // Using PaymentStatus enum instead of String
                        rs.getString("order_type"),
                        rs.getBigDecimal("tax_amount"),
                        rs.getBigDecimal("final_amount"),
                        rs.getBigDecimal("discount_amount"),
                        rs.getString("delivery_type"),
                        rs.getString("order_status")
                    );
                    orders.add(order);
                }
            } catch (SQLException e) {
                System.err.println("Error retrieving orders: " + e.getMessage());
                e.printStackTrace();
            }
            
            return orders;
        }

    // Update Product
        public static boolean updateProduct(Product product) {
            String query = "UPDATE Products SET name = ?, price = ?, stock_quantity = ?, category_id = ?, barcode = ?, image_url = ? WHERE product_id = ?";
            try (Connection conn = getConnection();
                 PreparedStatement ps = conn.prepareStatement(query)) {

                ps.setString(1, product.getName());
                ps.setBigDecimal(2, product.getPrice());
                ps.setInt(3, product.getStockQuantity());
                ps.setInt(4, product.getCategoryId());
                ps.setString(5, product.getBarcode());
                ps.setString(6, product.getImageUrl());  // Add image_url parameter
                ps.setInt(7, product.getProductId());

                return ps.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
 // Get all products
        public static List<Product> getAllProducts() {
            String query = "SELECT * FROM Products";
            List<Product> products = new ArrayList<>();

            try (Connection conn = DriverManager.getConnection(JDBC_CONNECTION_STRING);
                 PreparedStatement ps = conn.prepareStatement(query);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    Product product = new Product(
                        rs.getInt("product_id"),
                        rs.getString("name"),
                        rs.getBigDecimal("price"),
                        rs.getInt("category_id"),
                        rs.getInt("stock_quantity"),
                        rs.getString("barcode"),
                        rs.getString("image_url")
                    );
                    products.add(product);
                }
            } catch (SQLException e) {
                System.err.println("Error getting all products: " + e.getMessage());
                e.printStackTrace();
            }
            return products;
        }
            public static String getProductImageUrlByID(int productID) {
                String query = "SELECT image_url FROM Products WHERE product_id = ?";
                try (Connection conn = getConnection();
                     PreparedStatement ps = conn.prepareStatement(query)) {

                    ps.setInt(1, productID);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            return rs.getString("image_url");
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return null; // Image URL not found
            }
            public static boolean updateProductImageUrl(int productID, String imageUrl) {
                String query = "UPDATE Products SET image_url = ? WHERE product_id = ?";
                try (Connection conn = getConnection();
                     PreparedStatement ps = conn.prepareStatement(query)) {

                    ps.setString(1, imageUrl);
                    ps.setInt(2, productID);

                    return ps.executeUpdate() > 0;
                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                }
            }
    // Delete Product by ID
    public static boolean deleteProductByID(int productId) {
        String query = "DELETE FROM Products WHERE product_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setInt(1, productId);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Insert Order
    public static boolean insertOrder(Order order) {
        String query = "INSERT INTO Orders (customer_id, total_amount, payment_status, order_type, tax_amount, final_amount, discount_amount, delivery_type) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, order.getCustomerId());
            ps.setBigDecimal(2, order.getTotalAmount());
            ps.setString(3, order.getPaymentStatus().getDbValue());
            ps.setString(4, order.getOrderType().toString());  // Using the enum to ensure valid order type
            ps.setBigDecimal(5, order.getTaxAmount());
            ps.setBigDecimal(6, order.getFinalAmount());
            ps.setBigDecimal(7, order.getDiscountAmount());
            ps.setString(8, order.getDeliveryType());

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        order.setOrderId(rs.getInt(1));  // Set the generated order ID
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Update Order
    public static boolean updateOrder(Order order) {
        String query = "UPDATE Orders SET " +
            "customer_id = ?, " +
            "total_amount = ?, " +
            "payment_status = ?, " +
            "order_type = ?, " +
            "tax_amount = ?, " +
            "final_amount = ?, " +
            "discount_amount = ?, " +
            "delivery_type = ?, " +
            "order_status = ? " +
            "WHERE order_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            // Print out all order details for debugging
            System.out.println("DEBUG: Updating Order Details:");
            System.out.println("Order ID: " + order.getOrderId());
            System.out.println("Customer ID: " + order.getCustomerId());
            System.out.println("Total Amount: " + order.getTotalAmount());
            System.out.println("Payment Status: " + order.getPaymentStatus());
            System.out.println("Payment Status DB Value: " + order.getPaymentStatus().getDbValue());
            System.out.println("Order Type: " + order.getOrderType());
            System.out.println("Tax Amount: " + order.getTaxAmount());
            System.out.println("Final Amount: " + order.getFinalAmount());
            System.out.println("Discount Amount: " + order.getDiscountAmount());
            System.out.println("Delivery Type: " + order.getDeliveryType());
            System.out.println("Order Status: " + order.getOrderStatus());

            ps.setInt(1, order.getCustomerId());
            ps.setBigDecimal(2, order.getTotalAmount());
            ps.setString(3, order.getPaymentStatus().getDbValue());
            ps.setString(4, order.getOrderType());
            ps.setBigDecimal(5, order.getTaxAmount());
            ps.setBigDecimal(6, order.getFinalAmount());
            ps.setBigDecimal(7, order.getDiscountAmount());
            ps.setString(8, order.getDeliveryType());
            ps.setString(9, order.getOrderStatus());
            ps.setInt(10, order.getOrderId());

            try {
                int rowsUpdated = ps.executeUpdate();
                
                System.out.println("Rows Updated: " + rowsUpdated);
                
                if (rowsUpdated == 0) {
                    // If no rows were updated, check if the order exists
                    try (PreparedStatement checkPs = conn.prepareStatement(
                        "SELECT * FROM Orders WHERE order_id = ?")) {
                        
                        checkPs.setInt(1, order.getOrderId());
                        ResultSet rs = checkPs.executeQuery();
                        
                        if (!rs.next()) {
                            System.err.println("ERROR: Order #" + order.getOrderId() + " does not exist in the database!");
                        } else {
                            System.err.println("ERROR: Order #" + order.getOrderId() + " exists, but update failed.");
                            
                            // Print existing order details
                            System.err.println("Existing Order Details:");
                            System.err.println("Current Payment Status: " + rs.getString("payment_status"));
                            System.err.println("Current Order Status: " + rs.getString("order_status"));
                        }
                    }
                }
                
                return rowsUpdated > 0;
            } catch (SQLException updateEx) {
                System.err.println("SQL Update Error: " + updateEx.getMessage());
                updateEx.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Connection or Prepare Statement Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Delete Order by ID
    public static boolean deleteOrderByID(int orderId) {
        String query = "DELETE FROM Orders WHERE order_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setInt(1, orderId);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Insert Order Item
 // Enhanced method in Database class for inserting order items
    public static boolean insertOrderItem(OrderItem orderItem) {
        String query = "INSERT INTO Order_Items (order_id, product_id, quantity, price, subtotal) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            // Validate input parameters
            if (orderItem == null) {
                System.err.println("Error: Null OrderItem cannot be inserted");
                return false;
            }

            // Detailed logging before insertion
            System.out.println("Attempting to insert Order Item:");
            System.out.println("Order ID: " + orderItem.getOrderId());
            System.out.println("Product ID: " + orderItem.getProductId());
            System.out.println("Quantity: " + orderItem.getQuantity());
            System.out.println("Price: " + orderItem.getPrice());
            System.out.println("Subtotal: " + orderItem.getSubtotal());

            // Validate critical fields
            if (orderItem.getOrderId() <= 0 || orderItem.getProductId() <= 0 || 
                orderItem.getQuantity() <= 0 || orderItem.getPrice() == null || 
                orderItem.getSubtotal() == null) {
                System.err.println("Error: Invalid OrderItem parameters");
                return false;
            }

            ps.setInt(1, orderItem.getOrderId());
            ps.setInt(2, orderItem.getProductId());
            ps.setInt(3, orderItem.getQuantity());
            ps.setBigDecimal(4, orderItem.getPrice());
            ps.setBigDecimal(5, orderItem.getSubtotal());

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int generatedId = rs.getInt(1);
                        orderItem.setOrderItemId(generatedId);
                        System.out.println("Order Item inserted successfully. Generated ID: " + generatedId);
                        return true;
                    } else {
                        System.err.println("No generated keys were returned.");
                    }
                }
            } else {
                System.err.println("No rows were affected during Order Item insertion.");
            }
        } catch (SQLException e) {
            System.err.println("Database error inserting order item: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
 // Add these methods to your Database.java file if they don't already exist

 // Method to update product stock quantity
 public static boolean updateProductStock(int productId, int newStockQuantity) {
     String query = "UPDATE Products SET stock_quantity = ? WHERE product_id = ?";
     
     try (Connection conn = getConnection();
          PreparedStatement ps = conn.prepareStatement(query)) {
         
         ps.setInt(1, newStockQuantity);
         ps.setInt(2, productId);
         
         int rowsAffected = ps.executeUpdate();
         return rowsAffected > 0;
     } catch (SQLException e) {
         System.err.println("Error updating product stock: " + e.getMessage());
         e.printStackTrace();
         return false;
     }
 }

 // Method to adjust product stock (increment or decrement)
 public static boolean adjustProductStock(int productId, int adjustment) {
     // First get the current stock
     Product product = getProductByID(productId);
     if (product == null) {
         return false;
     }
     
     int currentStock = product.getStockQuantity();
     int newStock = currentStock + adjustment;
     
     // Ensure stock doesn't go negative
     if (newStock < 0) {
         return false;
     }
     
     // Update the stock
     return updateProductStock(productId, newStock);
 }

 // Method to get all products with stock below a threshold
 public static List<Product> getLowStockProducts(int threshold) {
     List<Product> lowStockProducts = new ArrayList<>();
     String query = "SELECT * FROM Products WHERE stock_quantity <= ?";
     
     try (Connection conn = getConnection();
          PreparedStatement ps = conn.prepareStatement(query)) {
         
         ps.setInt(1, threshold);
         
         try (ResultSet rs = ps.executeQuery()) {
             while (rs.next()) {
                 Product product = new Product(
                     rs.getInt("product_id"),
                     rs.getString("name"),
                     rs.getBigDecimal("price"),
                     rs.getInt("category_id"),
                     rs.getInt("stock_quantity"),
                     rs.getString("barcode"),
                     rs.getString("image_url")
                 );
                 lowStockProducts.add(product);
             }
         }
     } catch (SQLException e) {
         System.err.println("Error getting low stock products: " + e.getMessage());
         e.printStackTrace();
     }
     
     return lowStockProducts;
 }

 // Method to update customer loyalty points
 public static boolean updateCustomerLoyaltyPoints(int customerId, int newLoyaltyPoints) {
     String query = "UPDATE Customers SET loyalty_points = ? WHERE customer_id = ?";
     
     try (Connection conn = getConnection();
          PreparedStatement ps = conn.prepareStatement(query)) {
         
         ps.setInt(1, newLoyaltyPoints);
         ps.setInt(2, customerId);
         
         int rowsAffected = ps.executeUpdate();
         return rowsAffected > 0;
     } catch (SQLException e) {
         System.err.println("Error updating customer loyalty points: " + e.getMessage());
         e.printStackTrace();
         return false;
     }
 }

 // Method to add loyalty points to a customer
 public static boolean addCustomerLoyaltyPoints(int customerId, int pointsToAdd) {
     // First get the current points
     Customer customer = getCustomerByID(customerId);
     if (customer == null) {
         return false;
     }
     
     int currentPoints = customer.getLoyaltyPoints();
     int newPoints = currentPoints + pointsToAdd;
     
     // Update the points
     return updateCustomerLoyaltyPoints(customerId, newPoints);
 }

 // Method to redeem loyalty points from a customer
 public static boolean redeemCustomerLoyaltyPoints(int customerId, int pointsToRedeem) {
     // First get the current points
     Customer customer = getCustomerByID(customerId);
     if (customer == null) {
         return false;
     }
     
     int currentPoints = customer.getLoyaltyPoints();
     
     // Verify customer has enough points
     if (currentPoints < pointsToRedeem) {
         return false;
     }
     
     int newPoints = currentPoints - pointsToRedeem;
     
     // Update the points
     return updateCustomerLoyaltyPoints(customerId, newPoints);
 }
    // Batch insertion method for multiple order items
    public static boolean insertOrderItems(List<OrderItem> orderItems) {
        if (orderItems == null || orderItems.isEmpty()) {
            System.err.println("No order items to insert");
            return false;
        }

        String query = "INSERT INTO Order_Items (order_id, product_id, quantity, price, subtotal) VALUES (?, ?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);  // Start transaction

            try (PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                for (OrderItem orderItem : orderItems) {
                    // Extensive logging and validation
                    System.out.println("Attempting to insert Order Item:");
                    System.out.println("Order ID: " + orderItem.getOrderId());
                    System.out.println("Product ID: " + orderItem.getProductId());
                    System.out.println("Quantity: " + orderItem.getQuantity());
                    System.out.println("Price: " + orderItem.getPrice());
                    System.out.println("Subtotal: " + orderItem.getSubtotal());

                    // Validate each order item
                    if (orderItem.getOrderId() <= 0 || 
                        orderItem.getProductId() <= 0 || 
                        orderItem.getQuantity() <= 0) {
                        System.err.println("Invalid order item - skipping insertion");
                        continue;
                    }

                    ps.setInt(1, orderItem.getOrderId());
                    ps.setInt(2, orderItem.getProductId());
                    ps.setInt(3, orderItem.getQuantity());
                    ps.setBigDecimal(4, orderItem.getPrice());
                    ps.setBigDecimal(5, orderItem.getSubtotal());

                    ps.addBatch();
                }

                // Execute batch and log results
                int[] batchResults = ps.executeBatch();
                
                // Validate batch results
                int successfulInserts = 0;
                for (int result : batchResults) {
                    if (result > 0 || result == Statement.SUCCESS_NO_INFO) {
                        successfulInserts++;
                    }
                }

                // Commit transaction
                conn.commit();

                System.out.println("Batch insert completed. Successful inserts: " + successfulInserts + 
                                   " out of " + orderItems.size());

                return successfulInserts > 0;
            }
        } catch (SQLException e) {
            // Extensive error logging
            System.err.println("Batch insert error details:");
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Message: " + e.getMessage());
            e.printStackTrace();

            // Rollback transaction in case of error
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("Error during rollback: " + rollbackEx.getMessage());
                    rollbackEx.printStackTrace();
                }
            }
            return false;
        } finally {
            // Restore auto-commit and close connection
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    System.err.println("Error resetting auto-commit: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    // Comprehensive method to insert order with its items
    public static boolean insertOrderWithItems(Order order, List<OrderItem> orderItems) {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);  // Start transaction

            // First, validate order
            if (order == null) {
                System.err.println("Cannot insert null order");
                return false;
            }

            // Insert order first
            boolean orderInserted = insertOrder(order);
            if (!orderInserted) {
                System.err.println("Failed to insert order");
                conn.rollback();
                return false;
            }

            // Update order items with the new order ID
            for (OrderItem item : orderItems) {
                item.setOrderId(order.getOrderId());
            }

            // Insert order items
            boolean itemsInserted = insertOrderItems(orderItems);
            if (!itemsInserted) {
                System.err.println("Failed to insert order items");
                conn.rollback();
                return false;
            }

            // Commit transaction
            conn.commit();
            System.out.println("Order and order items inserted successfully");
            return true;

        } catch (SQLException e) {
            // Rollback in case of any error
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("Error during rollback: " + rollbackEx.getMessage());
                    rollbackEx.printStackTrace();
                }
            }

            System.err.println("Error inserting order with items: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            // Restore auto-commit
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    System.err.println("Error resetting auto-commit: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    // Delete Order Item by ID
    public static boolean deleteOrderItemByID(int orderItemId) {
        String query = "DELETE FROM Order_Items WHERE order_item_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setInt(1, orderItemId);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
 // Add to Database.java

    public static List<Invoice> getInvoicesForTheDay() {
        List<Invoice> invoices = new ArrayList<>();
        String query = "SELECT * FROM Invoices WHERE DATE(date_issued) = CURRENT_DATE";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Invoice invoice = new Invoice(
                    rs.getInt("invoice_id"),
                    rs.getInt("order_id"),
                    rs.getString("date_issued"),
                    rs.getBigDecimal("subtotal"),
                    rs.getBigDecimal("tax_amount"),
                    rs.getBigDecimal("final_amount"),
                    rs.getInt("customer_id")
                );
                invoices.add(invoice);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return invoices;
    }

    // Existing Database connection code and other methods remain unchanged
    
    
    /**
     * Get invoices for a specified date range
     * @param startDate The start date (inclusive)
     * @param endDate The end date (inclusive)
     * @return List of Invoice objects
     */
    public static List<Invoice> getInvoicesForDateRange(LocalDate startDate, LocalDate endDate) {
        List<Invoice> invoices = new ArrayList<>();
        String query = "SELECT * FROM Invoices WHERE DATE(date_issued) BETWEEN ? AND ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setDate(1, Date.valueOf(startDate));
            ps.setDate(2, Date.valueOf(endDate));
            
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Invoice invoice = new Invoice(
                    rs.getInt("invoice_id"),
                    rs.getInt("order_id"),
                    rs.getString("date_issued"),
                    rs.getBigDecimal("subtotal"),
                    rs.getBigDecimal("tax_amount"),
                    rs.getBigDecimal("final_amount"),
                    rs.getInt("customer_id")
                );
                invoices.add(invoice);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving invoices for date range: " + e.getMessage());
            e.printStackTrace();
        }
        
        return invoices;
    }
    
    /**
     * Get total invoice amount for today
     * @return BigDecimal total amount
     */
    public static BigDecimal getTotalInvoiceAmountForToday() {
        String query = "SELECT SUM(final_amount) FROM Invoices WHERE DATE(date_issued) = CURRENT_DATE";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                BigDecimal result = rs.getBigDecimal(1);
                return result != null ? result : BigDecimal.ZERO;
            }
        } catch (SQLException e) {
            System.err.println("Error calculating total invoice amount: " + e.getMessage());
            e.printStackTrace();
        }
        
        return BigDecimal.ZERO;
    }
    
    /**
     * Get invoice statistics for the day
     * @return JSONObject containing various statistics
     */
    public static JSONObject getInvoiceStatisticsForToday() {
        JSONObject stats = new JSONObject();
        
        try (Connection conn = getConnection()) {
            // Total count
            PreparedStatement ps = conn.prepareStatement(
                "SELECT COUNT(*) FROM Invoices WHERE DATE(date_issued) = CURRENT_DATE"
            );
            ResultSet rs = ps.executeQuery();
            stats.put("total_count", rs.next() ? rs.getInt(1) : 0);
            
            // Sum, Average, Min, Max amounts
            ps = conn.prepareStatement(
                "SELECT SUM(final_amount), AVG(final_amount), MIN(final_amount), MAX(final_amount) " +
                "FROM Invoices WHERE DATE(date_issued) = CURRENT_DATE"
            );
            rs = ps.executeQuery();
            
            if (rs.next()) {
                stats.put("total_amount", rs.getBigDecimal(1) != null ? rs.getBigDecimal(1) : BigDecimal.ZERO);
                stats.put("average_amount", rs.getBigDecimal(2) != null ? rs.getBigDecimal(2) : BigDecimal.ZERO);
                stats.put("min_amount", rs.getBigDecimal(3) != null ? rs.getBigDecimal(3) : BigDecimal.ZERO);
                stats.put("max_amount", rs.getBigDecimal(4) != null ? rs.getBigDecimal(4) : BigDecimal.ZERO);
            }
            
            // Get customer breakdown
            ps = conn.prepareStatement(
                "SELECT customer_id, COUNT(*) as invoice_count, SUM(final_amount) as total_amount " +
                "FROM Invoices WHERE DATE(date_issued) = CURRENT_DATE " +
                "GROUP BY customer_id ORDER BY total_amount DESC"
            );
            rs = ps.executeQuery();
            
            JSONArray customerBreakdown = new JSONArray();
            while (rs.next()) {
                JSONObject customerStat = new JSONObject();
                customerStat.put("customer_id", rs.getInt("customer_id"));
                customerStat.put("invoice_count", rs.getInt("invoice_count"));
                customerStat.put("total_amount", rs.getBigDecimal("total_amount"));
                customerBreakdown.put(customerStat);
            }
            stats.put("customer_breakdown", customerBreakdown);
            
        } catch (SQLException e) {
            System.err.println("Error generating invoice statistics: " + e.getMessage());
            e.printStackTrace();
            stats.put("error", e.getMessage());
        }
        
        return stats;
    }
    
    /**
     * Get payment method breakdown for invoices today
     * @return JSONArray of payment methods and amounts
     */
    public static JSONArray getPaymentMethodBreakdownForToday() {
        JSONArray breakdown = new JSONArray();
        
        String query = 
            "SELECT p.payment_type, COUNT(p.payment_id) as payment_count, SUM(p.payment_amount) as total_amount " +
            "FROM Payments p " +
            "JOIN Invoices i ON p.order_id = i.order_id " +
            "WHERE DATE(i.date_issued) = CURRENT_DATE " +
            "GROUP BY p.payment_type";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                JSONObject method = new JSONObject();
                method.put("payment_type", rs.getString("payment_type"));
                method.put("count", rs.getInt("payment_count"));
                method.put("amount", rs.getBigDecimal("total_amount"));
                breakdown.put(method);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving payment method breakdown: " + e.getMessage());
            e.printStackTrace();
        }
        
        return breakdown;
    }
    
    /**
     * Get hourly invoice counts and amounts for today
     * @return JSONArray with hourly breakdown
     */
    public static JSONArray getHourlyInvoiceBreakdownForToday() {
        JSONArray hourlyData = new JSONArray();
        
        String query = 
            "SELECT HOUR(date_issued) as hour, COUNT(*) as invoice_count, SUM(final_amount) as total_amount " +
            "FROM Invoices " +
            "WHERE DATE(date_issued) = CURRENT_DATE " +
            "GROUP BY HOUR(date_issued) " +
            "ORDER BY hour";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ResultSet rs = ps.executeQuery();
            
            // Initialize all 24 hours with zero values
            for (int i = 0; i < 24; i++) {
                JSONObject hourData = new JSONObject();
                hourData.put("hour", i);
                hourData.put("invoice_count", 0);
                hourData.put("total_amount", BigDecimal.ZERO);
                hourlyData.put(hourData);
            }
            
            // Update with actual data from database
            while (rs.next()) {
                int hour = rs.getInt("hour");
                if (hour >= 0 && hour < 24) {
                    JSONObject hourData = hourlyData.getJSONObject(hour);
                    hourData.put("invoice_count", rs.getInt("invoice_count"));
                    hourData.put("total_amount", rs.getBigDecimal("total_amount"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving hourly invoice breakdown: " + e.getMessage());
            e.printStackTrace();
        }
        
        return hourlyData;
    }

    /**
     * Get the total number of invoices generated for a specific order
     * @param orderId the order ID to check
     * @return the count of invoices
     */
    public static int getInvoiceCountForOrder(int orderId) {
        String query = "SELECT COUNT(*) FROM Invoices WHERE order_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting invoices for order: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Check if an invoice exists with matching order_id and date
     * This helps prevent duplicate invoices
     * @param orderId Order ID to check
     * @param dateIssued Date the invoice was issued
     * @return true if a matching invoice exists, false otherwise
     */
    public static boolean doesInvoiceExistForOrderAndDate(int orderId, String dateIssued) {
        String query = "SELECT COUNT(*) FROM Invoices WHERE order_id = ? AND DATE(date_issued) = DATE(?)";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setInt(1, orderId);
            ps.setString(2, dateIssued);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking for existing invoice: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Enhanced insertInvoice method with duplicate checking
     * @param invoice The invoice to insert
     * @return true if inserted successfully, false otherwise
     */
    public static boolean insertInvoiceWithDuplicateCheck(Invoice invoice) {
        // First, check if this invoice already exists
        if (doesInvoiceExistForOrderAndDate(invoice.getOrderId(), invoice.getInvoiceDate())) {
            System.out.println("Duplicate invoice detected for order ID " + invoice.getOrderId() + 
                              " and date " + invoice.getInvoiceDate());
            return false;
        }
        
        // If no duplicate, proceed with insert
        return insertInvoice(invoice);
    }
    public static BigDecimal getTotalInvoiceAmountForTheDay() {
        String query = "SELECT SUM(final_amount) FROM Invoices WHERE DATE(date_issued) = CURRENT_DATE";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return BigDecimal.ZERO;
    }
    public static boolean insertInvoice(Invoice invoice) {
    	String query = "INSERT INTO Invoices (order_id, date_issued, subtotal, discount, tax_amount, final_amount, customer_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, invoice.getOrderId());
            ps.setString(2, invoice.getInvoiceDate());  // Using invoiceDate for date_issued
            ps.setBigDecimal(3, invoice.getTotalAmount());  // Using totalAmount for subtotal
            ps.setBigDecimal(4, new BigDecimal("0.00"));  // Default discount value
            ps.setBigDecimal(5, invoice.getTaxAmount());
            ps.setBigDecimal(6, invoice.getFinalAmount());
            ps.setInt(7, 0);
            System.out.println("Executing SQL: " + query);
            int affectedRows = ps.executeUpdate();
            System.out.println("Affected rows: " + affectedRows);

            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        invoice.setInvoiceId(rs.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error inserting invoice: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("Failed to insert invoice");
        return false;
    }


    // Get Invoice by ID
    public static Invoice getInvoiceByID(int invoiceId) {
        String query = "SELECT * FROM Invoices WHERE invoice_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, invoiceId);
            try (ResultSet rs = ps.executeQuery()) {
            	if (rs.next()) {
            	    return new Invoice(
            	        rs.getInt("invoice_id"),
            	        rs.getInt("order_id"),
            	        rs.getString("date_issued"),     // Changed from "invoice_date"
            	        rs.getBigDecimal("subtotal"),    // Changed from "total_amount"
            	        rs.getBigDecimal("tax_amount"),
            	        rs.getBigDecimal("final_amount"),
            	        rs.getInt("customer_id")         // Added customer_id
            	    );
            	}
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    // Insert Transaction
    public static boolean insertTransaction(Transaction transaction) {
        String query = "INSERT INTO Transactions (transaction_id, order_id, total_amount, payment_method, transaction_date) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, transaction.getTransactionID());
            ps.setInt(2, transaction.getOrderID());  
            ps.setBigDecimal(3, transaction.getTotalAmount());
            ps.setString(4, transaction.getPaymentMethod());
            ps.setString(5, transaction.getTransactionDate());
            // Remove the line setting payment_status

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Insert failed
    }


    // Delete Transaction by ID
    public static boolean deleteTransactionByID(String transactionID) {
        String query = "DELETE FROM Transactions WHERE transaction_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, transactionID);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

 // Insert Payment
 // Insert Payment (corrected method)
    public static boolean insertPayment(Payment payment) {
        String query = "INSERT INTO Payments (transaction_id, order_id, payment_amount, payment_type, payment_status, payment_date) " +
                       "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            // Make sure payment status is valid for the database
            String paymentStatus = payment.getPaymentStatus() != null 
                ? payment.getPaymentStatus().getDbValue() // Use the mapped database value
                : PaymentStatus.PENDING.getDbValue();

            // Log payment for debugging
            System.out.println("Inserting payment: TransactionID=" + payment.getTransactionId() 
                              + ", OrderID=" + payment.getOrderId()
                              + ", Amount=" + payment.getPaymentAmount()
                              + ", Type=" + payment.getDbPaymentType()
                              + ", Status=" + paymentStatus);

            ps.setString(1, payment.getTransactionId());
            ps.setInt(2, payment.getOrderId());
            ps.setBigDecimal(3, payment.getPaymentAmount());
            ps.setString(4, payment.getDbPaymentType());
            ps.setString(5, paymentStatus);
            ps.setString(6, LocalDateTime.now().toString()); // Current timestamp

            int rowsAffected = ps.executeUpdate();

            // Retrieve the generated payment ID
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);
                        payment.setPaymentId(generatedId);
                    }
                }
            }

            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error inserting payment: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }



    // Delete Payment by Transaction ID
    public static boolean deletePaymentByTransactionID(String transactionID) {
        String query = "DELETE FROM Payments WHERE transaction_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, transactionID);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Deletion failed
    }

    // Update Transaction
    public static boolean updateTransaction(Transaction transaction) {
        String query = "UPDATE Transactions SET order_id = ?, total_amount = ?, payment_method = ?, transaction_date = ?, payment_status = ? WHERE transaction_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            // Corrected to pass orderID as an int
            ps.setInt(1, transaction.getOrderID());  // Use setInt for order_id
            ps.setBigDecimal(2, transaction.getTotalAmount());
            ps.setString(3, transaction.getPaymentMethod());
            ps.setString(4, transaction.getTransactionDate());
            ps.setString(5, transaction.getPaymentStatus().toString());
            ps.setString(6, transaction.getTransactionID());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    // Update Payment
    public static boolean updatePayment(Payment payment) {
        // Corrected query to match the database schema (use `payment_amount` instead of `amount`)
        String query = "UPDATE Payments SET payment_amount = ?, payment_type = ?, payment_status = ? WHERE transaction_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            // Set the payment amount (BigDecimal to represent currency values)
            ps.setBigDecimal(1, payment.getPaymentAmount());

            // Set the payment type (enum to string)
            ps.setString(2, payment.getPaymentType().toString());

            // Set the payment status (using `getDbValue` to match database value)
            ps.setString(3, payment.getPaymentStatus().getDbValue());

            // Set the transaction ID for the WHERE clause
            ps.setString(4, payment.getTransactionId());

            // Execute the update and check if any rows were affected
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            // Print the exception to help with debugging
            e.printStackTrace();
            return false;
        }
    }



 // Add these methods to your Database.java file

    /**
     * Gets the total sales for the current day based on invoices
     * @return the total sales amount as BigDecimal
     * @throws SQLException if a database error occurs
     */
    public static BigDecimal getTotalSalesForTheDay() throws SQLException {
        String query = "SELECT SUM(subtotal) FROM Invoices WHERE DATE(date_issued) = CURRENT_DATE";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                BigDecimal result = rs.getBigDecimal(1);
                return result != null ? result : BigDecimal.ZERO;
            }
        }
        return BigDecimal.ZERO;
    }

    /**
     * Gets the total payments for the current day
     * @return the total payments amount as BigDecimal
     * @throws SQLException if a database error occurs
     */
    public static BigDecimal getTotalPaymentsForTheDay() throws SQLException {
        String query = "SELECT SUM(final_amount) FROM Invoices WHERE DATE(date_issued) = CURRENT_DATE";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                BigDecimal result = rs.getBigDecimal(1);
                return result != null ? result : BigDecimal.ZERO;
            }
        }
        return BigDecimal.ZERO;
    }

    /**
     * Gets the total discounts for the current day
     * @return the total discounts amount as BigDecimal
     * @throws SQLException if a database error occurs
     */
    public static BigDecimal getTotalDiscountsForTheDay() throws SQLException {
        String query = "SELECT SUM(discount) FROM Invoices WHERE DATE(date_issued) = CURRENT_DATE";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                BigDecimal result = rs.getBigDecimal(1);
                return result != null ? result : BigDecimal.ZERO;
            }
        }
        return BigDecimal.ZERO;
    }

    /**
     * Gets the payment breakdown for the current day
     * @return a formatted string with payment method breakdowns
     * @throws SQLException if a database error occurs
     */
    public static String getPaymentBreakdownForTheDay() throws SQLException {
        String query = "SELECT p.payment_type, SUM(p.payment_amount) as total_amount " +
                      "FROM Payments p " +
                      "JOIN Invoices i ON p.order_id = i.order_id " +
                      "WHERE DATE(i.date_issued) = CURRENT_DATE " +
                      "GROUP BY p.payment_type";
        
        StringBuilder breakdown = new StringBuilder();
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            boolean hasData = false;
            
            while (rs.next()) {
                hasData = true;
                String paymentMethod = rs.getString("payment_type");
                BigDecimal amount = rs.getBigDecimal("total_amount");
                
                if (breakdown.length() > 0) {
                    breakdown.append(", ");
                }
                
                breakdown.append(paymentMethod).append(": ").append(amount);
            }
            
            if (!hasData) {
                return "No payment data available";
            }
        }
        
        return breakdown.toString();
    }

    /**
     * Generates and saves an end-of-day report to the database
     * @return true if successful, false otherwise
     */
    public static boolean generateAndSaveEndOfDayReport() {
        try {
            // Get data for the report
            BigDecimal totalSales = getTotalSalesForTheDay();
            BigDecimal totalPayments = getTotalPaymentsForTheDay();
            BigDecimal totalDiscounts = getTotalDiscountsForTheDay();
            String paymentBreakdown = getPaymentBreakdownForTheDay();
            
            // Check if a report for today already exists
            String checkSql = "SELECT report_id FROM EndOfDayReport WHERE date = CURRENT_DATE";
            
            try (Connection conn = getConnection();
                 PreparedStatement ps = conn.prepareStatement(checkSql);
                 ResultSet rs = ps.executeQuery()) {
                
                if (rs.next()) {
                    // Update existing report
                    int reportId = rs.getInt("report_id");
                    
                    String updateSql = "UPDATE EndOfDayReport SET " + 
                                      "total_sales = ?, " +
                                      "total_payments = ?, " +
                                      "total_discounts = ?, " +
                                      "payment_breakdown = ? " +
                                      "WHERE report_id = ?";
                                      
                    try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
                        updatePs.setBigDecimal(1, totalSales);
                        updatePs.setBigDecimal(2, totalPayments);
                        updatePs.setBigDecimal(3, totalDiscounts);
                        updatePs.setString(4, paymentBreakdown);
                        updatePs.setInt(5, reportId);
                        
                        return updatePs.executeUpdate() > 0;
                    }
                } else {
                    // Insert new report
                    String insertSql = "INSERT INTO EndOfDayReport (date, total_sales, total_payments, total_discounts, payment_breakdown) " +
                                      "VALUES (CURRENT_DATE, ?, ?, ?, ?)";
                                      
                    try (PreparedStatement insertPs = conn.prepareStatement(insertSql)) {
                        insertPs.setBigDecimal(1, totalSales);
                        insertPs.setBigDecimal(2, totalPayments);
                        insertPs.setBigDecimal(3, totalDiscounts);
                        insertPs.setString(4, paymentBreakdown);
                        
                        return insertPs.executeUpdate() > 0;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static boolean insertCustomer(Customer customer) {
        String query = "INSERT INTO Customers (customer_name, customer_email, contact, loyalty_points) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, customer.getCustomerName());
            stmt.setString(2, customer.getCustomerEmail());
            stmt.setString(3, customer.getContact());
            stmt.setInt(4, customer.getLoyaltyPoints());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String query = "SELECT * FROM Customers"; // Make sure this table name is correct for your SQLite DB

        try (Connection conn = getConnection(); // Use your existing getConnection method
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                // Get the customer fields from the result set
                int id = rs.getInt("customer_id");
                String fullName = rs.getString("customer_name");
                String email = rs.getString("customer_email");
                String phoneNumber = rs.getString("contact");
                
                // Check if the address column exists before trying to retrieve it
                String address = null;
                try {
                    address = rs.getString("address");
                } catch (SQLException e) {
                    // Address column doesn't exist, that's okay
                }
                
                int loyaltyPoints = rs.getInt("loyalty_points");

                // Create a Customer object and add it to the list
                Customer customer = new Customer(id, fullName, email, phoneNumber, address, loyaltyPoints);
                customers.add(customer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }
    public static Customer getCustomerByID(int customerId) {
        String query = "SELECT * FROM Customers WHERE customer_id = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Customer(
                    rs.getInt("customer_id"),
                    rs.getString("customer_name"),
                    rs.getString("customer_email"),
                    rs.getString("contact"),
                    null,  // Provide null for address
                    rs.getInt("loyalty_points")
                );
            } else {
                System.out.println("Customer with ID " + customerId + " not found");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Map<String, Object>> getHourlyTransactionsForTheDay() throws SQLException {
        String query = "SELECT " +
            "strftime('%H', transaction_date) as hour, " +
            "SUM(total_amount) as total_amount " +
            "FROM Transactions " +
            "WHERE date(transaction_date) = date('now') " +
            "GROUP BY hour " +
            "ORDER BY hour";
        
        List<Map<String, Object>> hourlyTransactions = new ArrayList<>();
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Map<String, Object> hourData = new HashMap<>();
                hourData.put("hour", rs.getString("hour") + ":00");
                hourData.put("total_amount", rs.getBigDecimal("total_amount"));
                hourlyTransactions.add(hourData);
            }
        }
        
        return hourlyTransactions;
    }
    public static boolean updateCustomer(Customer customer) {
        String query = "UPDATE Customers SET customer_name = ?, customer_email = ?, contact = ?, loyalty_points = ? WHERE customer_id = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, customer.getCustomerName());
            stmt.setString(2, customer.getCustomerEmail());
            stmt.setString(3, customer.getContact());
            stmt.setInt(4, customer.getLoyaltyPoints());
            stmt.setInt(5, customer.getCustomerId());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteCustomerByID(int customerId) {
        String query = "DELETE FROM Customers WHERE customer_id = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, customerId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static Map<String, String> getPaymentDetailsByOrderId(int orderId) {
        String query = "SELECT payment_type, transaction_id FROM Payments WHERE order_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                Map<String, String> paymentDetails = new HashMap<>();
                paymentDetails.put("paymentType", rs.getString("payment_type"));
                paymentDetails.put("transactionId", rs.getString("transaction_id"));
                return paymentDetails;
            }
        } catch (SQLException e) {
            System.err.println("Error getting payment details for order #" + orderId + ": " + e.getMessage());
            e.printStackTrace();
        }
        
        return null; // Payment details not found
    }

    // Get Order by ID
    public static Order getOrderByID(int orderId) {
        String query = "SELECT * FROM Orders WHERE order_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                	return new Order(
                		    rs.getInt("order_id"),
                		    rs.getInt("customer_id"),
                		    rs.getBigDecimal("total_amount"),
                		    rs.getDate("order_date"),
                		    PaymentStatus.fromDbValue(rs.getString("payment_status")),
                		    rs.getString("order_type"),
                		    rs.getBigDecimal("tax_amount"),
                		    rs.getBigDecimal("final_amount"),
                		    rs.getBigDecimal("discount_amount"),
                		    rs.getString("delivery_type"),
                		    rs.getString("order_status")
                		);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    


    // Get Transaction by ID
    public static Transaction getTransactionByID(String transactionID) {
        String query = "SELECT * FROM Transactions WHERE transaction_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, transactionID);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                // Handle the potential conversion of `order_id` (assuming it's an integer in the database)
                int orderId = rs.getInt("order_id");  // Use getInt() for order_id
                
                PaymentStatus paymentStatus;
                try {
                	paymentStatus = PaymentStatus.fromDbValue(rs.getString("payment_status"));
                } catch (IllegalArgumentException e) {
                    paymentStatus = PaymentStatus.PENDING;
                }
                return new Transaction(
                    rs.getString("transaction_id"),
                    orderId,  // Corrected to pass an int
                    rs.getBigDecimal("total_amount"),
                    rs.getString("payment_method"),
                    rs.getString("transaction_date"),
                    paymentStatus
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Transaction not found
    }


    // Get Payment by Transaction ID
    public static Payment getPaymentByTransactionID(String transactionID) {
        String query = "SELECT * FROM Payments WHERE transaction_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, transactionID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Payment(
                    rs.getString("transaction_id"),
                    rs.getBigDecimal("amount"),
                    PaymentType.valueOf(rs.getString("payment_type"))
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Payment not found
    }

    // Get Order Item by ID
    public static OrderItem getOrderItemByID(int orderItemId) {
        String query = "SELECT * FROM Order_Items WHERE order_item_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, orderItemId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new OrderItem(
                    rs.getInt("order_item_id"),
                    rs.getInt("order_id"),
                    rs.getInt("product_id"),
                    rs.getInt("quantity"),
                    rs.getBigDecimal("price"),
                    rs.getBigDecimal("subtotal")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Order item not found
    }

    // Get Order Items by Order ID
    public static List<OrderItem> getOrderItemsByOrderID(int orderId) {
        List<OrderItem> orderItems = new ArrayList<>();
        String query = "SELECT * FROM Order_Items WHERE order_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                orderItems.add(new OrderItem(
                    rs.getInt("order_item_id"),
                    rs.getInt("order_id"),
                    rs.getInt("product_id"),
                    rs.getInt("quantity"),
                    rs.getBigDecimal("price"),
                    rs.getBigDecimal("subtotal")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderItems;
    }
}
