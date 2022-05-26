package edu.ufp.inf.sd.rmi.project.server;

import edu.ufp.inf.sd.rmi.project.client.ObserverRI;

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface FroggerGameRI extends Remote {
    void attach(ObserverRI observerRI) throws RemoteException;

    void detach(ObserverRI observerRI) throws RemoteException;

    State getState() throws RemoteException;

    void setState(State state) throws RemoteException;

    void startGame() throws RemoteException;
}
