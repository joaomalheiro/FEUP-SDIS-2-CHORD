package protocols;

import chord.ChordManager;
import chord.ConnectionInfo;
import files.FileHandler;
import messages.*;
import peer.Peer;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

public class Backup implements Runnable{
    private String filename;
    private int repDegree;

    public Backup(String filename, int repDegree) {
        this.filename = filename;
        this.repDegree = repDegree;
    }

    @Override
    public void run() {

        BigInteger hashFile = null;
        try {
            String [] params = new String[] {filename, FileHandler.getFileSize(filename)};
            hashFile = ChordManager.encrypt(params);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ConnectionInfo ci = null;
        try {
            ci = new ConnectionInfo(hashFile, InetAddress.getLocalHost().getHostAddress(), Peer.port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        Message res = ChordManager.searchSuccessor2(ci);

        if(res instanceof SucessorMessage) {
            try {
                byte[] content = FileHandler.readFromFile("./testFiles/" + filename);
                MessageForwarder.sendMessage(new BackupMessage(ci, hashFile, repDegree, content, ((SucessorMessage) res).getCi().getIp(), ((SucessorMessage) res).getCi().getPort()));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } else if(res instanceof LookupMessage){
            MessageForwarder.sendMessage(new BackupInitMessage(ci, hashFile, this.repDegree, this.filename, res.getIpAddress(), res.getPort()));
        }

    }
}
