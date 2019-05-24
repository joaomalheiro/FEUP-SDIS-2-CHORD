package peer;

import chord.ChordInfo;
import chord.ConnectionInfo;
import messages.MessageForwarder;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class FixFingers implements Runnable {

    private int index = -1;

    @Override
    public void run() {
        int mBits = ChordInfo.getM() * 8;

        index++;

        if(index == mBits) {
            index = 0;
            ChordInfo.printFingerTable();
        }

        String key = ChordInfo.calculateNextKey(ChordInfo.peerHash, index, mBits);
        ArrayList<ConnectionInfo> fingerTable = ChordInfo.getFingerTable();

        if(index > (fingerTable.size() - 1)) {
            try {
                fingerTable.add(new ConnectionInfo(ChordInfo.peerHash, InetAddress.getLocalHost().getHostAddress(), Peer.port));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }

        String res = null;
        try {
            res = ChordInfo.searchSuccessor2(new ConnectionInfo(new BigInteger(key), InetAddress.getLocalHost().getHostAddress(), Peer.port));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        String[] tokens = null;

        if(res != null)
            tokens = res.split(" ");

        System.out.println("Key = " + key);
        System.out.println(res);

        if(tokens != null){
            if(tokens[0].equals("SUCCESSOR"))
                fingerTable.set(index, new ConnectionInfo(new BigInteger(tokens[1]), tokens[2], Integer.parseInt(tokens[3])));
            else if (tokens[0].equals("LOOKUP"))
            {
                try {
                    //parameters = new String[]{key, InetAddress.getLocalHost().getHostAddress(), String.valueOf(Peer.port)};
                    //msg = MessageForwarder.addHeader("LOOKUP", parameters);
                    //MessageForwarder.sendMessage(msg, tokens[1], Integer.parseInt(tokens[2]));
                    MessageForwarder.sendMessage(new LookupMessage(new ConnectionInfo(new BigInteger(key), InetAddress.getLocalHost().getHostAddress(), Peer.port)),tokens[1], Integer.parseInt(tokens[2]));
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        }

        ChordInfo.printFingerTable();

        Peer.executor.schedule(this, 1, TimeUnit.SECONDS);
    }
}
