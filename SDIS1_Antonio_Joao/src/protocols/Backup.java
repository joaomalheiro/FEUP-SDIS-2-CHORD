package protocols;

import java.io.File;
import java.io.IOException;

import files.FileHandler;


public class Backup implements Runnable {

    private String fileName;
    private int repDegree;

    public Backup(String fileName, int repDegree) {
        this.fileName = fileName;
        this.repDegree = repDegree;

    }

    @Override
    public void run() {
        try {
            FileHandler.splitFile(new File("./testFiles/" + this.fileName), this.repDegree );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
