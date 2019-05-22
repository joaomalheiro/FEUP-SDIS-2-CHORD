import org.omg.CORBA.CODESET_INCOMPATIBLE;

import java.math.BigInteger;

public class ConnectionInfo {

    private String ip;
    private int port;
    private BigInteger hashedKey;

    public ConnectionInfo(BigInteger hashedKey, String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.hashedKey = hashedKey;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return this.hashedKey + " " + this.ip + " " + this.port;
    }

    @Override
    public boolean equals(Object obj) {
        ConnectionInfo ci = (ConnectionInfo) obj;
        return (this.hashedKey == ci.getHashedKey() && this.port == ci.getPort() && this.ip == ci.getIp());
    }

    public BigInteger getHashedKey() {
        return hashedKey;
    }

    public void setHashedKey(BigInteger hashedKey) {
        this.hashedKey = hashedKey;
    }
}
