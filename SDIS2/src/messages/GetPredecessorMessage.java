package messages;

import chord.ChordManager;
import chord.ConnectionInfo;
import peer.Peer;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class GetPredecessorMessage extends Message {

    private ConnectionInfo ci;
    private String ipAddress;
    private int port;
    public GetPredecessorMessage(ConnectionInfo ci,String ipAddress,int port){
        this.ci = ci;
        this.ipAddress = ipAddress;
        this.port = port;
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
    public void handleMessage() throws UnknownHostException {
        if(ChordManager.predecessor == null){
            MessageForwarder.sendMessage(new ResponsePredecessorMessage(new ConnectionInfo(null, InetAddress.getLocalHost().getHostAddress(), Peer.port), ci.getIp(), ci.getPort()));
        } else {
            MessageForwarder.sendMessage(new ResponsePredecessorMessage(new ConnectionInfo(ChordManager.getPredecessor().getHashedKey(), ChordManager.getPredecessor().getIp(), ChordManager.getPredecessor().getPort()), ci.getIp(), ci.getPort()));
        }
    }

    @Override
    public String toString() {
        return "GET_PREDECESSOR " + this.ci.getIp() + " " + this.ci.getPort();
    }
}
