package com.company;

import com.rabbitmq.client.*;

import java.io.IOException;

public class AgencyAckReceiver implements Runnable {
    String name;
    Channel channel;
    public AgencyAckReceiver(String name, Channel channel) {
        this.name = name;
        this.channel = channel;
    }

    @Override
    public void run() {
        //queue & bind
        String acknowledgeQueue = "ackQueue" + name;
        String adminQueueName = "";
        try {
            adminQueueName = channel.queueDeclare().getQueue();
            channel.queueBind(adminQueueName, Administrator.ADMIN_EXCHANGE, "info.agency");
            channel.queueBind(adminQueueName, Administrator.ADMIN_EXCHANGE, "info.all");
            channel.basicQos(1);
            channel.queueDeclare(acknowledgeQueue, false, false, false, null);
            channel.queueBind(acknowledgeQueue, Agency.ACK_EXCHANGE, "agency." + this.name);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // consumer (handle msg)
        Consumer agencyConsumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("Received: " + message);
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        };
        try {
            channel.basicConsume(acknowledgeQueue, false, agencyConsumer);
            channel.basicConsume(adminQueueName, false, agencyConsumer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
