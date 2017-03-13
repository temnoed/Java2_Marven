package ru.geekbrains.java2.lesson8.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private MyServer owner;
    private Socket s;
    private DataOutputStream out;
    private DataInputStream in;
    private String name;
    private int authTimer;

    public int getAuthTimer() {
        return authTimer;
    }

    public void setAuthTimer(int authTimer) {
        this.authTimer = authTimer;
    }

    public String getName() {
        return name;
    }

    public ClientHandler(Socket s, MyServer owner) {
        try {
            this.s = s;
            this.owner = owner;
            out = new DataOutputStream(s.getOutputStream());
            in = new DataInputStream(s.getInputStream());
            name = "";
            authTimer = 0;
        } catch (IOException e) {
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                String w = in.readUTF();
                if (w != null) {
                    String[] n = w.split("\t");
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
                        } else {
                            if (t == null)
                                sendMsg("Auth Error: No such account");
                            if (owner.isNicknameUsed(t))
                                sendMsg("Auth Error: Account are busy");
                        }
                    }
                }
                Thread.sleep(100);
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
