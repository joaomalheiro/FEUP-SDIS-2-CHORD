package messages;


import chord.ChordManager;
import chord.ConnectionInfo;
import files.FileHandler;
import peer.Peer;

import java.io.IOException;
import java.math.BigInteger;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DeleteMessage extends Message {
    private BigInteger hashfile;
    private BigInteger originalSender;
    private String ipAddress;
    private int port;

    public DeleteMessage(BigInteger hashfile, BigInteger originalSender, String ipAddress, int port){
        this.hashfile = hashfile;
        this.originalSender = originalSender;
        this.ipAddress = ipAddress;
        this.port = port;
    }
    @Override
    public void handleMessage() throws IOException {
        if(FileHandler.checkFileExists("./peerDisk/peer" + Peer.getPeerAccessPoint() + "-"  + ChordManager.peerHash + "/backup/" + hashfile)){
            Files.deleteIfExists(Paths.get("./peerDisk/peer" + Peer.getPeerAccessPoint() + "-"  + ChordManager.peerHash + "/backup/" + hashfile));
        }

        if(ChordManager.peerHash.compareTo(originalSender) != 0)
                MessageForwarder.sendMessage(new DeleteMessage(hashfile,originalSender,ChordManager.getFingerTable().get(0).getIp(),ChordManager.getFingerTable().get(0).getPort()));
    }
   
    @Override
    public String toString() {

        String returnString =  "DELETE " + hashfile + " " + originalSender;

        return returnString;
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
