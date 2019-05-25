package messages;

import chord.ConnectionInfo;

import java.io.Serializable;
import java.net.UnknownHostException;

public abstract class Message implements Serializable{

    public abstract void handleMessage() throws UnknownHostException;
    public abstract String getIpAddress();
    public abstract int getPort();

}
