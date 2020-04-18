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

public class Agency {
    public static String ACK_EXCHANGE = "ackExchange";
    String name;
    int taskCounter = 0;

    public Agency(String name) {
        this.name = name;
    }

    public static void main(String[] args) throws IOException, TimeoutException {
        Agency agency = configureAgency();

        Connection connection = createConnection();
        if(connection == null){
            throw new RuntimeException("Connection not established");
        }
        Channel channel = connection.createChannel();

        agency.declareExchanges(channel);
        agency.prepareAndRunReceivingThread(channel);

        System.out.println("Please, write down wanted task:\n" +
                "C - Cargo transport\n" +
                "P - People transport\n" +
                "S - Satellite transport\n");

        while(true) {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String message = br.readLine();

            if(message.equals("exit"))
                break;

            String task = agency.buildTaskMessage(message);

            String finalMessage = agency.buildMessage(message);
            channel.basicPublish(Transporter.ORDER_EXCHANGE, "order." + task, null, finalMessage.getBytes("UTF-8"));
            System.out.println("Sent: " + message);
            agency.taskCounter++;
        }

        // close
        channel.close();
        connection.close();
    }

    private static Agency configureAgency(){
        String agencyName = null;
        try {
            System.out.println("AGENCY");
            System.out.println("Write agency name");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            agencyName = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        return new Agency(agencyName);
    }


    private static Connection createConnection(){
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            return factory.newConnection();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void declareExchanges(Channel channel){
        try {
            channel.exchangeDeclare(Transporter.ORDER_EXCHANGE, BuiltinExchangeType.TOPIC);
            channel.exchangeDeclare(ACK_EXCHANGE, BuiltinExchangeType.TOPIC);
            channel.exchangeDeclare(Administrator.ADMIN_EXCHANGE, BuiltinExchangeType.TOPIC);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void prepareAndRunReceivingThread(Channel channel){
        ExecutorService pool = Executors.newFixedThreadPool(1);
        AgencyAckReceiver ackReceiver = new AgencyAckReceiver(this.name, channel);
        Thread receiver = new Thread(ackReceiver);
        pool.execute(receiver);
    }

    private String buildTaskMessage(String message){
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
        return task;
    }

    private String buildMessage(String message){
        return message + '-' + this.taskCounter + '-' + this.name;
    }
}
