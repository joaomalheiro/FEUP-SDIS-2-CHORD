package peer;

import chord.CheckPredecessor;
import chord.ChordManager;
import chord.ConnectionInfo;
import chord.Stabilize;
import files.FileHandler;
import messages.LookupMessage;
import messages.MessageForwarder;
import protocols.Backup;
import protocols.Delete;
import protocols.Reclaim;
import protocols.Restore;

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
    public static Storage storage;
    private static Peer instance = null;

    static {
        try {
            connectionInfo = new ConnectionInfo(new BigInteger(String.valueOf(0)), InetAddress.getLocalHost().getHostAddress(), 0);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public static ScheduledExecutorService executor;

    public static void main(String args[]) throws IOException {
        if (args.length < 3 || args.length > 5)
            return;

        initAtributes(args);

        Thread th = new Thread(new PeerReceiver(port));
        th.start();

        executor = Executors.newScheduledThreadPool(100);

        ChordManager ci = new ChordManager();
        executor.submit(ci);
        
        storage = new Storage(100000);
        if (storage.getSpaceReserved() < storage.getSpaceOcupied())
            FileHandler.clearStorageSpace();

        if (connectionInfo.getPort() != 0) {
            MessageForwarder.sendMessage(new LookupMessage(new ConnectionInfo(ChordManager.peerHash, InetAddress.getLocalHost().getHostAddress(), Peer.port), Peer.connectionInfo.getIp(), Peer.connectionInfo.getPort()));
        }

        executor.scheduleAtFixedRate(checkPredecessor, 0, 500, TimeUnit.MILLISECONDS);
        executor.scheduleAtFixedRate(new Stabilize(), 0, 500, TimeUnit.MILLISECONDS);

        RMIStub stub;
        instance = new Peer();

        stub = (RMIStub) UnicastRemoteObject.exportObject(instance, 0);

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

    private static void initAtributes(String[] args) throws IOException {
        protocolVersion = args[0];
        peerAccessPoint = args[1];
        port = Integer.parseInt(args[2]);

        if (args.length == 4) {
            connectionInfo.setPort(Integer.parseInt(args[3]));
        } else if (args.length == 5) {
            connectionInfo.setIp(args[3]);
            connectionInfo.setPort(Integer.parseInt(args[4]));
        }


    }

    public static String getPeerAccessPoint() {
        return peerAccessPoint;
    }

    @Override
    public void backupProtocol(String file, int replicationDeg) {
        Backup backup = new Backup(file, replicationDeg);
        backup.run();
    }

    @Override
    public void restoreProtocol(String file) {
        Restore restore = new Restore(file);
        restore.run();
    }

    @Override
    public void deleteProtocol(String file) {
        Delete delete = new Delete(file);
        delete.run();
    }

    @Override
    public void reclaimProtocol(int reservedSpace) {
        Reclaim reclaim = new Reclaim(reservedSpace);
        reclaim.run();
    }

    @Override
    public String stateProtocol() {
        return null;
    }
}