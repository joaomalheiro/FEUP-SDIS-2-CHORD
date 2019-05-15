package requests;

import Utilities.Auxiliary;
import mains.Peer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ScheduledExecutorService;


public class RestoreRequest implements Runnable {
    private String file_path;
    private String fileId;
    private ScheduledExecutorService executor;
    private int chunksReceived = 0;
    private ArrayList<RestoredChunk> chunks = new ArrayList<>();
    private int numberChunks;

   public RestoreRequest (ScheduledExecutorService executor, String fp) {
        this.executor = executor;
        this.file_path = fp;

        this.fileId = Auxiliary.encodeFileId(new File(fp));
        Peer.restoreRequests.put(this.fileId, this);
    }

    public synchronized void receiveChunk(int chunkNo, byte[] body)
    {
      if(this.chunks.get(chunkNo).getBody() == null) {
          this.chunks.get(chunkNo).addBody(body);
          this.chunksReceived++;

          if(this.chunksReceived == this.numberChunks)
              notifyAll();
            }
    }

    private void createFile()
    {
        FileOutputStream out = null;

        File directoryPeer = new File("peer" + Peer.senderId);
        if (!directoryPeer.exists())
            if(!directoryPeer.mkdir())
                return;

        File directoryRestore = new File("peer" + Peer.senderId + "/restored");
        if (!directoryRestore.exists())
            if(!directoryRestore.mkdir())
                return;

        byte[] fpBytes = this.file_path.getBytes();
        StringBuilder result = new StringBuilder();

        for(byte b : fpBytes)
        {
            char c = (char) b;

            if(c == '/') {
                if(result.length() != 0)
                    result.delete(0, result.length());
            }

            else
                result.append(c);
        }

        String fileName = result.toString();

        try {
            out = new FileOutputStream("peer" + Peer.senderId + "/restored/" + fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        try {

            for(RestoredChunk chunk : this.chunks) {

                byte[] body = chunk.getBody();

                for (byte b : body)
                    out.write((char) b);
            }

            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    @Override
    public synchronized void run() {

        numberChunks = Peer.sent.get(this.fileId);

        for(int i = 0; i < this.numberChunks; i++)
        {
            RestoredChunk rc = new RestoredChunk(i, this.fileId);
            this.chunks.add(rc);
            executor.submit(rc);
        }

        try {
            wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        createFile();
      }
}
