package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

public class ClientHandler implements Runnable {
    String username;
    PrintWriter writer;
    BufferedReader reader;
    Socket socket;

    public String getUsername() {
        return username;
    }

    public Socket getSocket() {
        return socket;
    }

    public ClientHandler(String username, PrintWriter writer, BufferedReader reader, Socket socket) {
        this.username = username;
        this.writer = writer;
        this.reader = reader;
        this.socket = socket;
    }

    @Override
    public void run() {
        boolean running = true;
        while(running){
            try {
                String receivedMessage = reader.readLine();
                System.out.println("Server message logs:" + receivedMessage);
                sendMessageToConnectedClients(receivedMessage);
            } catch (SocketException se){
                running = false;
                handleSocketException(se);
            } catch (IOException e) {
                handleIOException(e);
            }
        }
    }
    private void sendMessageToConnectedClients(String receivedMessage){
        for(ClientHandler handler: Server.clients){
            try {
                if(!handler.getUsername().equals(this.username)) {
                    Socket socket = handler.getSocket();
                    PrintWriter externalWriter = new PrintWriter(socket.getOutputStream(), true);
                    externalWriter.println(username + ": " + receivedMessage);
                }
            } catch (IOException e) {
                handleIOException(e);
            }
        }
    }

    private void handleSocketException(SocketException se){
        Server.clients.remove(this);
        Server.client_count--;
        String disconnectedClient = "CLIENT DISCONNECTED: " + this.getUsername();
        for(ClientHandler handler: Server.clients){
            try {
                Socket socket = handler.getSocket();
                PrintWriter externalWriter = new PrintWriter(socket.getOutputStream(), true);
                externalWriter.println(disconnectedClient);
            } catch (IOException e) {
                handleIOException(e);
            }
        }
    }

    private void handleIOException(IOException e){
        e.printStackTrace();
        System.out.println("SERVER ERROR");
    }

}
