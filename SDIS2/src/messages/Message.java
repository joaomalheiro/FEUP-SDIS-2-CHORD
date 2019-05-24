package messages;

import chord.ConnectionInfo;

import java.io.Serializable;

public abstract class Message implements Serializable{

    public abstract void handleMessage();

}
