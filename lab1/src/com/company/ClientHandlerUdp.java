package com.company;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ClientHandlerUdp implements Runnable{
    DatagramSocket socket;
    byte[] receiveBuffer;

    public ClientHandlerUdp(DatagramSocket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        while(true){
            try {
                receiveBuffer = new byte[2048];
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(receivePacket);
                String message = new String(receivePacket.getData());
                System.out.println(Server.ANSI_BLUE + "Server message logs UDP: " + message + Server.ANSI_RESET);
                String username = "";
                for(ClientHandler handler: Server.clients) {
                    if(handler.getSocket().getPort() == receivePacket.getPort())
                        username = handler.getUsername();
                }
                for(ClientHandler handler: Server.clients) {
                    if(handler.getSocket().getPort() != receivePacket.getPort()) {
                        String finalMessage = username + Server.ANSI_BLUE + "\n" + message + Server.ANSI_RESET;
                        DatagramPacket sendPacket = new DatagramPacket(finalMessage.getBytes(), finalMessage.getBytes().length,
                                handler.getSocket().getInetAddress(), handler.getSocket().getPort());
                        socket.send(sendPacket);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
