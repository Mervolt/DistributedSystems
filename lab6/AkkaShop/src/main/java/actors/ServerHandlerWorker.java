package actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import model.NoPrice;
import model.Price;

import java.time.Duration;
import java.util.*;

public class ServerHandlerWorker extends AbstractActor {
    ActorRef client;
    private List<Price> prices = new Vector<>();

    public ServerHandlerWorker(ActorRef client) {
        this.client = client;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, request -> {

                    context().system().scheduler()
                            .scheduleOnce(Duration.ofMillis(300), () -> client.tell(getLowerPrice(request), getSelf()),
                                    context().system().dispatcher());

                    context()
                            .child("firstShopWorker").get().tell(request, getSelf());

                    context()
                            .child("secondShopWorker").get().tell(request, getSelf());


                })
                .match(Price.class, request -> {
                    prices.add(request);
                })
                .build();
    }

    private Object getLowerPrice(String productName) {
        int records = prices.size();
        if(records == 0)
            return new NoPrice("No prices available for product " + productName);
        if(records == 1)
            return prices.get(0);
        else{
            Price firstPrice = prices.get(0);
            Price secondPrice = prices.get(1);
            return firstPrice.getQuantity() < secondPrice.getQuantity() ? firstPrice : secondPrice;
        }
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();
        context()
                .actorOf(Props.create(ShopAssistantWorker.class), "firstShopWorker");

        context()
                .actorOf(Props.create(ShopAssistantWorker.class), "secondShopWorker");
    }
}
