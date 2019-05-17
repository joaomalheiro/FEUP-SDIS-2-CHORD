import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class PeerReceiver implements Runnable {
    private SSLServerSocket serverSocket;
    private int port;

    PeerReceiver (int port) {
        this.port = port;
    }

    @Override
    public void run() {

        //System.setProperty("-Djavax.net.ssl.keyStore", "client.keys");

        serverSocket = null;
        String clientSentence = null;

        SSLServerSocketFactory serverSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();

        try {
            serverSocket = (SSLServerSocket) serverSocketFactory.createServerSocket(port);
            serverSocket.setNeedClientAuth(true);
            serverSocket.setEnabledProtocols(serverSocket.getSupportedProtocols());
        } catch (IOException e) {
            System.err.println("Error creating server socket");
            e.printStackTrace();
        }
        SSLSocket connectionSocket = null;
        try {
            System.out.println("Server socket thread created and ready to receive");
            connectionSocket = (SSLSocket)serverSocket.accept();
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedReader inFromClient = null;

        try {
            inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            clientSentence = inFromClient.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Received: " + clientSentence);
    }
}
