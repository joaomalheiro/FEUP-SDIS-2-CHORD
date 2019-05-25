package messages;

import chord.ChordInfo;
import chord.ConnectionInfo;
import peer.Peer;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;

public class ResponsePredecessorMessage extends Message {
    private ConnectionInfo ci;

    public ResponsePredecessorMessage(ConnectionInfo ci) {
        this.ci = ci;
    }
    @Override
    public void handleMessage() throws UnknownHostException {
        if(ci.getHashedKey() == null){
            System.out.println("RECEIVED NULL");
            MessageForwarder.sendMessage(new PredecessorMessage(new ConnectionInfo(ChordInfo.peerHash, InetAddress.getLocalHost().getHostAddress(), Peer.port)), ci.getIp(), ci.getPort());
        } else if(ci.getHashedKey().compareTo(ChordInfo.peerHash) != 0){
            System.out.println("RECEIVED " + ci);
            ChordInfo.getFingerTable().set(0, new ConnectionInfo(ci.getHashedKey(), ci.getIp(), ci.getPort()));
        }
    }

    @Override
    public String toString() {
        String returnString;
        returnString = "RESPONSE_PREDECESSOR " + ci.getHashedKey() + " " + ci.getIp() + " " + ci.getPort();
        return returnString;
    }
}
