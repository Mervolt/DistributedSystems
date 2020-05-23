import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class Z2_RemoteActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    @Override
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                 // TODO: respond to string messages by changing them to uppercase
                .match(String.class, s -> {
                    String message = s.toUpperCase();
                    System.out.println("Received from local: " + message);
                    getContext().actorSelection("akka://local_system@127.0.0.1:2551/user/local").tell(message, null);
                })
                .matchAny(o -> log.info("received unknown message"))
                .build();
    }
}
