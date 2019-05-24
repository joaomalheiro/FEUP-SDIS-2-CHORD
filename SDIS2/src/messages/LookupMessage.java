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
        if(ChordInfo.getFingerTable().get(0).getPort() == Peer.port) {
            MessageForwarder.sendMessage(new SucessorMessage(ci.getHashedKey().toString(),new ConnectionInfo(ChordInfo.peerHash, InetAddress.getLocalHost().getHostAddress(),Peer.port)), ci.getIp(), ci.getPort());
        } else {
            ChordInfo.searchSuccessor(ci);
        }


    }

    @Override
    public String toString() {
        return "LOOKUP " + this.ci;
    }
}
