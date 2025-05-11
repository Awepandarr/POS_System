package uk.ac.Thematics;
import java.math.BigDecimal;
import java.util.Date;

class OrderItemDTO {
    private int productId;
    private int quantity;
    private BigDecimal price;
    private BigDecimal subtotal;
    private String orderDate;
    // Constructors
    public OrderItemDTO() {}

    public OrderItemDTO(int productId, int quantity, BigDecimal price, BigDecimal subtotal,String orderDate) {
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
        this.subtotal = subtotal; 
        this.setOrderDate(orderDate);
        }

    // Getters and Setters
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

	public String getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}


}
