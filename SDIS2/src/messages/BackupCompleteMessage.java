package messages;

import java.math.BigInteger;

public class BackupCompleteMessage extends Message {
    private int repDegree;
    private BigInteger hashFile;

    public BackupCompleteMessage(BigInteger hashFile, int repDegree) {
        this.hashFile = hashFile;
        this.repDegree = repDegree;
    }

    @Override
    public String toString() {
        return "BACKUP-COMPLETE" + this.hashFile + " " + this.repDegree;
    }

    public void handleMessage() {
    }
}
