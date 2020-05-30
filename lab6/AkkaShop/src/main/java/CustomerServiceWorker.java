import akka.actor.AbstractActor;
import akka.actor.Props;
import model.Price;

import java.util.HashMap;

public class CustomerServiceWorker extends AbstractActor {
    private HashMap<String, Price> responses;
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, request -> {
                    context()
                            .actorOf(Props.create(ShopAssistantWorker.class), "firstShopWorker")
                            .tell(request, getSelf());

                    context()
                            .actorOf(Props.create(ShopAssistantWorker.class), "secondShopWorker")
                            .tell(request, getSelf());
                })
                .match(Price.class, request -> {
                    //responses.put(, request.quantity);
                })
                .build();
    }
}
