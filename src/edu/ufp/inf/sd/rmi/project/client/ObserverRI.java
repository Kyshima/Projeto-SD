package edu.ufp.inf.sd.rmi.project.client;

import edu.ufp.inf.sd.rmi.project.server.FroggerGameRI;
import edu.ufp.inf.sd.rmi.project.server.State;
import frogger.Main;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ObserverRI extends Remote {

    //public ObserverImpl(String id, Main m, FroggerGameRI frogger) throws RemoteException;

    void update() throws RemoteException;

    public String getId() throws RemoteException;

    public State getLastObserverState() throws RemoteException;

    public int getGame() throws RemoteException;

    public void setGame(int game) throws RemoteException;
}
