package ru.geekbrains.java2.dz.dz8.server;

import java.sql.*;
// подключить библиотеку для SQL

/**
 * Класс для обработки базы данных
 */
public class SQLHandler implements AutoCloseable {

    /**
     * Поля класса характеризуют обычное подключение к БД
     * и запросы SQL
     */
    private static final String dbUrl =
            "jdbc:postgresql://localhost:5432/postgres";
    // ip адрес и порт БД
    private static final String user = "postgres";
    // имя БД
    private static final String password = "123456";

    private static final String SQL_SELECT =
            "SELECT Nickname FROM main " +
                    "WHERE Login = ? AND Password = ?;";
    // для запроса на выборку по логину и паролю

    private static final String SQL_INSERT =
            "INSERT INTO main " +
                    "(password, nickname, login) values (?, ?, ?);";
    // для добавления нового пользователя чата

    private static final String SQL_UPDATE =
            "UPDATE main SET password = ? " +
                    "WHERE login = ?;";
    // для замены замены пароля у пользователя чата

    private static Connection conn;
    // для создания соединения с БД использовать
    // стандартный интерфейс из java.sql

    //private static PreparedStatement pStmt;
    // для исполнения запросов SQL использовать
    // стандартный интерфейс для запросов с параметрами


    /**
     * Конструктор нужен чтобы
     * реализовать соединение с БД,
     * используя try-with-resources.
     * Таким образом сеттер ненужен и
     * закрывать соединение будем автоматически.
     */
    SQLHandler() {

        try {
            Class.forName("org.postgresql.Driver");
            // загрузить класс драйвера БД (рекомендуемый способ),
            // используя получение имени класса (Java Reflection API)
            // "за регистрацию нового драйвера отвечает сам драйвер:
            // он должен вызывать DriverManager.registerDriver,
            // и, как уже отмечалось, этот вызов должен происходить
            // автоматически при загрузке класса драйвера"
        } catch (ClassNotFoundException e) {
            // исключить невозможность найти класс драйвера
            e.printStackTrace();
            // вывести в системный поток ошибок
        }

        try {
            conn = DriverManager.getConnection(dbUrl, user, password);
            // получить соединение с БД в виде объекта
            // использовать стандартный класс-посредник
            // для работы с загруженным классом драйвером
        } catch (SQLException e) {
            // исключить неподключение к БД
            e.printStackTrace();
            // вывести в системный поток ошибок
        }
    }


    /**
     * Переписываем используя стандартный метод .close()
     *
     * @throws Exception
     */
    @Override
    public void close() throws Exception {
        try {
            conn.close();
        } catch (Exception c) {
            System.out.println("Connection Error");
        }
    }


    /**
     * Просто метод для подключения к БД
     */
    /*public static void connect() {

        try {
            Class.forName("org.postgresql.Driver");
            // загрузить класс драйвера БД (рекомендуемый способ),
            // используя получение имени класса (Java Reflection API)
            // "за регистрацию нового драйвера отвечает сам драйвер:
            // он должен вызывать DriverManager.registerDriver,
            // и, как уже отмечалось, этот вызов должен происходить
            // автоматически при загрузке класса драйвера"
        } catch (ClassNotFoundException e) {
            // исключить невозможность найти класс драйвера
            e.printStackTrace();
            // вывести в системный поток ошибок
        }

        try {
            conn = DriverManager.getConnection(dbUrl, user, password);
            // получить соединение с БД в виде объекта
            // использовать стандартный класс-посредник
            // для работы с загруженным классом драйвером
        } catch (SQLException e) {
            // исключить неподключение к БД
            e.printStackTrace();
            // вывести в системный поток ошибок
        }
    }*/


    /**
     * Просто метод для отключения от БД
     */
    /*public static void disconnect() {

        try {
            conn.close();
            // "Данное решение опасно, потому что
            // если в коде сгенерируется исключение,
            // то .close() не будет вызван.
            // Произойдет утечка ресурса.
        } catch (Exception c) {
            System.out.println("Connection Error");
        }
    }*/


    /**
     * @param login
     * @param password
     * @return
     */
    static String getNickByLoginPassword(String login, String password) {
        String w = null;
        // мне неясно зачем null,
        // ведь при инициализации по-умолчанию присваивается null?

        try (PreparedStatement pStmt = conn.prepareStatement(SQL_SELECT)) {
            // Используем автозакрывашку и создаём запрос на выборку

            pStmt.setString(1, login);
            // назначаем в запрос параметр

            pStmt.setString(2, password);
            // и ещё

            ResultSet rs = pStmt.executeQuery();
            // получи результат запроса в виде
            // хитрого объекта класса ResultSet,
            // который представляет собой таблицу БД и
            // курсор, перемещаемый по строчкам вниз

            if (rs.next())
                // двигаем курсор в первую строку

                w = rs.getString("Nickname");
            // и получаем ник

        } catch (SQLException e) {
            System.out.println("SQL Query Error");
        }
        return w;
    }


    /**
     * Всё как в вышеописанном методе
     *
     * @param password
     * @param nickname
     * @param login
     * @return
     */
    static boolean addNewUser(String password, String nickname, String login) {

        try (PreparedStatement pStmt = conn.prepareStatement(SQL_INSERT)) {
            pStmt.setString(1, password);
            pStmt.setString(2, nickname);
            pStmt.setString(3, login);

            if (pStmt.executeUpdate() > 0) return true;
            // executeUpdate() используется в запросах
            // типа INSERT ибо executeQuery() ошибается

        } catch (SQLException e) {
            System.out.println("SQL Query Error!!!");
        }
        return false;
    }


    /**
     * Всё как в вышеописанном методе
     *
     * @param login
     * @param password
     * @return
     */
    static boolean changePassword(String login, String password) {

        try (PreparedStatement pStmt = conn.prepareStatement(SQL_UPDATE)) {
            pStmt.setString(1, password);
            pStmt.setString(2, login);
            if (pStmt.executeUpdate() > 0) return true;

        } catch (SQLException e) {
            System.out.println("SQL Query Error!!!");
        }
        return false;
    }
}
