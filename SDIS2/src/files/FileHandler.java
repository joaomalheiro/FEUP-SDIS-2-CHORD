package files;

import chord.ChordManager;
import peer.Peer;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

public class FileHandler {

    public static byte[] readFromFile(String filename) throws IOException, ExecutionException, InterruptedException {

        Path path = Paths.get(filename);

        AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(path, StandardOpenOption.READ);

        int fileSize = (int) new File(filename).length();

        System.out.println(fileSize);

        ByteBuffer buffer = ByteBuffer.allocate(fileSize);

        Future<Integer> operation = fileChannel.read(buffer, 0);

        // run other code as operation continues in background
        operation.get();

        buffer.rewind();

        byte[] byteArray = new byte[buffer.remaining()];

        buffer.get(byteArray);

        return byteArray;
    }

    public static boolean checkFileExists(String filename) {
        Path path = Paths.get(filename);

        return Files.exists(path);
    }

    public static void writeFile(String filename, byte[] fileContent) throws IOException, ExecutionException, InterruptedException {

        Path path = Paths.get(filename);

        if (!checkFileExists(filename))
            Files.createFile(path);

        AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(path, StandardOpenOption.WRITE);

        int fileSize = fileContent.length;
        System.out.println(fileSize);

        ByteBuffer buffer = ByteBuffer.allocate(fileSize);

        buffer.put(fileContent);
        buffer.flip();

        Future<Integer> operation = fileChannel.write(buffer, 0);
        buffer.clear();

        //run other code as operation continues in background
        operation.get();

    }

    public static String getLastModified(String filename) throws IOException {

        Path path = Paths.get("./testFiles/" + filename);

        return String.valueOf(Files.getLastModifiedTime(path));
    }

    public static void createDir(String type) {

        String typeDirName = "./peerDisk/peer" + Peer.getPeerAccessPoint() + "-" + ChordManager.peerHash + "/" + type;

        Path dirPath = Paths.get(typeDirName);

        boolean backupExists = Files.exists(dirPath);

        if (backupExists) {
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
     *
     * @param filename
     * @return String
     */
    public static BigInteger encrypt(String filename) throws NoSuchAlgorithmException {

        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        BigInteger slot = null;
        try {
            String hashString = filename + getLastModified(filename);
            slot = new BigInteger(1, digest.digest(hashString.getBytes(StandardCharsets.UTF_8)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        int n = (int) Math.pow(2, 8);

        return slot.mod(new BigInteger("" + n));
    }

    public static void clearStorageSpace() throws IOException {
        Path rootPath = Paths.get("./peerDisk/peer" + Peer.getPeerAccessPoint() + "-" + ChordManager.peerHash);

        Files.walk(rootPath)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .peek(System.out::println)
                .forEach(FileHandler::deleteFile);
    }

    public static void deleteFile(File file){
        System.out.println(file);
        System.out.println(Peer.storage);
        System.out.println(Peer.storage.getSpaceReserved());
        System.out.println(Peer.storage.getSpaceOcupied());

        if(Peer.storage.getSpaceReserved() < Peer.storage.getSpaceOcupied()){
            handleDeleteFile(file);
            file.delete();
        } else return;
    }

    private static void handleDeleteFile(File file) {

    }


    // FUNCION COPIED FROM https://stackoverflow.com/questions/7255592/get-file-directory-size-using-java-7-new-io
    public static long getSize(Path startPath) throws IOException {
        final AtomicLong size = new AtomicLong(0);

        Files.walkFileTree(startPath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file,
                                             BasicFileAttributes attrs) throws IOException {
                size.addAndGet(attrs.size());
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc)
                    throws IOException {
                // Skip folders that can't be traversed
                System.out.println("skipped: " + file + "e=" + exc);
                return FileVisitResult.CONTINUE;
            }
        });

        return size.get();
    }
}
