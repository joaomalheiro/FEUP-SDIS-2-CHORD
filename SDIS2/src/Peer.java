import java.io.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


public class Peer implements RMIStub {

    private static String peerId;
    private static String peerAcessPoint;
    private static String protocolVersion;
    private static int sender;
    private static int port;
    public static ScheduledExecutorService executor;

    public static void main(String args[]) throws IOException, ClassNotFoundException {

        initAtributes(args);
        initializeServerSocket();

        int referencedPort = 0;

        //Um peer necessita de ter informações sobre pelo um peer de maneira.
        // Caso o peer seja o primeiro, só existem 5 args.
        // Caso contrário, o 6º argumento é o port do 1º peer

        executor = Executors.newScheduledThreadPool(100);

        if(args.length > 5)
            referencedPort = Integer.parseInt(args[5]);
        
        ChordInfo ci = new ChordInfo(port, referencedPort, sender, executor);
        executor.submit(ci);

        RMIStub stub;
        Peer peer = new Peer();

        stub = (RMIStub) UnicastRemoteObject.exportObject(peer, 0);

        try {
            Registry reg = LocateRegistry.getRegistry();
            reg.rebind(peerAcessPoint, stub);
            System.out.println("Peer connected through getRegistry");
        } catch (Exception e) {
            Registry reg = LocateRegistry.createRegistry(1099);
            reg.rebind(peerAcessPoint, stub);
            System.out.println("Peer connected through createRegistry");
        }
    }

    private static void initAtributes(String[] args) throws IOException, ClassNotFoundException {
        protocolVersion = args[0];
        peerId = (args[1]);
        peerAcessPoint = args[2];
        port = Integer.parseInt(args[3]);
        sender = Integer.parseInt(args[4]);
    }

    private static void initializeServerSocket() {

        Thread th = new Thread(new PeerReceiver(port));
        th.start();
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