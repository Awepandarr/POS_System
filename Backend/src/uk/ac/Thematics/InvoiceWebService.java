package uk.ac.Thematics;

import org.json.JSONObject;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import static spark.Spark.*;

public class InvoiceWebService {
    public InvoiceWebService() {
        
        // Create an invoice manually
        post("/invoice", (request, response) -> {
            try {
                JSONObject json = new JSONObject(request.body());
                int orderId = json.getInt("orderId");
                int customerId = json.getInt("customerId");
                BigDecimal subtotal = new BigDecimal(json.getString("subtotal"));
                BigDecimal discount = new BigDecimal(json.getString("discount"));
                BigDecimal taxAmount = new BigDecimal(json.getString("taxAmount"));
                BigDecimal serviceCharge = new BigDecimal(json.getString("serviceCharge"));
                BigDecimal finalAmount = new BigDecimal(json.getString("finalAmount"));
                
                // Use provided invoice date or current date
                String invoiceDate = json.has("invoiceDate") ? 
                    json.getString("invoiceDate") : 
                    LocalDateTime.now().toString();
                
                // Create invoice with correct values
                Invoice invoice = new Invoice(
                    0,          // ID will be set by database
                    orderId,
                    invoiceDate,
                    subtotal,   // Subtotal as provided
                    taxAmount,  // Tax amount
                    finalAmount, // Final amount
                    customerId  // Customer ID
                );
                
                // Check if an invoice already exists for this order
                if (Database.getInvoiceCountForOrder(orderId) > 0) {
                    response.status(409); // Conflict
                    return new JSONObject()
                        .put("message", "An invoice already exists for this order")
                        .toString();
                }
                
                // Insert the invoice
                boolean inserted = Database.insertInvoice(invoice);
                
                if (inserted) {
                    response.status(201);
                    response.type("application/json");
                    return new JSONObject()
                        .put("message", "Invoice created successfully")
                        .put("invoiceId", invoice.getInvoiceId())
                        .put("formattedInvoiceNumber", invoice.getFormattedInvoiceNumber())
                        .toString();
                } else {
                    response.status(500);
                    return new JSONObject()
                        .put("message", "Error creating invoice")
                        .toString();
                }
            } catch (Exception e) {
                e.printStackTrace();
                response.status(400);
                return new JSONObject()
                    .put("message", "Invalid input data: " + e.getMessage())
                    .toString();
            }
        });

        // Get an invoice by ID
        get("/invoice/:id", (request, response) -> {
            int invoiceId = Integer.parseInt(request.params(":id"));
            Invoice invoice = Database.getInvoiceByID(invoiceId);
            
            if (invoice == null) {
                response.status(404);
                return new JSONObject()
                    .put("message", "Invoice not found")
                    .toString();
            }
            
            response.type("application/json");
            return invoice.toJSON().toString();
        });
        
        // Get all invoices for today (for end-of-day reporting)
        get("/invoices/today", (request, response) -> {
            try {
                JSONObject result = new JSONObject();
                result.put("invoices", Database.getInvoiceStatisticsForToday());
                result.put("date", LocalDateTime.now().toString());
                
                response.type("application/json");
                return result.toString();
            } catch (Exception e) {
                e.printStackTrace();
                response.status(500);
                return new JSONObject()
                    .put("message", "Error retrieving invoices: " + e.getMessage())
                    .toString();
            }
        });
        
        // Generate invoice for a specific payment
        post("/invoice/fromPayment/:transactionId", (request, response) -> {
            String transactionId = request.params(":transactionId");
            
            try {
                // Get payment details
                Payment payment = Database.getPaymentByTransactionID(transactionId);
                if (payment == null) {
                    response.status(404);
                    return new JSONObject()
                        .put("message", "Payment not found")
                        .toString();
                }
                
                // Get order details
                int orderId = payment.getOrderId();
                Order order = Database.getOrderByID(orderId);
                if (order == null) {
                    response.status(404);
                    return new JSONObject()
                        .put("message", "Order not found")
                        .toString();
                }
                
                // Check if invoice already exists
                if (Database.getInvoiceCountForOrder(orderId) > 0) {
                    response.status(409); // Conflict
                    return new JSONObject()
                        .put("message", "An invoice already exists for this order")
                        .toString();
                }
                
                // Create invoice
                Invoice invoice = new Invoice(
                    0,
                    orderId,
                    LocalDateTime.now().toString(),
                    order.getTotalAmount(),
                    order.getTaxAmount(),
                    order.getFinalAmount(),
                    order.getCustomerId()
                );
                
                boolean inserted = Database.insertInvoice(invoice);
                
                if (inserted) {
                    response.status(201);
                    response.type("application/json");
                    return new JSONObject()
                        .put("message", "Invoice created successfully")
                        .put("invoiceId", invoice.getInvoiceId())
                        .toString();
                } else {
                    response.status(500);
                    return new JSONObject()
                        .put("message", "Error creating invoice")
                        .toString();
                }
            } catch (Exception e) {
                e.printStackTrace();
                response.status(500);
                return new JSONObject()
                    .put("message", "Error creating invoice: " + e.getMessage())
                    .toString();
            }
        });
    }
}
