package uk.ac.Thematics;

public enum PaymentType {
    CARD("Credit Card"),
    CASH("Cash");
    
    private final String dbValue;
    
    PaymentType(String dbValue) {
        this.dbValue = dbValue;
    }
    
    public String getDbValue() {
        return dbValue;
    }
}