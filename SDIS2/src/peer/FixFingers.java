package peer;

import chord.ChordManager;
import chord.ConnectionInfo;
import messages.LookupMessage;
import messages.Message;
import messages.MessageForwarder;
import messages.SucessorMessage;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class FixFingers implements Runnable {

    private int index = -1;

    @Override
    public void run() {
        int mBits = ChordManager.getM() * 8;

        index++;

        if(index == mBits) {
            index = 0;
            ChordManager.printFingerTable();
        }

        String key = ChordManager.calculateNextKey(ChordManager.peerHash, index, mBits);
        ArrayList<ConnectionInfo> fingerTable = ChordManager.getFingerTable();

        if(index > (fingerTable.size() - 1)) {
            try {
                fingerTable.add(new ConnectionInfo(ChordManager.peerHash, InetAddress.getLocalHost().getHostAddress(), Peer.port));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }

        Message res = null;
        try {
            res = ChordManager.searchSuccessor2(new ConnectionInfo(new BigInteger(key), InetAddress.getLocalHost().getHostAddress(), Peer.port));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        System.out.println("Key = " + key);
        //System.out.println(res);

        if(res != null){
            System.out.println(res);
            if(res instanceof SucessorMessage) {
                fingerTable.set(index, ((SucessorMessage) res).getCi());
            }
            else if (res instanceof LookupMessage)
            {
                System.out.println("Entrou if");
                MessageForwarder.sendMessage(res);
            }
        }

        ChordManager.printFingerTable();
    }
}
