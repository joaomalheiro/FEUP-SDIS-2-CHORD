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
    public void handleMessage() throws UnknownHostException {
        if(ChordInfo.getFingerTable().get(0).getIp() == InetAddress.getLocalHost().toString() && ChordInfo.getFingerTable().get(0).getPort() == Peer.port){
            MessageForwarder.sendMessage("SUCCESSOR " + ChordInfo.peerHash + " " + InetAddress.getLocalHost().getHostAddress() + " " + Peer.port, ci.getIp(), ci.getPort());
        }
        ChordInfo.searchSuccessor(ci);
    }
}
