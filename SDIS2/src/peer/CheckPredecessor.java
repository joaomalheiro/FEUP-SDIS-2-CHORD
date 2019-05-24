package peer;

import chord.ChordInfo;
import chord.ConnectionInfo;
import messages.Message;
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
            if (ChordInfo.predecessor != null) {
                try {
                    MessageForwarder.sendMessage(new PingMessage(new ConnectionInfo(null, InetAddress.getLocalHost().getHostAddress(), Peer.port)),ChordInfo.predecessor.getIp(), ChordInfo.predecessor.getPort());
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
