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
            MessageForwarder.sendMessage("PREDECESSOR " + ChordInfo.peerHash + " " + InetAddress.getLocalHost().getHostAddress() + " " + Peer.port, ci.getIp(), ci.getPort());
        } else {
            ChordInfo.getFingerTable().set(0, new ConnectionInfo(ci.getHashedKey(), ci.getIp(), ci.getPort()));
        }
    }
}