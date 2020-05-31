package actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import model.NoPrice;
import model.Price;

public class Client extends AbstractActor {
    final ActorRef server;
    String productName;

    public Client(ActorRef server, String productName) {
        this.server = server;
        this.productName = productName;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, request ->{
                        server.tell(request, getSelf());
                })
                .match(Price.class, request ->{
                    System.out.println("Lowest price for product " + productName + " : " + request.getQuantity());
                })
                .match(NoPrice.class, request ->{
                    System.out.println(request.getMessage());
                })
                .build();
    }

}