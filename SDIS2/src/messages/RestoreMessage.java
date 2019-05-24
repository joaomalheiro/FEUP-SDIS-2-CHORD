package messages;

import chord.ConnectionInfo;
import files.FileHandler;
import peer.Peer;

import java.io.IOException;
import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

public class RestoreMessage extends Message {
    private ConnectionInfo ci;
    private BigInteger hashFile;
    private String filename;

    public RestoreMessage(ConnectionInfo ci, BigInteger hashFile, String filename) {
        this.ci = ci;
        this.hashFile = hashFile;
        this.filename = filename;
    }

    @Override
    public String toString() {
        return "RESTORE " + this.ci + " " + this.hashFile + " " + this.filename;
    }

    public void handleMessage() {

        if(FileHandler.checkFileExists("./peerDisk/peer" + Peer.getPeerAccessPoint() + "/backup/" + hashFile)) {

            try {
                byte[] content = FileHandler.readFromFile("./peerDisk/peer" + Peer.getPeerAccessPoint() + "/backup/" + hashFile);

                //MessageForwarder.sendMessage(new RestoreCompleteMessage());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {

        }
        //MessageForwarder.sendMessage(new BackupCompleteMessage(this.hashFile), ci.getIp(), ci.getPort());

    }
}
