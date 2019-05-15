package requests;

import channels.Channel;
import channels.Mc;
import Utilities.Auxiliary;
import Utilities.Key;
import mains.Peer;
import Utilities.Value;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DeleteRequest implements Runnable {
    private ScheduledExecutorService executor;
    private String fileId;
    private boolean first = true;
    private boolean enhanced;

    public DeleteRequest(ScheduledExecutorService executor, String fp, boolean original, boolean enhanced) {
        this.executor = executor;
        this.enhanced = enhanced;

        this.fileId = Auxiliary.encodeFileId(new File(fp));
        if(original)
            Peer.deletes.add(fp);
    }

    @Override
    public void run() {
        if(first)
        {
            for (Map.Entry<Key, Value> entry : Peer.stores.entrySet()) {
                Key k = entry.getKey();

                if (k.file.equals(fileId))
                    entry.getValue().stores = 0;
            }
            executor.schedule(this, 1, TimeUnit.SECONDS);
        }

        first = false;
        String[] params = new String[]{this.fileId};
        String message = Auxiliary.addHeader("DELETE", params, enhanced);
        Channel.sendPacketBytes(message.getBytes(), Mc.address, Mc.port);
    }
}
