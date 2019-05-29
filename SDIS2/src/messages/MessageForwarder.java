package messages;

import peer.MessageHandler;
import peer.Peer;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;

public class MessageForwarder {

    public static void sendMessage(Message message){
        System.out.println("beggining of send message");

        Thread th = new Thread(new SendMessage(message));
        th.start();
        //SendMessage sm = new SendMessage(message);
        //Peer.executor.submit(sm);
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
