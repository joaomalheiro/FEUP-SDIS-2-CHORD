package messages;

import peer.Peer;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.ObjectOutputStream;
import java.net.InetAddress;

public class SendMessage implements Runnable{

    Message message;

    SendMessage(Message message)
    {
        this.message = message;
    }


    @Override
    public void run() {
        System.out.println("Sending " + message + " to :  " + message.getIpAddress() + message.getPort());

        if(message.getPort() == Peer.port)
            return;

        SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        SSLSocket clientSocket;
        try {
            clientSocket = (SSLSocket) socketFactory.createSocket(InetAddress.getByName(message.getIpAddress()), message.getPort());
            clientSocket.startHandshake();

            ObjectOutputStream outToServer = new ObjectOutputStream(clientSocket.getOutputStream());
            outToServer.writeObject(message);
        } catch (Exception e) {
            System.out.println("User disconnected");
        }
    }
}
