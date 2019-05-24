package messages;

import chord.ChordInfo;
import chord.ConnectionInfo;
import peer.Peer;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class PredecessorMessage extends Message {

    private ConnectionInfo ci;

    public PredecessorMessage(ConnectionInfo ci) {
        this.ci = ci;
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
}
