package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";


    protected static int client_count = 0;
    protected static int client_id = 0;
    protected static List<ClientHandler> clients = new ArrayList<>();
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        DatagramSocket udpSocket;
        try {
            serverSocket = new ServerSocket(12345);
            udpSocket = new DatagramSocket(12345);
            ClientHandlerUdp clientHandlerUdp = new ClientHandlerUdp(udpSocket);
            Thread threadUdp = new Thread(clientHandlerUdp);
            threadUdp.start();
            while (true) {
                String username = "User " + client_id;
                Socket clientSocket = serverSocket.accept();
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                ClientHandler clientHandler = new ClientHandler(username, writer, reader, clientSocket);
                Thread thread = new Thread(clientHandler);
                client_id++;
                client_count++;
                clients.add(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) serverSocket.close();
        }
    }
}
