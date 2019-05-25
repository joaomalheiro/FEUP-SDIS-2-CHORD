package messages;

import chord.ChordInfo;
import chord.ConnectionInfo;
import files.FileHandler;
import peer.Peer;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

public class BackupReadyMessage extends Message {
    private ConnectionInfo ci;
    private String filename;
    private int repDegree;
    private BigInteger hashFile;

    public BackupReadyMessage(ConnectionInfo ci, BigInteger hashFile, int repDegree, String filename) {
        this.hashFile = hashFile;
        this.repDegree = repDegree;
        this.ci = ci;
        this.filename = filename;
    }

    @Override
    public String toString() {
        return "BACKUP-READY" + this.hashFile + " " + " " + this.filename + " " + this.repDegree;
    }

    public void handleMessage() {

        try {
            byte[] content = FileHandler.readFromFile("./testFiles/" + filename);

            BigInteger fileHash = FileHandler.encrypt(filename);

            MessageForwarder.sendMessage(new BackupMessage(new ConnectionInfo(ChordInfo.peerHash, InetAddress.getLocalHost().getHostAddress(), Peer.port), fileHash, repDegree, content), ci.getIp(), ci.getPort());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
