import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;

public class SuccessorRequest implements Runnable{

    private int referencedPort;
    private int port;
    private String key;

    SuccessorRequest(int referencedPort, int port, String key)
    {
        this.referencedPort = referencedPort;
        this.port = port;
        this.key = key;
    }

    @Override
    public void run() {

        String sentence = "GETSUCCESSOR " + Peer.protocolVersion + " " + key + " " + port + " \r\n\r\n";

        Auxiliary.sendMessage(sentence, "localhost", String.valueOf(this.referencedPort));

        //Provavelmente vai ser preciso fazer algo que controle a chegada a resposta
    }
}
