package edu.ufp.inf.sd.rmi.project.client;

import edu.ufp.inf.sd.rmi.project.server.FroggerGameImpl;
import edu.ufp.inf.sd.rmi.project.server.FroggerGameRI;
import edu.ufp.inf.sd.rmi.project.server.GameSessionRI;
import edu.ufp.inf.sd.rmi.project.server.State;
import frogger.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.UnmarshalException;
import java.util.ArrayList;

public class Menu extends JFrame implements ActionListener {
    protected static Menu frame;
    public static FroggerClient fg;

    public void main(FroggerClient froggerClient) {
        fg = froggerClient;
        frame = new Menu();
        frame.setTitle("Menu");
        frame.setVisible(true);
        frame.setBounds(10, 10, 370, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
    }

    Container container = getContentPane();
    ArrayList<JLabel> GamesText = new ArrayList<JLabel>();
    ArrayList<JButton> GamesButton = new ArrayList<JButton>();

    Menu() {
        setLayoutManager();
        setLocationAndSizeStarter();
        addComponentsToContainerStarter();
        addActionEventStart();
    }

    public void setLayoutManager() {
        container.setLayout(null);
    }

    public void setLocationAndSizeStarter() {
        for(int i = 0; i<GamesText.size(); i++){
            GamesText.get(i).setBounds(50, 150 + (i-1)*70, 189, 30);
            GamesButton.get(i).setBounds(150, 150 + (i-1)*70, 81, 30);
        }
    }

    public void addComponentsToContainerStarter() {
        for(int i = 0; i<GamesText.size(); i++){
            container.add(GamesButton.get(i));
        }
    }

    public void addActionEventStart() {
        for(int i = 0; i<GamesText.size(); i++) {
            GamesButton.get(i).addActionListener(this);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (int i = 0; i < GamesText.size(); i++) {
            if (e.getSource() == GamesButton.get(i)) {
                for(int j = 0; j< FroggerGameImpl.observers.size();j++){
                    if (FroggerGameImpl.observers.get(j).getId().equals(Integer.toString(i))){
                        try {
                            System.out.println("old");
                            Main m = new Main();
                            FroggerGameImpl.observers.get(j).update();
                            m.run();
                        } catch (RemoteException ex) {
                            throw new RuntimeException(ex);
                        }
                    } else {
                        try {
                            System.out.println("novo");
                            FroggerGameRI r = new FroggerGameImpl();
                            Main m = new Main();
                            ObserverImpl ob = new ObserverImpl(Integer.toString(i),m,r);
                            m.run();
                        } catch (RemoteException ex) {
                            throw new RuntimeException(ex);
                        }

                    }
                }
            }
        }
    }
}
