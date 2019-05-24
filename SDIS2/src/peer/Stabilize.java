package peer;

import chord.ChordInfo;
import chord.ConnectionInfo;
import messages.GetPredecessorMessage;
import messages.Message;
import messages.MessageForwarder;

import java.net.InetAddress;

public class Stabilize implements Runnable {
    @Override
    public void run() {
    	//ChordInfo.printFingerTable();
            try {
                if(ChordInfo.getFingerTable().get(0).getPort() == Peer.port  && ChordInfo.predecessor != null){
                    ChordInfo.getFingerTable().set(0, ChordInfo.predecessor);
                } else if(ChordInfo.getFingerTable().get(0).getPort() != Peer.port ){
                    MessageForwarder.sendMessage(new GetPredecessorMessage(new ConnectionInfo(null, InetAddress.getLocalHost().getHostAddress(),Peer.port)),ChordInfo.getFingerTable().get(0).getIp() , ChordInfo.getFingerTable().get(0).getPort());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
}
