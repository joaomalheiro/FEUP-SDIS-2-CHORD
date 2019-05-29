package messages;

import chord.ChordInfo;
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

        if(receivedKey.equals(ChordInfo.peerHash.toString())) {
            index = 0;
        } else {
            for(index = 0; index < ChordInfo.getM() * 8; index++)
            {
                String res = ChordInfo.calculateNextKey(ChordInfo.peerHash, index, ChordInfo.getM() * 8);
                if(res.equals(receivedKey))
                    break;
            }
        }

        ChordInfo.getFingerTable().set(index,ci);

        if(receivedKey.equals(ChordInfo.peerHash))
            return;

        try {
            //System.out.println("Before creating");
            PredecessorMessage predecessorMessage = new PredecessorMessage(new ConnectionInfo(ChordInfo.peerHash, InetAddress.getLocalHost().getHostAddress(), Peer.port),ci.getIp(), ci.getPort());
            //System.out.println("Before send");
            MessageForwarder.sendMessage(predecessorMessage);
            //System.out.println("After send");

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
