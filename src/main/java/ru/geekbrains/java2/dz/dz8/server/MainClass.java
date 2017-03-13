package ru.geekbrains.java2.dz.dz8.server;
// сетеовой чат в рамках проекта j2Marven
// с использованием сборщика Marven

import javax.swing.*;
// подключаемся к библиотеке с окошками

/**
 * Класс для запуска сервера,
 * для создания пустого окна вместо консоли,
 * для подключения к БД.
 */
public class MainClass {

	public static void main(String[] args) {

		JFrame jf = new JFrame();
		// создать объект-окно
		jf.setBounds(500, 500, 100, 100);
		// задать размеры окна
		jf.setTitle("Server");
		// задать название окна
		jf.setVisible(true);
		// задать видимость окна

		SQLHandler.connect();
		// подключиться к базе данных
		// используя наш класс-обработчик

		MyServer w = new MyServer();
		// создать объект нашего сервера

		//SQLHandler.disconnect();
	}

}
