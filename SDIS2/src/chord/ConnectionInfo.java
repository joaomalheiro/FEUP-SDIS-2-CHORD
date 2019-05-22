package chord;

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
        if(obj instanceof  ConnectionInfo){
            ConnectionInfo ci = (ConnectionInfo) obj;
            return (this.hashedKey.equals(ci.getHashedKey()) && this.port == ci.getPort() && this.ip.equals(ci.getIp()));
        }
        return false;
    }

    public BigInteger getHashedKey() {
        return hashedKey;
    }
}
