package peer;

import messages.Message;

import java.io.IOException;
import java.net.UnknownHostException;

public class MessageHandler implements Runnable{

    Message message;

    MessageHandler(Object messageObject)
    {
        if (messageObject != null) {
            System.out.println("Received: " + messageObject);
            if(messageObject instanceof Message){
                message = (Message) messageObject;
            }
        }
    }

    @Override
    public void run() {

        try {
          message.handleMessage();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
