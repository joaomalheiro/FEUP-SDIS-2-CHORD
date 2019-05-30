package messages;

import chord.ChordManager;
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

       Message res = ChordManager.searchSuccessor2(ci);
       if(res instanceof SucessorMessage) {
           System.out.println("res is sucessor, gonna send backup ready with " + res.getIpAddress() + " " + res.getPort());
           MessageForwarder.sendMessage(new BackupReadyMessage(((SucessorMessage) res).getCi(),this.hashFile, this.repDegree, this.filename,ci.getIp(),ci.getPort()));
       } else if(res instanceof LookupMessage){
           MessageForwarder.sendMessage(new BackupInitMessage(ci,this.hashFile,this.repDegree,this.filename,res.getIpAddress(),res.getPort()));
       }
    }
}
