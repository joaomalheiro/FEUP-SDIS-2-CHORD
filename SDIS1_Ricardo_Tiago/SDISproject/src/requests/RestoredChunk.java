package requests;

import channels.Channel;
import channels.Mc;
import Utilities.Auxiliary;


public class RestoredChunk implements Runnable{

    private String fileId;
    private int chunkNo;
    private byte[] body = null;


    RestoredChunk(int chunkNo, String fileId)
    {
        this.fileId = fileId;
        this.chunkNo = chunkNo;
    }

    public void addBody(byte[] body) {this.body = body;}

    public byte[] getBody() {return body;}

    @Override
    public void run() {
        String[] params = new String[]{this.fileId, String.valueOf(this.chunkNo)};
        String message = Auxiliary.addHeader("GETCHUNK", params, false);
        Channel.sendPacketBytes(message.getBytes(), Mc.address, Mc.port);
    }
}
