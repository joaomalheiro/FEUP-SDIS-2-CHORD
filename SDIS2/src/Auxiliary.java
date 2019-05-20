import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;

public class Auxiliary {

    public static void handleMessage(String message)
    {
        String[] tokens = message.split(" ");

        switch(tokens[0])
        {
            case "GETSUCCESSOR":
                String response = ChordInfo.searchSuccessor(tokens[2], tokens[3]);
                sendMessage(response, "localhost", tokens[3]);
                break;
            case "SUCCESSOR":
                ChordInfo.setSuccessor(tokens[2]);
                break;
        }

    }

    public static String addHeader(String type, String[] params) {
        StringBuilder result = new StringBuilder();

        for (String param : params) {
            result.append(param);
            result.append(" ");
        }

        return type + " " +
                Peer.protocolVersion + " " +
                result.toString() +
                "\r\n\r\n";
    }

    public static void sendMessage(String message, String address, String port) {

        SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        SSLSocket clientSocket;
        try {
            clientSocket = (SSLSocket) socketFactory.createSocket(InetAddress.getByName(address), Integer.parseInt(port));
            clientSocket.startHandshake();

            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
            outToServer.writeBytes(message + 'n');

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
