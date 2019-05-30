package messages;

import chord.ChordInfo;
import chord.ConnectionInfo;
import files.FileHandler;
import peer.Peer;

import java.io.IOException;
import java.math.BigInteger;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;

public class RestoreFile extends Message {
    private ConnectionInfo ci;
    private BigInteger hashFile;
    private String filename;
    private byte[] body;
    private String ipAddress;
    private int port;

    public RestoreFile(ConnectionInfo ci, BigInteger hashFile, String filename, byte[] body, String ipAddress, int port) {
        this.ci = ci;
        this.filename = filename;
        this.hashFile = hashFile;
        this.body = body;
        this.ipAddress = ipAddress;
        this.port = port;
    }

    @Override
    public void handleMessage() throws UnknownHostException {

        try {
            FileHandler.writeFile("./peerDisk/peer" + Peer.getPeerAccessPoint() + "-" + ChordInfo.peerHash + "/restored/" + filename, body);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "RESTORE WITH FILE " + this.ci + " " + this.hashFile + " " + this.filename;
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
