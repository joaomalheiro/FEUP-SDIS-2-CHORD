package messages;

import chord.ChordInfo;
import chord.ConnectionInfo;
import peer.Peer;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class LookupMessage extends Message {
    private int repDegree;
    private String hashFile;
    private String type;
    private byte[] body;
    private ConnectionInfo ci;

    public LookupMessage(String type, ConnectionInfo ci) {
        this.type = type;
        this.ci = ci;
    }

    @Override
    public void handleMessage() {

    }
}
