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

        for(BigInteger i: ci.getFingerTable().keySet()){
            try {
                Auxiliary.sendMessage("GET_PREDECESSOR " + InetAddress.getLocalHost().getHostAddress() + Peer.port, ci.getFingerTable().get(i).getIp(), ci.getFingerTable().get(i).getPort());
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
    }
}
