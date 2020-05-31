package model;

public class DatabaseResponse {
    private String productName;
    private int queriedTimes;

    public DatabaseResponse(String productName, int queriedTimes) {
        this.productName = productName;
        this.queriedTimes = queriedTimes;
    }

    public int getQueriedTimes() {
        return queriedTimes;
    }
}
