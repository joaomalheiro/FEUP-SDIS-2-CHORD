package protocols;

import peer.Peer;


public class Reclaim implements Runnable {

    private int spaceReserved;

    public Reclaim(int spaceReserved) {
        this.spaceReserved = spaceReserved;

    }
    @Override
    public void run() {
        Peer.getStorage().setSpaceReserved(this.spaceReserved);
    }

}