package uk.ac.Thematics;

import static spark.Spark.*;
import org.json.JSONObject;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.logging.Logger;
import java.util.Map;
import java.util.logging.Level;

public class PaymentWebService {
    // Add a logger for debugging
    private static final Logger logger = Logger.getLogger(PaymentWebService.class.getName());

    public PaymentWebService() {
        // Route to process payment
        post("/api/payment", (req, res) -> {
            try {
                // Log the request body for debugging
                logger.info("Received payment request: " + req.body());
                
                // Parse incoming payment request
                JSONObject paymentRequest = new JSONObject(req.body());
                
                // Validate required payment fields
                validatePaymentRequest(paymentRequest);

                // Get the order ID and validate order exists
                int orderId = paymentRequest.getInt("orderId");
                Order order = Database.getOrderByID(orderId);
                
                if (order == null) {
                    logger.warning("Order not found with ID: " + orderId);
                    res.status(404);
                    return createErrorResponse("Order not found with ID: " + orderId);
                }

                // Create payment object
                String transactionId = paymentRequest.getString("transactionId");
                BigDecimal amount = new BigDecimal(paymentRequest.get("amount").toString());
                String paymentMethodStr = paymentRequest.getString("paymentMethod");
                
                // Determine payment type
                PaymentType paymentType = determinePaymentType(paymentMethodStr);
                
                // Check if this is a card payment and has card details
                if (paymentType == PaymentType.CARD && paymentRequest.has("cardDetails")) {
                    // Process card payment
                    JSONObject cardDetails = paymentRequest.getJSONObject("cardDetails");
                    
                    // Debug log the card details (in production, you'd never log full card details)
                    logger.info("Processing card payment with last 4 digits: " + 
                        cardDetails.getString("cardNumber").substring(
                            Math.max(0, cardDetails.getString("cardNumber").length() - 4)));
                    
                    // Validate card details
                    if (!validateCardDetails(cardDetails)) {
                        logger.warning("Invalid card details provided");
                        res.status(400);
                        return createErrorResponse("Invalid card details");
                    }
                    
                    // In a real system, you would integrate with a payment gateway here
                    // For testing, we'll use the test card numbers
                    String cardNumber = cardDetails.getString("cardNumber").replaceAll("\\s+", "");
                    
                    // Simulate declined cards for testing
                    if (cardNumber.equals("4000000000000002")) {
                        logger.warning("Test card declined: " + cardNumber);
                        res.status(400);
                        return createErrorResponse("Card declined by issuing bank");
                    }
                    
                    if (cardNumber.equals("4000000000009995")) {
                        logger.warning("Test card has insufficient funds: " + cardNumber);
                        res.status(400);
                        return createErrorResponse("Insufficient funds");
                    }
                    
                    // Check card expiration
                    String expiryDate = cardDetails.getString("expiryDate");
                    if (!isCardValid(expiryDate)) {
                        logger.warning("Card expired: " + expiryDate);
                        res.status(400);
                        return createErrorResponse("Card has expired");
                    }
                }
                
                // Create payment
                Payment payment = new Payment(transactionId, amount, paymentType);
                payment.setOrderId(orderId);
                payment.setPaymentStatus(PaymentStatus.PAID); // Set to PAID since we're processing it now
                order.setOrderStatus("Completed");
                // Save payment to database
                logger.info("Saving payment to database: " + payment.getTransactionId());
                boolean saved = Database.insertPayment(payment);
                
                
             // Inside the post("/api/payment") method, after successful payment insertion
             // Add this code after the line: boolean saved = Database.insertPayment(payment);

             if (saved) {
                 // Create transaction record
                 logger.info("Creating transaction record");
                 createTransactionRecord(payment);
                 
                 // Update order status to reflect payment
                 logger.info("Updating order status");
                 updateOrderStatus(order);
                 
                 // Create invoice for this payment
                 logger.info("Creating invoice for order #" + order.getOrderId());
                 
                 // Create invoice object
                 Invoice invoice = new Invoice(
                     0, // Invoice ID will be auto-generated
                     order.getOrderId(),
                     LocalDateTime.now().toString(), // Current timestamp
                     order.getTotalAmount(),
                     order.getTaxAmount(),
                     order.getFinalAmount(),
                     order.getCustomerId()
                 );
                 
                 // Save invoice to database
                 boolean invoiceSaved = Database.insertInvoice(invoice);
                 logger.info("Invoice created: " + invoiceSaved);
                 
                 // Prepare successful response
                 res.status(201);
                 res.type("application/json");
                 logger.info("Payment processed successfully");
                 return createSuccessResponse(payment);
             } else {
                    // Database save failed
                    logger.severe("Failed to save payment record to database");
                    res.status(500);
                    return createErrorResponse("Failed to save payment record");
                }

            } catch (IllegalArgumentException e) {
                // Invalid request
                logger.warning("Payment validation error: " + e.getMessage());
                res.status(400);
                return createErrorResponse(e.getMessage());
            } catch (Exception e) {
                // Unexpected error
                logger.log(Level.SEVERE, "Unexpected error in payment processing", e);
                res.status(500);
                return createErrorResponse("Unexpected server error: " + e.getMessage());
            }
        });
        get("/api/payment/details/:orderId", (req, res) -> {
            int orderId = Integer.parseInt(req.params(":orderId"));
            logger.info("Get payment details for order ID: " + orderId);
            
            // Query the database to get the payment details
            Map<String, String> paymentDetails = Database.getPaymentDetailsByOrderId(orderId);
            
            if (paymentDetails != null) {
                res.status(200);
                res.type("application/json");
                JSONObject response = new JSONObject();
                response.put("paymentMethod", paymentDetails.get("paymentType"));
                response.put("transactionId", paymentDetails.get("transactionId"));
                return response.toString();
            } else {
                res.status(404);
                return createErrorResponse("Payment details not found for order ID: " + orderId);
            }
        });

        // Route to get payment details
        get("/api/payment/:transactionId", (req, res) -> {
            String transactionId = req.params(":transactionId");
            logger.info("Get payment details for transaction: " + transactionId);
            
            Payment payment = Database.getPaymentByTransactionID(transactionId);
            
            if (payment != null) {
                res.status(200);
                res.type("application/json");
                return payment.toJSON().toString();
            } else {
                res.status(404);
                return createErrorResponse("Payment not found");
            }
        });
    }
    

