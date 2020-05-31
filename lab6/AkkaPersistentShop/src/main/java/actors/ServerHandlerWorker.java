package actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import model.DatabaseResponse;
import model.NoPrice;
import model.Price;
import model.ProductResponse;
import persistence.PersistenceManager;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.Duration;
import java.util.*;

public class ServerHandlerWorker extends AbstractActor {
    ActorRef client;
    private List<Price> prices = new Vector<>();
    Integer productQueryAmount = 1;

    public ServerHandlerWorker(ActorRef client) {
        this.client = client;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, request -> {

                    context().system().scheduler()
                            .scheduleOnce(Duration.ofMillis(300), () -> client.tell(getResponse(request), getSelf()),
                                    context().system().dispatcher());

                    context()
                            .child("firstShopWorker").get().tell(request, getSelf());

                    context()
                            .child("secondShopWorker").get().tell(request, getSelf());

                    context()
                            .child("databaseWorker").get().tell(request, getSelf());

                })
                .match(Price.class, request -> {
                    prices.add(request);
                })
                .match(DatabaseResponse.class, request -> {
                    productQueryAmount = request.getQueriedTimes();
                })
                .build();
    }

    private Object getResponse(String productName){
        Price betterPrice;
        int records = prices.size();
        if(records == 0)
            return new NoPrice("No prices available for product " + productName);
        if(records == 1)
            betterPrice =  prices.get(0);
        else {
            Price firstPrice = prices.get(0);
            Price secondPrice = prices.get(1);
            betterPrice = firstPrice.getQuantity() < secondPrice.getQuantity() ? firstPrice : secondPrice;
        }
        return new ProductResponse(productQueryAmount, betterPrice);
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();
        context()
                .actorOf(Props.create(ShopAssistantWorker.class), "firstShopWorker");

        context()
                .actorOf(Props.create(ShopAssistantWorker.class), "secondShopWorker");

        context()
                .actorOf(Props.create(DatabaseWorker.class), "databaseWorker");
    }
}
