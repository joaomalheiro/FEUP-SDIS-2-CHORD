package peer;

import chord.ChordManager;
import chord.ConnectionInfo;
import messages.GetPredecessorMessage;
import messages.MessageForwarder;

import java.net.InetAddress;

public class Stabilize implements Runnable {
    @Override
    public void run() {
    	//ChordManager.printFingerTable();
            try {
                if(ChordManager.getFingerTable().get(0).getPort() == Peer.port && ChordManager.predecessor != null){
                    ChordManager.getFingerTable().set(0, ChordManager.predecessor);
                } else if(ChordManager.getFingerTable().get(0).getPort() != Peer.port ){
                    MessageForwarder.sendMessage(new GetPredecessorMessage(new ConnectionInfo(null, InetAddress.getLocalHost().getHostAddress(),Peer.port), ChordManager.getFingerTable().get(0).getIp() , ChordManager.getFingerTable().get(0).getPort()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
}
