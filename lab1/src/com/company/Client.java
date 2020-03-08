package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        Socket socket = null;
        DatagramSocket datagramSocket = null;
        try {
            socket = new Socket("localhost", 12345);
            datagramSocket = new DatagramSocket(null);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            ClientReceiver receiver = new ClientReceiver(in);
            ClientReceiverUdp receiverUdp = new ClientReceiverUdp(datagramSocket);
            ClientReceiverMulticast receiverMulticast = new ClientReceiverMulticast();
            ClientSender sender = new ClientSender(out, datagramSocket, socket);
            Thread threadSender = new Thread(sender);
            Thread threadReceiverUdp = new Thread(receiverUdp);
            Thread threadReceiverMulticast = new Thread(receiverMulticast);
            Thread threadReceiver = new Thread(receiver);
            threadSender.start();
            threadReceiverUdp.start();
            threadReceiverMulticast.start();
            threadReceiver.start();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
