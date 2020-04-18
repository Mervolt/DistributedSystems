package com.company;

import com.rabbitmq.client.*;

import java.io.IOException;

public class AdministratorReceiver implements Runnable {
    Channel channel;
    public AdministratorReceiver(Channel channel) {
        this.channel = channel;
    }

    @Override
    public void run() {
        String adminQueueOrder = "adminQueueOrder";
        String adminQueueAck = "adminQueueAck";
        try {
            channel.queueDeclare(adminQueueOrder, false, false, false, null);
            channel.queueBind(adminQueueOrder, Transporter.ORDER_EXCHANGE , "#");

            channel.queueDeclare(adminQueueAck, false, false, false, null);
            channel.queueBind(adminQueueAck, Agency.ACK_EXCHANGE, "#");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // consumer (handle msg)
        Consumer adminConsumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("Received: " + message);
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        };
        try {
            channel.basicConsume(adminQueueOrder, false, adminConsumer);
            channel.basicConsume(adminQueueAck, false, adminConsumer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
