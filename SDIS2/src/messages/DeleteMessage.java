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
    private String ipAddress;
    private int port;

    public DeleteMessage(BigInteger hashfile,String ipAddress, int port){
        this.hashfile = hashfile;
        this.ipAddress = ipAddress;
        this.port = port;
    }
    @Override
    public void handleMessage() throws IOException {
        System.out.println("RECEIVED DELETE FOR " + hashfile);
        if(FileHandler.checkFileExists("./peerDisk/peer" + Peer.getPeerAccessPoint() + "-"  + ChordManager.peerHash + "/backup/" + hashfile)){
            Files.deleteIfExists(Paths.get("./peerDisk/peer" + Peer.getPeerAccessPoint() + "-"  + ChordManager.peerHash + "/backup/" + hashfile));
        }

        if(!ChordManager.numberInInterval(ChordManager.peerHash, ChordManager.getFingerTable().get(0).getHashedKey(), hashfile)){
            Message res = ChordManager.searchSuccessor2(new ConnectionInfo(hashfile,null,0));
            if(res instanceof SucessorMessage){
                MessageForwarder.sendMessage(new DeleteMessage(hashfile,ChordManager.getFingerTable().get(0).getIp(),ChordManager.getFingerTable().get(0).getPort()));
            } else if(res instanceof LookupMessage) {
                MessageForwarder.sendMessage(new DeleteMessage(hashfile,res.getIpAddress(),res.getPort()));
            }

        }
    }
   
    @Override
    public String toString() {

        String returnString =  "DELETE " + hashfile;

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
