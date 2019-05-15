package protocols;

import messages.Message;
import peer.Peer;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;

public class Restore implements Runnable {
    private String fileName;
    private File file;

    public Restore(String fileName){
        this.fileName = fileName;
    }

    @Override
    public void run() {

        file = new File("./testFiles/" + fileName);
        int nChunks = (int)file.length() / 64000 + 1;
        String fileId = Message.encrypt(file.getName() + file.lastModified());

        Peer.getMDR().insertFileId(fileId);

        for (int i = 0 ; i < nChunks; i++){

            Message msg = new Message(Peer.getProtocolVersion(), Integer.parseInt(Peer.getPeerId()), file.getName() + file.lastModified(), i, 0 , null);
            msg.createGetChunk();
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        byte[] data = mergeIntoFile(Peer.getMDR().getChunksFromFile(fileId));

        createFile(data);
    }

    private void createFile(byte[] data) {

        try {
            FileOutputStream streamToFile = new FileOutputStream("./peerDisk/peer" + Peer.getPeerId() + "/restored/" + file.getName());
            streamToFile.write(data);
            streamToFile.close();
        } catch (IOException e) {
            System.out.println("Restore Error : Could not write byte[] into restored file");
        }
    }

    private byte[] mergeIntoFile(HashMap<Integer,Chunk> chunks) {

        byte[] data = new byte[0];
        ByteArrayOutputStream outputMessageStream = new ByteArrayOutputStream();

        for (int i = 0; i < chunks.size(); i++){

            try {
                outputMessageStream.write(Arrays.copyOf(data, data.length));
                outputMessageStream.write(Arrays.copyOf(chunks.get(i).getData() ,chunks.get(i).getData().length));
                } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return outputMessageStream.toByteArray();
    }

}
