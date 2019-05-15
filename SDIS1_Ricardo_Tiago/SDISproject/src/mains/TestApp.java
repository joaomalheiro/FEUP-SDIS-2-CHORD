package mains;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class TestApp {

    public static void main(String[] args) {

        if(args.length < 2)
        {
            System.err.println("Correct usage: java TestApp <peer_ap> <sub_protocol> <opnd_1> <opnd_2>");
            System.exit(-1);
        }

        String accessPoint = args[0];
        String operation = args[1];

        PeerInterface pi = null;

        try {
            Registry registry = LocateRegistry.getRegistry("localhost");
            pi = (PeerInterface) registry.lookup(accessPoint);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }


        String file_path;

        switch(operation)
        {
            case "BACKUP": case "BACKUPENH":
                if(args.length != 4)
                {
                    System.err.println("BACKUP correct usage: java TestApp <peer_ap> BACKUP <file path> <replication degree>");
                    System.exit(-1);
                }

                file_path = args[2];
                int rd = Integer.parseInt(args[3]);
                try {
                  if(operation.equals("BACKUP"))
                    pi.backup(file_path, rd, false);
                  else
                    pi.backup(file_path, rd, true);
                } catch (RemoteException e) {
                    e.printStackTrace();
                    System.exit(-1);
                }
                break;

            case "RESTORE": case "RESTOREENH":
                if(args.length != 3)
                {
                    System.err.println("RESTORE correct usage: java TestApp <peer_ap> RESTORE <file path>");
                    System.exit(-1);
                }

                file_path = args[2];

                try {
                    if(operation.equals("RESTORE"))
                      pi.restore(file_path, false);
                    else
                      pi.restore(file_path, true);
                } catch (RemoteException e) {
                    e.printStackTrace();
                    System.exit(-1);
                }
                break;

            case "DELETE": case "DELETEENH":
                if(args.length != 3)
                {
                    System.err.println("DELETE correct usage: java TestApp <peer_ap> DELETE <file path>");
                    System.exit(-1);
                }
                file_path = args[2];

                try {
                  if(operation.equals("DELETE"))
                    pi.delete(file_path, false);
                  else
                    pi.delete(file_path, true);
                } catch (RemoteException e) {
                    e.printStackTrace();
                    System.exit(-1);
                }
                break;

            case "RECLAIM":
                if(args.length != 3)
                {
                    System.err.println("RECLAIM correct usage: java TestApp <peer_ap> RECLAIM <maximum amount of disk space>");
                    System.exit(-1);
                }
                long maximum_space = Long.parseLong(args[2]);

                try {
                    pi.reclaim(maximum_space);
                } catch (RemoteException e) {
                    e.printStackTrace();
                    System.exit(-1);
                }
                break;

            case "STATE":
                if(args.length != 2)
                {
                    System.err.println("STATE correct usage: java TestApp <peer_ap> STATE");
                    System.exit(-1);
                }

                try {
                    String message = pi.state();
                    System.out.print(message);
                } catch (RemoteException e) {
                    e.printStackTrace();
                    System.exit(-1);
                }
                break;
            default:
                System.err.println("Selected operation is not valid");
                System.exit(-1);
        }
    }
}
