package ru.geekbrains.java2.dz.dz8.server;

import java.sql.*;
// подключить библиотеку для SQL

/**
 * Класс для обработки базы данных
 */
public class SQLHandler {

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

    private static PreparedStatement stmt;
    // для исполнения запросов SQL использовать
    // стандартный интерфейс для запросов с параметрами


    /**
     * Просто метод для подключения к БД
     */
    public static void connect() {

        try {
            Class.forName("org.postgresql.Driver");
            // загрузить класс драйвера БД (рекомендуемый способ),
            // используя получение имени класса (Java Reflection API)
            // "за регистрацию нового драйвера отвечает сам драйвер:
            // он должен вызывать DriverManager.registerDriver,
            // и, как уже отмечалось, этот вызов должен происходить
            // автоматически при загрузке класса драйвера"
            conn = DriverManager.getConnection(dbUrl, user, password);
            // получить соединение с БД в виде объекта
            // использовать стандартный класс-посредник
            // для работы с загруженным классом драйвером
        } catch (SQLException | ClassNotFoundException e) {
            // исключить неподключение к БД и невозможность найти класс драйвера
            e.printStackTrace();
            // вывести в системный поток ошибок
        }
    }


    /**
     * Просто метод для отключения от БД
     */
    public static void disconnect() {
        try {
            conn.close();
        } catch (Exception c) {
            System.out.println("Connection Error");
        }
    }
    

    public static String getNickByLoginPassword(String login, String password) {
        String w = null;
        try {
            stmt = conn.prepareStatement(SQL_SELECT);
            stmt.setString(1, login);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
                w = rs.getString("Nickname");
        } catch (SQLException e) {
            System.out.println("SQL Query Error");
        }
        return w;
    }

    public static boolean addNewUser(String password, String nickname, String login) {
        try {
            stmt = conn.prepareStatement(SQL_INSERT);
            stmt.setString(1, password);
            stmt.setString(2, nickname);
            stmt.setString(3, login);
            if (stmt.executeUpdate() > 0) return true;
        } catch (SQLException e) {
            System.out.println("SQL Query Error!!!");
        }
        return false;
    }

    public static boolean changePassword(String login, String password) {
        try {
            stmt = conn.prepareStatement(SQL_UPDATE);
            stmt.setString(1, password);
            stmt.setString(2, login);
            if (stmt.executeUpdate() > 0) return true;
        } catch (SQLException e) {
            System.out.println("SQL Query Error!!!");
        }
        return false;
    }
}