    private void validatePaymentRequest(JSONObject request) {
        logger.info("Validating payment request");
        
        // Check for required fields
        String[] requiredFields = {
            "transactionId", "orderId", "amount", "paymentMethod"
        };

        for (String field : requiredFields) {
            if (!request.has(field)) {
                logger.warning("Missing required field: " + field);
                throw new IllegalArgumentException("Missing required field: " + field);
            }
        }
        
        // Validate amount is positive
        BigDecimal amount;
        try {
            amount = new BigDecimal(request.get("amount").toString());
        } catch (Exception e) {
            logger.warning("Invalid amount format: " + e.getMessage());
            throw new IllegalArgumentException("Invalid amount format");
        }
        
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            logger.warning("Payment amount must be greater than zero: " + amount);
            throw new IllegalArgumentException("Payment amount must be greater than zero");
        }
        
        // Validate card details if payment method is CARD
        if (request.getString("paymentMethod").equalsIgnoreCase("CARD")) {
            if (!request.has("cardDetails")) {
                logger.warning("Card payment requires card details");
                throw new IllegalArgumentException("Card payment requires card details");
            }
        }
    }

    private boolean validateCardDetails(JSONObject cardDetails) {
        logger.info("Validating card details");
        
        // Check required card fields
        String[] requiredFields = {"cardNumber", "expiryDate", "cvv", "cardholderName"};
        for (String field : requiredFields) {
            if (!cardDetails.has(field)) {
                logger.warning("Missing card detail: " + field);
                return false;
            }
        }
        
        // Validate card number (simple validation)
        String cardNumber = cardDetails.getString("cardNumber").replaceAll("\\s+", "");
        if (cardNumber.length() < 13 || cardNumber.length() > 19) {
            logger.warning("Invalid card number length: " + cardNumber.length());
            return false;
        }
        
        // This would be a good place to add Luhn algorithm validation
        
        // Validate CVV
        String cvv = cardDetails.getString("cvv");
        if (cvv.length() < 3 || cvv.length() > 4) {
            logger.warning("Invalid CVV length: " + cvv.length());
            return false;
        }
        
        // Validate expiry date format
        String expiryDate = cardDetails.getString("expiryDate");
        if (!expiryDate.matches("\\d{2}/\\d{2}")) {
            logger.warning("Invalid expiry date format: " + expiryDate);
            return false;
        }
        
        return true;
    }
    
    private boolean isCardValid(String expiryDate) {
        try {
            // Parse expiry date MM/YY
            String[] parts = expiryDate.split("/");
            int month = Integer.parseInt(parts[0]);
            int year = Integer.parseInt(parts[1]) + 2000; // Convert YY to YYYY
            
            // Create expiry date as last day of month
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime expiryDateTime = LocalDateTime.of(year, month, 1, 23, 59, 59)
                .plusMonths(1).minusDays(1);
            
            // Check if card is expired
            return expiryDateTime.isAfter(now);
        } catch (Exception e) {
            logger.warning("Error checking card validity: " + e.getMessage());
            return false;
        }
    }

    private PaymentType determinePaymentType(String paymentMethodStr) {
        if (paymentMethodStr.toUpperCase().equals("CARD")) {
            return PaymentType.CARD;
        } else if (paymentMethodStr.toUpperCase().equals("CASH")) {
            return PaymentType.CASH;
        } else {
            // Default to card for now
            logger.warning("Unknown payment method '" + paymentMethodStr + "', defaulting to CARD");
            return PaymentType.CARD;
        }
    }

    private void createTransactionRecord(Payment payment) {
        String dbPaymentMethod;
        switch (payment.getPaymentType()) {
            case CASH:
                dbPaymentMethod = "Cash";
                break;
            case CARD:
                dbPaymentMethod = "Credit Card";
                break;
            default:
                dbPaymentMethod = "Online";
                break;
        }

        Transaction transaction = new Transaction(
            payment.getTransactionId(),
            payment.getOrderId(),
            payment.getPaymentAmount(),
            dbPaymentMethod,
            LocalDateTime.now().toString(),
            payment.getPaymentStatus()
        );
        boolean success = Database.insertTransaction(transaction);
        if (!success) {
            logger.warning("Failed to create transaction record");
        } else {
            logger.info("Transaction record created successfully");
        }
    }
    
    private void updateOrderStatus(Order order) {
        try {
            // Explicitly set order status to COMPLETED
            order.setOrderStatus("Completed");
            
            // Set payment status to PAID
            order.paymentStatus = PaymentStatus.PAID;
            
            // Update the order in the database
            boolean updated = Database.updateOrder(order);
            if (updated) {
                System.out.println("Order #" + order.getOrderId() + " status updated to COMPLETED successfully");
            } else {
                System.err.println("Failed to update status for Order #" + order.getOrderId());
            }
        } catch (Exception e) {
            // Print error details
            System.err.println("Unexpected error updating order status for Order #" + order.getOrderId());
            e.printStackTrace();
        }
    }

    private JSONObject createSuccessResponse(Payment payment) {
        return new JSONObject()
            .put("status", "success")
            .put("message", "Payment processed successfully")
            .put("transactionId", payment.getTransactionId())
            .put("amount", payment.getPaymentAmount())
            .put("paymentMethod", payment.getDbPaymentType())
            .put("timestamp", LocalDateTime.now().toString());
    }

    private JSONObject createErrorResponse(String errorMessage) {
        return new JSONObject()
            .put("status", "error")
            .put("message", errorMessage);
    }
}