package messages;

import chord.ChordInfo;
import chord.ConnectionInfo;
import peer.Peer;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class SucessorMessage extends Message {

    private ConnectionInfo ci;

    public SucessorMessage(ConnectionInfo ci) {
        this.ci = ci;
    }

    @Override
    public void handleMessage() {

        ChordInfo.getFingerTable().set(0,ci);
        try {
            PredecessorMessage predecessorMessage = new PredecessorMessage(new ConnectionInfo(ChordInfo.peerHash, InetAddress.getLocalHost().getHostAddress(), Peer.port));
            MessageForwarder.sendMessage(predecessorMessage, ci.getIp(), ci.getPort());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }
}
