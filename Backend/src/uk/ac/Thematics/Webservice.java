package uk.ac.Thematics;

import static spark.Spark.*;

import java.sql.Connection;

import org.json.JSONObject;

public class Webservice {

    public static void main(String[] args) {
        port(8080); // Set port for the server
        System.out.println("Starting Webservice on port 8080...");

        // Enable CORS (Cross-Origin Resource Sharing) for all origins
        before((req, res) -> {
            res.header("Access-Control-Allow-Origin", "*"); // Allow all origins
            res.header("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With"); // Allow headers
            res.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS"); // Allow methods
        });
     
        get("/health", (req, res) -> {
            // Test database connection
            boolean dbConnected = false;
            try (Connection conn = Database.getConnection()) {
                dbConnected = conn != null && !conn.isClosed();
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            JSONObject health = new JSONObject();
            health.put("status", "OK");
            health.put("database", dbConnected ? "connected" : "disconnected");
            res.type("application/json");
            return health.toString();
        });

        // Handle the OPTIONS preflight requests explicitly for all routes
        options("/*", (req, res) -> {
            res.status(200);
            return "OK";
        });

        // Register the web services
        new ProductWebService();
        System.out.println("Product WebService initialized");

        new OrderWebService();
        System.out.println("Order WebService initialized");

        new PaymentWebService();
        System.out.println("Payment WebService initialized");
        
        new OrderItemWebService();
        System.out.println("Order Item WebService initialized");

        new TransactionWebService();
        System.out.println("Transaction WebService initialized");
        
        new BarcodeWebService();
        System.out.println("Barcode WebService initialized");
        
        new CustomerWebService();
        System.out.println("Customer WebService initialized");
        
        new InvoiceWebService();
        System.out.println("Invoice WebService initialized");
        
        new EndOfDayReport();
        System.out.println("End Of Day Report initialized");
        
        // Initialize the new services
        new LoyaltyPointsWebService();
        System.out.println("Loyalty Points WebService initialized");
        
        new StockWebService();
        System.out.println("Stock WebService initialized");

        System.out.println("Webservice started successfully on port 8080.");
    }
}