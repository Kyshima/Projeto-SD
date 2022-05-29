package edu.ufp.inf.sd.rmi.project.client;

import edu.ufp.inf.sd.rmi.project.server.*;
import edu.ufp.inf.sd.rmi.util.rmisetup.SetupContextRMI;
import frogger.*;
import jdk.nashorn.internal.runtime.Debug;
import jig.engine.physics.AbstractBodyLayer;
import jig.engine.util.Vector2D;

import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.rmi.*;
import java.rmi.registry.Registry;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Scanner;

/**
 * <p>
 * Title: Projecto SD</p>
 * <p>
 * Description: Projecto apoio aulas SD</p>
 * <p>
 * Copyright: Copyright (c) 2017</p>
 * <p>
 * Company: UFP </p>
 *
 * @author Rui S. Moreira
 * @version 3.0
 */
public class FroggerClient {

    private SetupContextRMI contextRMI;
    //public static GameFactoryRI gameFactoryRI;
    public static FroggerGameRI froggerGameRI;
    public static FroggerGameImpl froggerGame;

    static {
        try {
            froggerGameRI = new FroggerGameImpl();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean f = false;
    public int create = -1;
    public static Main g;
    public static ObserverImpl observer;


    public static void main(String[] args) throws InterruptedException, RemoteException {
        if (args != null && args.length < 2) {
            System.err.println("usage: java [options] edu.ufp.sd.inf.rmi._01_helloworld.server.HelloWorldClient <rmi_registry_ip> <rmi_registry_port> <service_name>");
            System.exit(-1);
        } else {
            FroggerClient hwc = new FroggerClient(args);
            //initObserver(args);
            hwc.playService();
        }
    }

    public FroggerClient(String args[]) {
        try {
            //List ans set args
            SetupContextRMI.printArgs(this.getClass().getName(), args);
            String registryIP = args[0];
            String registryPort = args[1];
            String serviceName = args[2];
            //Create a context for RMI setup
            contextRMI = new SetupContextRMI(this.getClass(), registryIP, registryPort, new String[]{serviceName});
            //gameFactoryRI=(GameFactoryRI)lookupServiceGF();
            froggerGameRI=(FroggerGameRI)lookupServiceFG();
        } catch (RemoteException e) {
            Logger.getLogger(FroggerClient.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private Remote lookupServiceFG() {
        try {
            //Get proxy MAIL_TO_ADDR rmiregistry
            Registry registry = contextRMI.getRegistry();
            //Lookup service on rmiregistry and wait for calls
            if (registry != null) {
                //Get service url (including servicename)
                String serviceUrl = contextRMI.getServicesUrl(0);
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "going MAIL_TO_ADDR lookup service @ {0}", serviceUrl);

                //============ Get proxy MAIL_TO_ADDR HelloWorld service ============
                froggerGameRI = (FroggerGameRI) registry.lookup(serviceUrl);
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "registry not bound (check IPs). :(");
                //registry = LocateRegistry.createRegistry(1099);
            }
        } catch (RemoteException | NotBoundException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
        return froggerGameRI;
    }

    private void playService() throws InterruptedException, RemoteException {
        froggerGame = new FroggerGameImpl();
        froggerGame.setObservers(froggerGameRI.getObservers());
        StarterFrame.main(this, froggerGame);
        while(!f){
            Thread.sleep(500);
        }

        if(create != -1){
            System.out.println("old");
            Main m = new Main();
            ObserverImpl ob = new ObserverImpl(Integer.toString(FroggerGameImpl.observers.size() + 1), m, froggerGame);
            FroggerGameImpl.observers.get(create).update();
            froggerGameRI.mainServer(ob);
            m.run();
            State s = froggerGame.getState();
            m.setMovingObjectsLayer(s.getTraffic());

        }else{
            System.out.println("novo");
            Main m = new Main();

            ObserverImpl ob = new ObserverImpl(Integer.toString(FroggerGameImpl.observers.size() + 1), m, froggerGame);

            //FroggerGameImpl.observers.add(ob);
            froggerGameRI.mainServer(ob);
            froggerGame.setObservers(froggerGameRI.getObservers());
            System.out.println(FroggerGameImpl.observers.size());
            m.run();
            State s = new State(m.getMovingObjectsLayer());
            froggerGame.setState(s);
        }
        /*Main g = new Main();
        g.run();*/
    }

    public static void initObserver(String args[]) {
        try {
            observer = new ObserverImpl("1", g, froggerGameRI);
        } catch (Exception e) {
            Logger.getLogger(FroggerClient.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public static void updateMoving() {
        Main.movingObjectsLayer = State.traffic;
    }
}
