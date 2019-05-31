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

public class Reclaim implements Runnable{
    private long reservedSpace;

    public Reclaim(long reservedSpace) {
        this.reservedSpace = reservedSpace;
    }

    @Override
    public void run() {
        try {
            Peer.storage.setSpaceReserved(this.reservedSpace);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
