package messages;

public class BackupMessage extends Message {

    private int repDegree;
    private String hashFile;
    private String type;
    private byte[] body;

    public BackupMessage(String type, String hashFile, int repDegree, byte[] body) {
        this.type = type;
        this.hashFile = hashFile;
        this.repDegree = repDegree;
        this.body = body;
    }

    @Override
    public String toString() {
        return "BACKUP " + this.hashFile + " " + this.repDegree;
    }

    public void handleMessage() {

    }
}
