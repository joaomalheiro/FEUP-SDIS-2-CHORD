import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Peer implements peer.RMIStub{

    private static String peerId;
    private static String peerAcessPoint;
    private static String protocolVersion;
    private static int sender;
    private static int port;

    private static ServerSocket serverSocket;

    public static void main(String args[]) throws IOException, ClassNotFoundException {

        initAtributes(args);
        initializeServerSocket();

        peer.RMIStub stub;
        Peer peer = new Peer();

        stub = (peer.RMIStub) UnicastRemoteObject.exportObject(peer, 0);

        try {
            Registry reg = LocateRegistry.getRegistry();
            reg.rebind(peerAcessPoint, stub);
            System.out.println("Peer connected through getRegistry");
        } catch (Exception e) {
            Registry reg = LocateRegistry.createRegistry(1099);
            reg.rebind(peerAcessPoint, stub);
            System.out.println("Peer connected through createRegistry");
        }

        if(sender == 2) {
           sendMessage();
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

    private static void sendMessage() throws IOException {

        SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        Socket clientSocket = socketFactory.createSocket(InetAddress.getByName("localhost"), 49999);
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        //BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String sentence = "opa duriola \n";
        outToServer.writeBytes(sentence + 'n');

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