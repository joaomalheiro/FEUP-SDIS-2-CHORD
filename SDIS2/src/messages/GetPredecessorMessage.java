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
            MessageForwarder.sendMessage("RESPONSE_PREDECESSOR " + "NULL" + " " + InetAddress.getLocalHost().getHostAddress() + " " + Peer.port , ci.getIp(), ci.getPort());
        } else {
            MessageForwarder.sendMessage("RESPONSE_PREDECESSOR " + ChordInfo.getPredecessor() + " " + InetAddress.getLocalHost().getHostAddress() + " " + Peer.port , ci.getIp() , ci.getPort());
        }
    }
}
