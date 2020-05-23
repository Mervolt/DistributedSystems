import akka.actor.ActorSystem;
import akka.stream.OverflowStrategy;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLOutput;
import org.w3c.dom.ls.LSOutput;

import java.time.LocalDate;
import java.util.Arrays;

public class Z3 {

    public static void main(String[] argv) throws Exception {

        ActorSystem system = ActorSystem.create("stream_system");

        Source.range(1, 10)
                .map(val -> val * 2)
                .runWith(Sink.foreach(val -> System.out.println(val)), system);

        Source.from(Arrays.asList("hello", "hola", "hallo", "ola", "ahlan", "ni hao", "shalom", "salut", "ciao", "hug", "yasou"))
                .map(val -> val.toUpperCase())
                .runWith(Sink.foreach(val -> System.out.println(val)), system);

        Source.range(-5, -1)
                .map(val -> val * 2)
                .runWith(Sink.last(), system)
                .thenAccept(System.out::println);

        long timeStart = System.currentTimeMillis();
        // TODO: final stream composition in task: source -> buffer -> map -> async -> map -> async -> runWith -> thenRun
        Source.range(1, 10)
                .buffer(3, OverflowStrategy.backpressure())
                .map(val -> {Thread.sleep(500); return val * 10;})
                .async()
                .map(val -> {Thread.sleep(500); return val * 2;})
                .async()
                .runWith(Sink.foreach(val -> System.out.println(val)), system)
                .thenRun(() -> timer(timeStart));


    }

    private static void timer(long timeStart) {
        long timeAmount = System.currentTimeMillis() - timeStart;
        System.out.println("Duration time in millis: " + String.valueOf(timeAmount));
    }

}
