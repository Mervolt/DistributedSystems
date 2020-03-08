package com.company;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class ClientReceiverMulticast implements Runnable {
    byte[] receiveBuffer;

    @Override
    public void run() {
        MulticastSocket multicastSocket = null;
        try {
            multicastSocket = new MulticastSocket(1234);
            multicastSocket.joinGroup(InetAddress.getByName("225.0.0.0"));
            while (true) {
                receiveBuffer = new byte[2048];
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                multicastSocket.receive(receivePacket);
                String message = new String(receivePacket.getData());
                System.out.println(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
