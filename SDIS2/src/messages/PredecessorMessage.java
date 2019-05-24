package messages;

import chord.ChordInfo;
import chord.ConnectionInfo;

public class PredecessorMessage extends Message {

    private ConnectionInfo ci;

    public PredecessorMessage(ConnectionInfo ci) {
        this.ci = ci;
    }

    @Override
    public void handleMessage() {
        ChordInfo.predecessor = ci;
    }
}