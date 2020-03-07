package com.company;

import java.io.PrintWriter;
import java.util.Scanner;

public class ClientSender implements  Runnable {
    PrintWriter writer;

    public ClientSender(PrintWriter writer) {
        this.writer = writer;
    }

    @Override
    public void run() {
        while(true) {
            Scanner scanner = new Scanner(System.in);
            String message = scanner.nextLine();
            writer.println(message);
        }

    }
}
