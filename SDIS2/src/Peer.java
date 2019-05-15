import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Peer {

    private static String peerId;
    private static String peerAcessPoint;
    private static String protocolVersion;

    private static ServerSocket serverSocket;

    //private static Storage storage;

    public static void main(String args[]) throws IOException, ClassNotFoundException {

        initAtributes(args);

        initializeServerSocket();

        //RMIStub stub = null;
        Peer peer = new Peer();

        String clientSentence;

        while (true) {
            Socket connectionSocket = serverSocket.accept();
            BufferedReader inFromClient =
                    new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
            clientSentence = inFromClient.readLine();
            System.out.println("Received: " + clientSentence);
        }


        //stub = (RMIStub) UnicastRemoteObject.exportObject(peer, 0);

        /*try {
            Registry reg = LocateRegistry.getRegistry();
            reg.rebind(peerAcessPoint, stub);

            System.out.println("Peer connected through getRegistry");
        } catch (Exception e) {
            Registry reg = LocateRegistry.createRegistry(1099);
            reg.rebind(peerAcessPoint, stub);

            System.out.println("Peer connected through createRegistry");
        }
        */

    }

    private static void initAtributes(String[] args) throws IOException, ClassNotFoundException {
        protocolVersion = args[0];
        peerId = (args[1]);
        peerAcessPoint = args[2];

        //storage = new Storage(1000000);
    }

    private static void initializeServerSocket() {
        int port = 0;

        SSLServerSocketFactory serverSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();

        try {
            serverSocket = serverSocketFactory.createServerSocket(port);
        } catch (IOException e) {
            System.err.println("Error creating server socket");
            e.printStackTrace();
        }

        //Thread th = new Thread(serverSocket);
    }
}