package com.company;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

public class Agency {
    public static String ACK_EXCHANGE = "ackExchange";
    String name;

    public Agency(String name) {
        this.name = name;
    }

    public static void main(String[] args) throws IOException, TimeoutException {

        System.out.println("AGENCY");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String agencyName = reader.readLine();
        Agency agency = new Agency(agencyName);

        System.out.println(agency.name);
        // connection & channel
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // exchange
        channel.exchangeDeclare(Transporter.ORDER_EXCHANGE, BuiltinExchangeType.TOPIC);
        channel.exchangeDeclare(ACK_EXCHANGE, BuiltinExchangeType.TOPIC);
        channel.exchangeDeclare(Administrator.ADMIN_EXCHANGE, BuiltinExchangeType.TOPIC);

        ExecutorService pool = Executors.newFixedThreadPool(1);
        AgencyAckReceiver ackReceiver = new AgencyAckReceiver(agency.name, channel);
        Thread receiver = new Thread(ackReceiver);
        pool.execute(receiver);




        while(true) {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String message = br.readLine();

            if(message.equals("exit"))
                break;
            String task;
            switch (message) {
                case "C":
                    task = "Cargo";
                    break;
                case "P":
                    task = "People";
                    break;
                case "S":
                    task = "Satellite";
                    break;
                default:
                    throw new IllegalArgumentException("Wrong task");
            }
            message = message + '-' + agency.name;
            channel.basicPublish(Transporter.ORDER_EXCHANGE, "order." + task, null, message.getBytes("UTF-8"));
            System.out.println("Sent: " + message);
        }

        // close
        channel.close();
        connection.close();
    }
}
