
package uk.ac.Thematics;

import java.math.BigDecimal;

public class OrderItem {
    private int orderItemId;
    private int orderId;
    private int productId;
    private int quantity;
    private BigDecimal price;
    private BigDecimal subtotal;

    // Constructor
    public OrderItem(int orderItemId, int orderId, int productId, int quantity, BigDecimal price, BigDecimal subtotal) {
        this.orderItemId = orderItemId;
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
        this.subtotal = subtotal;
    }

    // Getters
    public int getOrderItemId() {
        return orderItemId;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    // Setters
    public void setOrderItemId(int orderItemId) {
        this.orderItemId = orderItemId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
}

