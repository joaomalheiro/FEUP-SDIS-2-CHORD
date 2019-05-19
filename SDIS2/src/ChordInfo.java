import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

public class ChordInfo implements Runnable{
    private static int mBytes = 1; //hash size in bytes
    private static String peerHash;
    private static String predecessor = null;
    private static ArrayList<String> fingerTable = new ArrayList<> (mBytes * 8);

    ChordInfo()
    {
        setChord();
    }

    /**
     * Calls functions to create hash and fill finger table
     */
    private void setChord()
    {
        int mBytes = 1; //hash size in bytes
        ArrayList<String> fingerTable = new ArrayList<> (mBytes * 8);

        ChordInfo.peerHash = getPeerHash(mBytes);

        System.out.println("Peer hash = " + Integer.parseInt(ChordInfo.peerHash,16) +" (Hexadecimal value = " + ChordInfo.peerHash + ")");

        if(Peer.referencedPort != 0)
            Peer.executor.submit(new SucessorRequest(Peer.referencedPort, Peer.port, ChordInfo.peerHash));

        getFingerTable(fingerTable, mBytes * 8, Peer.referencedPort);

        for(int i = 0; i < fingerTable.size(); i++)
            System.out.println("Index #" + i + " = " + fingerTable.get(i));
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
     * Sends message to known peer, asking for successor
     *
     * @param referencedPort port of existing peer that was passed by argument
     */
    private void getSuccessor(int referencedPort, String key) {
        /*SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        SSLSocket clientSocket = null;
        try {
            clientSocket = (SSLSocket) socketFactory.createSocket(InetAddress.getByName("localhost"), referencedPort);
            clientSocket.startHandshake();
            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
            String sentence = "GETSUCCESSOR 1.0 " + this.senderId + " " + key + " \r\n\r\n";
            outToServer.writeBytes(sentence + 'n');
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    /**
     * Fills initial finger table
     *
     * @param fingerTable struct to be filled
     * @param numberEntries finger table size (hash size in bits)
     * @param referencedPort port of existing peer that was passed by argument
     */

    private void getFingerTable(ArrayList<String> fingerTable, int numberEntries, int referencedPort) {
        BigInteger hashBI = new BigInteger(ChordInfo.peerHash, 16);

        for(int i = 0; i < numberEntries; i++)
        {
            if(referencedPort == 0)
            {
                fingerTable.add(ChordInfo.peerHash);
                continue;
            }

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

    //NOT TESTED !!
    public String searchSuccessor(String senderHash)
    {
        String message = null;

        //Ainda só há um node no sistema, por isso o predecessor e o sucessor serão o peer que enviou a mensagem e vice-versa
        if(ChordInfo.predecessor == null)
        {
            ChordInfo.predecessor = senderHash;
            for(String finger: fingerTable)
                finger = senderHash;

            message = "SUCCESSOR 1.0 " + " " + ChordInfo.peerHash + " \r\n\r\n";
        }

        else
        {   /*Se o node S que enviou a mensagem, e sendo N o node que a recebeu, se encontrar em [N,sucessor(N)]
              então sucessor(S) = sucessor(N)*/
            if(Integer.parseInt(senderHash) > Integer.parseInt(ChordInfo.peerHash))
                if(Integer.parseInt(senderHash) < Integer.parseInt(ChordInfo.fingerTable.get(0)))
                {
                    ChordInfo.predecessor = senderHash;
                    message = "SUCCESSOR 1.0 " + " " + ChordInfo.fingerTable.get(0) + " \r\n\r\n";
                }

            /*Se a condição anterior não acontecer, então vai-se procurar o predecessor com a chava mais alta,
              mas que seja menor que a chave node que enviou a mensagem*/
            else
            {
                for(int i = ChordInfo.fingerTable.size() - 1; i >= 0; i--)
                {
                    String key = fingerTable.get(i);
                    if(Integer.parseInt(key) < Integer.parseInt(senderHash))
                    {
                        //TODO
                    }

                }
            }
        }

        return message;
    }


    @Override
    public void run() {

    }
}
