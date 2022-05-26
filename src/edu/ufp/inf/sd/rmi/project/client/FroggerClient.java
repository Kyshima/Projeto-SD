package edu.ufp.inf.sd.rmi.project.client;

import edu.ufp.inf.sd.rmi.project.server.FroggerGameRI;
import edu.ufp.inf.sd.rmi.project.server.GameSessionImpl;
import edu.ufp.inf.sd.rmi.project.server.GameSessionRI;
import edu.ufp.inf.sd.rmi.util.rmisetup.SetupContextRMI;
import frogger.Goal;
import frogger.Main;
import frogger.MovingEntityFactory;
import jig.engine.util.Vector2D;

import java.net.SocketException;
import java.rmi.*;
import java.rmi.registry.Registry;
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
    public static ObserverImpl observer;

    public static void main(String[] args) {
        if (args != null && args.length < 2) {
            System.err.println("usage: java [options] edu.ufp.sd.inf.rmi._01_helloworld.server.HelloWorldClient <rmi_registry_ip> <rmi_registry_port> <service_name>");
            System.exit(-1);
        } else {
            FroggerClient hwc = new FroggerClient(args);
            initObserver(args);
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
    private static void initObserver(String args[]) {
        try {
            observer = new ObserverImpl();
            /*String username=this.jTextFieldUsername.getText();
            //observer = new ObserverImpl(username, this, args);
            observer=new ObserverImpl(username, this, this.subjectRI);*/
        } catch (Exception e) {
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

    /*private Remote lookupServiceGF() {
        try {
            //Get proxy MAIL_TO_ADDR rmiregistry
            Registry registry = contextRMI.getRegistry();
            //Lookup service on rmiregistry and wait for calls
            if (registry != null) {
                //Get service url (including servicename)
                String serviceUrl = contextRMI.getServicesUrl(0);
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "going MAIL_TO_ADDR lookup service @ {0}", serviceUrl);

                //============ Get proxy MAIL_TO_ADDR HelloWorld service ============
                gameFactoryRI = (GameFactoryRI) registry.lookup(serviceUrl);
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "registry not bound (check IPs). :(");
                //registry = LocateRegistry.createRegistry(1099);
            }
        } catch (RemoteException | NotBoundException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
        return gameFactoryRI;
    }*/
    
    private void playService() {
        StarterFrame.main();


        //============ Call HelloWorld remote service ============
        //guest ufp
        /*try {
            GameSessionRI gameSession = this.gameFactoryRI.login(u, p);
            if (gameSession != null)
            {
                System.out.println("Usuario " + u + " a entrar com sucesso!");
                gameSession.criarJogo();
            }

            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "going MAIL_TO_ADDR finish, bye. ;)");
        } catch (Exception ex) {
            if (ex instanceof ConnectException){
                System.out.println("Username/Password Errado");
            }else if(ex instanceof UnmarshalException){
                System.out.println("Jogo Fechado com Sucesso");
            }else Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }*/
    }
}
