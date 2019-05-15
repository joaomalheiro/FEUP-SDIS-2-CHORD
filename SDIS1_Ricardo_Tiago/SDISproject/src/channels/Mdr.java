package channels;

import Utilities.Key;
import mains.Peer;
import requests.RestoreRequest;

import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Arrays;

public class Mdr extends Channel{
    public static InetAddress address;
    public static int port;
    public static MulticastSocket socket;

    public Mdr (String addr, int port){
        Mdr.address = getAddress(addr);
        Mdr.port = port;
        Mdr.socket = getMCSocket(address, port);
    }

    private static byte[] getBody (byte[] msg)
    {
        byte[] body = new byte[64000];
        int count = 0, index = 0;

        for(int i = 0; i < msg.length - 1; i++) {

            if(count >= 2)
            {
                body[index] = msg[i];
                index++;
            }

            else if(msg[i] == 10)
                count++;
        }

        return Arrays.copyOf(body, index);
    }

    @Override
    public void run() {
        while(true) {
            byte[] msg = getPacketMessage(socket);
            if(msg != null) {
                String message = new String(msg).replaceAll("\0", "");

                if (message != null) {
                    String[] tokens = message.split(" ");
                    if (Integer.parseInt(tokens[2]) != Peer.senderId && tokens[0].equals("CHUNK")) {
                            Mc.addChunk(new Key(tokens[3], Integer.parseInt(tokens[4])));

                            byte[] body;

                            RestoreRequest req = Peer.restoreRequests.get(tokens[3]);
                            body = getBody(msg);
                            req.receiveChunk(Integer.parseInt(tokens[4]), body);
                    }
                }
            }
        }
    }
}