package uk.ac.Thematics;
import static spark.Spark.*;
import org.json.JSONObject;

public class BarcodeWebService {

    public BarcodeWebService() {
        // Define endpoint for barcode scanning
        get("/product", (request, response) -> {
            String barcode = request.queryParams("barcode");
            if (barcode == null || barcode.isEmpty()) {
                response.status(400);  // Bad request if barcode is missing
                return "Barcode is required";
            }

            try {
                // Query product details from the database using the barcode
                Product product = Database.getProductDetailsByBarcode(barcode);

                // If no product is found, return a message
                if (product == null) {
                    response.status(404);  // Not found
                    return "Product not found";
                }

                response.header("Content-Type", "application/json");
                return product.toJSON().toString();  // Return the product details as JSON
            } catch (Exception e) {
                response.status(500);  // Internal server error
                return "Error retrieving product details";
            }
        });
    }
}
