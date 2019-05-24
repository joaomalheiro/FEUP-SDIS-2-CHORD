package messages;

import peer.CheckPredecessor;
import peer.Peer;

public class PongMessage extends Message {
    @Override
    public void handleMessage() {
        synchronized(Peer.checkPredecessor){
            CheckPredecessor.dead = false;
            Peer.checkPredecessor.notify();
        }
    }
}
