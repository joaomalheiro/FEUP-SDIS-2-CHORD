package peer;

import java.rmi.Remote;
import java.rmi.RemoteException;


/**
 *
 */
public interface RMIStub extends Remote {

    /**
     * Interface method to call the Backup Protocol class
     * @param file  file
     * @param replicationDeg    replication degree
     * @throws RemoteException  exception
     */
    void backupProtocol(String file, int replicationDeg) throws RemoteException;

    /**
     * Interface method to call the Restore Protocol class
     * @param file  file
     * @throws RemoteException  exception
     */
    void restoreProtocol(String file) throws RemoteException;

    /**
     * Interface method to call the Delete Protocol class
     * @param file  file
     * @throws RemoteException  exception
     */
    void deleteProtocol(String file) throws RemoteException;

    /**
     * Interface method to call the Reclaim Protocol class
     * @param reservedSpace reserved space
     * @throws RemoteException  exception
     */
    void reclaimProtocol(long reservedSpace) throws RemoteException;
}
