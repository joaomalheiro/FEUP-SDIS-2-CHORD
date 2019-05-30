package messages;

import chord.ChordManager;
import chord.ConnectionInfo;
import files.FileHandler;
import peer.Peer;

import java.io.IOException;
import java.math.BigInteger;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;

public class RestoreSucessor extends Message {
    private ConnectionInfo ci;
    private BigInteger originalHash;
    private BigInteger hashFile;
    private String filename;
    private String ipAddress;
    private int port;

    RestoreSucessor(ConnectionInfo ci, BigInteger originalHash, BigInteger hashFile, String filename, String ipAddress, int port){
        this.ci = ci;
        this.originalHash = originalHash;
        this.hashFile = hashFile;
        this.filename = filename;
        this.ipAddress = ipAddress;
        this.port = port;
    }

    @Override
    public String toString() {
        return "RESTORE-SUCESSOR: " + this.ci + " " + this.hashFile + " " + this.filename;
    }

    @Override
    public void handleMessage() throws UnknownHostException {

        if(ChordManager.peerHash.compareTo(originalHash) == 0) {
            System.out.println("Restore failed, there is no such file in the system");
            return;
        }

        if(FileHandler.checkFileExists("./peerDisk/peer" + Peer.getPeerAccessPoint() + "-" + ChordManager.peerHash + "/backup/" + hashFile)) {
            try {
                byte[] content = FileHandler.readFromFile("./peerDisk/peer" + Peer.getPeerAccessPoint() + "-" + ChordManager.peerHash  + "/backup/" + hashFile);

                MessageForwarder.sendMessage(new RestoreFile(ci, hashFile, filename, content, ci.getIp(), ci.getPort()));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            MessageForwarder.sendMessage(new RestoreSucessor(ci, originalHash, hashFile, filename, ChordManager.getFingerTable().get(0).getIp(), ChordManager.getFingerTable().get(0).getPort()));
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
