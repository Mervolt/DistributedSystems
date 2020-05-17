package com.company;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

public class ZooObserver implements Watcher {
    
    private static final String NODE_PATH = "/z";
    private final ZooKeeper zooKeeper;
    private String[] executableArgs;
    private Process executable = null;
    int descendantCounter = 0;

    public ZooObserver(ZooKeeper zooKeeper, String[] executableArgs) throws KeeperException, InterruptedException{
        this.zooKeeper = zooKeeper;
        this.executableArgs = executableArgs;
        if (zooKeeper.exists(NODE_PATH, this) == null)
            waitForNode();
        else {
            waitForNode();
            countDescendants(NODE_PATH);
        }
    }

    @Override
    public void process(WatchedEvent event) {
        assert event.getPath().equals(NODE_PATH);
        try {
            switch (event.getType()) {
                case NodeDeleted:
                    System.out.println("Deleted node ");
                    terminate();
                    waitForNode();
                    descendantCounter = 0;
                    break;
                case NodeChildrenChanged:
                    System.out.println("Node children changed ");
                    countDescendants(NODE_PATH);
                    waitForNode();
                    break;
                case NodeCreated:
                    System.out.println("Created node ");
                    waitForNode();
                    run();
                    countDescendants(NODE_PATH);
                    break;
            }
        }
        catch (KeeperException e) {
            terminate();
        }
        catch (InterruptedException e) {
            System.out.println("Interrupted ");
        }
    }

    private void waitForNode() throws KeeperException, InterruptedException {
        zooKeeper.exists(NODE_PATH, this, null, null);
        if(zooKeeper.exists(NODE_PATH, false) != null)
            watchDescendants(NODE_PATH);
    }

    private void watchDescendants(String path) throws KeeperException, InterruptedException {
        zooKeeper.getChildren(path, this);
        for(String child: zooKeeper.getChildren(path, this)){
            watchDescendants(path + "/" + child);
        }
    }

    private void countDescendants(String path) throws KeeperException, InterruptedException {
        try {
            for(String child : zooKeeper.getChildren(path, false)){
                countDescendants(path + "/" + child);
            }
            if(path.equals(NODE_PATH)) {
                if(descendantCounter == 1){
                    System.out.println(String.format(("Path %s has 1 descendant"), NODE_PATH));
                }
                else
                    System.out.println(String.format("Path %s has %d descendants", NODE_PATH, descendantCounter));
                descendantCounter = 0;
            }
            else
                descendantCounter++;
        } catch (KeeperException  e) {
            System.out.println("Keeper exception during counting children");
            terminate();
            waitForNode();
        } catch (InterruptedException e){
            System.out.println("Interrupted exception during counting children");
        }
    }

    private void terminate() {
        if (isExecutableEligibleForTermination()) {
            executable.destroy();
        }
    }

    private boolean isExecutableEligibleForTermination(){
        return executable != null && executable.isAlive();
    }

    private void run() {
        try {
            if (isExecutableEligibleForRun())
                executable = Runtime.getRuntime().exec(executableArgs);
        }
        catch (IOException e) {
            System.out.println("Fail during launching application");
        }
    }

    private boolean isExecutableEligibleForRun(){
        return executable == null || !executable.isAlive();
    }
}
