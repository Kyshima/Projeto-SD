package edu.ufp.inf.sd.rmi.project.server;

import edu.ufp.inf.sd.rmi.project.client.ObserverRI;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


public class FroggerGameImpl extends UnicastRemoteObject implements FroggerGameRI {

    //public static State subjectState;
    public final DBMockup dbMockup = new DBMockup();
    public HashMap<String, GameSessionImpl> session = new HashMap<String, GameSessionImpl>();
    public static ArrayList<ObserverRI> observers = new ArrayList<>();

    public ArrayList<Integer> numGames = new ArrayList<>();

    public int games;

    public DBMockup getDbMockup() {
        return dbMockup;
    }

    public FroggerGameImpl() throws RemoteException {
        super();
    }

    @Override
    public void attach(ObserverRI observerRI) throws RemoteException {
        if(observers.isEmpty()){
            observers.add(0,observerRI);
            observers.add(1,observerRI);
            observers.set(0,observers.get(1));
            observers.remove(1);
        } else if (!observers.contains(observerRI)){


            observers.add(observerRI);
        }
        System.out.print("atachou com games nr: "+observerRI.getGame() + "\t");
            for (ObserverRI o: observers) {
                System.out.print(o.getId()+" "+o.getGame()+"\t");
            }
            System.out.println();
    }

    @Override
    public void detach(ObserverRI observerRI) throws RemoteException {
        observers.remove(observerRI);
    }

    @Override
    public GameSessionRI login(String usr, String pwd) throws RemoteException {
        if (dbMockup.exists(usr, pwd)) {
            if(session.containsKey(usr)) return session.get(usr);
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
    public int mainServer(ObserverRI observer) throws RemoteException {
        this.attach(observer);
        System.out.println(observers.size() + " " + getAllUpdates(observer.getGame()).size() + " " + observer.getGame());

        int n = observer.getGame()-1;

        if(n >= numGames.size() || n < 0){
            //index does not exists
            numGames.add(n,0);
        }else{
            // index exists
            int f = numGames.get(n);
            numGames.set(n,f+1);
        }

        return numGames.get(n);
    }

    @Override
    public ArrayList<ObserverRI> getObservers() throws RemoteException {
        return observers;
    }
    @Override
    public void setObservers(ArrayList<ObserverRI> observers) throws RemoteException {
        FroggerGameImpl.observers = observers;
    }

    @Override
    public void addGames() throws RemoteException{
        games++;
    }

    @Override
    public void removeGames() throws RemoteException{
        games--;
    }

    public int listGames() throws RemoteException{
        return games;
    }

    @Override
    public void update(int game, State s) throws RemoteException {
        for (ObserverRI o : observers) {
            if(o.getGame() == game)
            {
                o.update(s);
            }
        }
    }

    @Override
    public State getUpdate(int game) throws RemoteException {
        for(ObserverRI o : observers) {
            if(o.getGame() == game)
            {
                return o.getLastObserverState();
            }
        }
        return null;
    }

    @Override
    public ArrayList<State> getAllUpdates(int game) throws RemoteException{
        System.out.println("Numero do jogo: " + game);
        ArrayList<State> g= new ArrayList<>();
        for(ObserverRI o : observers) {
            if(o.getGame() == game)
            {
                System.out.println("igualzinho "+o.getGame());
                g.add(o.getLastObserverState());
            } else System.out.println("diferentinho"+o.getGame());
        }
        return g;
    }
}
