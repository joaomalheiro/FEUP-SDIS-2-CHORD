package peer;

import files.FileHandler;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Hashtable;

public class Storage {
    private long spaceReserved;
    private long spaceOcupied;
    private Hashtable<BigInteger, String> fileStored;

    public Storage(long spaceReserved) throws IOException {
        this.spaceReserved = spaceReserved;
        updateSpaceOcupied();
        /*if (this.spaceReserved < this.spaceOcupied) {
            FileHandler.clearStorageSpace();
        }*/

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
        Path pathPeerFolder = Paths.get("./peerDisk/peer" + Peer.getPeerAccessPoint());
        this.spaceOcupied = FileHandler.getSize(pathPeerFolder) / 1024;
        System.out.println("SPACE OCUPPIED: " + this.spaceOcupied);
    }

    public boolean allowChunk(byte[] chunkData) throws IOException {
        updateSpaceOcupied();
        return spaceOcupied + (chunkData.length / 1024) < spaceReserved;
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