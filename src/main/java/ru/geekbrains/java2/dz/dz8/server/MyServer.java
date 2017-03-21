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
        // сокет сервера, к которуму будут цепляться клинеты?

        Socket s = null;
        // ещё один сокет для новоявленных клиентов

        final int CLIENT_AUTH_TIMEOUT = 300;
        // время на Ау клиента

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
                            // увеличиваем счётчик времени Ау клиента на 1

                            if (o.getAuthTimer() > CLIENT_AUTH_TIMEOUT) {
                                // и тотчас проверяем этот счётчик
                                // на достижение предела

                                hss.add(o);
                                // если время Ау "пришло"
                                // добавляем объект-клиента в хэш-множество

                            }
                        }

                    }

                    for (ClientHandler o : hss) {
                        // для каждого клиента в хэш-множестве

                        o.close();
                        // закрываем методом из ClientHandler
                    }
                } catch (InterruptedException e) {
                    // обрабатываем тсключение-нарушение потока
                    e.printStackTrace();
                }
            }
        }).start();



        try {
            // основной поток:

            server = new ServerSocket(8189);
            // сервер запустили
            System.out.println("Server created. Waiting for client...");

            while (true) {
                s = server.accept();
                // ждём клиента
                System.out.println("Client connected");

                ClientHandler h = new ClientHandler(s, this);

                clients.add(h);
                // полезай, клиент, в список

                new Thread(h).start();
                // так как всё готово для работы над клиентом
                // запускаем в параллельном потоке обработчик этого самого клиента
                // ибо он (обработчик) унаследованный от runnable
            }


        } catch (IOException e) {
            e.printStackTrace();


        } finally {
            // многие не рекомендуют использовать
            // finnally вообще, а мы будем!

            try {
                server.close();
                // стандартным методом прикроем сокет сервера

                System.out.println("Server closed");
                s.close();
                // и клиента, который последним пришёл

            } catch (IOException  e) {
                e.printStackTrace();
            }
        }
    }




    /**
     * Удалить клиента из списка при
     * отключении клиента
     * @param o
     */
    void remove(ClientHandler o) {
        clients.remove(o);
        // стандартный метод работы сос писками
        // удаляет первый соответствующий объект из списка
    }


    /**
     * Проверяет есть ли в списке клиент с данными ником
     * @param nick
     * @return
     */
    boolean isNicknameUsed(String nick) {
        for (ClientHandler o : clients) {
            if (o.getName().equals(nick))
                return true;
        }
        return false;
    }


    /**
     * Главный метод, который высылает клиентам
     * строки с сообщениями от сервера.
     * @param msg
     */
    void broadcastMsg(String msg) {

        String[] s = msg.split("@#");
        // делим строку сообщения символами @#

        System.out.println(Arrays.toString(s));
        // покажем нашу строку без спецсимволов

        if (s.length == 2) {
            // тут проверяем входит ли имя в наше сообщение:

            for (ClientHandler o : clients) {
                // пробежимся по всем клиентам-объектам

                if (s[0].split(": ")[1].equals(o.getName()))
                    // и если имя  в сообщении совпадает с именем клиента,

                    o.sendMsg(s[1]);
                // посылаем тому срочку.
            }

        } else {
            // если имя не входит в строку сообщения, то

            for (ClientHandler o : clients) {
                if (!o.getName().isEmpty())
                    // посылаем сообщение только тем объектам, у которых вообще есть имя

                    o.sendMsg(msg);
            }
        }
    }
}
