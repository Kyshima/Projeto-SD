package edu.ufp.inf.sd.rmi.project.client;

import edu.ufp.inf.sd.rmi.project.server.FroggerGameRI;
import edu.ufp.inf.sd.rmi.project.server.State;
import frogger.MovingEntityFactory;
import froggerServer.MovingEntity;
import jig.engine.physics.AbstractBodyLayer;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ObserverImpl extends UnicastRemoteObject implements ObserverRI {

    public static AbstractBodyLayer<MovingEntity> traffic;
    private State lastObserverState;

    public State getLastObserverState() {
        return lastObserverState;
    }

    protected FroggerGameRI frogger;

    public ObserverImpl(AbstractBodyLayer<MovingEntity> traffic) throws RemoteException {
        super();
    }

    public ObserverImpl() throws RemoteException {
        super();
    }

    public ObserverImpl(FroggerGameRI froggerGameRI) throws RemoteException {
        super();
        this.frogger = froggerGameRI;
        this.frogger.attach(this);
    }

    @Override
    public void update() throws RemoteException {
    }
}
