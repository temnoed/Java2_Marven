package ru.geekbrains.java2.lesson7.server;
public class MainClass {
    public static void main(String[] args) {
        SQLHandler.connect();
        MyServer w = new MyServer();
        //SQLHandler.disconnect();
    }
}
