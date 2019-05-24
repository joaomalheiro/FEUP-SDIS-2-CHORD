package peer;

import chord.ChordInfo;
import chord.ConnectionInfo;
import messages.MessageForwarder;

import java.io.*;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Peer implements RMIStub {
    private static String peerAccessPoint;
    private static String protocolVersion;
    public static int port;
    public static ConnectionInfo connectionInfo;
    public static final CheckPredecessor checkPredecessor = new CheckPredecessor(500);
    static {
        try {
            connectionInfo = new ConnectionInfo(new BigInteger(String.valueOf(0)),InetAddress.getLocalHost().getHostAddress(), 0);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
    public static ScheduledExecutorService executor;

    public static void main(String args[]) throws IOException{
        if(args.length < 3 || args.length > 5)
            return;

        initAtributes(args);

        Thread th = new Thread(new PeerReceiver(port));
        th.start();

        executor = Executors.newScheduledThreadPool(100);
        
        ChordInfo ci = new ChordInfo();
        executor.submit(ci);

        if(connectionInfo.getPort() != 0)
            MessageForwarder.sendMessage("LOOKUP " + ChordInfo.peerHash + " "
                    + InetAddress.getLocalHost().getHostAddress() + " " +
                    Peer.port, Peer.connectionInfo.getIp(), Peer.connectionInfo.getPort());

        executor.scheduleAtFixedRate(checkPredecessor,0,1000, TimeUnit.MILLISECONDS);
        executor.scheduleAtFixedRate(new Stabilize(),0,5000, TimeUnit.MILLISECONDS);

        RMIStub stub;
        Peer peer = new Peer();

        stub = (RMIStub) UnicastRemoteObject.exportObject(peer, 0);

        try {
            Registry reg = LocateRegistry.getRegistry();
            reg.rebind(peerAccessPoint, stub);
            System.out.println("peer.Peer connected through getRegistry");
        } catch (Exception e) {
            Registry reg = LocateRegistry.createRegistry(1099);
            reg.rebind(peerAccessPoint, stub);
            System.out.println("peer.Peer connected through createRegistry");
        }
    }

    private static void initAtributes(String[] args){
        protocolVersion = args[0];
        peerAccessPoint = args[1];
        port = Integer.parseInt(args[2]);

        if(args.length == 4) {
            connectionInfo.setPort(Integer.parseInt(args[3]));
        } else
            if(args.length == 5){
                connectionInfo.setIp(args[3]);
                connectionInfo.setPort(Integer.parseInt(args[4]));
            }
    }

    @Override
    public void backupProtocol(String file, int replicationDeg) {

    }

    @Override
    public void restoreProtocol(String file) {

    }

    @Override
    public void deleteProtocol(String file) {

    }

    @Override
    public void reclaimProtocol(int reservedSpace) {

    }

    @Override
    public String stateProtocol() {
        return null;
    }
}