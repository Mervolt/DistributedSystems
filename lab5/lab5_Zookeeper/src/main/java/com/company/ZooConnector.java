package com.company;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class ZooConnector {

    private static final String NODE_PATH = "/z";
    private final ZooKeeper zooKeeper;
    private final Watcher watcher;

    public static void main(String[] args) throws InterruptedException, IOException, KeeperException {
        if (args.length < 2) {
            System.out.println("Required host and application path");
        } else {
            String host = args[0];
            String[] executableArgs = Arrays.copyOfRange(args, 1, args.length);
            ZooConnector zooConnector = new ZooConnector(host, executableArgs);
            zooConnector.run();
        }

    }

    private ZooConnector(String host, String[] appArgs) throws IOException, KeeperException, InterruptedException {
        zooKeeper = new ZooKeeper(host, 3000, (ignore) -> {});
        watcher = new ZooObserver(zooKeeper, appArgs);
    }

    private void run() throws IOException, KeeperException, InterruptedException {
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String input = inputReader.readLine().trim();
            switch (input) {
                case "printTree":
                    printTree(NODE_PATH);
                    break;
                case "quit":
                    System.exit(0);
                    break;
            }
        }
    }

    private void printTree(String path) throws KeeperException, InterruptedException {
        if (zooKeeper.exists(path, false) != null) {
            System.out.println("Znode path: " + path);
            for (String child : zooKeeper.getChildren(path, false)) {
                printTree(path + "/" + child);
            }
        }
        else
            System.out.println("Znode: " + path + " does not exist");
    }
}
