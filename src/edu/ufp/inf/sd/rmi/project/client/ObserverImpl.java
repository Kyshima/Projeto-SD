package edu.ufp.inf.sd.rmi.project.client;

import edu.ufp.inf.sd.rmi.project.server.FroggerGameImpl;
import edu.ufp.inf.sd.rmi.project.server.FroggerGameRI;
import edu.ufp.inf.sd.rmi.project.server.State;
import frogger.Main;
import frogger.MovingEntityFactory;
import jig.engine.physics.AbstractBodyLayer;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ObserverImpl extends UnicastRemoteObject implements ObserverRI {

    public String id;
    public int game;
    public State lastObserverState;
    public FroggerGameRI frogger;
    public Main m;

    public ObserverImpl(String id, Main m, FroggerGameRI frogger, int game) throws RemoteException {
        super();
        this.id = id;
        this.m = m;
        this.frogger = frogger;
        this.game = game;
        this.lastObserverState = getLastObserverState();
    }

    //public static AbstractBodyLayer<MovingEntity> traffic;

    public String getId() throws RemoteException { return id; }

    public State getLastObserverState() throws RemoteException { return lastObserverState; }

    @Override
    public void update(State s) throws RemoteException { lastObserverState = s; }

    public int getGame() throws RemoteException { return game; }

    public void setGame(int game) throws RemoteException { this.game = game; }
}
