package uk.ac.Thematics;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.json.JSONArray;
import org.json.JSONObject;

import static spark.Spark.*;

public class EndOfDayReport {

    public EndOfDayReport() {
        // Route to generate and retrieve the end of day report
        get("/endOfDayReport", (request, response) -> {
            // Debug the EndOfDayReport table first
            Database.debugEndOfDayReportTable();
            
            // Fix any database lock issues
            Database.fixDatabaseLock();
            
            JSONObject report = generateEndOfDayReport();
            
            // Save the report to the database
            try {
                Database.generateAndSaveEndOfDayReport();
            } catch (Exception e) {
                System.err.println("Error saving end of day report: " + e.getMessage());
                e.printStackTrace();
            }
            
            response.type("application/json");
            return report.toString();
        });
        
        // Route to get reports by date range
        get("/endOfDayReport/history", (request, response) -> {
            String startDateStr = request.queryParams("startDate");
            String endDateStr = request.queryParams("endDate");
            
            LocalDate startDate = null;
            LocalDate endDate = null;
            
            // Log received parameters
            System.out.println("DEBUG: Received startDate: " + startDateStr);
            System.out.println("DEBUG: Received endDate: " + endDateStr);
            
            try {
                // If no dates provided, default to last 7 days
                if (startDateStr == null || endDateStr == null) {
                    startDate = LocalDate.now().minusDays(7);
                    endDate = LocalDate.now();
                    System.out.println("DEBUG: Using default date range - Start: " + startDate + ", End: " + endDate);
                } else {
                    startDate = LocalDate.parse(startDateStr);
                    endDate = LocalDate.parse(endDateStr);
                }
            } catch (Exception e) {
                System.err.println("DEBUG: Date parsing error: " + e.getMessage());
                response.status(400);
                return new JSONObject()
                    .put("error", "Invalid date format")
                    .put("message", "Dates should be in YYYY-MM-DD format")
                    .toString();
            }
            
            JSONObject reportHistory = Database.getEndOfDayReportHistory(startDate, endDate);
            
            // Log the returned report history
            System.out.println("DEBUG: Returned Report History: " + reportHistory.toString(2));
            
            response.type("application/json");
            return reportHistory.toString();
        });
        
        // Route to get a specific report by date
        get("/endOfDayReport/date/:date", (request, response) -> {
            String dateStr = request.params(":date");
            LocalDate date = LocalDate.parse(dateStr);
            
            JSONObject report = getReportByDate(date);
            
            if (report == null) {
                response.status(404);
                return new JSONObject().put("error", "No report found for date: " + dateStr).toString();
            }
            
            response.type("application/json");
            return report.toString();
        });
        
        // Route to generate a report for a specific date (useful for testing or creating historical reports)
        post("/endOfDayReport/generate/:date", (request, response) -> {
            String dateStr = request.params(":date");
            LocalDate date = LocalDate.parse(dateStr);
            
            boolean success = generateReportForDate(date);
            
            if (!success) {
                response.status(500);
                return new JSONObject().put("error", "Failed to generate report for date: " + dateStr).toString();
            }
            
            // Retrieve the generated report
            JSONObject report = getReportByDate(date);
            
            response.type("application/json");
            return report.toString();
        });
    }
    
    /**
     * Generate a comprehensive end of day report
     * @return JSONObject containing report data
     */
    public static JSONObject generateEndOfDayReport() {
        JSONObject report = new JSONObject();
        
        try {
            // Get data from invoices and payments
            BigDecimal totalSales = Database.getTotalSalesForTheDay();
            BigDecimal totalPayments = Database.getTotalPaymentsForTheDay(); 
            BigDecimal totalDiscounts = Database.getTotalDiscountsFromEndOfDayReport();
            String paymentBreakdown = Database.getPaymentBreakdownForTheDay();
            
            // Output debug info
            System.out.println("Report values being returned:");
            System.out.println("- Total Sales: " + totalSales);
            System.out.println("- Total Payments: " + totalPayments);
            System.out.println("- Total Discounts: " + totalDiscounts);
            System.out.println("- Payment Breakdown: " + paymentBreakdown);
            
            // Get counts
            int invoiceCount = getInvoiceCount();
            int customerCount = getUniqueCustomerCount();
            
            // Add data to report
            report.put("report_date", LocalDate.now().toString());
            report.put("generated_at", LocalDateTime.now().toString());
            report.put("total_sales", totalSales);
            report.put("total_payments", totalPayments);
            report.put("total_discounts", totalDiscounts);
            report.put("payment_breakdown", paymentBreakdown);
            report.put("invoice_count", invoiceCount);
            report.put("customer_count", customerCount);
            
            // Add top products sold today
            JSONArray topProducts = getTopSellingProducts(5);
            report.put("top_products", topProducts);
            
        } catch (Exception e) {
            e.printStackTrace();
            report.put("error", "Error generating report: " + e.getMessage());
        }
        
        return report;
    }
    
