package edu.ufp.inf.sd.rmi.project.server;

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface GameFactoryRI extends Remote {
    public GameSessionRI login(String usr, String pwd) throws RemoteException;
    public boolean register (String usr,String pwd) throws RemoteException;
}
