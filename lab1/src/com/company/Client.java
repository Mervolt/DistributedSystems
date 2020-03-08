package com.company;

import jdk.swing.interop.SwingInterOpUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {
    public static void main(String[] args) {
        Socket socket = null;
        DatagramSocket datagramSocket = null;
        try {
            ExecutorService pool = Executors.newFixedThreadPool(3);
            socket = new Socket("localhost", 12345);
            datagramSocket = new DatagramSocket(null);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            ClientReceiver receiver = new ClientReceiver(in);
            ClientReceiverUdp receiverUdp = new ClientReceiverUdp(datagramSocket);
            ClientSender sender = new ClientSender(out, datagramSocket, socket);
            Thread threadSender = new Thread(sender);
            Thread threadReceiverUdp = new Thread(receiverUdp);
            Thread threadReceiver = new Thread(receiver);
            pool.execute(threadSender);
            pool.execute(threadReceiver);
            pool.execute(threadReceiverUdp);
        } catch (IOException e) {
            System.out.println(Server.ANSI_RED + "SERVER IS NOT RUNNING" + Server.ANSI_RESET);
        }
    }

}
