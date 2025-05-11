package uk.ac.Thematics;

import java.math.BigDecimal;
import org.json.JSONObject;

public class Payment {
    private int paymentId;
    private String transactionId;
    private int orderId;
    private BigDecimal paymentAmount;
    private PaymentType paymentType;
    private PaymentStatus paymentStatus;
    private String dbPaymentType;

    // Constructor
    public Payment(String transactionId, BigDecimal paymentAmount, PaymentType paymentType) {
        this.transactionId = transactionId;
        this.paymentAmount = paymentAmount;
        this.paymentType = paymentType;
        this.paymentStatus = PaymentStatus.PENDING; // Default status
        
        // Set the DB payment type string based on the enum
        this.dbPaymentType = mapPaymentTypeToDbValue(paymentType);
    }

    // Map PaymentType enum to database value
    private String mapPaymentTypeToDbValue(PaymentType type) {
        switch (type) {
            case CARD:
                return "CARD";
            case CASH:
                return "CASH";
            default:
                return "CARD"; // Default value
        }
    }

    // Getters and Setters
    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public BigDecimal getPaymentAmount() {
        return paymentAmount;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getDbPaymentType() {
        return dbPaymentType;
    }

    public void setDbPaymentType(String dbPaymentType) {
        this.dbPaymentType = dbPaymentType;
    }

    // Convert to JSON for API responses
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("paymentId", paymentId);
        json.put("transactionId", transactionId);
        json.put("orderId", orderId);
        json.put("paymentAmount", paymentAmount);
        json.put("paymentType", dbPaymentType);
        json.put("paymentStatus", paymentStatus.getDbValue());
        return json;
    }
}