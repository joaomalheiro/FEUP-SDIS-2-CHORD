


public class Peer {

    private static String peerId;
    private static String peerAcessPoint;
    private static String protocolVersion;

    //private static Storage storage;

    public static void main(String args[]) throws IOException, ClassNotFoundException {

        initAtributes(args);

        //RMIStub stub = null;

        Peer peer = new Peer();
        //stub = (RMIStub) UnicastRemoteObject.exportObject(peer, 0);

        /*try {
            Registry reg = LocateRegistry.getRegistry();
            reg.rebind(peerAcessPoint, stub);

            System.out.println("Peer connected through getRegistry");
        } catch (Exception e) {
            Registry reg = LocateRegistry.createRegistry(1099);
            reg.rebind(peerAcessPoint, stub);

            System.out.println("Peer connected through createRegistry");
        }
        */
    }

    private static void initAtributes(String[] args) throws IOException, ClassNotFoundException {
        protocolVersion = args[0];
        peerId = (args[1]);
        peerAcessPoint = args[2];

        //storage = new Storage(1000000);
    }


}