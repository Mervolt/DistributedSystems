package actors;

import akka.actor.AbstractActor;
import akka.actor.Props;

public class Server extends AbstractActor {
    private int handlerCounter = 0;
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, request ->{
                    context()
                            .actorOf(Props.create(ServerHandlerWorker.class, getSender()), getHandlerIdAndIncrementCounter())
                            .tell(request, getSender());
                })
                .build();
    }


    private String getHandlerIdAndIncrementCounter(){
        String handlerId = "handler" + handlerCounter;
        handlerCounter++;
        return handlerId;
    }
}
