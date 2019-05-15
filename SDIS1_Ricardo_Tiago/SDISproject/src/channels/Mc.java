package channels;

import Utilities.Auxiliary;
import Utilities.Key;
import Utilities.Value;
import requests.DeleteRequest;
import requests.StoreRequest;
import mains.*;

import java.io.File;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;

public class Mc extends Channel{
    public static InetAddress address;
    public static int port;
    public static MulticastSocket socket;
    private static ArrayList<Key> chunksReceived = new ArrayList<>();
    private static ArrayList<Key> putChunksReceived = new ArrayList<>();

    public Mc (String addr, int port){
        Mc.address = getAddress(addr);
        Mc.port = port;
        Mc.socket = getMCSocket(address, port);
    }

    public static void addChunk(Key chunkNo) {chunksReceived.add(chunkNo);}

    public static void addPutChunk(Key chunkNo) {putChunksReceived.add(chunkNo);}

    private static byte[] retrieveChunk(String fileId, String chunkNo)
    {
        int bytesRead;
        byte[] buf = new byte[65000], trimmedBuf = new byte[65000];
        FileInputStream inputStream;

        int rd = 1;

        while (rd < 10) {

            File file = new File("peer" + Peer.senderId + "/backup/" + fileId + "/chk" + chunkNo);

            if (file.isFile())
                break;

            rd++;
        }

        if(rd == 10)
            return null;

        try {
            inputStream = new FileInputStream("peer" + Peer.senderId + "/backup/" + fileId + "/chk" + chunkNo);

            while ((bytesRead = inputStream.read(buf)) > 0)
                trimmedBuf = Arrays.copyOf(buf, bytesRead);

            inputStream.close();
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }

        return trimmedBuf;
    }

    @Override
    public void run() {
        Random rand;
        int interval;
        while(true) {
            byte[] msg = getPacketMessage(socket);
            if(msg != null) {
                String message = new String(msg).replaceAll("\0", "");

                if (message != null) {
                    String[] tokens = message.split(" ");
                    if (Integer.parseInt(tokens[2]) != Peer.senderId) {
                        Key key = null;
                        if(!tokens[0].equals("DELETE") && !tokens[0].equals("JOIN"))
                            key = new Key(tokens[3], Integer.parseInt(tokens[4]));
                        switch (tokens[0]) {
                            case "REMOVED":
                                if (Peer.stores.containsKey(key)) {
                                    Value value = Peer.stores.get(key);
                                    value.decrement();
                                    Integer rd = Peer.rds.get(tokens[3]);
                                    if (value.stores < rd) {
                                        byte[] body;
                                        if ((body = retrieveChunk(tokens[3], tokens[4])) != null) {
                                            rand = new Random();
                                            interval = rand.nextInt(401);

                                            putChunksReceived.clear();

                                            try {
                                                Thread.sleep(interval);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                                System.exit(-1);
                                            }

                                            if (!putChunksReceived.contains(key)) {
                                                String header;
                                                int messageSize;
                                                byte[] headerBytes, putChunkMessage;
                                                String[] params = new String[]{tokens[3], tokens[4], Integer.toString(rd)};
                                                System.out.println("sending PUTCHUNK for " + tokens[3] + " #" + tokens[4]);
                                                header = Auxiliary.addHeader("PUTCHUNK", params, false);
                                                headerBytes = header.getBytes();
                                                messageSize = headerBytes.length + body.length;

                                                putChunkMessage = new byte[messageSize];
                                                System.arraycopy(headerBytes, 0, putChunkMessage, 0, headerBytes.length);
                                                System.arraycopy(body, 0, putChunkMessage, headerBytes.length, body.length);

                                                Channel.sendPacketBytes(putChunkMessage, Mdb.address, Mdb.port);
                                            }
                                        }
                                    }
                                }
                                break;
                            case "STORED":
                                StoreRequest req = Peer.requests.get(tokens[3]);
                                if (req != null)
                                    req.store(Integer.parseInt(tokens[4]));
                                if (Peer.stores.containsKey(key))
                                    Peer.stores.get(key).increment();
                                else {
                                    Value value = new Value(1);
                                    Peer.stores.put(key, value);
                                }
                                break;
                            case "DELETE":
                                File directory = new File("peer" + Peer.senderId + "/backup/" + tokens[3]);
                                Auxiliary.clearDirectory(directory);
                                for (Map.Entry<Key, Value> entry : Peer.stores.entrySet()) {
                                    Key k = entry.getKey();

                                    if (k.file.equals(tokens[3]))
                                        entry.getValue().stores = 0;
                                }
                                break;
                            case "GETCHUNK":
                                byte[] body;

                                if ((body = retrieveChunk(tokens[3], tokens[4])) != null) {
                                    rand = new Random();
                                    interval = rand.nextInt(401);

                                    chunksReceived.clear();

                                    try {
                                        Thread.sleep(interval);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                        System.exit(-1);
                                    }

                                    if (!chunksReceived.contains(key)) {
                                        String header;
                                        int messageSize;
                                        byte[] headerBytes, getChunkMessage;
                                        String[] params = new String[]{tokens[3], tokens[4]};
                                        System.out.println("sending CHUNK for " + tokens[3] + " #" + tokens[4]);
                                        header = Auxiliary.addHeader("CHUNK", params, false);
                                        headerBytes = header.getBytes();
                                        messageSize = headerBytes.length + body.length;

                                        getChunkMessage = new byte[messageSize];
                                        System.arraycopy(headerBytes, 0, getChunkMessage, 0, headerBytes.length);
                                        System.arraycopy(body, 0, getChunkMessage, headerBytes.length, body.length);

                                        Channel.sendPacketBytes(getChunkMessage, Mdr.address, Mdr.port);
                                    }

                                    chunksReceived.clear();
                                }
                                break;
                            case "JOIN":
                                if(Peer.version.equals("1.1")) {
                                    for (String file : Peer.deletes) {
                                        System.out.println("Sent delete after join");
                                        DeleteRequest del = new DeleteRequest(Peer.executor, file, false, true);
                                        Peer.executor.submit(del);
                                    }
                                }
                                break;
                        }
                    }
                }
            }
        }
    }
}

