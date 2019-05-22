package peer;

import chord.ChordInfo;
import chord.ConnectionInfo;
import messages.MessageForwarder;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class FixFingers implements Runnable {

    private int index = -1;

    @Override
    public void run() {
        int mBits = ChordInfo.getM() * 8;

        index++;

        if(index == mBits)
            index = 0;

        String key = ChordInfo.calculateNextKey(ChordInfo.peerHash, index, mBits);
        ArrayList<ConnectionInfo> fingerTable = ChordInfo.getFingerTable();

        String res = ChordInfo.searchSuccessor2(new ConnectionInfo(new BigInteger(key), "localhost", Peer.port));
        String[] tokens = res.split(" ");

        if(tokens[0].equals("SUCCESSOR"))
            fingerTable.set(index, new ConnectionInfo(new BigInteger(key), tokens[2], Integer.parseInt(tokens[3])));
        else if (tokens[0].equals("LOOKUP"))
        {
            String msg = "";
            for(int i = 0; i < 4; i++)
                msg += tokens[i] + " ";

            MessageForwarder.sendMessage(msg, tokens[4], Integer.parseInt(tokens[5]));
        }

        Peer.executor.schedule(this, 1, TimeUnit.SECONDS);
    }
}
