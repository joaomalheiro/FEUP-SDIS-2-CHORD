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

    public static void main(String args[]) throws IOException, ClassNotFoundException {

        initAtributes(args);
        initializeServerSocket();

        setJSSE();

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

    private static void setJSSE() {
        System.setProperty("-Djavax.net.ssl.keyStorePassword", "123456");
        System.setProperty("-Djavax.net.ssl.trustStore", "truststore");
        System.setProperty("-Djavax.net.ssl.trustStorePassword", "123456");
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

        System.setProperty("-Djavax.net.ssl.keyStore", "client.keys");

        String cypher_suite = "TLS_DHE_DSS_WITH_AES_256_CBC_SHA256";

        SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        SSLSocket clientSocket = (SSLSocket) socketFactory.createSocket(InetAddress.getByName("localhost"), 49999);
        clientSocket.startHandshake();
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