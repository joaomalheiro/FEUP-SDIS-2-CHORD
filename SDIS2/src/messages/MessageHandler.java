package messages;

import chord.ChordInfo;
import chord.ConnectionInfo;
import peer.CheckPredecessor;
import peer.Peer;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class MessageHandler {
    public static void handleMessage(String message) throws UnknownHostException {
        String[] tokens = message.split(" ");
        String ipAddress;
        int port;
        Message handler = null;

        switch(tokens[0])
        {
            case "GETSUCCESSOR":
                //String response = chord.ChordInfo.searchSuccessor(tokens[2], tokens[3]);
                //sendMessage(response, "localhost", tokens[3]);
                break;
            case "LOOKUP":
                handler = new LookupMessage(new ConnectionInfo(new BigInteger(tokens[1]),tokens[2],Integer.parseInt(tokens[3])));
                break;

            case "PREDECESSOR":
                handler = new PredecessorMessage(new ConnectionInfo(new BigInteger(tokens[1]), tokens[2],Integer.parseInt(tokens[3])));
                break;
            case "PING":
                ipAddress = tokens[1];
                port = Integer.parseInt(tokens[2]);

                MessageForwarder.sendMessage("PONG", ipAddress, port);
                handler = new PingMessage(new ConnectionInfo(null, ipAddress,port));
                break;

            case "GET_PREDECESSOR":
                handler = new GetPredecessorMessage(new ConnectionInfo(null, tokens[1],Integer.parseInt(tokens[2])));
                break;

            case "RESPONSE_PREDECESSOR":
                handler = new ResponsePredecessorMessage(new ConnectionInfo(new BigInteger(tokens[1]), tokens[2],Integer.parseInt(tokens[3])));
                break;

            case "PONG":
                handler = new PongMessage();
                break;

            default:
                break;
        }
        handler.handleMessage();

    }
}
