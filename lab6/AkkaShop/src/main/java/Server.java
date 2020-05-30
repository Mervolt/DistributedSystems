import akka.actor.AbstractActor;
import akka.actor.Props;
import model.Price;

public class Server extends AbstractActor {

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, request ->{
                    context()
                            .actorOf(Props.create(CustomerServiceWorker.class), "customerService")
                            .tell(request, getSelf());
                })
                .match(Price.class, request ->{

                })
                .build();
    }
}
