package requests;

import channels.Channel;
import channels.Mdb;
import Utilities.Auxiliary;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Chunk implements Runnable{
    private ScheduledExecutorService executor;
    private int chunkNo;
    private String  fileId;
    private byte[] body;
    private int rd;
    private int stores;
    private int sends = 0;
    private boolean enhanced;

    Chunk(int chunkNo, String fileId, byte[] body, int rd, ScheduledExecutorService executor, int stores, boolean enhanced)
    {
        this.chunkNo = chunkNo;
        this.fileId = fileId;
        this.body = body;
        this.rd = rd;
        this.executor = executor;
        this.stores = stores;
        this.enhanced  = enhanced;
    }

    public void store() { stores++; }

    public int getChunkNo() {return this.chunkNo;}

    @Override
    public void run(){
        int messageSize;
        byte [] headerBytes, message;

        if (stores < rd) {
            String header;
            String [] params;

            System.out.println("send " + sends + " of #" + chunkNo + " with stores=" + stores);
            params = new String[]{String.valueOf(fileId), String.valueOf(chunkNo), String.valueOf(rd)};
            header = Auxiliary.addHeader("PUTCHUNK", params, enhanced);

            headerBytes = header.getBytes();
            messageSize = headerBytes.length + body.length;

            message = new byte[messageSize];
            System.arraycopy(headerBytes, 0, message, 0, headerBytes.length);
            System.arraycopy(body, 0, message, headerBytes.length, body.length);

            Channel.sendPacketBytes(message, Mdb.address, Mdb.port);
            sends++;
            if(sends == 5){
                System.out.println("too many sends of #" + chunkNo);
            } else {
                switch (sends) {
                    case 2:
                        executor.schedule(this, 2, TimeUnit.SECONDS);
                        break;
                    case 3:
                        executor.schedule(this, 4, TimeUnit.SECONDS);
                        break;
                    case 4:
                        executor.schedule(this, 8, TimeUnit.SECONDS);
                        break;
                    default:
                        executor.schedule(this, 1, TimeUnit.SECONDS);
                        break;
                }
            }
        } else {
            System.out.println("rd achieved #" + chunkNo);
        }
    }
}
