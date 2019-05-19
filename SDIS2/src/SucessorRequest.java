import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;

public class SucessorRequest implements Runnable{

    private int referencedPort;
    private int port;
    private String key;

    SucessorRequest(int referencedPort, int port, String key)
    {
        this.referencedPort = referencedPort;
        this.port = port;
        this.key = key;
    }

    @Override
    public void run() {

        SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        SSLSocket clientSocket;
        try {
            clientSocket = (SSLSocket) socketFactory.createSocket(InetAddress.getByName("localhost"), referencedPort);
            clientSocket.startHandshake();
            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
            String sentence = "GETSUCCESSOR 1.0 " + key + " " + port + " \r\n\r\n";
            outToServer.writeBytes(sentence + 'n');
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
