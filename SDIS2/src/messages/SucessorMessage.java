package messages;

import chord.ChordInfo;
import chord.ConnectionInfo;
import peer.Peer;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class SucessorMessage extends Message {

    private String receivedKey;
    private ConnectionInfo ci;

    public SucessorMessage(String receivedKey, ConnectionInfo ci) {
        this.receivedKey = receivedKey;
        this.ci = ci;
    }

    @Override
    public void handleMessage() {

        int index;

        if(receivedKey.equals(ChordInfo.peerHash.toString())) {
            index = 0;
        } else {
            for(index = 0; index < ChordInfo.getM() * 8; index++)
            {
                String res = ChordInfo.calculateNextKey(ChordInfo.peerHash, index, ChordInfo.getM() * 8);
                if(res.equals(ci.getHashedKey().toString()))
                    break;
            }
        }

        ChordInfo.getFingerTable().set(index,ci);

        try {
            PredecessorMessage predecessorMessage = new PredecessorMessage(new ConnectionInfo(ChordInfo.peerHash, InetAddress.getLocalHost().getHostAddress(), Peer.port));
            MessageForwarder.sendMessage(predecessorMessage, ci.getIp(), ci.getPort());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {

        String returnString =  "SUCCESSOR " + receivedKey + " " + ci.getHashedKey().toString() + " " + ci.getIp() + " " + ci.getPort();

        return returnString;
    }
}
