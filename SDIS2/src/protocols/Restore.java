package protocols;

import chord.ChordInfo;
import chord.ConnectionInfo;
import files.FileHandler;
import messages.BackupMessage;
import messages.MessageForwarder;
import messages.RestoreMessage;
import peer.Peer;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

public class Restore implements Runnable {
    private String filename;

    Restore(String filename) {
        this.filename = filename;
    }
    @Override
    public void run() {

        try {
            BigInteger fileHash = FileHandler.encrypt(filename);

            //search sucessor
            MessageForwarder.sendMessage(new RestoreMessage(new ConnectionInfo(ChordInfo.peerHash, InetAddress.getLocalHost().getHostAddress(), Peer.port), fileHash, filename), "", 2 );
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
