package ru.geekbrains.java2.dz.dz8.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


/**
 * Наш обработчик клиентов,
 * он же и есть суть описание клиента
 * с которым общается наш сервер.
 * Отдельный поток через run
 */
public class ClientHandler implements Runnable {

    /**
     * Объект нашего сервера
     */
    private MyServer owner;

    /**
     * Сокет
     */
    private Socket s;

    /**
     * Поток отправки данных на сервер
     */
    private DataOutputStream out;

    /**
     * Поток принимаемых от сервера данных
     */
    private DataInputStream in;

    /**
     * Имя
     */
    private String name;

    /**
     * Таймер-счётчик авторизации
     */
    private int authTimer;


    /**
     * Часть обработчика клиента,
     * обозначающая время его авторизации.
     * Используется в классе MyServer.
     *
     * @return authTimer
     */
    int getAuthTimer() {
        return authTimer;
    }

    void setAuthTimer(int authTimer) {
        this.authTimer = authTimer;
    }


    /**
     * @return name
     */
    String getName() {
        return name;
    }


    /**
     * Связывание некого внешнего сокета
     * с нашим сокетом клиента и
     * "того" сервера с "нашим" сервером.
     */
    ClientHandler(Socket s, MyServer owner) {

        try {
            this.s = s;
            this.owner = owner;

            out = new DataOutputStream(s.getOutputStream());
            // поток данных выходит из сокета
            // мы назовём его out

            in = new DataInputStream(s.getInputStream());

            name = "";

            authTimer = 0;
        } catch (IOException e) {
            // потому что потоки через сокеты вдруг чего
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        try {

            while (true) {
                // цикл приёма строки от клиента

                String w = in.readUTF();
                // "ожидаем пока клиент пришлет строку текста"
                // вот это и есть рабочая лошадка
                // нашего полу-клиента-полу-обработчика.
                // ловим строку во встречном потоке.


                if (w != null) {

                    String[] n = w.split("\t");
                    // 

                    if (n[0].equals("addNewUser")) {
                        sendMsg("addNewUser: " + SQLHandler.addNewUser(n[1], n[2], n[3]));
                        continue;
                    }
                    if (n[0].equals("changePassword")) {
                        sendMsg("changePassword: " + SQLHandler.changePassword(n[1], n[2]));
                        continue;
                    }
                    if (n[0].equals("auth")) {
                        String t = SQLHandler.getNickByLoginPassword(n[1], n[2]);
                        if (t != null && !owner.isNicknameUsed(t)) {
                            owner.broadcastMsg(t + " connected to the chatroom");
                            name = t;
                            sendMsg("zxcvb");

                            break;
                            // если подконнектили пользователя к чату,
                            // выходим из цикла приёма строчки
                            // в следующий цикл приёма строчки,
                            // который будет принимать имя пользователя (?)

                        } else {
                            if (t == null)
                                sendMsg("Auth Error: No such account");
                            if (owner.isNicknameUsed(t))
                                sendMsg("Auth Error: Account are busy");
                        }
                    }
                }

                Thread.sleep(100);
                // надо дать серверу время,
                // чтобы подумать-среагировать

            }

            while (true) {
                String w = in.readUTF();
                if (w != null) {
                    owner.broadcastMsg(name + ": " + w);
                    System.out.println(name + ": " + w);
                    if (w.equalsIgnoreCase("END")) break;
                }
                Thread.sleep(100);
            }

        } catch (IOException e) {
            System.out.println("IOException");
        } catch (InterruptedException e) {
            System.out.println("Thread sleep error");
        } finally {
            close();
        }
    }

    public void close() {
        try {
            System.out.println("Client disconnected");
            owner.remove(this);
            s.close();
            if (!name.isEmpty())
                owner.broadcastMsg(name + " disconnected from the chatroom");
        } catch (IOException e) {
        }
    }

    public void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
            out.flush();
        } catch (IOException e) {
        }
    }
}
