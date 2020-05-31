import actors.Client;
import actors.Server;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class AkkaShop {
    final ActorSystem system = ActorSystem.create("local_system");
    final ActorRef server = system.actorOf(Props.create(Server.class), "server");
    private int clientCounter = 0;
    private ConcurrentMap<String, ActorRef> actors = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        AkkaShop shop = new AkkaShop();

        boolean running = true;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        try {
            while(running){
                String input = reader.readLine();
                if(input.equals("quit"))
                    running = false;
                else
                    shop.askForProducts(input);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void askForProducts(String products) {
        List<String> productList = splitProducts(products);
        for(String product : productList){
            String clientId = getClientIdAndIncrementCounter();
            ActorRef client = system.actorOf(Props.create(Client.class, server, product), clientId);
            actors.put(clientId, client);
            client.tell(product, null);
        }
    }

    private List<String> splitProducts(String products) {
        String[] splitProducts = products.split(";");
        return Arrays.asList(splitProducts);
    }

    private String getClientIdAndIncrementCounter(){
        String clientId = "client" + clientCounter;
        clientCounter++;
        return clientId;
    }
}
