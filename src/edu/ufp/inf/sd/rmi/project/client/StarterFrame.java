package edu.ufp.inf.sd.rmi.project.client;

import edu.ufp.inf.sd.rmi.project.server.GameSessionRI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.UnmarshalException;

public class StarterFrame extends JFrame implements ActionListener {
    protected static StarterFrame frame;

    public static void main() {
        frame = new StarterFrame();
        frame.setTitle("Start");
        frame.setVisible(true);
        frame.setBounds(10, 10, 370, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
    }

    Container container = getContentPane();
    JLabel emailLabel = new JLabel("EMAIL");
    JLabel passwordLabel = new JLabel("PASSWORD");
    JTextField emailTextField = new JTextField();
    JPasswordField passwordField = new JPasswordField();
    JButton registerButton = new JButton("REGISTER");
    JButton loginButton = new JButton("LOGIN");
    JButton registerButtonC = new JButton("REGISTER");
    JButton loginButtonC = new JButton("LOGIN");
    JButton resetButton = new JButton("RESET");
    JCheckBox showPassword = new JCheckBox("Show Password");

    StarterFrame() {
        setLayoutManager();
        setLocationAndSizeStarter();
        addComponentsToContainerStarter();
        addActionEventStart();
    }

     protected void RegisterFrame() {
        setLayoutManager();
        setLocationAndSizeRegister();
        addComponentsToContainerRegister();
        addActionEventRegister();

         container.revalidate();
         container.repaint();
    }

    protected void LoginFrame() {
        setLayoutManager();
        setLocationAndSizeLogin();
        addComponentsToContainerLogin();
        addActionEventLogin();

        container.revalidate();
        container.repaint();
    }

    public void setLayoutManager() {
        container.setLayout(null);
    }

    public void setLocationAndSizeStarter() {
        registerButtonC.setBounds(50, 300, 100, 30);
        loginButtonC.setBounds(200, 300, 100, 30);
    }

    public void setLocationAndSizeRegister() {
        emailLabel.setBounds(50, 150, 100, 30);
        emailTextField.setBounds(150, 150, 150, 30);

        passwordLabel.setBounds(50, 220, 100, 30);
        passwordField.setBounds(150, 220, 150, 30);

        showPassword.setBounds(150, 250, 150, 30);
        registerButton.setBounds(50, 300, 100, 30);
        resetButton.setBounds(200, 300, 100, 30);
    }

    public void setLocationAndSizeLogin() {
        emailLabel.setBounds(50, 150, 100, 30);
        emailTextField.setBounds(150, 150, 150, 30);

        passwordLabel.setBounds(50, 220, 100, 30);
        passwordField.setBounds(150, 220, 150, 30);

        showPassword.setBounds(150, 250, 150, 30);
        loginButton.setBounds(50, 300, 100, 30);
        resetButton.setBounds(200, 300, 100, 30);
    }

    public void addComponentsToContainerStarter() {
        container.add(registerButtonC);
        container.add(loginButtonC);
    }

    public void addComponentsToContainerRegister() {
        container.add(emailLabel);
        container.add(passwordLabel);
        container.add(emailTextField);
        container.add(passwordField);
        container.add(showPassword);
        container.add(registerButton);
        container.add(resetButton);
    }

    public void addComponentsToContainerLogin() {
        container.add(emailLabel);
        container.add(passwordLabel);
        container.add(emailTextField);
        container.add(passwordField);
        container.add(showPassword);
        container.add(loginButton);
        container.add(resetButton);
    }

    public void addActionEventStart() {
            registerButtonC.addActionListener(this);
            loginButtonC.addActionListener(this);
    }

    public void addActionEventRegister() {
            registerButton.addActionListener(this);
            resetButton.addActionListener(this);
            showPassword.addActionListener(this);
    }

    public void addActionEventLogin() {
            loginButton.addActionListener(this);
            resetButton.addActionListener(this);
            showPassword.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == registerButtonC) {
            container.remove(registerButtonC);
            container.remove(loginButtonC);
            repaint();
            RegisterFrame();
        }

        if (e.getSource() == registerButton) {
                String emailText;
                String pwdText;
                emailText = emailTextField.getText();
                pwdText = String.valueOf(passwordField.getPassword());
                try {
                    boolean r = FroggerClient.gameFactoryRI.register(emailText, pwdText);
                    if (r) {
                        container.remove(emailLabel);
                        container.remove(passwordLabel);
                        container.remove(emailTextField);
                        container.remove(passwordField);
                        container.remove(showPassword);
                        container.remove(registerButton);
                        container.remove(resetButton);
                        repaint();
                        LoginFrame();
                    }
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }
        }

        if (e.getSource() == loginButtonC) {
            container.remove(registerButtonC);
            container.remove(loginButtonC);
            repaint();
            LoginFrame();
        }

        if (e.getSource() == loginButton) {
                String emailText;
                String pwdText;
                emailText = emailTextField.getText();
                pwdText = String.valueOf(passwordField.getPassword());

                try {
                    GameSessionRI gameSession = FroggerClient.gameFactoryRI.login(emailText, pwdText);
                    if (gameSession != null) {
                        setVisible(false);
                        dispose();
                        System.out.println("Usuario " + emailText + " a entrar com sucesso!");
                        gameSession.criarJogo();
                    } else {
                        JOptionPane.showMessageDialog(this, "Username/Password Errado!");
                    }
                } catch (Exception ex) {
                    if (ex instanceof ConnectException) {
                        //JOptionPane.showMessageDialog(this, "Username/Password Errado!");
                        System.out.println("Username/Password Errado");
                    } else if (ex instanceof UnmarshalException) {
                        System.out.println("Jogo Fechado com Sucesso");
                    }
                }
        }

        if (e.getSource() == resetButton) {
            emailTextField.setText("");
            passwordField.setText("");
        }

        if (e.getSource() == showPassword) {
            if (showPassword.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar('*');
            }
        }
    }
}
