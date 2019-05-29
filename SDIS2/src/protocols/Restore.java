package protocols;

import chord.ChordInfo;
import chord.ConnectionInfo;
import files.FileHandler;
import messages.*;
import peer.Peer;

import java.awt.*;
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
            hashFile = FileHandler.encrypt(filename);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        ConnectionInfo ci = null;
        try {
            ci = new ConnectionInfo(hashFile, InetAddress.getLocalHost().getHostAddress(), Peer.port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        Message res = ChordInfo.searchSuccessor2(ci);

        System.out.println("res - " + res.getIpAddress() + " " + res.getPort());

        if(res instanceof SucessorMessage) {
            MessageForwarder.sendMessage(new RestoreMessage(ci, hashFile, this.filename,((SucessorMessage) res).getCi().getIp(),((SucessorMessage) res).getCi().getPort()));
        } else if(res instanceof LookupMessage){
            MessageForwarder.sendMessage(new RestoreInitMessage(ci, hashFile,this.filename, res.getIpAddress(),res.getPort()));
        }

    }
}
