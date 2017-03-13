package ru.geekbrains.java2.lesson8.client;

import javax.swing.*;
import java.awt.*;


public class AboutWindow extends JFrame {
    public AboutWindow() throws HeadlessException {
        super("About Messenger"); //Заголовок окна
        setBounds(100, 100, 200, 200); //Если не выставить
        setSize(200, 100);
        setResizable(false);
        setTitle("About Messenger");
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    }

    public  void paint (Graphics g) {
        g.setColor(Color.BLUE);
        g.drawString("Easy Messenger" , 50, 40);
        g.setColor(Color.BLACK);
        g.drawString("Author: Ivan Zhukov" , 20, 60);
        g.setColor(Color.GRAY);
        g.drawString("GeekBrains, 2017" , 45, 80);
    }
}
