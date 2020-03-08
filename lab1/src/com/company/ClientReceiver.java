package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.SocketException;

public class ClientReceiver implements Runnable {
    BufferedReader reader;

    public ClientReceiver(BufferedReader reader) {
        this.reader = reader;
    }

    @Override
    public void run() {
        try {
            while (true) {
                String message = reader.readLine();
                System.out.println(message);
            }
        }catch(SocketException se){
            System.out.println(Server.ANSI_RED + "SERVER SHUTDOWN!!!" + Server.ANSI_RESET);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
