package messages;

import peer.Peer;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;

public class MessageForwarder {

    public synchronized static void sendMessage(Message message){
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
            e.printStackTrace();
        }
    }

    public static String addHeader(String type, String[] params) {
        StringBuilder result = new StringBuilder();

        for (String param : params) {
            result.append(param);
            result.append(" ");
        }

        return type + " " +
                result.toString() +
                "\r\n\r\n";
    }
}
