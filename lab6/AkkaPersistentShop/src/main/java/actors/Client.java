package actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import model.NoPrice;
import model.Price;
import model.ProductResponse;

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
                .match(ProductResponse.class, request ->{
                    System.out.println("Lowest price for product " + productName + " : "
                            + request.getPrice().getQuantity());
                    System.out.println("Product " + productName + " times Queried " + request.getTimesQueried());
                })
                .match(NoPrice.class, request ->{
                    System.out.println(request.getMessage());
                })
                .build();
    }

}