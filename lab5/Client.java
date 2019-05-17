import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.io.InputStream;

public class Client {

    public static void main(String[] args) {

        String host = "localhost";
        int port = 8443;
        String cypher_suite = "TLS_DHE_DSS_WITH_AES_256_CBC_SHA256";

        SSLSocket s = null;
        SSLSocketFactory sf = null;

        sf = (SSLSocketFactory) SSLSocketFactory.getDefault();

        try {
            s = (SSLSocket) sf.createSocket(host, port);
            s.startHandshake();

            InputStream in = s.getInputStream();

            byte[] b = new byte [100];
            in.read(b);

            String msg = new String(b);
            System.out.println(msg);
        }
        catch( IOException e) {
            e.printStackTrace();
            return;
        }

    }
}
