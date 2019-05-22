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
                if(ci.getFingerTable().size() != 0)
                Auxiliary.sendMessage("GET_PREDECESSOR " + InetAddress.getLocalHost().getHostAddress() + " " + Peer.port, ci.getFingerTable().get(0).getIp(), ci.getFingerTable().get(0).getPort());
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
}
