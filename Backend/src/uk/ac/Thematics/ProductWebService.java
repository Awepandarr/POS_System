package uk.ac.Thematics;

import static spark.Spark.*;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class ProductWebService {

    public ProductWebService() {
        // Get all products
        get("/api/products", (req, res) -> {
            try {
                List<Product> products = Database.getAllProducts(); // Fetch all products
                if (products == null || products.isEmpty()) {
                    res.status(404);
                    return "No products found";
                }
                res.type("application/json");
                JSONArray productArray = new JSONArray();
                for (Product product : products) {
                    productArray.put(product.toJSON()); // Convert each product to JSON
                }
                return productArray.toString(); // Return the array of products as a JSON string
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500); // Internal server error
                return "Internal Server Error: " + e.getMessage();
            }
        });
        
        // Route to get product by barcode - FIXED PATH
        get("/api/product/barcode/:barcode", (req, res) -> {
            String barcode = req.params(":barcode");
            Product product = Database.getProductDetailsByBarcode(barcode);
            if (product == null) {
                res.status(404);
                return "Product not found";
            }
            res.type("application/json");
            return product.toJSON().toString();
        });

        // Route to create a new product
        post("/api/product", (req, res) -> {
            try {
                JSONObject requestBody = new JSONObject(req.body());
                Product product = new Product(
                    0, // Will be set by the database
                    requestBody.getString("name"),
                    requestBody.getBigDecimal("price"),
                    requestBody.getInt("categoryId"),
                    requestBody.getInt("stockQuantity"),
                    requestBody.getString("barcode"),
                    requestBody.getString("image_url")
                );

                boolean success = Database.insertProduct(product);
                if (success) {
                    res.status(201);
                    res.type("application/json");
                    return product.toJSON().toString(); // Return the created product
                } else {
                    res.status(400);
                    return "Failed to create product";
                }
            } catch (Exception e) {
                e.printStackTrace();  // Log the stack trace to the console
                res.status(500);      // Internal server error status code
                return "Internal Server Error: " + e.getMessage();
            }
        });

        // Route to update an existing product
        put("/api/product/:productId", (req, res) -> {
            try {
                JSONObject requestBody = new JSONObject(req.body());
                Product product = new Product(
                    Integer.parseInt(req.params(":productId")),
                    requestBody.getString("name"),
                    requestBody.getBigDecimal("price"),
                    requestBody.getInt("categoryId"),
                    requestBody.getInt("stockQuantity"),
                    requestBody.getString("barcode"),
                    requestBody.optString("image_url", "") // Optional image URL parameter
                );

                boolean success = Database.updateProduct(product);
                if (success) {
                    res.status(200);
                    res.type("application/json");
                    return product.toJSON().toString();
                } else {
                    res.status(404);
                    return "Product not found";
                }
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return "Internal Server Error: " + e.getMessage();
            }
        });

        // Route to delete a product by ID
        delete("/api/product/:productId", (req, res) -> {
            int productId = Integer.parseInt(req.params(":productId"));
            boolean success = Database.deleteProductByID(productId);
            if (success) {
                res.status(200);
                return "Product deleted successfully";
            } else {
                res.status(404);
                return "Product not found";
            }
        });

        // Route to get product by ID
        get("/api/product/id/:productId", (req, res) -> {
            int productId = Integer.parseInt(req.params(":productId"));
            Product product = Database.getProductByID(productId);
            if (product == null) {
                res.status(404);
                return "Product not found";
            }
            res.type("application/json");
            return product.toJSON().toString();
        });
        
        // Original barcode endpoint for backward compatibility
        get("/product", (req, res) -> {
            String barcode = req.queryParams("barcode");
            if (barcode == null || barcode.isEmpty()) {
                res.status(400);
                return "Barcode is required";
            }
            
            Product product = Database.getProductDetailsByBarcode(barcode);
            if (product == null) {
                res.status(404);
                return "Product not found";
            }
            
            res.type("application/json");
            return product.toJSON().toString();
        });

        // Get product image URL by product ID
        get("/api/product/image/:productId", (req, res) -> {
            try {
                int productId = Integer.parseInt(req.params(":productId"));
                Product product = Database.getProductByID(productId);
                
                if (product == null) {
                    res.status(404);
                    return "Product not found";
                }
                
                String imageUrl = product.getImageUrl();
                if (imageUrl == null || imageUrl.isEmpty()) {
                    res.status(404);
                    return "Image URL not found for this product";
                }
                
                res.type("application/json");
                JSONObject response = new JSONObject();
                response.put("productId", productId);
                response.put("imageUrl", imageUrl);
                
                return response.toString();
            } catch (NumberFormatException e) {
                res.status(400);
                return "Invalid product ID format";
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return "Internal Server Error: " + e.getMessage();
            }
        });
    }
}

