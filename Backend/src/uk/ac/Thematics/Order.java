package uk.ac.Thematics;

import org.json.JSONObject;
import java.math.BigDecimal;
import java.util.Date;

public class Order {
    private int orderId;
    private int customerId;
    private BigDecimal totalAmount;
    PaymentStatus paymentStatus;  // Change this to PaymentStatus
    private String orderType;
    private BigDecimal taxAmount;
    private BigDecimal finalAmount;
    private BigDecimal discountAmount;
    private String deliveryType;
    private String orderStatus;
    private Date order_date;

    // Constructor updated to accept PaymentStatus enum
    public Order(int orderId, int customerId, BigDecimal totalAmount,Date order_date, PaymentStatus paymentStatus, 
            String orderType, BigDecimal taxAmount, BigDecimal finalAmount, 
            BigDecimal discountAmount, String deliveryType, String orderStatus) {
   this.orderId = orderId;
   this.customerId = customerId;
   this.totalAmount = totalAmount;
   this.order_date=order_date;
   this.paymentStatus = paymentStatus;
   this.orderType = orderType;  // Make sure this accepts a String that matches the DB constraint
   this.taxAmount = taxAmount;
   this.finalAmount = finalAmount;
   this.discountAmount = discountAmount;
   this.deliveryType = deliveryType;
   this.orderStatus = orderStatus;
}
    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
    // Getters
    public int getOrderId() { return orderId; }
    public int getCustomerId() { return customerId; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }  // Updated return type
    public String getOrderType() { return orderType; }
    public BigDecimal getTaxAmount() { return taxAmount; }
    public BigDecimal getFinalAmount() { return finalAmount; }
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public String getDeliveryType() { return deliveryType; }
    public String getOrderStatus() { return orderStatus; }

    // Setter for orderId
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    // Convert to JSON
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("orderId", orderId);
        json.put("customerId", customerId);
        json.put("totalAmount", totalAmount);
        json.put("orderDate", this.order_date!= null ? this.order_date.toString() : "");
        json.put("paymentStatus", paymentStatus.name());  // Convert PaymentStatus enum to string
        json.put("orderType", orderType);
        json.put("taxAmount", taxAmount);
        json.put("finalAmount", finalAmount);
        json.put("discountAmount", discountAmount);
        json.put("deliveryType", deliveryType);
        json.put("orderStatus", orderStatus);
        return json;
    }
	public Date getOrder_date() {
		return order_date;
	}
	public void setOrder_date(Date order_date) {
		this.order_date = order_date;
	}
}