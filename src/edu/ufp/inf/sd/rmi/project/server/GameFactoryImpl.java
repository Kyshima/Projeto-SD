package edu.ufp.inf.sd.rmi.project.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;


public class GameFactoryImpl extends UnicastRemoteObject implements GameFactoryRI {
    private final DBMockup dbMockup = new DBMockup();
    private HashMap<String, GameSessionImpl> session = new HashMap<String, GameSessionImpl>();

    public GameFactoryImpl() throws RemoteException {
        super();
    }

    public DBMockup getDbMockup() {
        return dbMockup;
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
