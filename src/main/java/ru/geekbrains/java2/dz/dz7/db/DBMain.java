package ru.geekbrains.java2.dz.dz7.db;


public class DBMain {

	public static void main(String[] args) {
		DateBase dateBase = new DateBase();

		dateBase.getConnectionToDB(DateBase.dbUrl,
				DateBase.user,
				DateBase.password,
				"log3");

		// dateBase.insertChatUser("nickNew", "logNew", "passNew");
		// dateBase.changeChatPassword("logNew", "111");
	}

}
