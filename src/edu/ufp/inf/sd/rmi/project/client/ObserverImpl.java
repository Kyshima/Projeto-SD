package edu.ufp.inf.sd.rmi.project.client;

import edu.ufp.inf.sd.rmi.project.server.FroggerGameRI;
import edu.ufp.inf.sd.rmi.project.server.GameSessionRI;
import edu.ufp.inf.sd.rmi.project.server.State;
import frogger.Main;
import frogger.MovingEntity;
import frogger.MovingEntityFactory;
import jig.engine.util.Vector2D;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class ObserverImpl extends UnicastRemoteObject implements ObserverRI {

    public MovingEntityFactory[] moving;
    private State lastObserverState;

    public State getLastObserverState() {
        return lastObserverState;
    }

    protected FroggerGameRI frogger;

    public ObserverImpl() throws RemoteException {
        super();
    }

    public ObserverImpl(MovingEntityFactory[] moving) throws RemoteException {
        super();
        this.moving = moving;
    }

    public ObserverImpl(FroggerGameRI froggerGameRI) throws RemoteException {
        super();
        this.frogger = froggerGameRI;
        this.frogger.attach(this);
    }

    @Override
    public void update() throws RemoteException {
        this.lastObserverState = frogger.getState();
    }
}
