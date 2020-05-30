import akka.actor.AbstractActor;

public class ShopAssistantWorker extends AbstractActor {
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, request -> {

                })
                .build();
    }
}
