package peer;

import files.FileHandler;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Hashtable;

import chord.ChordManager;

public class Storage {
    private long spaceReserved;
    private long spaceOcupied;
    private Hashtable<BigInteger, String> fileStored;

    public Storage(long spaceReserved) throws IOException {
        this.spaceReserved = spaceReserved;
        updateSpaceOcupied();
        
        fileStored = new Hashtable<>();
    }

    public void insertHashtable(BigInteger hashFile, String fileName) {
        fileStored.put(hashFile, fileName);
    }

    public long getSpaceReserved() {
        return spaceReserved;
    }

    public long getSpaceOcupied() {
        return spaceOcupied;
    }

    public void updateSpaceOcupied() throws IOException {
        Path pathPeerFolder = Paths.get("./peerDisk/peer" + Peer.getPeerAccessPoint() + "-" + ChordManager.peerHash);
        this.spaceOcupied = FileHandler.getSize(pathPeerFolder) / 1024;
    }


    public void setSpaceReserved(long spaceReserved) throws IOException {
        this.spaceReserved = spaceReserved;
        updateSpaceOcupied();
        if (this.spaceReserved < this.spaceOcupied) {
            FileHandler.clearStorageSpace();
        }
    }

    public Hashtable<BigInteger, String > getHashtable() {
        return fileStored;
    }
}