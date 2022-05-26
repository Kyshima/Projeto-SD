package edu.ufp.inf.sd.rmi.project.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import frogger.Main;


public class GameSessionImpl extends UnicastRemoteObject implements GameSessionRI {
    private GameFactoryImpl gameFactory;
    private String user;

    public GameSessionImpl() throws RemoteException {
        super();
    }

    public GameSessionImpl(GameFactoryImpl gameFactory, String user) throws RemoteException
    {
        this.gameFactory = gameFactory;
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
