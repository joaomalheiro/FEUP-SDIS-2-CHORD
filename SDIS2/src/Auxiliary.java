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

        switch(tokens[0])
        {
            case "GETSUCCESSOR":
                //String response = ChordInfo.searchSuccessor(tokens[2], tokens[3]);
                //sendMessage(response, "localhost", tokens[3]);
                break;

            case "LOOKUP":
                BigInteger keyHash = new BigInteger(tokens[1]);
                String ipAdress = tokens[2];
                int port = Integer.parseInt(tokens[3]);

                if(ChordInfo.getFingerTable().size() == 0){
                    Auxiliary.sendMessage("SUCCESSOR " + keyHash + " " + InetAddress.getLocalHost().getHostAddress() + " " + Peer.port, ipAdress, port);
                    break;
                }

                    ChordInfo.searchSuccessor(keyHash, new ConnectionInfo(ipAdress, port));

                break;

            case "SUCCESSOR":
                ChordInfo.addEntry(new BigInteger(tokens[1]), tokens[2], Integer.parseInt(tokens[3]));
                Auxiliary.sendMessage("PREDECESSOR " + ChordInfo.peerHash, tokens[2], Integer.parseInt(tokens[3]));
                break;

            case "PREDECESSOR":
                ChordInfo.peerHash = new BigInteger(tokens[1]);
                break;

            case "GET_PREDECESSOR":
                Auxiliary.sendMessage("RESPONSE_PREDECESSOR " + ChordInfo.getPredecessor() + " " + InetAddress.getLocalHost().getHostAddress() + " " + Peer.port , tokens[1] , Integer.parseInt(tokens[2]));
                break;

            case "RESPONSE_PREDECESSOR":
                BigInteger predecessor = new BigInteger(tokens[1]);
                if (predecessor == ChordInfo.getPredecessor()) {
                    break;
                }
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
