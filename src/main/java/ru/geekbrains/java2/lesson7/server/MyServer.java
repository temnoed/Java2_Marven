package ru.geekbrains.java2.lesson7.server;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


public class MyServer {

    private ArrayList<ClientHandler> clients = new ArrayList<ClientHandler>();

    public MyServer() {
        ServerSocket server = null;
        Socket s = null;
        try {
            server = new ServerSocket(8189);
            System.out.println("Server created. Waiting for client...");
            while (true) {
                s = server.accept();
                System.out.println("Client connected");
                ClientHandler h = new ClientHandler(s, this);
                clients.add(h);
                new Thread(h).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                server.close();
                System.out.println("Server closed");
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void remove(ClientHandler o) {
        clients.remove(o);
    }

    public void broadcastMsg(String msg) {
        for (ClientHandler o : clients) {
            o.sendMsg(msg);
        }
    }
}
