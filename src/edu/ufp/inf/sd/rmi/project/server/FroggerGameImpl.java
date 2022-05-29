package edu.ufp.inf.sd.rmi.project.server;

import edu.ufp.inf.sd.rmi.project.client.ObserverRI;
import frogger.Main;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;


public class FroggerGameImpl extends UnicastRemoteObject implements FroggerGameRI {

    public static State subjectState;
    public final DBMockup dbMockup = new DBMockup();
    public HashMap<String, GameSessionImpl> session = new HashMap<String, GameSessionImpl>();
    public static ArrayList<ObserverRI> observers = new ArrayList();

    public DBMockup getDbMockup() {
        return dbMockup;
    }

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
        subjectState = state;
        notifyAllObservers();
    }

    @Override
    public void startGame() throws RemoteException {
        Main s = new Main();
        s.run();
    }


    public void notifyAllObservers() throws RemoteException {
        for (ObserverRI o : observers) {
            o.update();
        }
    }

    @Override
    public GameSessionRI login(String usr, String pwd) throws RemoteException {
        if (dbMockup.exists(usr, pwd)) {
            GameSessionImpl gameSession = new GameSessionImpl(this, usr);
            session.put(usr, gameSession);
            return gameSession;
        }
        return null;
    }

    @Override
    public boolean register(String usr, String pwd) throws RemoteException {
        if (!dbMockup.exists(usr, pwd)) {
            dbMockup.register(usr, pwd);
            return true;
        } else return false;
    }

    @Override
    public void mainServer(ObserverRI observer) throws RemoteException {
        this.attach(observer);
    }

    @Override
    public ArrayList<ObserverRI> getObservers() throws RemoteException {
        return observers;
    }
    @Override
    public void setObservers(ArrayList<ObserverRI> observers) throws RemoteException {
        FroggerGameImpl.observers = observers;
    }
}
