package files;


import chord.ConnectionInfo;
import messages.BackupMessage;

import messages.MessageForwarder;

import chord.ChordManager;

import peer.Peer;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;


public class FileHandler {

    public static byte[] readFromFile(String filename) throws IOException, ExecutionException, InterruptedException {

        Path path = Paths.get(filename);

        AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(path, StandardOpenOption.READ);

        int fileSize = (int) new File(filename).length();

        ByteBuffer buffer = ByteBuffer.allocate(fileSize);

        Future<Integer> operation = fileChannel.read(buffer, 0);

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

        ByteBuffer buffer = ByteBuffer.allocate(fileSize);

        buffer.put(fileContent);
        buffer.flip();

        Future<Integer> operation = fileChannel.write(buffer, 0);
        buffer.clear();

        operation.get();

    }

    public static String getFileSize(String filename) throws IOException {

        Path path = Paths.get("./testFiles/" + filename);

        return String.valueOf(Files.size(path));
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

    public static void clearStorageSpace() throws IOException {
        Path rootPath = Paths.get("./peerDisk/peer" + Peer.getPeerAccessPoint() + "-" + ChordManager.peerHash);

        final List<Path> pathsToDelete = Files.walk(rootPath).sorted(Comparator.reverseOrder()).collect(Collectors.toList());
        for(Path path : pathsToDelete) {
            if(Peer.storage.getSpaceReserved() < Peer.storage.getSpaceOcupied()) {
            	if(Files.isRegularFile(path)){
            		handleDeleteFile(path);
                    Files.deleteIfExists(path);
            	}
            }
        }
    }


    private static void handleDeleteFile(Path path) throws IOException {
        byte[] content = Files.readAllBytes(path);
        DeleteHandler handler = new DeleteHandler(new ConnectionInfo(ChordManager.peerHash, InetAddress.getLocalHost().getHostAddress(), Peer.port), new BigInteger(path.getFileName().toString()), 1, content);
        Peer.executor.submit(handler);
    }


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
                return FileVisitResult.CONTINUE;
            }
        });

        return size.get();
    }
}
