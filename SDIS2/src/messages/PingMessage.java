package messages;

import chord.ConnectionInfo;

public class PingMessage  extends  Message{

    private ConnectionInfo ci;

    public PingMessage(ConnectionInfo ci){
        this.ci = ci;
    }
    @Override
    public void handleMessage() {
        MessageForwarder.sendMessage("PONG", ci.getIp(), ci.getPort());
    }
}
