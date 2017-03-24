package ru.geekbrains.java2.dz.dz8.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;


public class ChangePasswordWindow extends JFrame {
    final String SERVER_ADDR = "localhost";
    final int SERVER_PORT = 8189;
    Socket socket;
    DataInputStream in;
    DataOutputStream out;


    ChangePasswordWindow() throws HeadlessException {
        super("Change Password"); //Заголовок окна
        setSize(600, 70);
        setResizable(false);
        setTitle("Change Password");
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

        FlowLayout layout = new FlowLayout();
        JPanel addNewUserPanel = new JPanel(layout);
        setLayout(layout);

        JLabel labelLogin = new JLabel("Login");
        JTextField jtfLogin = new JTextField("Login");
        JLabel labelPassword = new JLabel("Password");
        JTextField jtfPass = new JTextField("Password");
        JButton jbChangePassword = new JButton("Change Password");

        jtfLogin.setEditable(true);
        jtfPass.setEditable(true);

        addNewUserPanel.add(labelLogin);
        addNewUserPanel.add(jtfLogin);
        addNewUserPanel.add(labelPassword);
        addNewUserPanel.add(jtfPass);

        addNewUserPanel.add(jbChangePassword);
        add(addNewUserPanel, layout);


        jbChangePassword.addActionListener(lambda -> {
            String str = "changePassword\t" + jtfLogin.getText() + "\t" + jtfPass.getText();
            System.out.println(str);
            connect(str);
        });
        setVisible(true);
    }


    /**
     * Ещё один коннект...
     * @param cmd
     */
    public void connect(String cmd) {
        try {
            socket = new Socket(SERVER_ADDR, SERVER_PORT);
            OutputStream o = socket.getOutputStream();
            out = new DataOutputStream(o);
            out.writeUTF(cmd);
            out.flush();
            in = new DataInputStream(socket.getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
