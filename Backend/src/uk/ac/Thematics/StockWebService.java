package uk.ac.Thematics;

import static spark.Spark.*;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.List;

public class StockWebService {

    public StockWebService() {
        // Route to get current stock for a product
        get("/api/stock/:productId", (req, res) -> {
            try {
                int productId = Integer.parseInt(req.params(":productId"));
                Product product = Database.getProductByID(productId);
                
                if (product == null) {
                    res.status(404);
                    return "Product not found";
                }
                
                JSONObject response = new JSONObject();
                response.put("productId", product.getProductId());
                response.put("name", product.getName());
                response.put("stockQuantity", product.getStockQuantity());
                
                res.type("application/json");
                return response.toString();
            } catch (NumberFormatException e) {
                res.status(400);
                return "Invalid product ID format";
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return "Internal server error: " + e.getMessage();
            }
        });

        // Route to update stock quantity
        put("/api/stock/:productId", (req, res) -> {
            try {
                JSONObject requestBody = new JSONObject(req.body());
                int productId = Integer.parseInt(req.params(":productId"));
                
                if (!requestBody.has("stockQuantity")) {
                    res.status(400);
                    return "Stock quantity must be specified";
                }
                
                int stockQuantity = requestBody.getInt("stockQuantity");
                if (stockQuantity < 0) {
                    res.status(400);
                    return "Stock quantity cannot be negative";
                }
                
                // Get the product
                Product product = Database.getProductByID(productId);
                if (product == null) {
                    res.status(404);
                    return "Product not found";
                }
                
                // Update the stock quantity
                int previousQuantity = product.getStockQuantity();
                product.setStockQuantity(stockQuantity);
                
                // Update the product in the database
                boolean updated = Database.updateProduct(product);
                if (!updated) {
                    res.status(500);
                    return "Failed to update product";
                }
                
                // Return the updated stock
                JSONObject response = new JSONObject();
                response.put("productId", product.getProductId());
                response.put("name", product.getName());
                response.put("previousQuantity", previousQuantity);
                response.put("newQuantity", stockQuantity);
                
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

        // Route to adjust stock (increment or decrement)
        post("/api/stock/:productId/adjust", (req, res) -> {
            try {
                JSONObject requestBody = new JSONObject(req.body());
                int productId = Integer.parseInt(req.params(":productId"));
                
                if (!requestBody.has("adjustment")) {
                    res.status(400);
                    return "Stock adjustment must be specified";
                }
                
                int adjustment = requestBody.getInt("adjustment");
                
                // Get the product
                Product product = Database.getProductByID(productId);
                if (product == null) {
                    res.status(404);
                    return "Product not found";
                }
                
                // Calculate new quantity
                int previousQuantity = product.getStockQuantity();
                int newQuantity = previousQuantity + adjustment;
                
                // Validate new quantity
                if (newQuantity < 0) {
                    res.status(400);
                    return "Stock quantity cannot go below zero";
                }
                
                // Update the stock quantity
                product.setStockQuantity(newQuantity);
                
                // Update the product in the database
                boolean updated = Database.updateProduct(product);
                if (!updated) {
                    res.status(500);
                    return "Failed to update product";
                }
                
                // Return the updated stock
                JSONObject response = new JSONObject();
                response.put("productId", product.getProductId());
                response.put("name", product.getName());
                response.put("previousQuantity", previousQuantity);
                response.put("adjustment", adjustment);
                response.put("newQuantity", newQuantity);
                
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

        // Route to get low stock items (below threshold)
        get("/api/stock/low-stock", (req, res) -> {
            try {
                // Default threshold is 10 unless specified
                int threshold = 10;
                if (req.queryParams("threshold") != null) {
                    threshold = Integer.parseInt(req.queryParams("threshold"));
                }
                
                List<Product> products = Database.getAllProducts();
                JSONArray lowStockProducts = new JSONArray();
                
                // Filter for low stock
                for (Product product : products) {
                    if (product.getStockQuantity() <= threshold) {
                        JSONObject productJson = new JSONObject();
                        productJson.put("productId", product.getProductId());
                        productJson.put("name", product.getName());
                        productJson.put("stockQuantity", product.getStockQuantity());
                        productJson.put("threshold", threshold);
                        lowStockProducts.put(productJson);
                    }
                }
                
                JSONObject response = new JSONObject();
                response.put("threshold", threshold);
                response.put("count", lowStockProducts.length());
                response.put("products", lowStockProducts);
                
                res.type("application/json");
                return response.toString();
            } catch (NumberFormatException e) {
                res.status(400);
                return "Invalid threshold format";
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return "Internal server error: " + e.getMessage();
            }
        });

        // Route to update stock for multiple products at once (batch update)
        post("/api/stock/batch-update", (req, res) -> {
            try {
                JSONObject requestBody = new JSONObject(req.body());
                
                if (!requestBody.has("products")) {
                    res.status(400);
                    return "Products array must be specified";
                }
                
                JSONArray products = requestBody.getJSONArray("products");
                JSONArray results = new JSONArray();
                boolean allSuccessful = true;
                
                // Process each product update
                for (int i = 0; i < products.length(); i++) {
                    JSONObject productUpdate = products.getJSONObject(i);
                    
                    if (!productUpdate.has("productId") || !productUpdate.has("stockQuantity")) {
                        JSONObject errorResult = new JSONObject();
                        errorResult.put("error", "Product update missing required fields");
                        errorResult.put("productUpdate", productUpdate);
                        results.put(errorResult);
                        allSuccessful = false;
                        continue;
                    }
                    
                    int productId = productUpdate.getInt("productId");
                    int stockQuantity = productUpdate.getInt("stockQuantity");
                    
                    // Get the product
                    Product product = Database.getProductByID(productId);
                    if (product == null) {
                        JSONObject errorResult = new JSONObject();
                        errorResult.put("error", "Product not found");
                        errorResult.put("productId", productId);
                        results.put(errorResult);
                        allSuccessful = false;
                        continue;
                    }
                    
                    // Update the stock quantity
                    int previousQuantity = product.getStockQuantity();
                    product.setStockQuantity(stockQuantity);
                    
                    // Update the product in the database
                    boolean updated = Database.updateProduct(product);
                    if (!updated) {
                        JSONObject errorResult = new JSONObject();
                        errorResult.put("error", "Failed to update product");
                        errorResult.put("productId", productId);
                        results.put(errorResult);
                        allSuccessful = false;
                        continue;
                    }
                    
                    // Add success result
                    JSONObject successResult = new JSONObject();
                    successResult.put("productId", productId);
                    successResult.put("name", product.getName());
                    successResult.put("previousQuantity", previousQuantity);
                    successResult.put("newQuantity", stockQuantity);
                    results.put(successResult);
                }
                
                // Return the results
                JSONObject response = new JSONObject();
                response.put("success", allSuccessful);
                response.put("totalProcessed", products.length());
                response.put("results", results);
                
                res.type("application/json");
                return response.toString();
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return "Internal server error: " + e.getMessage();
            }
        });
    }
}
