package ru.geekbrains.java2.dz.dz8.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;


public class MyServer {

    private ArrayList<ClientHandler> clients = new ArrayList<ClientHandler>();
    // по проекту частью сервера является список из клиентов,
    // представленных в виде объектов класса ClientHandler


    public MyServer() {
        ServerSocket server = null;
        // сокет, к которуму будут цепляться клинеты?
        // нельзя ли эти объекты сделать полями класса ?

        Socket s = null;
        // ещё один сокет

        final int CLIENT_AUTH_TIMEOUT = 300;
        //

        new Thread(() -> {
            // заменим исходный вариант на
            // способ с лямбдой
            // new Thread(new Runnable() {
            // в этом потоке

            while (true) {
                // бесколнечный цикл, в котором ...

                try {
                    TimeUnit.SECONDS.sleep(1);
                    // пауза в потоке для ...
                    // "without this server do not have time to write something to client"

                    HashSet<ClientHandler> hss = new HashSet<>();
                    // создай хэш-множество объектов-клиентов
                    // типа нашего обработчика

                    for (ClientHandler o : clients) {
                        // для каждого клиента из списка,
                        // который и есть часть нашего сервера

                        if (o.getName().isEmpty()) {
                            // проверяем, а есть ли у него имя?

                            o.setAuthTimer(o.getAuthTimer() + 1);
                            // эээ, надо читать чертёж ClientHandler,
                            // чтобы понять что-нибудь

                            if (o.getAuthTimer() > CLIENT_AUTH_TIMEOUT) {
                                hss.add(o);
                            }
                        }

                    }

                    for (ClientHandler o : hss) {
                        o.close();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

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

    public boolean isNicknameUsed(String nick) {
        for (ClientHandler o : clients) {
            if (o.getName().equals(nick))
                return true;
        }
        return false;
    }

    public void broadcastMsg(String msg) {
        String[] s = msg.split("@#");
        System.out.println(Arrays.toString(s));
        if (s.length == 2) {
            for (ClientHandler o : clients) {
                if (s[0].split(": ")[1].equals(o.getName()))
                    o.sendMsg(s[1]);
            }
        } else {
            for (ClientHandler o : clients) {
                if (!o.getName().isEmpty())
                    o.sendMsg(msg);
            }
        }
    }
}
