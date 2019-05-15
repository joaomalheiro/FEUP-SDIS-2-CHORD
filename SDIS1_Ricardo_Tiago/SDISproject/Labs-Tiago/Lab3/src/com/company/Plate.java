package com.company;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Plate extends Remote{
    String operation(String str)throws RemoteException;
}
