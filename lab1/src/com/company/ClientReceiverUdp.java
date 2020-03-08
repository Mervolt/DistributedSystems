package com.company;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ClientReceiverUdp implements Runnable {
    DatagramSocket socket;
    byte[] receiveBuffer;

    public ClientReceiverUdp(DatagramSocket socket) {
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
                System.out.println(message);
            } catch (IOException e) {
                System.exit(0); //socket is closed by sender
            }
        }
    }
}
