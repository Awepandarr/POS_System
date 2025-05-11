package uk.ac.Thematics;

import static spark.Spark.*;
import org.json.JSONObject;

public class TransactionWebService {

    public TransactionWebService() {
        // Route to get a transaction by ID
        get("/api/transaction/:transactionId", (req, res) -> {
            String transactionId = req.params(":transactionId");
            Transaction transaction = Database.getTransactionByID(transactionId);
            if (transaction == null) {
                res.status(404);
                return "Transaction not found";
            }
            res.type("application/json");
            return transaction.toJSON().toString();
        });

        // Route to create a new transaction
        post("/api/transaction", (req, res) -> {
            JSONObject requestBody = new JSONObject(req.body());
            
            // Fix: orderId is an integer, so use getInt instead of getString
            Transaction transaction = new Transaction(
                requestBody.getString("transactionId"),
                requestBody.getInt("orderId"),  // Corrected: Use getInt for orderId
                requestBody.getBigDecimal("totalAmount"),
                requestBody.getString("paymentMethod"),
                requestBody.getString("transactionDate"),
                PaymentStatus.valueOf(requestBody.getString("paymentStatus"))
            );

            boolean success = Database.insertTransaction(transaction);
            if (success) {
                res.status(201);
                return transaction.toJSON().toString();
            } else {
                res.status(400);
                return "Failed to create transaction";
            }
        });

        // Route to update a transaction
        put("/api/transaction/:transactionId", (req, res) -> {
            JSONObject requestBody = new JSONObject(req.body());
            
            // Fix: orderId is an integer, so use getInt instead of getString
            Transaction transaction = new Transaction(
                req.params(":transactionId"),
                requestBody.getInt("orderId"),  // Corrected: Use getInt for orderId
                requestBody.getBigDecimal("totalAmount"),
                requestBody.getString("paymentMethod"),
                requestBody.getString("transactionDate"),
                PaymentStatus.valueOf(requestBody.getString("paymentStatus"))
            );

            boolean success = Database.updateTransaction(transaction);
            if (success) {
                res.status(200);
                return transaction.toJSON().toString();
            } else {
                res.status(404);
                return "Transaction not found";
            }
        });

        // Route to delete a transaction
        delete("/api/transaction/:transactionId", (req, res) -> {
            String transactionId = req.params(":transactionId");
            boolean success = Database.deleteTransactionByID(transactionId);
            if (success) {
                res.status(200);
                return "Transaction deleted successfully";
            } else {
                res.status(404);
                return "Transaction not found";
            }
        });
    }
}

