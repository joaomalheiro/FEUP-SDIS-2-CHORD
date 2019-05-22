package peer;

import chord.ChordInfo;
import messages.MessageForwarder;

import java.net.InetAddress;

public class CheckPredecessor implements Runnable{
    public static boolean dead;
    @Override
    public void run(){
        while(true) {
            if (ChordInfo.predecessor != null) {
                dead = true;
                try {
                    MessageForwarder.sendMessage("PING " + InetAddress.getLocalHost().getHostAddress() + " " + Peer.port, ChordInfo.predecessor.getIp(), ChordInfo.predecessor.getPort());
                    wait(250);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(dead){
                    ChordInfo.predecessor = null;
                }
            }
        }
    }
}
