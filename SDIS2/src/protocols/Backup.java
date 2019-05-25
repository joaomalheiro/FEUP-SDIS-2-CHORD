package protocols;

import chord.ChordInfo;
import chord.ConnectionInfo;
import files.FileHandler;
import messages.BackupMessage;
import messages.MessageForwarder;
import peer.Peer;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
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

    }
}
