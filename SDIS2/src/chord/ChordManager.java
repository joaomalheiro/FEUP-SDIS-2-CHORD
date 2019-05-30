package chord;

import files.FileHandler;
import messages.LookupMessage;
import messages.Message;
import messages.SucessorMessage;
import peer.FixFingers;
import peer.Peer;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class ChordManager implements Runnable{
    private final static int M = 32; //bits
    public static BigInteger peerHash;
    private static ArrayList<ConnectionInfo> fingerTable = new ArrayList<>(M);
    public static ConnectionInfo predecessor = null;

    public ChordManager(){
        try {
            setChord();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public static ConnectionInfo getPredecessor() {
        return predecessor;
    }

    public static ArrayList<ConnectionInfo> getFingerTable() {
        return fingerTable;
    }

    public static int getM(){ return M;}

    /**
     * Calls functions to create hash and fill finger table
     */
    private void setChord() throws UnknownHostException {
        String [] params = new String[] {String.valueOf(Peer.port)};
        ChordManager.peerHash = encrypt(params);
        System.out.println("peer.Peer hash = " + peerHash + "\n");

        FileHandler.createDir("backup");
        FileHandler.createDir("restored");

        initFingerTable();
        printFingerTable();

        FixFingers ff = new FixFingers();
        Peer.executor.scheduleAtFixedRate(ff,0,500, TimeUnit.MILLISECONDS);
    }

    private void initFingerTable() throws UnknownHostException {
        //for(int i = 0; i < mBytes * 8; i++) {
            fingerTable.add(new ConnectionInfo(peerHash, InetAddress.getLocalHost().getHostAddress(), Peer.port));
        //}
    }

    public static void printFingerTable() {
        System.out.println("FingerTable");

        /*for(ConnectionInfo finger : fingerTable){
            System.out.println(finger.getHashedKey() + " : " + finger.getIp() + " : " + finger.getPort());
        }*/

        for (int i = 0; i < fingerTable.size(); i++)
        {
            ConnectionInfo finger = fingerTable.get(i);
            System.out.println(i + " : " + finger.getHashedKey() + " : " + finger.getIp() + " : " + finger.getPort());
        }

    }

    private static long convertToDec(String hex)
    {
        String digits = "0123456789ABCDEF";
        hex = hex.toUpperCase();
        long val = 0;
        for (int i = 0; i < hex.length(); i++) {
            char c = hex.charAt(i);
            int d = digits.indexOf(c);
            val = 16 * val + d;
        }
        return val;
    }
    /**
     * Creates hash with size hashSize from server's port
     *
     * @param params List of parameters to be used for hashing
     * @return hash
     */
    public static BigInteger encrypt(String[] params)
    {
        String originalString = "";
        MessageDigest md = null;
        StringBuilder result = new StringBuilder();

        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Error getting instance of MessageDigest");
            System.exit(1);
        }

        for(int i = 0; i < params.length; i++) {
            originalString += params[i];

            if(i < params.length - 1)
                originalString += " ";
        }

        System.out.println(originalString);

        md.update(originalString.getBytes());
        byte[] hashBytes = md.digest();

        byte[] trimmedHashBytes = Arrays.copyOf(hashBytes, M/8);

        for (byte byt : trimmedHashBytes)
            result.append(Integer.toString((byt & 0xff) + 0x100, 16).substring(1));

        long resultLong = convertToDec(result.toString());

        return BigInteger.valueOf(resultLong);
    }

    /**
     * Calculates the key of the next entry of the fingertable - ( hash + 2^index) mod 2^m
     *
     * @param hash peer's hash
     * @param index finger table's index to be filled
     * @param m hash size (bits)
     * @return next key's hash
     */
    public static String calculateNextKey(BigInteger hash, int index, int m)
    {
        //Exemplo
        // hash = 10, index = 0, m = 7 => 10 + 2^0 = 11
        // hash = 10, index = 3, m = 7 => 10 + 2^3 = 18
        // hash = 125, index = 3, m = 7 => 125 + 2^3 = 133 mod 2^7 = 8

        BigInteger add = new BigInteger(String.valueOf((int) Math.pow(2, index)));
        BigInteger mod =  new BigInteger(String.valueOf((int) Math.pow(2, m)));

        BigInteger res = hash.add(add).mod(mod);
        return res.toString();
    }

    public static Message searchSuccessor2(ConnectionInfo senderInfo)
    {
        BigInteger successorKey = fingerTable.get(0).getHashedKey();

        if(numberInInterval(peerHash, successorKey, senderInfo.getHashedKey()))
            return new SucessorMessage(senderInfo.getHashedKey().toString(),new ConnectionInfo(fingerTable.get(0).getHashedKey(),fingerTable.get(0).getIp(),fingerTable.get(0).getPort()),senderInfo.getIp(),senderInfo.getPort());

        else {
            for(int i = fingerTable.size()-1; i >= 0; i--){

                if(fingerTable.get(i).getHashedKey() == null)
                    continue;

                if(numberInInterval(peerHash, senderInfo.getHashedKey(), fingerTable.get(i).getHashedKey())) {
                    if(fingerTable.get(i).getHashedKey().equals(ChordManager.peerHash))
                        continue;

                    return new LookupMessage(senderInfo,fingerTable.get(i).getIp(), fingerTable.get(i).getPort());
                }
            }

            try {
                return new SucessorMessage(senderInfo.getHashedKey().toString(),new ConnectionInfo(ChordManager.peerHash, InetAddress.getLocalHost().getHostAddress(), Peer.port),senderInfo.getIp(),senderInfo.getPort());
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public static boolean numberInInterval(BigInteger begin, BigInteger end, BigInteger value)
    {
        boolean result = false;
        int cmp = begin.compareTo(end);

        if(cmp == 1) {
            if (value.compareTo(begin) == 1 || value.compareTo(end) == -1)
                result = true;
        }

        else if (cmp == -1) {
            if (value.compareTo(begin) == 1 && value.compareTo(end) == -1)
                result = true;
        }

        else {
            if (value.compareTo(begin) == 0)
                result = true;
        }

        return result;
    }

    /*
    public static void setSuccessor(String key)
    {
        if(fingerTable.size() == 0) {
            fingerTable.add(key);
        }

        fingerTable.set(0,key);
    }
*/
    @Override
    public void run() {

    }
}
