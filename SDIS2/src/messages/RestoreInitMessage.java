package messages;

import chord.ChordInfo;
import chord.ConnectionInfo;

import java.math.BigInteger;
import java.net.UnknownHostException;

public class RestoreInitMessage extends Message {
    private ConnectionInfo ci;
    private BigInteger hashFile;
    private String filename;
    private String ipAddress;
    private int port;

    public RestoreInitMessage(ConnectionInfo ci, BigInteger hashFile,String filename, String ipAddress, int port) {
        this.hashFile = hashFile;
        this.filename = filename;
        this.ci = ci;
        this.ipAddress = ipAddress;
        this.port = port;
    }

    @Override
    public String toString() {
        return "RESTORE-INIT" + this.ci + " " + this.hashFile + " " + this.filename;
    }

    @Override
    public void handleMessage() throws UnknownHostException {

        Message res = ChordInfo.searchSuccessor2(ci);

        System.out.println(" " + res.getIpAddress() + " " + res.getPort());
        if(res instanceof SucessorMessage) {
            MessageForwarder.sendMessage(new RestoreMessage(ci,this.hashFile, this.filename,((SucessorMessage) res).getCi().getIp(),((SucessorMessage) res).getCi().getPort()));
        } else if(res instanceof LookupMessage){
            MessageForwarder.sendMessage(new RestoreInitMessage(ci,this.hashFile,this.filename, res.getIpAddress(),res.getPort()));
        }

    }

    @Override
    public String getIpAddress() {
        return ipAddress;
    }

    @Override
    public int getPort() {
        return port;
    }
}
