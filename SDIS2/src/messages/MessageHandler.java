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
                    MessageForwarder.sendMessage("SUCCESSOR " + ChordInfo.peerHash + " " + InetAddress.getLocalHost().getHostAddress() + " " + Peer.port, ipAddress, port);
                    break;
                }
                ChordInfo.searchSuccessor(new ConnectionInfo(keyHash,ipAddress, port));
                break;
            case "SUCCESSOR":
                ChordInfo.getFingerTable().set(0,new ConnectionInfo(new BigInteger(tokens[1]), tokens[2], Integer.parseInt(tokens[3])));
                MessageForwarder.sendMessage("PREDECESSOR " + ChordInfo.peerHash + " " + InetAddress.getLocalHost().getHostAddress() + " " + Peer.port, tokens[2], Integer.parseInt(tokens[3]));
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

                MessageForwarder.sendMessage("PONG", ipAddress, port);
                break;

            case "GET_PREDECESSOR":
                if(ChordInfo.predecessor == null){
                    MessageForwarder.sendMessage("RESPONSE_PREDECESSOR " + "NULL" + " " + InetAddress.getLocalHost().getHostAddress() + " " + Peer.port , tokens[1] , Integer.parseInt(tokens[2]));
                } else {
                    MessageForwarder.sendMessage("RESPONSE_PREDECESSOR " + ChordInfo.getPredecessor() + " " + InetAddress.getLocalHost().getHostAddress() + " " + Peer.port , tokens[1] , Integer.parseInt(tokens[2]));
                }
                break;

            case "RESPONSE_PREDECESSOR":
                //chord.ConnectionInfo predecessor = new chord.ConnectionInfo(new BigInteger(tokens[1]), tokens[2], Integer.parseInt(tokens[3]));
                if(tokens[1].equals("NULL")){
                    MessageForwarder.sendMessage("PREDECESSOR " + ChordInfo.peerHash + " " + InetAddress.getLocalHost().getHostAddress() + " " + Peer.port, tokens[2], Integer.parseInt(tokens[3]));
                } else {
                    ChordInfo.getFingerTable().set(0, new ConnectionInfo(new BigInteger(tokens[1]), tokens[2], Integer.parseInt(tokens[3])));
                }
                break;

            case "PONG":
                synchronized(Peer.checkPredecessor){
                    CheckPredecessor.dead = false;
                    Peer.checkPredecessor.notify();
                }
                break;

            default:
                break;
        }

    }
}
