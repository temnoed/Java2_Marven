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
     * У клиента есть Имя
     */
    private String name;


    /**
     * И есть Login
     */
    private String login;

    /**
     * И есть Pass
     */
    private String password;

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
     * @return login
     */
    String getLogin() {
        return login;
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
                // цикл приёма строки от клиента:
                // обрабатываем служебные команда от клиента:
                // Ау, смена пароля и т.п.

                String w = in.readUTF();
                // "ожидаем пока клиент пришлет строку текста"
                // вот это и есть рабочая лошадка
                // нашего полу-клиента-полу-обработчика.
                // ловим строку во встречном потоке.


                if (w != null) {

                    String[] n = w.split("\t");
                    // разделяем строку по пробелам\табуляциям
                    // и укладываем кусочки в массив строк.
                    // В последующих ячейках массива: 0-команда.

                    if (n[0].equals("addNewUser")) {
                        // если пришла команда добавить пользователя

                        sendMsg("addNewUser: " + SQLHandler.addNewUser(n[1], n[2], n[3]));
                        // посылаем клиенту сообщение
                        // о результатах добавления нового пользователя

                        continue;
                        // переход к ожиданию следующей входящей строки
                    }

                    if (n[0].equals("changePassword")) {
                        // если пришла команда сменить пароль пользователя

                        sendMsg("changePassword: " + SQLHandler.changePassword(n[1], n[2]));
                        // посылаем клиенту сообщение
                        // о результатах смены пароля

                        continue;
                        // переход к ожиданию следующей входящей строки
                    }

                    if (n[0].equals("auth")) {
                        // если пришла команда Ау 1-логин, 2-пароль.

                        String t = SQLHandler.getNickByLoginPassword(n[1], n[2]);
                        // вводим ещё одну строку
                        // для хранения ника пользователя

                        if (t != null && !owner.isNicknameUsed(t)) {
                            // есть ли пользователь с таким ником, то
                            // кстати, t будет не null, если пароль правильный

                            owner.broadcastMsg(t + " connected to the chatroom");
                            // оповестить пользователей о присоединении юзера.

                            name = t;
                            // имя присоединённого пользователя
                            // и остальное сообщаем полям
                            // нашего класса ClientHandler
                            login = n[1];
                            password = n[2];

                            sendMsg("zxcvb");
                            // посылаем заклинание = Ау произошло

                            break;
                            // если подконнектили пользователя к чату,
                            // выходим из цикла приёма строчки
                            // в следующий цикл приёма строчки,
                            // который будет двигать
                            // собственно переписку данного пользователя.

                        } else {
                            // если невозможно подключить к чату:


                            if (t == null) {

                                if (SQLHandler.hasNotSuchLogin(n[1])) {
                                    // если нет такого логина
                                    sendMsg("Auth Error: No such login.");

                                } else {

                                    if (!SQLHandler.isPasswordCorrect(n[1], n[2])) {
                                        // пароль не пароль
                                        sendMsg("Auth Error: Password not correct.");
                                    }
                                }


                            } else {

                                if (owner.isNicknameUsed(t))
                                    // имя уже занято
                                    sendMsg("Auth Error: Account are busy, are using.");
                            }
                        }
                    }
                }

                Thread.sleep(100);
                // надо дать серверу время,
                // чтобы подумать-среагировать

            }


            while (true) {
                // второй цикл нужен, чтобы
                // от пользователя принять сообщение и
                // отправлять сообщение до клиента.

                String w = in.readUTF();
                // принять строку


                if (w != null) {
                    // если она не пустая

                    System.out.println(name + ": " + w);
                    // первым делом продублируй передачу в консольку

                    if (w.equalsIgnoreCase("END")) break;
                    // дальше проверяем на служебные комманды:
                    // END служебное слово - конец сессии


                    if (w.matches("^CHANGENICK\\s(\\w+){3,32}$")) {
                        // используем регулярное выражение стыреное из Инета
                        // чтобы читать команду "сменить ник!"

                        changeNick(w, login, password );
                        // w - команда, логин, пасс
                        // манипулируем полями нашего класса

                        continue;
                    }


                    owner.broadcastMsg(name + ": " + w);
                    // передай строку
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


    /**
     * Метод закрывает сокет и
     * удаляет рбъект ClientHandler
     * из списка клиентов сервера.
     * То есть, фактически отключает данного клиента.
     */
    public void close() {
        try {
            System.out.println("Client disconnected");

            owner.remove(this);
            // использует метод класса MyServer

            s.close();
            // "наш" сокет

            if (!name.isEmpty())
                // хороший способ проверить строку вместо !null

                owner.broadcastMsg(name + " disconnected from the chatroom");
            // ну и оповещаем в окно клиента

        } catch (IOException e) {
            // обрабытывыем ошибки ввода вывода
            // inputstream outputstream.
        }
    }


    /**
     * Посылаем строку-сообщение
     * используя пару стандартных методов
     *
     * @param msg
     */
    public void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
            out.flush();
            // очищаем буфер вывода

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Метод меняет ник пользователя
     * отталкиваясь от служебной команды
     **/
    private boolean changeNick(String strCommand, String login_m, String password_m) {
        // в метод обязательно передаём логин и пароль "для безопасности"

        String newNick;
        newNick = strCommand.split("\\s")[1];
        // откусываем правую часть от команды - новый ник
        // и выполняем замену в БД

        if (SQLHandler.setNewNickname(newNick, login_m, password_m)) {
            name = newNick;
            System.out.println ("Nick changed. New nick: " + name);
            sendMsg("Nick changed. New nick: " + name);
            return true;

        } else {
            return false;
        }
    }

} // END ClientHandler