package files;

import peer.Peer;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class FileHandler {

    public static String readFromFile(String filename) throws IOException, ExecutionException, InterruptedException {

        Path path = Paths.get(filename);

        AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(path, StandardOpenOption.READ);
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        Future<Integer> operation = fileChannel.read(buffer, 0);

        // run other code as operation continues in background
        operation.get();

        String fileContent = new String(buffer.array()).trim();

        buffer.clear();

        return fileContent;
    }

    public static void writeFile(String filename, String fileContent) throws IOException, ExecutionException, InterruptedException {

        Path path = Paths.get(filename);

        if(!Files.exists(path))
            Files.createFile(path);

        AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(path, StandardOpenOption.WRITE);

        ByteBuffer buffer = ByteBuffer.allocate(1024);

        buffer.put(fileContent.getBytes());
        buffer.flip();

        Future<Integer> operation = fileChannel.write(buffer, 0);
        buffer.clear();

        //run other code as operation continues in background
        operation.get();
    }

    public static void createDir(String type){

        String typeDirName = "./peerDisk/peer" + Peer.getPeerAccessPoint() + "/" + type;

        Path dirPath = Paths.get(typeDirName);

        boolean backupExists = Files.exists(dirPath);

        if(backupExists) {
            System.out.println("Directory already exists");
        } else {
            try {
                Files.createDirectories(dirPath);
            } catch (IOException ioExceptionObj) {
                System.out.println("Problem creating directory");
            }
        }
    }
    /**
     * Encrypts a string(text) that is the filename and returns the new hashed fileId
     * @param text
     * @return String
     */
    public static String encrypt(String text) {

        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(encodedhash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "error in hashing";
    }

    /**
     * Helper that passes a hashed byte[] into a string
     * @param hash
     * @return
     */
    private static String bytesToHex(byte[] hash) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
