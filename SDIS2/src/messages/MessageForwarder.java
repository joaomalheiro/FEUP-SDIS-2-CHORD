package messages;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;

public class MessageForwarder {

    public static void sendMessage(String message, String address, int port) {
    	System.out.println("Sending: " + message);
        SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        SSLSocket clientSocket;
        try {
            clientSocket = (SSLSocket) socketFactory.createSocket(InetAddress.getByName(address), port);
            clientSocket.startHandshake();

            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());

            outToServer.writeBytes(message + '\n');

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
