import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import model.NoPrice;
import model.Price;

public class Client extends AbstractActor {

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, request ->{
                        getServer().tell(request, null);
                })
                .match(Price.class, request ->{
                    System.out.println("Lowest price" + request.quantity);
                })
                .build();
    }

    private ActorRef getServer() {
        return AkkaShop.server;
    }
}
