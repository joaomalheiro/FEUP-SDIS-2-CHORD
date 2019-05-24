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
                    MessageForwarder.sendMessage("SUCCESSOR " + tokens[1] + " " + ChordInfo.peerHash + " " + InetAddress.getLocalHost().getHostAddress() + " " + Peer.port, ipAddress, port);
                    break;
                }
                ChordInfo.searchSuccessor(new ConnectionInfo(keyHash,ipAddress, port));
                break;
            case "SUCCESSOR":
                int index;

                if(tokens[1].equals(ChordInfo.peerHash.toString()))
                    index = 0;

                else
                    for(index = 0; index < ChordInfo.getM() * 8; index++)
                    {
                        String res = ChordInfo.calculateNextKey(ChordInfo.peerHash, index, ChordInfo.getM() * 8);
                        if(res.equals(tokens[1]))
                            break;
                    }

                ChordInfo.getFingerTable().set(index,new ConnectionInfo(new BigInteger(tokens[2]), tokens[3], Integer.parseInt(tokens[4])));
                MessageForwarder.sendMessage("PREDECESSOR " + ChordInfo.peerHash + " " + InetAddress.getLocalHost().getHostAddress() + " " + Peer.port, tokens[3], Integer.parseInt(tokens[4]));
                ChordInfo.printFingerTable();
                break;

            case "PREDECESSOR":
                ChordInfo.predecessor = new ConnectionInfo(new BigInteger(tokens[1]),tokens[2],Integer.parseInt(tokens[3]));
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
                    //ChordInfo.getFingerTable().set(0, new ConnectionInfo(new BigInteger(tokens[1]), tokens[2], Integer.parseInt(tokens[3])));
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
