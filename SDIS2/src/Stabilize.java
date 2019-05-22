import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Stabilize implements Runnable {
    private ChordInfo ci;

    Stabilize(ChordInfo ci) {
        this.ci = ci;
    }

    @Override
    public void run() {
            try {
                if(ChordInfo.getFingerTable().get(0).getPort() == Peer.port  && ChordInfo.predecessor != null){
                    ChordInfo.getFingerTable().set(0, ChordInfo.predecessor);
                } else if(ChordInfo.getFingerTable().get(0).getPort() != Peer.port ){
                    Auxiliary.sendMessage("GET_PREDECESSOR " + InetAddress.getLocalHost().getHostAddress() + " " + Peer.port, ci.getFingerTable().get(0).getIp(), ci.getFingerTable().get(0).getPort());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
}
