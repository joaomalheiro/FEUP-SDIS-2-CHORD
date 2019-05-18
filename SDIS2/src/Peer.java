import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.math.BigInteger;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

public class Peer implements RMIStub {

    private static String peerId;
    private static String peerAcessPoint;
    private static String protocolVersion;
    private static int sender;
    private static int port;

    public static void main(String args[]) throws IOException, ClassNotFoundException {

        initAtributes(args);
        initializeServerSocket();

        setJSSE();
        setChord();

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

        if(sender == 2) {
           sendMessage();
        }
    }

    private static void setJSSE() {
        System.setProperty("javax.net.ssl.keyStore", "server.keys");
        System.setProperty("javax.net.ssl.keyStorePassword", "123456");
        System.setProperty("javax.net.ssl.trustStore", "truststore");
        System.setProperty("javax.net.ssl.trustStorePassword", "123456");
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

        String cypher_suite = "TLS_DHE_DSS_WITH_AES_256_CBC_SHA256";

        SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        SSLSocket clientSocket = (SSLSocket) socketFactory.createSocket(InetAddress.getByName("localhost"), 49999);
        clientSocket.startHandshake();
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        //BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String sentence = "opa duriola \n";
        outToServer.writeBytes(sentence + 'n');

    }

    public static void setChord()
    {
        int mBytes = 1; //hash size in bytes
        String peerHash;
        ArrayList<Integer> fingerTable = new ArrayList<> (mBytes * 8);

        peerHash = getPeerHash(mBytes);
        getFingerTable(peerHash, fingerTable, mBytes * 8);
    }

    public static String getPeerHash(int hashSize)
    {
        String originalString = null;
        MessageDigest md = null;
        StringBuilder result = new StringBuilder();

        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Error getting instance of MessageDigest");
            System.exit(1);
        }

        originalString = "" + port;

        md.update(originalString.getBytes());
        byte[] hashBytes = md.digest();

        byte[] trimmedHashBytes = Arrays.copyOf(hashBytes, hashSize);


        for (byte byt : trimmedHashBytes)
            result.append(Integer.toString((byt & 0xff) + 0x100, 16).substring(1));

        return result.toString();
    }

    private static void getFingerTable(String hash, ArrayList<Integer> fingerTable, int numberEntries) {

        BigInteger hashBI = new BigInteger(hash, 16);

        for(int i = 0; i < numberEntries; i++)
        {
            String nextKey = calculateNextKey(hashBI, i, numberEntries);
            //Falta código para obter o peer a partir da nextKey
        }
    }

    private static String calculateNextKey(BigInteger hash, int index, int m)
    {
        BigInteger add = new BigInteger(String.valueOf((int) Math.pow(2, index)));
        BigInteger mod =  new BigInteger(String.valueOf((int) Math.pow(2, m)));

        BigInteger res = hash.add(add).mod(mod);
        return res.toString(16);
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