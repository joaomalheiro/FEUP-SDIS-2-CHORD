package peer;

import chord.ChordInfo;
import messages.MessageForwarder;

import java.net.InetAddress;

public class CheckPredecessor implements Runnable{
    public static boolean dead;
    private int timeout;

    CheckPredecessor(int timeout){
        this.timeout = timeout;
    }

    @Override
    public void run(){
        synchronized(this){
            if (ChordInfo.predecessor != null) {
                try {
                    MessageForwarder.sendMessage("PING " + InetAddress.getLocalHost().getHostAddress() + " " + Peer.port, ChordInfo.predecessor.getIp(), ChordInfo.predecessor.getPort());
                    this.wait(timeout);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (dead) {
                    ChordInfo.predecessor = null;
                }
            }
        }
    }
}
