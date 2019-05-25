package messages;

import peer.CheckPredecessor;
import peer.Peer;

public class PongMessage extends Message {
    private String ipAddress;
    private int port;

    public PongMessage(String ipAddress,int port){
        this.ipAddress = ipAddress;
        this.port = port;
    }
    @Override
    public void handleMessage() {
        synchronized(Peer.checkPredecessor){
            CheckPredecessor.dead = false;
            Peer.checkPredecessor.notify();
        }
    }
    @Override
    public String getIpAddress() {
        return this.ipAddress;
    }

    @Override
    public int getPort() {
        return this.port;
    }
    @Override
    public String toString() {
        return "PONG";
    }
}
