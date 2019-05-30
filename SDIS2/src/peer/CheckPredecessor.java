package peer;

import chord.ChordManager;
import chord.ConnectionInfo;
import messages.MessageForwarder;
import messages.PingMessage;

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
            if (ChordManager.predecessor != null) {
                try {
                    MessageForwarder.sendMessage(new PingMessage(new ConnectionInfo(null, InetAddress.getLocalHost().getHostAddress(), Peer.port), ChordManager.predecessor.getIp(), ChordManager.predecessor.getPort()));
                    this.wait(timeout);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (dead) {
                    ChordManager.predecessor = null;
                }
            }
        }
    }
}
