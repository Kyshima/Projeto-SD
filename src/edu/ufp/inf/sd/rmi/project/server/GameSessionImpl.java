package edu.ufp.inf.sd.rmi.project.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;


public class GameSessionImpl extends UnicastRemoteObject implements GameSessionRI {
    private FroggerGameRI froggerGame;
    private String user;

    public GameSessionImpl() throws RemoteException {
        super();
    }

    public GameSessionImpl(FroggerGameImpl froggerGame, String user) throws RemoteException
    {
        this.froggerGame = froggerGame;
        this.user = user;
    }

    @Override
    public void listarJogos() throws RemoteException {

    }
}
