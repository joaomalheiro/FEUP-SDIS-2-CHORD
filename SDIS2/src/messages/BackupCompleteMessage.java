package messages;

public class BackupCompleteMessage extends Message {
    private int repDegree;
    private String hashFile;

    public BackupCompleteMessage(String hashFile, int repDegree) {
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
