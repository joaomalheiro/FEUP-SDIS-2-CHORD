import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Peer implements RMIStub {
    private static String peerAccessPoint;
    public static String protocolVersion;
    public static int port;
    public static ConnectionInfo connectionInfo;

    static {
        try {
            connectionInfo = new ConnectionInfo(InetAddress.getLocalHost().getHostAddress(), 0);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public static ScheduledExecutorService executor;

    public static void main(String args[]) throws IOException, ClassNotFoundException {
        if(args.length != 4 && args.length != 3)
            return;

        if(args.length == 4)
            connectionInfo.setPort(Integer.parseInt(args[3]));

        if(args.length == 5) {
            connectionInfo.setIp(args[3]);
            connectionInfo.setPort(Integer.parseInt(args[4]));
        }

        initAtributes(args);

        Thread th = new Thread(new PeerReceiver(port));
        th.start();

        executor = Executors.newScheduledThreadPool(100);
        
        ChordInfo ci = new ChordInfo();
        executor.submit(ci);

        if(connectionInfo.getPort() != 0)
            Auxiliary.sendMessage("LOOKUP " + ci.peerHash + " " + InetAddress.getLocalHost().getHostAddress() + " " + Peer.port, Peer.connectionInfo.getIp(), Peer.connectionInfo.getPort());

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