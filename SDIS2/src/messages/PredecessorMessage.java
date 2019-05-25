package messages;

import chord.ChordInfo;
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
        ChordInfo.predecessor = ci;
    }

    @Override
    public String toString() {
        String returnString = null;
        try {
            returnString =  "PREDECESSOR " + ChordInfo.peerHash + " " + InetAddress.getLocalHost().getHostAddress() + " " + Peer.port;
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
