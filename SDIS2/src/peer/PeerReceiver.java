package peer;

import messages.Message;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.*;
import java.net.UnknownHostException;


public class PeerReceiver implements Runnable {
    private SSLServerSocket serverSocket;
    private int port;

    PeerReceiver (int port) {
        this.port = port;
        setJSSE();
        setServerSocket();
    }

    /**
     * Configures the keystore and truststore
     */
    private static void setJSSE() {
        System.setProperty("javax.net.ssl.keyStore", "src/server.keys");
        System.setProperty("javax.net.ssl.keyStorePassword", "123456");
        System.setProperty("javax.net.ssl.trustStore", "src/truststore");
        System.setProperty("javax.net.ssl.trustStorePassword", "123456");
    }

    /**
     * Creates server socket
     */
    private void setServerSocket()
    {
        serverSocket = null;

        SSLServerSocketFactory serverSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();

        try {
            serverSocket = (SSLServerSocket) serverSocketFactory.createServerSocket(port);
            serverSocket.setNeedClientAuth(true);
            serverSocket.setEnabledProtocols(serverSocket.getSupportedProtocols());
            System.out.println("Server socket thread created and ready to receive");
        } catch (IOException e) {
            System.err.println("Error creating server socket");
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        Object messageObject = null;
        SSLSocket connectionSocket = null;

        //Ciclo infinito para receber mensagens no serverSocket
        while(true) {

            try {
                connectionSocket = (SSLSocket) serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }

            ObjectInputStream inFromClient = null;
            try {
                inFromClient = new ObjectInputStream(connectionSocket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(connectionSocket != null){
                if(inFromClient != null) {
                    try {
                        messageObject = inFromClient.readObject();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    if (messageObject != null) {
                        System.out.println("Received: " + messageObject);
                        if(messageObject instanceof Message){
                            Message message = (Message) messageObject;
                            try {
                                message.handleMessage();
                            } catch (UnknownHostException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }

        }
    }
}
