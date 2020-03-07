package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
    public static void main(String[] args) throws IOException {
        Socket socket = null;
        try {
            String message = initializeClient();
            while(!message.equals("logout")) {
                socket = new Socket("localhost", 12345);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new
                        InputStreamReader(socket.getInputStream()));
                out.println(message);
                String response = in.readLine();
                System.out.println("Received msg: " + response);
                message = scanner.nextLine();
                out.println(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) socket.close();
        }
    }

    private static String initializeClient(){
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Please, write your username");
            StringBuilder welcomingMessageBuilder = new StringBuilder("New client: ");
            welcomingMessageBuilder.append(scanner.nextLine());
            scanner.close();
            Socket socket = new Socket("localhost", 12345);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new
                    InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return welcomingMessageBuilder.toString();
    }
}