    /**
     * Get a report from the database by date
     * @param date The date to retrieve the report for
     * @return JSONObject containing the report data, or null if no report exists
     */
    private static JSONObject getReportByDate(LocalDate date) {
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = Database.getConnection();
            
            String sql = "SELECT * FROM EndOfDayReport WHERE date = ?";
            ps = conn.prepareStatement(sql);
            ps.setDate(1, Date.valueOf(date));
            
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                JSONObject report = new JSONObject();
                report.put("report_id", rs.getInt("report_id"));
                report.put("date", rs.getDate("date").toString());
                report.put("total_sales", rs.getBigDecimal("total_sales"));
                report.put("total_payments", rs.getBigDecimal("total_payments"));
                report.put("total_discounts", rs.getBigDecimal("total_discounts"));
                report.put("payment_breakdown", rs.getString("payment_breakdown"));
                
                return report;
            }
            
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error retrieving report from database: " + e.getMessage());
            return null;
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Get reports from the database within a date range
     * @param startDate The start date (inclusive)
     * @param endDate The end date (inclusive)
     * @return JSONArray containing the report data
     */
    private static JSONArray getReportsByDateRange(LocalDate startDate, LocalDate endDate) {
        Connection conn = null;
        PreparedStatement ps = null;
        JSONArray reports = new JSONArray();
        
        try {
            conn = Database.getConnection();
            
            String sql = "SELECT * FROM EndOfDayReport WHERE date BETWEEN ? AND ? ORDER BY date DESC";
            ps = conn.prepareStatement(sql);
            ps.setDate(1, Date.valueOf(startDate));
            ps.setDate(2, Date.valueOf(endDate));
            
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                JSONObject report = new JSONObject();
                report.put("report_id", rs.getInt("report_id"));
                report.put("date", rs.getDate("date").toString());
                report.put("total_sales", rs.getBigDecimal("total_sales"));
                report.put("total_payments", rs.getBigDecimal("total_payments"));
                report.put("total_discounts", rs.getBigDecimal("total_discounts"));
                report.put("payment_breakdown", rs.getString("payment_breakdown"));
                
                reports.put(report);
            }
            
            return reports;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error retrieving reports from database: " + e.getMessage());
            return reports;
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Generate a report for a specific date
     * @param date The date to generate the report for
     * @return true if successful, false otherwise
     */
    private static boolean generateReportForDate(LocalDate date) {
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = Database.getConnection();
            
            // Get sales data for the specified date
            BigDecimal totalSales = getTotalSalesForDate(date);
            BigDecimal totalPayments = getTotalPaymentsForDate(date);
            BigDecimal totalDiscounts = getTotalDiscountsForDate(date);
            String paymentBreakdown = getPaymentBreakdownForDate(date);
            
            // Check if a report for this date already exists
            String checkSql = "SELECT report_id FROM EndOfDayReport WHERE date = ?";
            ps = conn.prepareStatement(checkSql);
            ps.setDate(1, Date.valueOf(date));
            
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                // Update existing report
                int reportId = rs.getInt("report_id");
                
                String updateSql = "UPDATE EndOfDayReport SET " + 
                                  "total_sales = ?, " +
                                  "total_payments = ?, " +
                                  "total_discounts = ?, " +
                                  "payment_breakdown = ? " +
                                  "WHERE report_id = ?";
                                  
                ps = conn.prepareStatement(updateSql);
                ps.setBigDecimal(1, totalSales);
                ps.setBigDecimal(2, totalPayments);
                ps.setBigDecimal(3, totalDiscounts);
                ps.setString(4, paymentBreakdown);
                ps.setInt(5, reportId);
                
                return ps.executeUpdate() > 0;
            } else {
                // Insert new report
                String insertSql = "INSERT INTO EndOfDayReport (date, total_sales, total_payments, total_discounts, payment_breakdown) " +
                                  "VALUES (?, ?, ?, ?, ?)";
                                  
                ps = conn.prepareStatement(insertSql);
                ps.setDate(1, Date.valueOf(date));
                ps.setBigDecimal(2, totalSales);
                ps.setBigDecimal(3, totalPayments);
                ps.setBigDecimal(4, totalDiscounts);
                ps.setString(5, paymentBreakdown);
                
                return ps.executeUpdate() > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error generating report for date: " + e.getMessage());
            return false;
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Get the total sales for a specific date
     * @param date The date to get sales for
     * @return The total sales amount
     */
    private static BigDecimal getTotalSalesForDate(LocalDate date) throws SQLException {
        String query = "SELECT SUM(subtotal) FROM Invoices WHERE DATE(date_issued) = ?";
        
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setDate(1, Date.valueOf(date));
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                BigDecimal result = rs.getBigDecimal(1);
                return result != null ? result : BigDecimal.ZERO;
            }
        }
        return BigDecimal.ZERO;
    }
    
