package model;

public class ProductResponse {
    int timesQueried;
    Price price;

    public ProductResponse(int timesQueried, Price price) {
        this.timesQueried = timesQueried;
        this.price = price;
    }

    public int getTimesQueried() {
        return timesQueried;
    }

    public Price getPrice() {
        return price;
    }
}
