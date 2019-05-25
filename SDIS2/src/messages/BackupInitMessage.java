package messages;

import chord.ConnectionInfo;

import java.math.BigInteger;

public class BackupInitMessage extends Message {
    private ConnectionInfo ci;
    private String filename;
    private int repDegree;
    private BigInteger hashFile;

    public BackupInitMessage(ConnectionInfo ci,BigInteger hashFile, int repDegree, String filename) {
        this.hashFile = hashFile;
        this.repDegree = repDegree;
        this.ci = ci;
        this.filename = filename;
    }

    @Override
    public String toString() {
        return "BACKUP-INIT" + this.hashFile + " " + " " + this.filename + " " + this.repDegree;
    }

    public void handleMessage() {

        //searches sucessor of hashfile, if found send backupreadymessage
    }
}
