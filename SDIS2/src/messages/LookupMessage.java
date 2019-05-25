package messages;

import chord.ChordInfo;
import chord.ConnectionInfo;
import peer.Peer;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class LookupMessage extends Message {
    private ConnectionInfo ci;
    private String ipAddress;
    private int port;

    public LookupMessage(ConnectionInfo ci,String ipAddress,int port) {
        this.ci = ci;
        this.ipAddress = ipAddress;
        this.port = port;
    }

    @Override
    public void handleMessage() throws UnknownHostException {
        if(ChordInfo.getFingerTable().get(0).getPort() == Peer.port) {
            MessageForwarder.sendMessage(new SucessorMessage(ci.getHashedKey().toString(),new ConnectionInfo(ChordInfo.peerHash, InetAddress.getLocalHost().getHostAddress(),Peer.port), ci.getIp(), ci.getPort()));
        } else {
            Message message = ChordInfo.searchSuccessor2(ci);
            MessageForwarder.sendMessage(message);
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
        return "LOOKUP " + this.ci;
    }
}
