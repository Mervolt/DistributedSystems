package com.company;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class Agency {

    public static void main(String[] args) throws IOException, TimeoutException {
        // connection & channel
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // exchange
        channel.exchangeDeclare(Transporter.ORDER_EXCHANGE, BuiltinExchangeType.DIRECT);


        //System.out.println("Agency");

        while(true) {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String message = br.readLine();

            if(message.equals("exit"))
                break;
            channel.basicPublish(Transporter.ORDER_EXCHANGE, message, null, message.getBytes("UTF-8"));
            System.out.println("Sent: " + message);
        }

        // close
        channel.close();
        connection.close();
    }
}
