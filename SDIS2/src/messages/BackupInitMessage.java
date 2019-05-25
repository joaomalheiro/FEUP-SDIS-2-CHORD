package messages;

import chord.ChordInfo;
import chord.ConnectionInfo;

import java.math.BigInteger;

public class BackupInitMessage extends Message {
    private ConnectionInfo ci;
    private String filename;
    private int repDegree;
    private BigInteger hashFile;
    private String ipAddress;
    private int port;

    public BackupInitMessage(ConnectionInfo ci,BigInteger hashFile, int repDegree, String filename,String ipAddress,int port) {
        this.hashFile = hashFile;
        this.repDegree = repDegree;
        this.ci = ci;
        this.filename = filename;
        this.ipAddress = ipAddress;
        this.port = port;
    }

    @Override
    public String toString() {
        return "BACKUP-INIT" + this.hashFile + " " + " " + this.filename + " " + this.repDegree;
    }
    @Override
    public String getIpAddress() {
        return this.ipAddress;
    }

    @Override
    public int getPort() {
        return this.port;
    }

    public void handleMessage() {

       Message res = ChordInfo.searchSuccessor2(ci);
       if(res instanceof SucessorMessage) {
           MessageForwarder.sendMessage(new BackupReadyMessage(((SucessorMessage) res).getCi(),this.hashFile,this.repDegree,this.filename,ci.getIp(),ci.getPort()));
       } else if(res instanceof LookupMessage){
           MessageForwarder.sendMessage(new BackupInitMessage(ci,this.hashFile,this.repDegree,this.filename,res.getIpAddress(),res.getPort()));
       }
    }
}
