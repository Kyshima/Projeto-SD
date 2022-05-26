package edu.ufp.inf.sd.rmi.project.client;

import edu.ufp.inf.sd.rmi.project.server.GameSessionRI;
import edu.ufp.inf.sd.rmi.project.server.State;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ObserverImpl extends UnicastRemoteObject implements ObserverRI {
    private String id;
    private State lastObserverState;

    public String getId() {
        return id;
    }

    public State getLastObserverState() {
        return lastObserverState;
    }

    protected FroggerClient froggerClient;
    protected GameSessionRI gameSessionRI;

    public ObserverImpl(String id, FroggerClient f, GameSessionRI gameSessionRI) throws RemoteException {
        super();
        this.id = id;
        this.froggerClient = f;
        this.gameSessionRI = gameSessionRI;
    }

    @Override
    public void update() throws RemoteException {

    }
}
