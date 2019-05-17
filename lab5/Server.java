import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.io.OutputStream;

public class Server {

    public static void main(String[] args) {

        int port = 8443;
        String cypher_suite = "TLS_DHE_DSS_WITH_AES_256_CBC_SHA256";

        SSLServerSocket s = null;
        SSLServerSocketFactory ssf;

        ssf = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();

        try {
            s = (SSLServerSocket) ssf.createServerSocket(port);
            s.setNeedClientAuth(true);
            SSLSocket socket = (SSLSocket) s.accept();
            OutputStream out = socket.getOutputStream();

            String example = "This is an example";
            byte[] bytes = example.getBytes();

            out.write(bytes);
        }
        catch( IOException e) {
            e.printStackTrace();
            return;
        }
    }
}
