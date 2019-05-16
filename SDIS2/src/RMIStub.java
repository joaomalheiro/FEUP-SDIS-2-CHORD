package peer;

import java.rmi.Remote;
import java.rmi.RemoteException;


/**
 *
 */
public interface RMIStub extends Remote {

    /**
     * Interface method to call the Backup Protocol class
     * @param file
     * @param replicationDeg
     * @throws RemoteException
     */
    void backupProtocol(String file, int replicationDeg) throws RemoteException;

    /**
     * Interface method to call the Restore Protocol class
     * @param file
     * @throws RemoteException
     */
    void restoreProtocol(String file) throws RemoteException;

    /**
     * Interface method to call the Delete Protocol class
     * @param file
     * @throws RemoteException
     */
    void deleteProtocol(String file) throws RemoteException;

    /**
     * Interface method to call the Reclaim Protocol class
     * @param reservedSpace
     * @throws RemoteException
     */
    void reclaimProtocol(int reservedSpace) throws RemoteException;

    String stateProtocol() throws RemoteException;


}
