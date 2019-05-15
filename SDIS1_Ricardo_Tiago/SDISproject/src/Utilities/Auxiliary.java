package Utilities;

import mains.Peer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Auxiliary {
    public static String addHeader(String type, String[] params, boolean enhanced) {
        StringBuilder result = new StringBuilder();
        String version;

        for (String param : params) {
            result.append(param);
            result.append(" ");
        }

        if(enhanced)
            version = "1.1";
        else
            version = "1.0";

        return type + " " +
                version + " " +
                Peer.senderId + " " +
                result.toString() +
                "\r\n\r\n";
    }

    public static String encodeFileId(File file) {
        String originalString = null;
        MessageDigest md = null;
        StringBuilder result = new StringBuilder();

        try {
            originalString = file.getName() + "_" +
                    file.lastModified() + "_" +
                    Files.getOwner(file.toPath());
        } catch (IOException e) {
            System.err.println("Error retrieving file information");
            System.exit(-1);
        }

        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Error getting instance of MessageDigest");
            System.exit(-2);
        }

        md.update(originalString.getBytes());

        for (byte byt : md.digest())
            result.append(Integer.toString((byt & 0xff) + 0x100, 16).substring(1));

        return result.toString();
    }

    public static void clearDirectory(File directory) {
        if (directory.exists()) {
            try {
                String[] files = directory.list();
                if(files != null){
                    for (String s : files) {
                        File currentFile = new File(directory.getPath(), s);
                        if (!currentFile.delete())
                            throw new Exception("couldn't delete file");
                    }
                    if (!directory.delete())
                        throw new Exception("couldn't delete directory");
                }
            } catch(Exception e){
                e.printStackTrace();
                System.exit(-1);
            }
        }
    }

    public static long getDirectorySize(File folder) {
        long length = 0;
        File[] files = folder.listFiles();
        if(files != null)
            for (File file : files) {
                if (file.isFile())
                    length += file.length();
                else
                    length += getDirectorySize(file);
            }
        return length;
    }
}
