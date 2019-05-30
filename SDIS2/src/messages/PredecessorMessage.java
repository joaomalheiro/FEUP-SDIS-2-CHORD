package messages;

import chord.ChordManager;
import chord.ConnectionInfo;
import peer.Peer;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class PredecessorMessage extends Message {

    private ConnectionInfo ci;
    private String ipAddress;
    private int port;

    public PredecessorMessage(ConnectionInfo ci,String ipAddress,int port) {
        this.ci = ci;
        this.ipAddress = ipAddress;
        this.port = port;
    }

    @Override
    public void handleMessage() {
        if(ChordManager.predecessor == null ||
                ChordManager.numberInInterval(ChordManager.predecessor.getHashedKey(), ChordManager.peerHash,ci.getHashedKey()))
        ChordManager.predecessor = ci;
    }

    @Override
    public String toString() {
        String returnString = null;
        try {
            returnString =  "PREDECESSOR " + ChordManager.peerHash + " " + InetAddress.getLocalHost().getHostAddress() + " " + Peer.port;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return returnString;
    }
    @Override
    public String getIpAddress() {
        return this.ipAddress;
    }

    @Override
    public int getPort() {
        return this.port;
    }
}
