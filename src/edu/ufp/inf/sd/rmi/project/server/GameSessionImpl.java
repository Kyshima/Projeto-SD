package edu.ufp.inf.sd.rmi.project.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import frogger.Frogger;
import frogger.Main;


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
    public FroggerGameRI criarJogo() throws RemoteException {
        FroggerGameImpl froggerGame = new FroggerGameImpl();
        //froggerGame.attach();
        froggerGame.startGame();
        return froggerGame;
    }

    @Override
    public void listarJogos() throws RemoteException {

    }
}
