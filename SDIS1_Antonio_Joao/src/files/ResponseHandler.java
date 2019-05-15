package files;

import java.util.concurrent.TimeUnit;

import messages.Message;
import peer.Peer;

public class ResponseHandler implements Runnable{

    private long wait_time = 1;
    private int tries = 0;
    private int repDegree;
    private Message msg;
    private String key;

    /**
     * Constructor for the class ResponseHandler that is a helper for the backup procotol repDegree rules
     * @param repDegree
     * @param key
     * @param msg
     */
    public ResponseHandler(int repDegree, String key,Message msg) {
        this.repDegree = repDegree;
        this.key = key;
        this.msg = msg;
    }

    /**
     *
     */
    @Override
    public void run() {
        int stored = Peer.getMC().getRepDegreeStorage().getRepDegree(key);
        do {
            try {
                TimeUnit.SECONDS.sleep(wait_time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            stored = Peer.getMC().getRepDegreeStorage().getRepDegree(key);
            if(stored >= repDegree){
                return;
            }
            wait_time *=2;

            msg.createPutChunk();
            tries++;

        }while(stored <repDegree || tries !=5);

    }



}