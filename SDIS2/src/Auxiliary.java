import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Auxiliary {

    public static void handleMessage(String message) throws UnknownHostException {
        String[] tokens = message.split(" ");
        String ipAddress;
        int port;

        switch(tokens[0])
        {
            case "GETSUCCESSOR":
                //String response = ChordInfo.searchSuccessor(tokens[2], tokens[3]);
                //sendMessage(response, "localhost", tokens[3]);
                break;
            case "LOOKUP":
                BigInteger keyHash = new BigInteger(tokens[1]);
                ipAddress = tokens[2];
                port = Integer.parseInt(tokens[3]);

                if(ChordInfo.getFingerTable().size() == 0){
                    Auxiliary.sendMessage("SUCCESSOR " + keyHash + " " + InetAddress.getLocalHost().getHostAddress() + " " + Peer.port, ipAddress, port);
                    break;
                }

                try {
                    ChordInfo.lookup(keyHash, new ConnectionInfo(ipAddress, port));
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                break;
            case "SUCCESSOR":
                ChordInfo.addEntry(new BigInteger(tokens[1]), tokens[2], Integer.parseInt(tokens[3]));
                Auxiliary.sendMessage("PREDECESSOR " + ChordInfo.peerHash, tokens[2], Integer.parseInt(tokens[3]));
                break;
            case "PREDECESSOR":
                ChordInfo.peerHash = new BigInteger(tokens[1]);
                break;
            case "PING":
                ipAddress = tokens[1];
                port = Integer.parseInt(tokens[2]);

                Auxiliary.sendMessage("PONG", ipAddress, port);
                break;
            case "PONG":
                CheckPredecessor.dead = false;
                break;
            default:
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

    public static void sendMessage(String message, String address, int port) {

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
}
