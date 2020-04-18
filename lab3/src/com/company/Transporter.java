package com.company;

import com.rabbitmq.client.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeoutException;

public class Transporter {
    public static String ORDER_EXCHANGE = "orderExchange";
    String routingKey = "";
    boolean cargoTransport = false;
    boolean peopleTransport = false;
    boolean satellitePlacing = false;

    public static void main(String[] args) throws IOException, TimeoutException {
        System.out.println("TRANSPORTER");
        Transporter transporter = new Transporter();
        transporter.declareResponsibilites();

        // connection & channel
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // exchanges
        channel.exchangeDeclare(ORDER_EXCHANGE, BuiltinExchangeType.TOPIC);
        channel.exchangeDeclare(Agency.ACK_EXCHANGE, BuiltinExchangeType.TOPIC);
        channel.exchangeDeclare(Administrator.ADMIN_EXCHANGE, BuiltinExchangeType.TOPIC);

        // queue & bind
        String adminQueueName = channel.queueDeclare().getQueue();
        channel.queueBind(adminQueueName, Administrator.ADMIN_EXCHANGE, "info.trans");
        channel.queueBind(adminQueueName, Administrator.ADMIN_EXCHANGE, "info.all");

        String cargoQueue = "cargoQueue";
        if(transporter.routingKey.contains("C")) {
            channel.queueDeclare(cargoQueue, false, false, false, null);
            channel.queueBind(cargoQueue, ORDER_EXCHANGE, "order.Cargo");
        }
        String peopleQueue = "peopleQueue";
        if(transporter.routingKey.contains("P")) {
            channel.queueDeclare(peopleQueue, false, false, false, null);
            channel.queueBind(peopleQueue, ORDER_EXCHANGE, "order.People");
        }
        String satelliteQueue = "satelliteQueue";
        if(transporter.routingKey.contains("S")) {
            channel.queueDeclare(satelliteQueue, false, false, false, null);
            channel.queueBind(satelliteQueue, ORDER_EXCHANGE, "order.Satellite");
        }
        channel.basicQos(1);

        // consumer (handle msg)
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String receivedMessage = new String(body, "UTF-8");
                System.out.println("Received: " + receivedMessage);
                channel.basicAck(envelope.getDeliveryTag(), false);
                String message = "Confirmation of " + receivedMessage;
                String[] splittedMessage = receivedMessage.split("-");
                String agencyId = splittedMessage[splittedMessage.length-1];
                channel.basicPublish(Agency.ACK_EXCHANGE, "agency." + agencyId , null, message.getBytes("UTF-8"));
            }
        };

        Consumer adminInfoConsumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String receivedMessage = new String(body, "UTF-8");
                System.out.println("Received: " + receivedMessage);
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        };

        // start listening
        System.out.println("Waiting for messages...");

        channel.basicConsume(adminQueueName, false, adminInfoConsumer);
        if(transporter.routingKey.contains("C"))
            channel.basicConsume(cargoQueue, false, consumer);
        if(transporter.routingKey.contains("P"))
            channel.basicConsume(peopleQueue, false, consumer);
        if(transporter.routingKey.contains("S"))
            channel.basicConsume(satelliteQueue, false, consumer);
    }

    private void declareResponsibilites() throws IOException {
        System.out.println("Please write down 2 numbers you are interested in :\n" +
                " C. Cargo transport\n P. People transport\n S. Satellite placing\n" +
                "e.g. CP");
        BufferedReader declareTypeReader = new BufferedReader(new InputStreamReader(System.in));
        String responsibility = declareTypeReader.readLine();
        if(responsibility.contains("C") && responsibility.contains("P") && responsibility.contains("S")) {
            System.out.println("Too many responsibilities");
            System.exit(1);
        }
        if(responsibility.contains("C")) {
            this.cargoTransport = true;
            routingKey = "C";
        }
        if(responsibility.contains("P")) {
            this.peopleTransport = true;
            routingKey += "P";
        }
        if(responsibility.contains("S")) {
            this.satellitePlacing = true;
            routingKey += "S";
        }
        if(!validateResponsibilities()){
            System.out.println("Too less responsibilities");
            System.exit(2);
        }
    }

    private boolean validateResponsibilities(){
        if(!this.cargoTransport && !this.peopleTransport)
            return false;
        if(!this.cargoTransport && !this.satellitePlacing)
            return false;
        if(!this.peopleTransport && !this.satellitePlacing)
            return false;
        return true;
    }
}
