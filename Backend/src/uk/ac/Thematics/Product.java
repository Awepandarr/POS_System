package uk.ac.Thematics;
import org.json.JSONObject;
import java.math.BigDecimal;

public class Product {
    private int productId;
    private String name;
    private BigDecimal price;
    private int categoryId;
    private int stockQuantity;
    private String barcode;
    private String image_url;
    
    // Constructor
    public Product(int productId, String name, BigDecimal price, int categoryId, int stockQuantity, String barcode, String image_url) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.categoryId = categoryId;
        this.stockQuantity = stockQuantity;
        this.barcode = barcode;
        this.image_url = image_url;
    }
    
    // Getters
    public int getProductId() { return productId; }
    public String getName() { return name; }
    public BigDecimal getPrice() { return price; }
    public int getCategoryId() { return categoryId; }
    public int getStockQuantity() { return stockQuantity; }
    public String getBarcode() { return barcode; }
    public String getImageUrl() { return image_url; }  // Added getter for image_url
 
    // Setter for productId
    public void setProductId(int productId) {
        this.productId = productId;
    }
    public void setStockQuantity(int stockQuantity) {
    	this.stockQuantity=stockQuantity;
    	
    }
    
    // Convert to JSON
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("productId", productId);
        json.put("name", name);
        json.put("price", price);
        json.put("categoryId", categoryId);
        json.put("stockQuantity", stockQuantity);
        json.put("barcode", barcode);
        json.put("image_url", image_url);  // Added image_url to JSON output
        return json;
    }
}
