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


public class AddNewUserWindow extends JFrame {
    final String SERVER_ADDR = "localhost";
    final int SERVER_PORT = 8189;
    Socket socket;
    DataInputStream in;
    DataOutputStream out;

    public AddNewUserWindow() throws HeadlessException {
        super("Add New User"); //Заголовок окна
        setBounds(100, 100, 200, 200); //Если не выставить
        setSize(600, 70);
        setResizable(false);
        setTitle("Add New User");
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

        FlowLayout layout = new FlowLayout();
        JPanel addNewUserPanel = new JPanel(layout);
        setLayout(layout);

        JLabel labelNickname = new JLabel ("Nickname");
        JTextField jtfNickname = new JTextField("Nickname");
        JLabel labelLogin = new JLabel ("Login");
        JTextField jtfLogin = new JTextField("Login");
        JLabel labelPassword = new JLabel ("Password");
        JTextField jtfPass = new JTextField("Password");
        JButton jbAddNewUser = new JButton("Add New User");

        jtfNickname.setEditable(true);
        jtfLogin.setEditable(true);
        jtfPass.setEditable(true);

        addNewUserPanel.add(labelNickname);
        addNewUserPanel.add(jtfNickname);
        addNewUserPanel.add(labelLogin);
        addNewUserPanel.add(jtfLogin);
        addNewUserPanel.add(labelPassword);
        addNewUserPanel.add(jtfPass);

        addNewUserPanel.add(jbAddNewUser);
        add(addNewUserPanel, layout);


        jbAddNewUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String str = "addNewUser\t" + jtfPass.getText() + "\t" + jtfNickname.getText() + "\t" + jtfLogin.getText();
                System.out.println(str);
                connect(str);
            }
        });
        setVisible(true);
    }



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
