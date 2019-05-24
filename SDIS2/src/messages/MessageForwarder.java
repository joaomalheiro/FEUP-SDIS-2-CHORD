package messages;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;

public class MessageForwarder {

    public synchronized static void sendMessage(Message message, String address, int port){
        System.out.println("Sending " + message + " to :  " + address + port);
        SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        SSLSocket clientSocket;
        try {
            clientSocket = (SSLSocket) socketFactory.createSocket(InetAddress.getByName(address), port);
            clientSocket.startHandshake();

            ObjectOutputStream outToServer = new ObjectOutputStream(clientSocket.getOutputStream());
            outToServer.writeObject(message);
        } catch (IOException e) {
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
