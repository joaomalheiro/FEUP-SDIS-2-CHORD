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
    private String ipAddress;
    private int port;

    public ResponsePredecessorMessage(ConnectionInfo ci,String ipAddress,int port) {
        this.ci = ci;
        this.ipAddress = ipAddress;
        this.port = port;
    }
    @Override
    public void handleMessage() throws UnknownHostException {
        if(ci.getHashedKey() == null){
            System.out.println("RECEIVED NULL");
        } else if(ChordInfo.numberInInterval(ChordInfo.peerHash,ChordInfo.getFingerTable().get(0).getHashedKey(),ci.getHashedKey())) {
            ChordInfo.getFingerTable().set(0, new ConnectionInfo(ci.getHashedKey(), ci.getIp(), ci.getPort()));
        }
        MessageForwarder.sendMessage(new PredecessorMessage(new ConnectionInfo(ChordInfo.peerHash, InetAddress.getLocalHost().getHostAddress(), Peer.port), ci.getIp(), ci.getPort()));

    }

    @Override
    public String toString() {
        String returnString;
        returnString = "RESPONSE_PREDECESSOR " + ci.getHashedKey() + " " + ci.getIp() + " " + ci.getPort();
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
}
