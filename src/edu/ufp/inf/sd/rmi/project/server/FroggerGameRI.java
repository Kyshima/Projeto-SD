package edu.ufp.inf.sd.rmi.project.server;

import edu.ufp.inf.sd.rmi.project.client.ObserverRI;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;


public interface FroggerGameRI extends Remote {
    void setObservers(ArrayList<ObserverRI> observers) throws RemoteException;

    void attach(ObserverRI observerRI) throws RemoteException;

    void detach(ObserverRI observerRI) throws RemoteException;

    State getState() throws RemoteException;

    void setState(State state) throws RemoteException;

    public void notifyAllObservers() throws RemoteException;

    void startGame() throws RemoteException;

    public GameSessionRI login(String usr, String pwd) throws RemoteException;
    public boolean register (String usr,String pwd) throws RemoteException;

    public void mainServer(ObserverRI observer) throws RemoteException;

    public ArrayList<ObserverRI> getObservers() throws RemoteException;
}
