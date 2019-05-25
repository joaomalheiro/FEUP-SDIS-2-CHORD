package messages;

import chord.ChordInfo;
import chord.ConnectionInfo;
import peer.Peer;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class GetPredecessorMessage extends Message {

    private ConnectionInfo ci;
    public GetPredecessorMessage(ConnectionInfo ci){
        this.ci = ci;
    }

    @Override
    public void handleMessage() throws UnknownHostException {
        if(ChordInfo.predecessor == null){
            MessageForwarder.sendMessage(new ResponsePredecessorMessage(new ConnectionInfo(null, InetAddress.getLocalHost().getHostAddress(), Peer.port)), ci.getIp(), ci.getPort());
        } else {
            MessageForwarder.sendMessage(new ResponsePredecessorMessage(new ConnectionInfo(ChordInfo.getPredecessor().getHashedKey(),InetAddress.getLocalHost().getHostAddress(),Peer.port)), ci.getIp(), ci.getPort());
        }
    }

    @Override
    public String toString() {
        return "GET_PREDECESSOR " + this.ci.getIp() + " " + this.ci.getPort();
    }
}
