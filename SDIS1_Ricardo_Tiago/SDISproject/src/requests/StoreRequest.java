package requests;

import Utilities.Auxiliary;
import Utilities.Key;
import Utilities.Value;
import mains.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ScheduledExecutorService;

public class StoreRequest implements Runnable {
    private ScheduledExecutorService executor;
    private String file_path;
    private File file;
    private String fileId;
    private int rd;
    private ArrayList<Chunk> chunks = new ArrayList<>();
    private boolean enhanced;

    public StoreRequest(ScheduledExecutorService executor, String fp, int rd, boolean enhanced) {
        this.executor = executor;
        this.rd = rd;
        this.file_path = fp;
        this.file = new File(fp);
        this.enhanced = enhanced;

        this.fileId = Auxiliary.encodeFileId(file);
        Peer.requests.put(this.fileId, this);
        while(Peer.deletes.contains(fp))
            Peer.deletes.remove(fp);
    }

    public void store(int chunkNo) {
        chunks.get(chunkNo).store();
    }

    private void splitIntoChunks() {
        int maxChunkSize = 64000, chunkNo = 0, bytesRead;
        byte[] buf = new byte[maxChunkSize];

        FileInputStream inputStream = null;

        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        try {
            while ((bytesRead = inputStream.read(buf)) > 0) {
                byte[] trimmedBuf  = Arrays.copyOf(buf, bytesRead);

                Integer stores = 0;
                Value value = Peer.stores.get(new Key(fileId, chunkNo));
                if(value != null)
                    stores = value.stores;
                Chunk chunk = new Chunk(chunkNo, this.fileId, trimmedBuf, rd, executor, stores, enhanced);
                this.chunks.add(chunk);

                chunkNo++;
            }

            if(file.length() % maxChunkSize == 0)
            {
                Integer stores = 0;
                Value value = Peer.stores.get(new Key(fileId, chunkNo));
                if(value != null)
                    stores = value.stores;
                Chunk chunk = new Chunk(chunkNo, this.fileId, null, rd, executor, stores, enhanced);
                this.chunks.add(chunk);
            }
        } catch(IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public String getFile_path() {return this.file_path;}

    public int getRd() {return this.rd;}

    public ArrayList<Chunk> getChunks() {return this.chunks;}

    @Override
    public void run() {
        splitIntoChunks();
        Peer.sent.put(fileId, this.chunks.size());

        for(Chunk c : chunks)
            executor.submit(c);
    }
}
