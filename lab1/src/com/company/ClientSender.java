package com.company;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;
import java.util.Scanner;

public class ClientSender implements  Runnable {
    PrintWriter writer;
    DatagramSocket socket;
    Socket tcpSocket;
    String ascii =
            "                       _\n" +
                    "            _,..-\"\"\"--' `,.-\".\n" +
                    "          ,'      __.. --',  |\n" +
                    "        _/   _.-\"' |    .' | |       ____\n" +
                    "  ,.-\"\"'    `-\"+.._|     `.' | `-..,',--.`.\n" +
                    " |   ,.                      '    j 7    l \\__\n" +
                    " |.-'                            /| |    j||  .\n" +
                    " `.                   |         / L`.`\"\"','|\\  \\\n" +
                    "   `.,----..._       ,'`\"'-.  ,'   \\ `\"\"'  | |  l\n" +
                    "     Y        `-----'       v'    ,'`,.__..' |   .\n" +
                    "      `.                   /     /   /     `.|   |\n" +
                    "        `.                /     l   j       ,^.  |L\n" +
                    "          `._            L       +. |._   .' \\|  | \\\n" +
                    "            .`--...__,..-'\"\"'-._  l L  \"\"\"    |  |  \\\n" +
                    "          .'  ,`-......L_       \\  \\ \\     _.'  ,'.  l\n" +
                    "       ,-\"`. / ,-.---.'  `.      \\  L..--\"'  _.-^.|   l\n" +
                    " .-\"\".'\"`.  Y  `._'   '    `.     | | _,.--'\"     |   |\n" +
                    "  `._'   |  |,-'|      l     `.   | |\"..          |   l\n" +
                    "  ,'.    |  |`._'      |      `.  | |_,...---\"\"\"\"\"`    L\n" +
                    " /   |   j _|-' `.     L       | j ,|              |   |\n" +
                    "`--,\"._,-+' /`---^..../._____,.L',' `.             |\\  |\n" +
                    "   |,'      L                   |     `-.          | \\j\n" +
                    "            .                    \\       `,        |  |\n" +
                    "             \\                __`.Y._      -.     j   |\n" +
                    "              \\           _.,'       `._     \\    |  j\n" +
                    "              ,-\"`-----\"\"\"\"'           |`.    \\  7   |\n" +
                    "             /  `.        '            |  \\    \\ /   |\n" +
                    "            |     `      /             |   \\    Y    |\n" +
                    "            |      \\    .             ,'    |   L_.-')\n" +
                    "             L      `.  |            /      ]     _.-^._\n" +
                    "              \\   ,'  `-7         ,-'      / |  ,'      `-._\n" +
                    "             _,`._       `.   _,-'        ,',^.-            `.\n" +
                    "          ,-'     v....  _.`\"',          _:'--....._______,.-'\n" +
                    "        ._______./     /',,-'\"'`'--.  ,-'  `.\n" +
                    "                 \"\"\"\"\"`.,'         _\\`----...' mh\n" +
                    "                        --------\"\"'";


    public ClientSender(PrintWriter writer, DatagramSocket socket, Socket tcpSocket) {
        this.writer = writer;
        this.socket = socket;
        this.tcpSocket = tcpSocket;
    }

    @Override
    public void run() {
        try {
            socket.bind(tcpSocket.getLocalSocketAddress());
            System.out.println(Server.ANSI_YELLOW + "Welcome to server" + Server.ANSI_RESET);
        while(true) {
            Scanner scanner = new Scanner(System.in);
            String message = scanner.nextLine();
            if(message.equals("U")) {
                sendUdpMessage();
            }
            else if(message.equals("logout")){
                socket.close();
                tcpSocket.close();
                System.exit(0);
            }
            else
                sendTcpMessage(message);
        }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("IO");
        }
    }
    private void sendUdpMessage(){
        try {
            DatagramPacket sendPacket = new DatagramPacket(ascii.getBytes(), ascii.getBytes().length,
                    InetAddress.getByName("localhost"), 12345);
            socket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendTcpMessage(String message) {
        writer.println(message);
    }
}
