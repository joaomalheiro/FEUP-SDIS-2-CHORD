package requests;

import Utilities.Key;
import mains.Peer;

import java.io.File;
import java.util.Map;

public class StateRequest implements Runnable{

    private String stateMessage = "";

    public StateRequest(){}

    public String getStateMessage()
    {
        while(stateMessage.equals(""))
        {}
        return this.stateMessage;
    }

    @Override
    public void run() {
        StringBuilder builder = new StringBuilder();
        builder.append("Backups initiated\n\n");
        int i = 1;

        for(Map.Entry<String, StoreRequest> entry : Peer.requests.entrySet()) {
            String fileId = entry.getKey();
            StoreRequest request = entry.getValue();
            builder.append("Backup #");
            builder.append(i);
            builder.append("\n\n");
            builder.append("File path: ");
            builder.append(request.getFile_path());
            builder.append("\n");
            builder.append("File id: ");
            builder.append(fileId);
            builder.append("\n");
            builder.append("Desired replication degree: ");
            builder.append(request.getRd());
            builder.append("\n\n");

            for(Chunk c: request.getChunks())
            {
                builder.append("Chunk id: chk_");
                builder.append(c.getChunkNo());
                builder.append("\n");
                builder.append("Perceived replication degree: ");
                builder.append(Peer.stores.get(new Key(fileId, c.getChunkNo())));
                builder.append("\n\n");
            }

            builder.append("\n");
            i++;
        }

        builder.append("Stored Files\n\n");

        File directory = new File("peer" + Peer.senderId + "/backup");
        File[] directoryListing = directory.listFiles();
        if (directoryListing != null) {
            for (File fileDirectory : directoryListing) {
                File[] chunks = fileDirectory.listFiles();
                builder.append("Fileid: ");
                builder.append(fileDirectory.getName());
                builder.append("\n\n");
                if(chunks != null)
                    for (File chunk : chunks) {
                        builder.append("Chunk id: ");
                        builder.append(chunk.getName());
                        builder.append("\n");
                        builder.append("Chunk size: ");
                        builder.append(chunk.length());
                        builder.append(" bytes\n");
                        builder.append("Perceived replication degree: ");
                        builder.append(Peer.stores.get(new Key(fileDirectory.getName(), Integer.parseInt(chunk.getName().substring(3)))));
                        builder.append("\n\n");
                    }
            }
        }

        stateMessage = builder.toString();
    }
}
