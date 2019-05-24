package messages;

import chord.ChordInfo;
import chord.ConnectionInfo;
import peer.Peer;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class LookupMessage extends Message {
    private ConnectionInfo ci;

    public LookupMessage(ConnectionInfo ci) {
        this.ci = ci;
    }

    @Override
    public void handleMessage() {

    }
}
