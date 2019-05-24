package messages;

import chord.ConnectionInfo;

public class PingMessage  extends  Message{

    private ConnectionInfo ci;

    public PingMessage(ConnectionInfo ci){
        this.ci = ci;
    }

    @Override
    public void handleMessage() {
        MessageForwarder.sendMessage(new PongMessage(), ci.getIp(), ci.getPort());
    }

    @Override
    public String toString() {
        return "PING " + this.ci;
    }
}
