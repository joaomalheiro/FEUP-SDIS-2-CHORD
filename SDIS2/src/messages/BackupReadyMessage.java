package messages;

import chord.ChordManager;
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
    private String ipAddress;
    private int port;

    public BackupReadyMessage(ConnectionInfo ci, BigInteger hashFile, int repDegree, String filename,String ipAddress,int port) {
        this.hashFile = hashFile;
        this.repDegree = repDegree;
        this.ci = ci;
        this.filename = filename;
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
        return "BACKUP-READY" + this.hashFile + " " + " " + this.filename + " " + this.repDegree;
    }

    public void handleMessage() {

            try {

                byte[] content = FileHandler.readFromFile("./testFiles/" + filename);

                if((this.ci.getPort() == Peer.port) && (this.ci.getIp().equals(InetAddress.getLocalHost().getHostAddress()))) {

                    try {
                        FileHandler.writeFile("./peerDisk/peer" + Peer.getPeerAccessPoint() + "-" + ChordManager.peerHash + "/backup/" + hashFile, content);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                String [] params = new String[] {filename, FileHandler.getLastModified(filename)};
                BigInteger fileHash = ChordManager.encrypt(params);

                MessageForwarder.sendMessage(new BackupMessage(new ConnectionInfo(ChordManager.peerHash, InetAddress.getLocalHost().getHostAddress(), Peer.port), fileHash, repDegree, content, ci.getIp(), ci.getPort()));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }
}
