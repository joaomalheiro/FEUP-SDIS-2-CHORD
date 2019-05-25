package peer;

import chord.ChordInfo;
import chord.ConnectionInfo;
import messages.LookupMessage;
import messages.Message;
import messages.MessageForwarder;
import messages.SucessorMessage;

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

        Message res = null;
        try {
            res = ChordInfo.searchSuccessor2(new ConnectionInfo(new BigInteger(key), InetAddress.getLocalHost().getHostAddress(), Peer.port));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        System.out.println("Key = " + key);
        System.out.println(res);

        if(res != null){
            if(res instanceof SucessorMessage)
                fingerTable.set(index, ((SucessorMessage) res).getCi());
            else if (res instanceof LookupMessage)
            {
                //parameters = new String[]{key, InetAddress.getLocalHost().getHostAddress(), String.valueOf(Peer.port)};
                //msg = MessageForwarder.addHeader("LOOKUP", parameters);
                //MessageForwarder.sendMessage(msg, tokens[1], Integer.parseInt(tokens[2]));
                MessageForwarder.sendMessage(res);
            }
        }

        ChordInfo.printFingerTable();

        Peer.executor.schedule(this, 1, TimeUnit.SECONDS);
    }
}
