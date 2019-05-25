package messages;

import chord.ConnectionInfo;

public class PingMessage  extends  Message{

    private ConnectionInfo ci;
    private String ipAddress;
    private int port;

    public PingMessage(ConnectionInfo ci,String ipAddress,int port){
        this.ci = ci;
        this.ipAddress = ipAddress;
        this.port = port;
    }

    @Override
    public void handleMessage() {
        MessageForwarder.sendMessage(new PongMessage( ci.getIp(), ci.getPort()));
    }

    @Override
    public String toString() {
        return "PING " + this.ci;
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
