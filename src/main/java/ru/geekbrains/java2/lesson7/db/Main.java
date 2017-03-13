package ru.geekbrains.java2.lesson7.db;

/**
 * Created by Home-pc on 19.10.2016.
 */
public class Main {
    public static void main(String[] args) {
        DateBase dateBase = new DateBase();
        dateBase.getConnectionToDB(DateBase.dbUrl,
                DateBase.user,
                DateBase.password,
                "log3");
    }
}
