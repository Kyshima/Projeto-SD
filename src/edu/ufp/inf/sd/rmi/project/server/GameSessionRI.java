package edu.ufp.inf.sd.rmi.project.server;

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface GameSessionRI extends Remote {
    public void criarJogo() throws RemoteException;
    public void listarJogos() throws RemoteException;
}
