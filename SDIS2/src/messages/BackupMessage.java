package messages;

import chord.ChordInfo;
import chord.ConnectionInfo;
import files.FileHandler;
import peer.Peer;

import java.io.IOException;
import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

public class BackupMessage extends Message {
    private int repDegree;
    private BigInteger hashFile;
    private ConnectionInfo ci;
    private byte[] body;
    private String ipAddress;
    private int port;

    public BackupMessage(ConnectionInfo ci, BigInteger hashFile, int repDegree, byte[] body,String ipAddress,int port) {
        this.ci = ci;
        this.hashFile = hashFile;
        this.repDegree = repDegree;
        this.body = body;
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
    public String toString() {
        return "BACKUP " + this.hashFile + " " + this.repDegree;
    }

    public void handleMessage() {
        //check if peer has space

        try {
            FileHandler.writeFile("./peerDisk/peer" + Peer.getPeerAccessPoint() + "-" + ChordInfo.peerHash + "/backup/" + hashFile, body);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        MessageForwarder.sendMessage(new BackupCompleteMessage(this.hashFile,this.repDegree,ci.getIp(), ci.getPort()));

    }
}
