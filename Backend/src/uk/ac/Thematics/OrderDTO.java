package uk.ac.Thematics;

import java.math.BigDecimal;
import java.util.List;

//Order DTO for transferring order data
class OrderDTO {
    private int customerId;
    private BigDecimal totalAmount;
    private String orderType;
    private BigDecimal taxAmount;
    private BigDecimal finalAmount;
    private BigDecimal discountAmount;
    private String deliveryType;
    private List<OrderItemDTO> orderItems;

    // Constructors
    public OrderDTO() {}

    public OrderDTO(int customerId, BigDecimal totalAmount, String orderType, 
                    BigDecimal taxAmount, BigDecimal finalAmount, 
                    BigDecimal discountAmount, String deliveryType, 
                    List<OrderItemDTO> orderItems) {
        this.customerId = customerId;
        this.totalAmount = totalAmount;
        this.orderType = orderType;
        this.taxAmount = taxAmount;
        this.finalAmount = finalAmount;
        this.discountAmount = discountAmount;
        this.deliveryType = deliveryType;
        this.orderItems = orderItems;
    }

    // Getters and Setters
    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public String getOrderType() { return orderType; }
    public void setOrderType(String orderType) { this.orderType = orderType; }

    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }

    public BigDecimal getFinalAmount() { return finalAmount; }
    public void setFinalAmount(BigDecimal finalAmount) { this.finalAmount = finalAmount; }

    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }

    public String getDeliveryType() { return deliveryType; }
    public void setDeliveryType(String deliveryType) { this.deliveryType = deliveryType; }

    public List<OrderItemDTO> getOrderItems() { return orderItems; }
    public void setOrderItems(List<OrderItemDTO> orderItems) { this.orderItems = orderItems; }
}

// OrderItemDTO Class

