package ru.geekbrains.java2.dz.dz8.client;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class MyWindow extends JFrame {

    JTextField jtf;
    JTextArea jta;

    final String SERVER_ADDR = "localhost";
    final int SERVER_PORT = 8189;
    Socket sock;
    DataInputStream in;
    DataOutputStream out;
    boolean authorized = false;


    MyWindow() {

        // Во первой разрисовываем окошки
        setBounds(600, 300, 500, 500);
        setTitle("Client");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jta = new JTextArea();
        jta.setEditable(false);
        jta.setLineWrap(true);
        JScrollPane jsp = new JScrollPane(jta);
        add(jsp, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        JPanel authPanel = new JPanel(new GridLayout());

        JTextField jtfLogin = new JTextField();
        JTextField jtfPass = new JTextField();
        JButton jbAuth = new JButton("Auth");
        authPanel.add(jtfLogin);
        authPanel.add(jtfPass);
        authPanel.add(jbAuth);
        jtfLogin.setToolTipText("Login");
        add(authPanel, BorderLayout.NORTH);


        // нажималка на Ау кнопку
        jbAuth.addActionListener(lambda -> {
            //@Override
            //public void actionPerformed(ActionEvent e) {
            if (sock == null || sock.isConnected()) {
                System.out.println("TRY");
                connectToServer();
            }
            sendAuthCmd("auth\t" + jtfLogin.getText() + "\t" + jtfPass.getText());
        });


        add(bottomPanel, BorderLayout.SOUTH);
        JButton jbSend = new JButton("SEND");
        bottomPanel.add(jbSend, BorderLayout.EAST);
        jtf = new JTextField();
        bottomPanel.add(jtf, BorderLayout.CENTER);


        jbSend.addActionListener(lambda -> {
            if (!jtf.getText().trim().isEmpty()) {
                sendMsg();
                jtf.grabFocus();
            }
        });


        jtf.addActionListener(lambda -> sendMsg());


        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                try {
                    out.writeUTF("end");
                    out.flush();
                    out.close();
                    in.close();
                } catch (IOException exc) {
                    exc.printStackTrace();
                } finally {
                    try {
                        sock.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });


        JMenuBar mainMenu = new JMenuBar();
        JMenu menuActions = new JMenu("Actions");
        JMenu menuAbout = new JMenu("About");
        JMenuItem menuAboutMessenger = new JMenuItem("About Messenger");
        JMenuItem menuAddNewUser = new JMenuItem("Add New User");
        JMenuItem menuChangePassword = new JMenuItem("Change Password");
        JMenuItem menuFileSave = new JMenuItem("Save");
        JMenuItem menuFileLoad = new JMenuItem("Load");
        JMenuItem menuFileExit = new JMenuItem("Exit");
        setJMenuBar(mainMenu);
        mainMenu.add(menuActions);
        mainMenu.add(menuAbout);
        menuActions.add(menuAddNewUser);
        menuActions.add(menuChangePassword);
        menuActions.add(menuFileSave);
        menuActions.add(menuFileLoad);
        menuActions.addSeparator(); // разделительная линия
        menuActions.add(menuFileExit);
        menuAbout.add(menuAboutMessenger);


        menuAddNewUser.addActionListener(lambda -> {
            AddNewUserWindow about = new AddNewUserWindow();
            about.setVisible(true);
        });


        menuChangePassword.addActionListener(lambda -> {
            ChangePasswordWindow about = new ChangePasswordWindow();
            about.setVisible(true);
        });


        menuAboutMessenger.addActionListener(lambda -> {
            AboutWindow about = new AboutWindow();
            about.setVisible(true);
        });


        menuFileSave.addActionListener(lambda -> {
            JFileChooser fileopen = new JFileChooser();
            int ret = fileopen.showDialog(null, "Save file");
            if (ret == JFileChooser.APPROVE_OPTION) {
                File file = fileopen.getSelectedFile();
                String data = jta.getText();
                saveData(file, data);
            }
        });


        menuFileLoad.addActionListener(lambda -> {
            JFileChooser fileopen = new JFileChooser();
            int ret = fileopen.showDialog(null, "Load file");
            if (ret == JFileChooser.APPROVE_OPTION) {
                File file = fileopen.getSelectedFile();
                jta.setText(loadData(file));
            }
        });


        menuFileExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });


        setVisible(true);
    }


    /**
     * Соединение с сервером классическим методом
     */
    private void connectToServer() {

        try {
            sock = new Socket(SERVER_ADDR, SERVER_PORT);
            in = new DataInputStream(sock.getInputStream());
            out = new DataOutputStream(sock.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        new Thread(() -> {
            // сей поток ждёт команды от сервера на
            // удачность подключения или отключения
            try {
                while (true) {
                    String w = in.readUTF();
                    if (w != null) {
                        if (w.equals("zxcvb")) {
                            jta.setText("Connected\n");
                            authorized = true;
                            // суть цикла в том, чтобы установить ключ Ау
                            break;
                            // выход из цикла в случае удачного коннекта

                        } else {
                            jta.append(w);
                            // добавляем строку в "документ-объект"

                            jta.append("\n");
                        }
                    }
                }


                while (true) {
                    String w = in.readUTF();
                    if (w != null) {
                        if (w.equalsIgnoreCase("end session")) break;
                        // а тут выход, если команда от сервера на отключение

                        jta.append(w);
                        jta.append("\n");
                        jta.setCaretPosition(jta.getDocument().getLength());
                        // устанавливаем позицию курсора в документе
                    }
                    Thread.sleep(100);
                }

            } catch (Exception e) {
                authorized = false;
                System.out.println("ERR");
            }

        }).start();
    }


    /**
     * Хитрейший метод.
     * Посылает на сервер команду-строку и очищает буфер.
     * @param str
     */
    private void sendAuthCmd(String str) {
        try {
            out.writeUTF(str);
            out.flush();
        } catch (IOException e) {
            System.out.println("Send auth error");
        }
    }


    /**
     * Посылает строчку сообщения и очищает поле.
     */
    public void sendMsg() {
        if (authorized) {
            try {
                String a = jtf.getText();
                out.writeUTF(a);
                out.flush();
                jtf.setText("");
            } catch (IOException e) {
                System.out.println("Send msg error");
            }
        }
    }


    /**
     * В файл
     * @param filename
     * @param data
     */
    private static void saveData(File filename, String data) {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(data);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Из файла
     * @param filename
     * @return
     */
    private static String loadData(File filename) {
        StringBuilder response = new StringBuilder("");
        try (Scanner scanner = new Scanner(filename)) {
            while (scanner.hasNext()) {
                response.append(scanner.nextLine()).append("\n");
            }
        } catch (FileNotFoundException e) {
            System.out.println(e);
            e.printStackTrace();
        }
        return response.toString();
    }
}
