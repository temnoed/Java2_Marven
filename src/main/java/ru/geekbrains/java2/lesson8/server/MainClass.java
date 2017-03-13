package ru.geekbrains.java2.lesson8.server;

import javax.swing.*;

public class MainClass {
    public static void main(String[] args) {
        JFrame jf = new JFrame();
        jf.setBounds(500,500,100,100);
        jf.setTitle("Server");
        jf.setVisible(true);
        SQLHandler.connect();
        MyServer w = new MyServer();
        //SQLHandler.disconnect();
    }
}
