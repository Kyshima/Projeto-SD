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

    public static int butoes;

    public static void main(FroggerClient froggerClient) throws RemoteException {
        fg = froggerClient;
        butoes = FroggerClient.froggerGameRI.listGames()-1;

        for (int i = 0; i < butoes; i++) {
            GamesText.add(i, new JLabel(FroggerClient.froggerGameRI.getObservers().get(i).getGame() + " Jogo"));
            GamesButton.add(i, new JButton(FroggerClient.froggerGameRI.getObservers().get(i).getGame() + " Jogo"));
        }

        frame = new Menu();
        frame.setTitle("Menu");
        frame.setVisible(true);
        frame.setBounds(10, 10, 370, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
    }

    Container container = getContentPane();
    static ArrayList<JLabel> GamesText = new ArrayList<JLabel>();
    static ArrayList<JButton> GamesButton = new ArrayList<JButton>();

    JButton plus = new JButton("+");

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
        for (int i = 0; i < GamesText.size(); i++) {
            GamesText.get(i).setBounds(50, 150 + (i - 1) * 70, 189, 30);
            GamesButton.get(i).setBounds(150, 150 + (i - 1) * 70, 81, 30);
        }
        plus.setBounds(270, 515, 50, 30);
    }

    public void addComponentsToContainerStarter() {
        for (int i = 0; i < GamesText.size(); i++) {
            container.add(GamesButton.get(i));
        }
        container.add(plus);
    }

    public void addActionEventStart() {
        for (int i = 0; i < GamesText.size(); i++) {
            GamesButton.get(i).addActionListener(this);
        }
        plus.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (int i = 0; i < GamesText.size(); i++) {
            if (e.getSource() == GamesButton.get(i)) {
                for (int j = 0; j < FroggerGameImpl.observers.size(); j++) {
                    try {
                        if (FroggerGameImpl.observers.get(j).getId().equals(Integer.toString(i))) {
                            setVisible(false);
                            dispose();
                            fg.create = i;
                            fg.f = true;
                            FroggerClient.froggerGameRI.getObservers().get(j).setGame(j);
                            //FroggerClient.m.gameNum = j;
                        }
                    } catch (RemoteException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        }

            if (e.getSource() == plus) {
                setVisible(false);
                dispose();
                fg.create = -1;
                fg.f = true;
                try {
                    FroggerClient.froggerGameRI.addGames();
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
}
