package ru.geekbrains.java2.lesson8.server;

import java.sql.*;

public class SQLHandler {
    private static final String dbUrl = "jdbc:postgresql://localhost:5432/postgres";
    private static final String user = "postgres";
    private static final String password = "123456"; // наверно нехорошо делать пароль публичным, но подругому не получилось
    private static final String SQL_SELECT = "SELECT Nickname FROM main WHERE Login = ? AND Password = ?;";
    private static final String SQL_INSERT = "INSERT INTO main (password, nickname, login) values (?, ?, ?);";
    private static final String SQL_UPDATE = "UPDATE main SET password = ? WHERE login = ?;";

    private static Connection conn;
    private static PreparedStatement stmt;

    public static void connect() {
        try {
            DriverManager.registerDriver(new org.postgresql.Driver());
            conn = DriverManager.getConnection(dbUrl, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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
