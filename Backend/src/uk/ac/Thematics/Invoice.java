package uk.ac.Thematics;

import org.json.JSONObject;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Enhanced Invoice class with improved functionality
 */
public class Invoice {
    private int invoiceId;
    private int orderId;
    private String invoiceDate;
    private BigDecimal totalAmount;
    private BigDecimal taxAmount;
    private BigDecimal finalAmount;
    private int customerId;
    private String invoiceStatus;
    
    /**
     * Constructor for Invoice object
     */
    public Invoice(int invoiceId, int orderId, String invoiceDate, 
                  BigDecimal totalAmount, BigDecimal taxAmount, 
                  BigDecimal finalAmount, int customerId) {
        this.invoiceId = invoiceId;
        this.orderId = orderId;
        this.invoiceDate = invoiceDate;
        this.totalAmount = totalAmount;
        this.taxAmount = taxAmount;
        this.finalAmount = finalAmount;
        this.customerId = customerId;
        this.invoiceStatus = "ISSUED";
    }

    // Getter methods
    public int getInvoiceId() {
        return invoiceId;
    }

    public int getOrderId() {
        return orderId;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public BigDecimal getFinalAmount() {
        return finalAmount;
    }
    
    public int getCustomerId() {
        return customerId;
    }
    
    public String getInvoiceStatus() {
        return invoiceStatus;
    }

    // Setter methods
    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public void setFinalAmount(BigDecimal finalAmount) {
        this.finalAmount = finalAmount;
    }
    
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
    
    public void setInvoiceStatus(String invoiceStatus) {
        this.invoiceStatus = invoiceStatus;
    }

    /**
     * Calculate the discount amount for this invoice
     * @return BigDecimal representing the discount amount
     */
    public BigDecimal getDiscountAmount() {
        // If totalAmount + taxAmount = finalAmount, then there's no discount
        BigDecimal calculatedTotal = totalAmount.add(taxAmount);
        if (calculatedTotal.compareTo(finalAmount) <= 0) {
            return BigDecimal.ZERO;
        }
        
        // Otherwise, calculate discount
        return calculatedTotal.subtract(finalAmount);
    }
    
    /**
     * Get the formatted invoice number with proper formatting
     * @return String formatted invoice number
     */
    public String getFormattedInvoiceNumber() {
        // Format: INV-YYYYMMDD-XXXX where XXXX is the invoice ID
        String datePart = "";
        try {
            // Try to parse the date from invoiceDate
            LocalDateTime date = LocalDateTime.parse(invoiceDate);
            datePart = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        } catch (Exception e) {
            // If parsing fails, use current date
            datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        }
        
        // Pad invoice ID with leading zeros to ensure 4 digits
        String invoiceIdFormatted = String.format("%04d", invoiceId);
        
        return "INV-" + datePart + "-" + invoiceIdFormatted;
    }
    
    /**
     * Check if this invoice is associated with a specific customer
     * @param targetCustomerId The customer ID to check
     * @return true if the invoice belongs to the specified customer, false otherwise
     */
    public boolean isForCustomer(int targetCustomerId) {
        return this.customerId == targetCustomerId;
    }
    
    /**
     * Check if the invoice was created today
     * @return true if the invoice was created today, false otherwise
     */
    public boolean isCreatedToday() {
        try {
            LocalDateTime invoiceDateTime = LocalDateTime.parse(invoiceDate);
            LocalDateTime now = LocalDateTime.now();
            
            return invoiceDateTime.toLocalDate().equals(now.toLocalDate());
        } catch (Exception e) {
            // If parsing fails, default to false
            return false;
        }
    }

    /**
     * Convert the Invoice object to JSON format
     * @return JSONObject representing the invoice
     */
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("invoiceId", this.invoiceId);
        json.put("formattedInvoiceNumber", getFormattedInvoiceNumber());
        json.put("orderId", this.orderId);
        json.put("invoiceDate", this.invoiceDate);
        json.put("totalAmount", this.totalAmount);
        json.put("taxAmount", this.taxAmount);
        json.put("discountAmount", getDiscountAmount());
        json.put("finalAmount", this.finalAmount);
        json.put("customerId", this.customerId);
        json.put("invoiceStatus", this.invoiceStatus);
        
        return json;
    }
    
    /**
     * Create a printable representation of the invoice
     * @return String containing invoice details in a formatted way
     */
    public String toPrintableFormat() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("INVOICE: ").append(getFormattedInvoiceNumber()).append("\n");
        sb.append("Date: ").append(invoiceDate).append("\n");
        sb.append("Order ID: ").append(orderId).append("\n");
        sb.append("Customer ID: ").append(customerId).append("\n\n");
        
        sb.append("Subtotal: $").append(totalAmount).append("\n");
        sb.append("Tax: $").append(taxAmount).append("\n");
        
        BigDecimal discount = getDiscountAmount();
        if (discount.compareTo(BigDecimal.ZERO) > 0) {
            sb.append("Discount: $").append(discount).append("\n");
        }
        
        sb.append("-------------------------------\n");
        sb.append("TOTAL: $").append(finalAmount).append("\n");
        sb.append("-------------------------------\n\n");
        
        sb.append("Thank you for your business!");
        
        return sb.toString();
    }
}