package messages;

import chord.ChordInfo;
import chord.ConnectionInfo;
import peer.CheckPredecessor;
import peer.Peer;

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
                //String response = chord.ChordInfo.searchSuccessor(tokens[2], tokens[3]);
                //sendMessage(response, "localhost", tokens[3]);
                break;
            case "LOOKUP":
                BigInteger keyHash = new BigInteger(tokens[1]);
                ipAddress = tokens[2];
                port = Integer.parseInt(tokens[3]);

                if(ChordInfo.getFingerTable().get(0).getPort() == Peer.port){
                    System.out.println("Size 0");
                    Auxiliary.sendMessage("SUCCESSOR " + ChordInfo.peerHash + " " + InetAddress.getLocalHost().getHostAddress() + " " + Peer.port, ipAddress, port);
                    break;
                }
                ChordInfo.searchSuccessor(new ConnectionInfo(keyHash,ipAddress, port));
                break;
            case "SUCCESSOR":
                ChordInfo.getFingerTable().set(0,new ConnectionInfo(new BigInteger(tokens[1]), tokens[2], Integer.parseInt(tokens[3])));
                Auxiliary.sendMessage("PREDECESSOR " + ChordInfo.peerHash + " " + InetAddress.getLocalHost().getHostAddress() + " " + Peer.port, tokens[2], Integer.parseInt(tokens[3]));
                break;

            case "PREDECESSOR":
                ChordInfo.peerHash = new BigInteger(tokens[1]);
                ChordInfo.predecessor = new ConnectionInfo(ChordInfo.peerHash,tokens[2],Integer.parseInt(tokens[3]));
                System.out.println(ChordInfo.predecessor.getHashedKey());
                /* if(chord.ChordInfo.getFingerTable().size() == 0) {
                    chord.ChordInfo.addEntry(new BigInteger(tokens[1]), tokens[2], Integer.parseInt(tokens[3]));
                }*/
                break;
            case "PING":
                ipAddress = tokens[1];
                port = Integer.parseInt(tokens[2]);

                Auxiliary.sendMessage("PONG", ipAddress, port);
                break;

            case "GET_PREDECESSOR":
                if(ChordInfo.predecessor == null){
                    Auxiliary.sendMessage("RESPONSE_PREDECESSOR " + "NULL" + " " + InetAddress.getLocalHost().getHostAddress() + " " + Peer.port , tokens[1] , Integer.parseInt(tokens[2]));
                } else {
                    Auxiliary.sendMessage("RESPONSE_PREDECESSOR " + ChordInfo.getPredecessor() + " " + InetAddress.getLocalHost().getHostAddress() + " " + Peer.port , tokens[1] , Integer.parseInt(tokens[2]));
                }
                break;

            case "RESPONSE_PREDECESSOR":
                //chord.ConnectionInfo predecessor = new chord.ConnectionInfo(new BigInteger(tokens[1]), tokens[2], Integer.parseInt(tokens[3]));
                if(tokens[1].equals("NULL")){
                    Auxiliary.sendMessage("PREDECESSOR " + ChordInfo.peerHash + " " + InetAddress.getLocalHost().getHostAddress() + " " + Peer.port, tokens[2], Integer.parseInt(tokens[3]));
                } else {
                    ChordInfo.getFingerTable().set(0, new ConnectionInfo(new BigInteger(tokens[1]), tokens[2], Integer.parseInt(tokens[3])));
                }
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
