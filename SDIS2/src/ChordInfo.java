import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class ChordInfo implements Runnable{
    private static int mBytes = 1; //hash size in bytes
    public static BigInteger peerHash;
    private static BigInteger predecessor = null;
    private static ConcurrentSkipListMap<BigInteger, ConnectionInfo> fingerTable = new ConcurrentSkipListMap <> ();

    ChordInfo() throws UnknownHostException {
        try {
            setChord();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public static ConcurrentSkipListMap<BigInteger, ConnectionInfo> getFingerTable() {
        return fingerTable;
    }

    public static void addEntry(BigInteger hashedID, String address, int port) {
        fingerTable.put(hashedID, new ConnectionInfo(address, port));
        printFingerTable();
    }

    /**
     * Calls functions to create hash and fill finger table
     */
    private void setChord() throws UnknownHostException {
        int mBytes = 1; //hash size in bytes
        ArrayList<String> fingerTable = new ArrayList<> (mBytes * 8);

        ChordInfo.peerHash = BigInteger.valueOf(Integer.parseInt(getPeerHash(mBytes),16));

        System.out.println("Peer hash = " + peerHash + "\n");

        //se não for o primeiro peer no sistema
        if(Peer.connectionInfo.getPort() != 0) {
            Peer.executor.submit(new SuccessorRequest(Peer.connectionInfo.getPort(), Peer.port));
        }
        printFingerTable();
    }
    
    private static void printFingerTable() {
        System.out.println("FingerTable");
        fingerTable.forEach((key, value) -> System.out.println(key + ":" + value));
    }


    /**
     * Creates hash with size hashSize from server's port
     *
     * @param hashSize hash size
     * @return hash
     */
    private String getPeerHash(int hashSize)
    {
        String originalString;
        MessageDigest md = null;
        StringBuilder result = new StringBuilder();

        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Error getting instance of MessageDigest");
            System.exit(1);
        }

        originalString = "" + Peer.port;

        md.update(originalString.getBytes());
        byte[] hashBytes = md.digest();

        byte[] trimmedHashBytes = Arrays.copyOf(hashBytes, hashSize);

        for (byte byt : trimmedHashBytes)
            result.append(Integer.toString((byt & 0xff) + 0x100, 16).substring(1));

        return result.toString();
    }

    /**
     * Fills initial finger table
     *
     * @param fingerTable struct to be filled
     * @param numberEntries finger table size (hash size in bits)
     * @param referencedPort port of existing peer that was passed by argument
     */

    private void getFingerTable(ArrayList<String> fingerTable, int numberEntries, int referencedPort) {
        BigInteger hashBI = ChordInfo.peerHash;

        for(int i = 0; i < numberEntries; i++)
        {
            String nextKey = calculateNextKey(hashBI, i, numberEntries);
            //getSuccessor(referencedPort, nextKey);
            // se não existir peer com esta chave (K), o peer responsável vai ser aquele com menor chave >= K
        }
    }

    /**
     * Calculates the key of the next entry of the fingertable - ( hash + 2^index) mod 2^m
     *
     * @param hash peer's hash
     * @param index finger table's index to be filled
     * @param m hash size (bits)
     * @return next key's hash
     */
    private String calculateNextKey(BigInteger hash, int index, int m)
    {
        //Exemplo
        // hash = 10, index = 0, m = 7 => 10 + 2^0 = 11
        // hash = 10, index = 3, m = 7 => 10 + 2^3 = 18
        // hash = 125, index = 3, m = 7 => 125 + 2^3 = 133 mod 2^7 = 8

        BigInteger add = new BigInteger(String.valueOf((int) Math.pow(2, index)));
        BigInteger mod =  new BigInteger(String.valueOf((int) Math.pow(2, m)));

        BigInteger res = hash.add(add).mod(mod);
        return res.toString(16);
    }

    /*
    //NOT TESTED !!
    public static String searchSuccessor(String senderKey, String senderPort)
    {
        String message = null;
        String parameters[];

        long senderKeyLong = Long.parseLong(senderKey, 16);


        /*Se o node S que enviou a mensagem, e sendo N o node que a recebeu, se encontrar em [N,sucessor(N)]
        então sucessor(S) = sucessor(N)*/
        /*if(senderKeyLong > Long.parseLong(peerHash,16)) {
            if (senderKeyLong < Long.parseLong(fingerTable.get(0),16)) {
                parameters = new String[]{fingerTable.get(0)};
                message = Auxiliary.addHeader("SUCCESSOR", parameters);
            }
        }

        /*Se a condição anterior não acontecer, então vai-se procurar o predecessor com a chava mais alta,
          mas que seja menor que a chave do node que enviou a mensagem*/
        /*else
        {
            for(int i = ChordInfo.fingerTable.size() - 1; i >= 0; i--)
            {
                String key = fingerTable.get(i);
                //TODO testar isto quando se preencher a finger table no stabilize
                if(Long.parseLong(fingerTable.get(0),16) > Long.parseLong(peerHash,16))
                    if (Long.parseLong(fingerTable.get(0),16) < senderKeyLong) {
                        parameters = new String[]{senderKey, senderPort};
                        return Auxiliary.addHeader("GETSUCCESSOR", parameters);
                    }
            }

            parameters = new String[]{peerHash};
            message = Auxiliary.addHeader("SUCCESSOR", parameters);
        }

        return message;
    }
    */

    public static void lookup(BigInteger keyHash, ConnectionInfo senderInfo) throws UnknownHostException {

        if((keyHash.compareTo(peerHash) == -1 || keyHash.compareTo(peerHash) == 0) && (keyHash.compareTo(predecessor) == 1)) {
            Auxiliary.sendMessage("HELLO, IT'S ME : " + InetAddress.getLocalHost().getHostAddress() + " - " + Peer.port, senderInfo.getIp(), senderInfo.getPort());
        } else {
            searchClosestHash(keyHash, senderInfo);
        }

        //TODO: check if it faster clockwise or reverse clockwise when hashkey is smaller than own key
    }

    private static void searchClosestHash(BigInteger keyHash, ConnectionInfo senderInfo) throws UnknownHostException {

        printFingerTable();

        boolean found = false;
        BigInteger n = null;

        for(BigInteger i: fingerTable.keySet()){
            System.out.println(i);
            System.out.println(keyHash);
            if(i.compareTo(keyHash) == 1) {
                Auxiliary.sendMessage("HELLO, IT'S ME : " + InetAddress.getLocalHost().getHostAddress() + " - " + Peer.port, fingerTable.get(n).getIp(), fingerTable.get(n).getPort());
                found = true;
                break;
            }
            n = i;
        }

        if(!found)
            Auxiliary.sendMessage("LOOKUP " + keyHash + " " + InetAddress.getLocalHost().getHostAddress() + " " + Peer.port, fingerTable.get(n).getIp(), fingerTable.get(n).getPort());
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
