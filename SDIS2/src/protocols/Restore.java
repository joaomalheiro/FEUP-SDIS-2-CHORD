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
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

public class Restore implements Runnable {
    private String filename;

    public Restore(String filename) {
        this.filename = filename;
    }

    @Override
    public void run() {

        BigInteger hashFile = null;
        try {
            String [] params = new String[] {filename, FileHandler.getLastModified(filename)};
            hashFile = ChordManager.encrypt(params);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(FileHandler.checkFileExists("./peerDisk/peer" + Peer.getPeerAccessPoint() + "-" + ChordManager.peerHash + "/backup/" + hashFile)){
            try {
                byte[] content = FileHandler.readFromFile("./peerDisk/peer" + Peer.getPeerAccessPoint() + "-" + ChordManager.peerHash  + "/backup/" + hashFile);

                FileHandler.writeFile("./peerDisk/peer" + Peer.getPeerAccessPoint() + "-" + ChordManager.peerHash + "/restored/" + filename, content);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } else {

            ConnectionInfo ci = null;
            try {
                ci = new ConnectionInfo(hashFile, InetAddress.getLocalHost().getHostAddress(), Peer.port);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

            Message res = ChordManager.searchSuccessor2(ci);
            if (res instanceof SucessorMessage) {
                MessageForwarder.sendMessage(new RestoreMessage(ci, hashFile, this.filename, ((SucessorMessage) res).getCi().getIp(), ((SucessorMessage) res).getCi().getPort()));
            } else if (res instanceof LookupMessage) {
                MessageForwarder.sendMessage(new RestoreInitMessage(ci, hashFile, this.filename, res.getIpAddress(), res.getPort()));
            }

        }
    }
}
