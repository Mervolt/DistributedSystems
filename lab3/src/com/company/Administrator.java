package com.company;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

public class Administrator {
    public static String ADMIN_EXCHANGE = "adminExchange";
    public static void main(String[] args) throws IOException, TimeoutException {
        // connection & channel
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(Transporter.ORDER_EXCHANGE, BuiltinExchangeType.TOPIC);
        channel.exchangeDeclare(Agency.ACK_EXCHANGE, BuiltinExchangeType.TOPIC);
        channel.exchangeDeclare(ADMIN_EXCHANGE, BuiltinExchangeType.TOPIC);

        ExecutorService pool = Executors.newFixedThreadPool(1);
        AdministratorReceiver administratorReceiver = new AdministratorReceiver(channel);
        Thread receiver = new Thread(administratorReceiver);
        pool.execute(receiver);

        while(true) {
            System.out.println("1.Wysłanie wiadomości do przewoźników\n" +
                    "2.Wysłanie wiadomości do agencji\n" +
                    "3.Wysłanie wiadomości do wszystkich\n");
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String choice = br.readLine();
            String routingKey = "";
            if(choice.equals("1"))
                routingKey = "info.trans";
            else if(choice.equals("2"))
                routingKey = "info.agency";
            else
                routingKey = "info.all";
            System.out.println("Podaj wiadomość:");
            String message = br.readLine();
            channel.basicPublish(ADMIN_EXCHANGE, routingKey, null, message.getBytes("UTF-8"));

        }


    }
}
