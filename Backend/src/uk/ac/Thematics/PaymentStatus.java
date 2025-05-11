package uk.ac.Thematics;

public enum PaymentStatus {
    PENDING("Pending"),
    PAID("Paid"),
    FAILED("Failed"),
    CANCELLED("Cancelled");

    private final String dbValue;

    PaymentStatus(String dbValue) {
        this.dbValue = dbValue;
    }
 // Add this method to PaymentStatus.java
    public static PaymentStatus fromDbValue(String dbValue) {
        for (PaymentStatus status : values()) {
            if (status.getDbValue().equals(dbValue)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No payment status with DB value: " + dbValue);
    }
    public String getDbValue() {
        return dbValue;
    }
}
