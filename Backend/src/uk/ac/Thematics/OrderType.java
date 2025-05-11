package uk.ac.Thematics;

public enum OrderType {
    ONLINE("Online"),
    IN_STORE("In-Store");

    private final String dbValue;

    OrderType(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    @Override
    public String toString() {
        return dbValue;
    }
}

