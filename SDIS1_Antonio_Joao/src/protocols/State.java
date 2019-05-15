package protocols;

import messages.Message;
import peer.Peer;

import java.io.File;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

public class State {


    public String run() {

        String state = "";
        state = "Space Reserved: " + Peer.getStorage().getSpaceReserved() + "\n" + "Space occupied by chunks: " + Peer.getStorage().getSpaceOcupied() + "\n";
        File peerFolder = new File("./peerDisk/peer" + Peer.getPeerId() + "/backup");

        for(int i = 0; i < peerFolder.listFiles().length; i++) {
            state = state + "File number: " + i + "\n";
            state = state + "Absolute Filepath : " + peerFolder.listFiles()[i].getAbsolutePath() + "\n";

            for(int j = 0; j < peerFolder.listFiles()[i].listFiles().length; j++) {
                state = state + "   ChunkID: " + peerFolder.listFiles()[i].listFiles()[j].getName() + "\n";
                state = state + "   Chunk size: " + peerFolder.listFiles()[i].listFiles()[j].length() + "\n";
                state = state + "   Perceived Rep Degree: " + Peer.getMC().getRepDegreeStorage().getRepDegree("fileId" + peerFolder.listFiles()[i].getName() + "chkn" + peerFolder.listFiles()[i].listFiles()[j].getName().replace("chk","")) + "\n";
            }
        }

        state += "Chunks that Peer " + Peer.getPeerId() + " has initiated backup protocol: ";
        Iterator it = Peer.getFilesInitiated().iterator();
        while(it.hasNext()) {
            File file = (File) it.next();
            state += "File pathname: " + file.getAbsolutePath() + " \n";
            state += "FileId with hash: " + Message.encrypt(file.getName() + file.lastModified()) + " \n";
            state += "Desired repDegree: " + Peer.getMC().getRepDegreeStorage().getDesiredRepDegree(Message.encrypt(file.getName() + file.lastModified())) + " \n";
            int chkn = 0;
            while(Peer.getMC().getRepDegreeStorage().existsRepDegree("fileId" + Message.encrypt(file.getName() + file.lastModified()) + "chkn" + chkn)){
                state += "    ChunkId: " + chkn + " Perceived repDegree: " + Peer.getMC().getRepDegreeStorage().getRepDegree("fileId" + Message.encrypt(file.getName() + file.lastModified()) + "chkn" + chkn) + "\n";
                chkn++;
            }

        }

        return state;
    }
}
