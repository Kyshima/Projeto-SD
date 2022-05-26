package edu.ufp.inf.sd.rmi.project.server;

import edu.ufp.inf.sd.rmi.project.client.ObserverRI;
import frogger.Main;
import frogger.MainServerSide;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;


public class FroggerGameImpl extends UnicastRemoteObject implements FroggerGameRI {

    private State subjectState;
    private final DBMockup dbMockup = new DBMockup();
    private HashMap<String, GameSessionImpl> session = new HashMap<String, GameSessionImpl>();
    private final ArrayList<ObserverRI> observers = new ArrayList();

    public FroggerGameImpl() throws RemoteException {
        super();
    }

    public DBMockup getDbMockup() {
        return dbMockup;
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
        MainServerSide s = new MainServerSide();
        s.run();
        Main f = new Main();
        f.run();
    }


    public void notifyAllObservers() throws RemoteException {
        for (ObserverRI o : observers) {
            o.update();
        }
    }

    @Override
    public GameSessionRI login(String usr, String pwd) throws RemoteException {
        if (dbMockup.exists(usr, pwd))
        {
            GameSessionImpl gameSession = new GameSessionImpl(this, usr);
            session.put(usr, gameSession);
            return gameSession;
        }
        return null;
    }

    @Override
    public boolean register(String usr, String pwd) throws RemoteException {
        if (!dbMockup.exists(usr, pwd)){
            dbMockup.register(usr,pwd);
            return true;
        } else return false;
    }
}
