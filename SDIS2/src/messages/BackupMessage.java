package messages;

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

    public BackupMessage(ConnectionInfo ci, BigInteger hashFile, int repDegree, byte[] body) {
        this.ci = ci;
        this.hashFile = hashFile;
        this.repDegree = repDegree;
        this.body = body;
    }

    @Override
    public String toString() {
        return "BACKUP " + this.hashFile + " " + this.repDegree;
    }

    public void handleMessage() {
        //check if peer has space

        try {
            FileHandler.writeFile("./peerDisk/peer" + Peer.getPeerAccessPoint() + "/backup/" + hashFile, body);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        MessageForwarder.sendMessage(new BackupCompleteMessage(this.hashFile,this.repDegree), ci.getIp(), ci.getPort());

    }
}
