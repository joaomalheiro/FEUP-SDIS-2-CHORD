import java.io.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Peer implements RMIStub {
    public static String peerAccessPoint;
    public static String protocolVersion;
    public static int port;
    public static int referencedPort;
    public static ScheduledExecutorService executor;

    public static void main(String args[]) throws IOException, ClassNotFoundException {
        if(args.length != 4)
            return;

        initAtributes(args);

        Thread th = new Thread(new PeerReceiver(port));
        th.start();

        executor = Executors.newScheduledThreadPool(100);
        
        ChordInfo ci = new ChordInfo();
        executor.submit(ci);

        RMIStub stub;
        Peer peer = new Peer();

        stub = (RMIStub) UnicastRemoteObject.exportObject(peer, 0);

        try {
            Registry reg = LocateRegistry.getRegistry();
            reg.rebind(peerAccessPoint, stub);
            System.out.println("Peer connected through getRegistry");
        } catch (Exception e) {
            Registry reg = LocateRegistry.createRegistry(1099);
            reg.rebind(peerAccessPoint, stub);
            System.out.println("Peer connected through createRegistry");
        }
    }

    private static void initAtributes(String[] args) throws IOException, ClassNotFoundException {
        protocolVersion = args[0];
        peerAccessPoint = args[1];
        port = Integer.parseInt(args[2]);
        referencedPort = Integer.parseInt(args[3]);
    }

    @Override
    public void backupProtocol(String file, int replicationDeg) throws RemoteException {

    }

    @Override
    public void restoreProtocol(String file) throws RemoteException {

    }

    @Override
    public void deleteProtocol(String file) throws RemoteException {

    }

    @Override
    public void reclaimProtocol(int reservedSpace) throws RemoteException {

    }

    @Override
    public String stateProtocol() throws RemoteException {
        return null;
    }
}