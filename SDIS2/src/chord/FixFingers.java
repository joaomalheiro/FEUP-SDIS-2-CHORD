package chord;

import chord.ChordManager;
import chord.ConnectionInfo;
import messages.LookupMessage;
import messages.Message;
import messages.MessageForwarder;
import messages.SucessorMessage;
import peer.Peer;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class FixFingers implements Runnable {

    private int index = -1;

    @Override
    public void run() {
        index++;

        if(index == ChordManager.getM() ) {
            index = 0;
        }

        String key = ChordManager.calculateNextKey(ChordManager.peerHash, index, ChordManager.getM() );
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

        if(res != null){
            if(res instanceof SucessorMessage) {
                fingerTable.set(index, ((SucessorMessage) res).getCi());
            }
            else if (res instanceof LookupMessage)
            {
                MessageForwarder.sendMessage(res);
            }
        }
    }
}
