package channels;

import Utilities.Auxiliary;
import Utilities.Key;
import mains.Peer;
import Utilities.Value;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Arrays;
import java.util.Random;

public class Mdb extends Channel{
    public static InetAddress address;
    public static int port;
    public static MulticastSocket socket;

    public Mdb (String addr, int port){
        Mdb.address = getAddress(addr);
        Mdb.port = port;
        Mdb.socket = getMCSocket(address, port);
    }

    private static void createChunk(byte[] msg, String fileId, String chunkNo)
    {
        FileOutputStream out = null;

        File directoryPeer = new File("peer" + Peer.senderId);
        if (!directoryPeer.exists())
            if(!directoryPeer.mkdir())
                return;

        File directoryBackup = new File("peer" + Peer.senderId + "/backup");
        if (!directoryBackup.exists())
            if(!directoryBackup.mkdir())
                return;

        File directoryFile = new File("peer" + Peer.senderId + "/backup/" + fileId);
        if (!directoryFile.exists())
            if(!directoryFile.mkdir())
                return;

        try {
            out = new FileOutputStream("peer" + Peer.senderId + "/backup/" + fileId + "/chk" + chunkNo);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        try {
            out.write(msg);

            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private static byte[] getContent(byte[] msg){
        int count = 0;
        int i;

        for(i = 0; i < msg.length - 1; i++) {
            if(count >= 2)
                break;
            else if(msg[i] == 10)
                count++;
        }

        return Arrays.copyOfRange(msg, i, msg.length-1);
    }

    @Override
    public void run() {
        while(true) {
            byte[] msg = getPacketMessage(socket);
            if(msg != null) {
                String message = new String(msg).replaceAll("\0", "");

                if (message != null) {
                    String[] tokens = message.split(" ");

                    if (tokens[0].equals("PUTCHUNK") ) {
                        if (!Peer.rds.containsKey(tokens[3]))
                            Peer.rds.put(tokens[3], Integer.parseInt(tokens[5]));

                        if (Integer.parseInt(tokens[2]) != Peer.senderId && !Peer.sent.containsKey(tokens[3])) {
                            byte[] content = getContent(msg);
                            Key key = new Key(tokens[3], Integer.parseInt(tokens[4]));
                            Mc.addPutChunk(key);

                            File chunk = new File("peer" + Peer.senderId + "/backup/" + tokens[3] + "/chk" + tokens[4]);

                            if (!chunk.exists() && Peer.getUsedSpace() + content.length <= Peer.allowedSpace) {
                                boolean enhanced = tokens[1].equals("1.1");

                                String[] params = new String[]{tokens[3], tokens[4]};
                                message = Auxiliary.addHeader("STORED", params, enhanced);

                                Random rand = new Random();
                                int interval = rand.nextInt(401);
                                try {
                                    Thread.sleep(interval);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                    System.exit(-1);
                                }

                                if(enhanced) {
                                    if(!Peer.version.equals("1.1")){
                                        System.out.println("Received enhanced PUTCHUNK, but peer version can't handle");
                                        System.exit(-1);
                                    }
                                    if(Peer.stores.containsKey(key))
                                        if (Peer.stores.get(key).stores >= Integer.parseInt(tokens[5]))
                                            continue;
                                }

                                System.out.println("sending STORE for " + tokens[3] + " #" + tokens[4]);

                                createChunk(content, tokens[3], tokens[4]);

                                if(Peer.stores.containsKey(key))
                                    Peer.stores.get(key).increment();
                                else {
                                    Value value = new Value(1);
                                    Peer.stores.put(key, value);
                                }

                                sendPacket(message, Mc.address, Mc.port);
                            }
                        }
                    }
                }
            }
        }
    }
}
