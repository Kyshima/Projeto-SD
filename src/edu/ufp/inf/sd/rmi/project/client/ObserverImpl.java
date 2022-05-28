package edu.ufp.inf.sd.rmi.project.client;

import edu.ufp.inf.sd.rmi.project.server.FroggerGameImpl;
import edu.ufp.inf.sd.rmi.project.server.FroggerGameRI;
import edu.ufp.inf.sd.rmi.project.server.State;
import frogger.Main;
import frogger.MovingEntityFactory;
import froggerServer.MovingEntity;
import jig.engine.physics.AbstractBodyLayer;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ObserverImpl extends UnicastRemoteObject implements ObserverRI {

    //public static AbstractBodyLayer<MovingEntity> traffic;
    public String id;
    public State lastObserverState;

    public FroggerGameRI frogger;

    public Main m;


    public String getId() {
        return id;
    }

    public State getLastObserverState() {
        return lastObserverState;
    }

    public ObserverImpl(String id, Main m, FroggerGameRI frogger) throws RemoteException {
        super();
        this.id = id;
        this.m = m;
        this.frogger = frogger;
        this.frogger.attach(this);
    }

    @Override
    public void update() throws RemoteException {
        lastObserverState = FroggerGameImpl.subjectState;
        StarterFrame.updateMoving();
    }
}
