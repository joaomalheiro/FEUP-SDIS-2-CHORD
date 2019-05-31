package testapp;

import peer.RMIStub;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class TestApp {

    public static void main(String args[]) throws RemoteException, NotBoundException {

        String peerAcessPoint = args[0];
        String protocol = args[1];

        String operand1 = null;
        int operand2 = 1;
        long aux = 100;

        Registry registry = LocateRegistry.getRegistry("localhost");
        RMIStub stub = (RMIStub) registry.lookup(peerAcessPoint);

        System.out.println("Initiation Peer : " + peerAcessPoint + "\n" + "Protocol : " + protocol);

        switch (protocol){
            case "BACKUP":
                operand1 = args[2];
                try {
                    operand2 = Integer.parseInt(args[3]);
                    if(operand2 <= 0 || operand2 >= 10)
                        throw new Exception("rd amount");
                } catch (Exception e) {
                    System.err.println("rd must be an integer from 1 to 9");
                    System.exit(-1);
                }
                stub.backupProtocol(operand1, operand2);
                break;
            case "RESTORE":
                operand1 = args[2];
                stub.restoreProtocol(operand1);
                break;
            case "DELETE":
                operand1 = args[2];
                stub.deleteProtocol(operand1);
                break;
            case "RECLAIM":
                operand1 = args[2];
                stub.reclaimProtocol(Integer.parseInt(operand1));
                break;
        }
    }

}
