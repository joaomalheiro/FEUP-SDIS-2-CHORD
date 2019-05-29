package peer;

import files.FileHandler;
import messages.Message;
import peer.Peer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Hashtable;

public class Storage {
    private long spaceReserved;
    private long spaceOcupied;
    private Hashtable<Integer, Integer> initBackupChunks;

    public Storage(long spaceReserved) throws IOException {
        this.spaceReserved = spaceReserved;
        updateSpaceOcupied();
        if (this.spaceReserved < this.spaceOcupied) {
            clearStorageSpace();
        }

        initBackupChunks = new Hashtable<>();
    }

    public void insertHashtable(Integer chunkId, Integer repDegree) {
        initBackupChunks.put(chunkId, repDegree);
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
    }

    public boolean allowChunk(byte[] chunkData) throws IOException {
        updateSpaceOcupied();
        return spaceOcupied + (chunkData.length / 1024) < spaceReserved;
    }

    public void setSpaceReserved(long spaceReserved) throws IOException {
        this.spaceReserved = spaceReserved;
        updateSpaceOcupied();
        if (this.spaceReserved < this.spaceOcupied) {
            clearStorageSpace();
        }
    }

    private void clearStorageSpace() {
        FileHandler.deleteFile("./peerDisk/peer" + Peer.getPeerAccessPoint());
    }


   /* private void handleDeleteFile(File file) {
        String absolutePath = file.getAbsolutePath();
        String fileId;
        int chunkNumber;

        String[] path;
        if (System.getProperty("os.name").equals("Linux")) {
            path = absolutePath.split("/");
        } else {
            path = absolutePath.split("\\\\");
        }

        if (path[path.length - 1].contains("chk")) {
            fileId = path[path.length - 2];
            chunkNumber = Integer.parseInt(path[path.length - 1].replace("chk", ""));
            //System.out.println(fileId + " " + chunkNumber);
            Message deleteMsg = new Message(Peer.getProtocolVersion(), Integer.parseInt(Peer.getPeerAccessPoint()), fileId, chunkNumber, 0, null);
            deleteMsg.createRemoved();

        }

    }*/

    public Hashtable<Integer, Integer> getHashtable() {
        return initBackupChunks;
    }
}