package messages;

import chord.ChordManager;
import chord.ConnectionInfo;
import peer.Peer;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class SucessorMessage extends Message {

    private String receivedKey;
    private ConnectionInfo ci;
    private String ipAddress;
    private int port;

    public SucessorMessage(String receivedKey, ConnectionInfo ci,String ipAddress,int port) {
        this.receivedKey = receivedKey;
        this.ci = ci;
        this.ipAddress = ipAddress;
        this.port = port;
    }

    @Override
    public void handleMessage() {

        int index;

        if(receivedKey.equals(ChordManager.peerHash.toString())) {
            index = 0;
        } else {
            for(index = 0; index < ChordManager.getM(); index++)
            {
                String res = ChordManager.calculateNextKey(ChordManager.peerHash, index, ChordManager.getM());
                if(res.equals(receivedKey))
                    break;
            }
        }

        ChordManager.getFingerTable().set(index,ci);

        if(receivedKey.equals(ChordManager.peerHash))
            return;

        try {
            PredecessorMessage predecessorMessage = new PredecessorMessage(new ConnectionInfo(ChordManager.peerHash, InetAddress.getLocalHost().getHostAddress(), Peer.port),ci.getIp(), ci.getPort());
            MessageForwarder.sendMessage(predecessorMessage);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {

        String returnString =  "SUCCESSOR " + receivedKey + " " + ci.getHashedKey().toString() + " " + ci.getIp() + " " + ci.getPort();

        return returnString;
    }
    @Override
    public String getIpAddress() {
        return this.ipAddress;
    }

    @Override
    public int getPort() {
        return this.port;
    }

    public ConnectionInfo getCi() {
        return ci;
    }
}
