package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        Socket socket = null;
        try {
            socket = new Socket("localhost", 12345);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            ClientReceiver receiver = new ClientReceiver(in);
            ClientSender sender = new ClientSender(out);
            Thread threadSender = new Thread(sender);
            Thread threadReceiver = new Thread(receiver);
            threadSender.start();
            threadReceiver.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
