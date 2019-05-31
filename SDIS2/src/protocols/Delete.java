package protocols;

import chord.ChordManager;
import chord.ConnectionInfo;
import files.FileHandler;
import messages.*;
import peer.Peer;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

public class Delete implements Runnable{
    private BigInteger hashfile;

    public Delete(String filename) {

            try {
            String [] params = new String[] {filename, FileHandler.getFileSize(filename)};
            this.hashfile = ChordManager.encrypt(params);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        if(FileHandler.checkFileExists("./peerDisk/peer" + Peer.getPeerAccessPoint() + "-"  + ChordManager.peerHash + "/backup/" + hashfile)){
            try {
                Files.deleteIfExists(Paths.get("./peerDisk/peer" + Peer.getPeerAccessPoint() + "-"  + ChordManager.peerHash + "/backup/" + hashfile));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
       
        MessageForwarder.sendMessage(new DeleteMessage(hashfile,ChordManager.peerHash,ChordManager.getFingerTable().get(0).getIp(),ChordManager.getFingerTable().get(0).getPort()));
    }
}
