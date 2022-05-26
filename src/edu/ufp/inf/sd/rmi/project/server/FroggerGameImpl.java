package edu.ufp.inf.sd.rmi.project.server;

import edu.ufp.inf.sd.rmi.project.client.ObserverRI;
import frogger.Main;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;


public class FroggerGameImpl extends UnicastRemoteObject implements FroggerGameRI {

    private State subjectState;
    private final ArrayList<ObserverRI> observers = new ArrayList();

    public FroggerGameImpl() throws RemoteException {
        super();
    }

    @Override
    public void attach(ObserverRI observerRI) throws RemoteException {
        if (!observers.contains(observerRI))
            observers.add(observerRI);
    }

    @Override
    public void detach(ObserverRI observerRI) throws RemoteException {
        observers.remove(observerRI);
    }

    @Override
    public State getState() throws RemoteException {
        return subjectState;
    }

    @Override
    public void setState(State state) throws RemoteException {
        this.subjectState = state;
        notifyAllObservers();
    }

    @Override
    public void startGame() throws RemoteException {
        Main f = new Main();
        f.run();
    }


    public void notifyAllObservers() throws RemoteException {
        for (ObserverRI o : observers) {
            o.update();
        }
    }
}
