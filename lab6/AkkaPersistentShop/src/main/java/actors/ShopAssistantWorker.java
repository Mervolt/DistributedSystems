package actors;

import akka.actor.AbstractActor;
import model.Price;

import java.util.Random;

public class ShopAssistantWorker extends AbstractActor {
    Random random = new Random();
    int sleepRange = 401;
    int shortestSleep = 100;
    int priceRange = 10;
    int lowestPrice = 1;

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, request -> {
                    int sleepTime = random.nextInt(sleepRange) + shortestSleep;
                    Thread.sleep(sleepTime);
                    Integer priceAmount = random.nextInt(priceRange) + lowestPrice;
                    Price price = new Price(priceAmount);
                    getSender().tell(price, null);
                })
                .build();
    }
}
