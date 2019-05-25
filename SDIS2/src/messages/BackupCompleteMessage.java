package messages;

import java.math.BigInteger;

public class BackupCompleteMessage extends Message {
    private int repDegree;
    private BigInteger hashFile;
    private String ipAddress;
    private int port;

    public BackupCompleteMessage(BigInteger hashFile, int repDegree,String ipAddress,int port) {
        this.hashFile = hashFile;
        this.repDegree = repDegree;
        this.ipAddress = ipAddress;
        this.port = port;
    }

    @Override
    public String toString() {
        return "BACKUP-COMPLETE" + this.hashFile + " " + this.repDegree;
    }

    public void handleMessage() {
    }

    @Override
    public String getIpAddress() {
        return this.ipAddress;
    }

    @Override
    public int getPort() {
        return this.port;
    }
}
