package uk.ac.Thematics;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

public class Transaction {
    private String transactionID; // Transaction ID
    private int orderID; // Link to the order
protected BigDecimal totalAmount; // Total amount of the transaction
    protected String paymentMethod; // Payment method used
    private List<Product> products; // List of purchased products
    protected String transactionDate; // Date of the transaction
    protected PaymentStatus paymentStatus; // Payment status of the transaction

    // Constructor
    public Transaction(String transactionID, int orderID, BigDecimal totalAmount, String paymentMethod, String transactionDate, PaymentStatus paymentStatus) {
        this.transactionID = transactionID;
        this.orderID = orderID;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.transactionDate = transactionDate;
        this.paymentStatus = paymentStatus;
        this.products = new ArrayList<>();
    }

    // Method to add a product to the transaction
    public void addProduct(Product product) {
        products.add(product);
        recalculateTotalAmount();
    }

    // Method to recalculate the total amount
    public void recalculateTotalAmount() {
        totalAmount = BigDecimal.ZERO;
        for (Product product : products) {
            totalAmount = totalAmount.add(product.getPrice());  // Directly using the BigDecimal price
        }
    }

    // Method to process the payment
    public boolean processPayment(Payment payment) {
        // Check if the payment amount is sufficient
        if (payment.getPaymentAmount().compareTo(totalAmount) >= 0) {
            // If the payment is sufficient, set the payment status to PAID
            this.paymentStatus = PaymentStatus.PAID;
            this.paymentMethod = payment.getPaymentType().toString();  // Store the payment method
            return true; // Payment successful
        }
        // If the payment is insufficient, set the payment status to FAILED
        this.paymentStatus = PaymentStatus.FAILED;
        return false; // Payment failed
    }

    // Getter for transaction ID
    public String getTransactionID() {
        return transactionID;
    }

    // Getter for order ID
    public int getOrderID() {
        return orderID;
    }

    // Getter for total amount
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    // Getter for payment method
    public String getPaymentMethod() {
        return paymentMethod;
    }

    // Getter for transaction date
    public String getTransactionDate() {
        return transactionDate;
    }

    // Getter for the list of products
    public List<Product> getProducts() {
        return products;
    }

    // Getter for payment status
    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("transactionId", transactionID);
        json.put("orderId", orderID);
        json.put("totalAmount", totalAmount);
        json.put("paymentMethod", paymentMethod);
        json.put("transactionDate", transactionDate);
        json.put("paymentStatus", paymentStatus.name());  // Assuming PaymentStatus is an enum
        // If you want to include product details as well, you could loop through the list of products and add them to the JSON object.
        return json;
    }
}