    /**
     * Get the total payments for a specific date
     * @param date The date to get payments for
     * @return The total payments amount
     */
    private static BigDecimal getTotalPaymentsForDate(LocalDate date) throws SQLException {
        String query = "SELECT SUM(final_amount) FROM Invoices WHERE DATE(date_issued) = ?";
        
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setDate(1, Date.valueOf(date));
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                BigDecimal result = rs.getBigDecimal(1);
                return result != null ? result : BigDecimal.ZERO;
            }
        }
        return BigDecimal.ZERO;
    }
    
    /**
     * Get the total discounts for a specific date
     * @param date The date to get discounts for
     * @return The total discounts amount
     */
    private static BigDecimal getTotalDiscountsForDate(LocalDate date) throws SQLException {
        String query = "SELECT total_discounts FROM EndOfDayReport WHERE date = ?";
        
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setDate(1, Date.valueOf(date));
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                BigDecimal result = rs.getBigDecimal("total_discounts");
                System.out.println("Discount value from EndOfDayReport for " + date + ": " + result);
                return result != null ? result : BigDecimal.ZERO;
            }
        }
        
        System.out.println("No EndOfDayReport found for " + date + ", returning zero");
        return BigDecimal.ZERO;
    }
    
    /**
     * Get the payment breakdown for a specific date
     * @param date The date to get payment breakdown for
     * @return A formatted payment breakdown string
     */
    private static String getPaymentBreakdownForDate(LocalDate date) throws SQLException {
        String query = "SELECT p.payment_type, SUM(p.payment_amount) as total_amount " +
                      "FROM Payments p " +
                      "JOIN Invoices i ON p.order_id = i.order_id " +
                      "WHERE DATE(i.date_issued) = ? " +
                      "GROUP BY p.payment_type";
        
        StringBuilder breakdown = new StringBuilder();
        
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setDate(1, Date.valueOf(date));
            ResultSet rs = ps.executeQuery();
            
            boolean hasData = false;
            
            while (rs.next()) {
                hasData = true;
                String paymentMethod = rs.getString("payment_type");
                BigDecimal amount = rs.getBigDecimal("total_amount");
                
                if (breakdown.length() > 0) {
                    breakdown.append(", ");
                }
                
                breakdown.append(paymentMethod).append(": ").append(amount);
            }
            
            if (!hasData) {
                return "No payment data available";
            }
        }
        
        return breakdown.toString();
    }
    
    /**
     * Get the number of invoices for today
     * @return The invoice count
     */
    private static int getInvoiceCount() {
        try {
            Connection conn = Database.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                "SELECT COUNT(*) FROM Invoices WHERE DATE(date_issued) = CURRENT_DATE"
            );
            
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Get the number of unique customers served today
     * @return The customer count
     */
    private static int getUniqueCustomerCount() {
        try {
            Connection conn = Database.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                "SELECT COUNT(DISTINCT customer_id) FROM Invoices WHERE DATE(date_issued) = CURRENT_DATE"
            );
            
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Get the top selling products for today
     * @param limit The number of products to return
     * @return JSONArray of top products
     */
    private static JSONArray getTopSellingProducts(int limit) {
        JSONArray products = new JSONArray();
        
        String query = 
            "SELECT p.product_id, p.name, SUM(oi.quantity) as quantity_sold, " +
            "SUM(oi.quantity * oi.price) as total_revenue " +
            "FROM Order_Items oi " +
            "JOIN Products p ON oi.product_id = p.product_id " +
            "JOIN Orders o ON oi.order_id = o.order_id " +
            "JOIN Invoices i ON o.order_id = i.order_id " +
            "WHERE DATE(i.date_issued) = CURRENT_DATE " +
            "GROUP BY p.product_id, p.name " +
            "ORDER BY quantity_sold DESC " +
            "LIMIT ?";
        
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                JSONObject product = new JSONObject();
                product.put("product_id", rs.getInt("product_id"));
                product.put("name", rs.getString("name"));
                product.put("quantity_sold", rs.getInt("quantity_sold"));
                product.put("revenue", rs.getBigDecimal("total_revenue"));
                products.put(product);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return products;
    }
}