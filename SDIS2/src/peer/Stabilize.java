package peer;

import chord.ChordInfo;
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
                    MessageForwarder.sendMessage("GET_PREDECESSOR " + InetAddress.getLocalHost().getHostAddress() + " " + Peer.port, ChordInfo.getFingerTable().get(0).getIp(), ChordInfo.getFingerTable().get(0).getPort());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
}
