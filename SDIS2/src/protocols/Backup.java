package protocols;

import chord.ChordInfo;
import chord.ConnectionInfo;
import files.FileHandler;
import messages.MessageForwarder;
import peer.Peer;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
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
        try {
            byte[] content = FileHandler.readFromFile("./testFiles/" + filename);

            String crlf = " " + (char) 0xD + (char) 0xA + (char) 0xD + (char) 0xA;

            BigInteger fileHash = FileHandler.encrypt(filename);


            FileHandler.writeFile("./peerDisk/peer" + Peer.getPeerAccessPoint() + "/backup/" + FileHandler.encrypt(filename), content);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
