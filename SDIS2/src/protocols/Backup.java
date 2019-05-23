package protocols;

import chord.ConnectionInfo;
import files.FileHandler;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class Backup implements Runnable{

    private String filename;
    private ConnectionInfo destinationPeer;

    Backup(String filename, ConnectionInfo destinationPeer) {
        this.filename = filename;
        this.destinationPeer = destinationPeer;
    }

    @Override
    public void run() {
        try {
            String fileContent = FileHandler.readFromFile(filename);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
