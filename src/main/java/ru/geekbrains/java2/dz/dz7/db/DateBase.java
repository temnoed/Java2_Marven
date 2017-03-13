package ru.geekbrains.java2.dz.dz7.db;

import java.sql.*;


class DateBase {
	static String dbUrl = "jdbc:postgresql://localhost:5432/postgres";
	static String user = "postgres";
	static String password = "123456";
	private String SQL_SELECT_ALL = "SELECT login, nickname, password  FROM main";
	private static Connection connection;


	/**
	 * Метод добавления нового пользователя чата в таблицу базы данных.
	 * Используем интерфейс PreparedStatement.
	 *
	 * @return 1 if Ok or 0 if not.
	 */
	int insertChatUser(String nick, String log, String pass) {
		String query = "INSERT INTO main " +
				"(nickname, login, password) " +
				"VALUES (?,?,?)";
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, nick);
			preparedStatement.setString(2, log);
			preparedStatement.setString(3, pass);
			return preparedStatement.executeUpdate(); // если использовать метод .executeQuery(),
			// то для запроса INSERT возникает ошибка на стадии выполнения.
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}


	/**
	 * Метод для замены пароля по логину пользователя.
	 * Используем интерфейс PreparedStatement.
	 *
	 * @return 1 if Ok or 0 if not.
	 */
	int changeChatPassword(String log, String newPass) {
		String query = "UPDATE main " +
				"SET password = ? " +
				"WHERE login = ? ";
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, newPass);
			preparedStatement.setString(2, log);
			return preparedStatement.executeUpdate(); // если использовать метод .executeQuery(),
			// то для запроса INSERT возникает ошибка на стадии выполнения.
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}


	void getConnectionToDB(String dbUrl, String user, String password, String login) {
		String SQL_SELECT = "SELECT login, nickname, password  " +
				"FROM main " +
				"where login = ?";

		try {
			DriverManager.registerDriver(new org.postgresql.Driver());
		} catch (SQLException e) {
			e.printStackTrace();
		}

		try {
			connection = DriverManager.getConnection(dbUrl, user, password);
			if (connection != null) {
				System.out.println("connection to db");
				System.out.println("Подключение к базе данных прошло успешно!\n");
				Statement statement = connection.createStatement();
				PreparedStatement preparedStatement = connection.prepareStatement(SQL_SELECT);
				if (login != null && !login.isEmpty()) {
					findByLogin(preparedStatement, login);
//                    findByLogin(statement, login);
				} else {
					showFullTableColumnNumber(statement);
//                showFullTable(statement);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	private void findByLogin(PreparedStatement preparedStatement, String log) {
		try {
			preparedStatement.setString(1, log);
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				String login = resultSet.getString(1);
				String nickname = resultSet.getString(2);
				String password = resultSet.getString(3);
				System.out.println("login: " + login + "_ nickname: " + nickname + "_ password: " + password);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	private void findByLogin(Statement statement, String log) {
		try {
			String sql = SQL_SELECT_ALL + " where login ='" + log + "'";
			System.out.println(sql);
			ResultSet resultSet = statement.executeQuery(sql);
			while (resultSet.next()) {
				String login = resultSet.getString(1);
				String nickname = resultSet.getString(2);
				String password = resultSet.getString(3);
				System.out.println("login: " + login + "+ nickname: " + nickname + "+ password: " + password);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	private void showFullTableColumnNumber(Statement statement) {
		try {
			ResultSet resultSet = statement.executeQuery(SQL_SELECT_ALL);
			while (resultSet.next()) {
				String login = resultSet.getString(1);
				String nickname = resultSet.getString(2);
				String password = resultSet.getString(3);
				System.out.println("login: " + login + "- nickname: " + nickname + "- password: " + password);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public void showFullTable(Statement statement) {
		try {
			ResultSet resultSet = statement.executeQuery(SQL_SELECT_ALL);
			while (resultSet.next()) {
				String login = resultSet.getString("login");
				String nickname = resultSet.getString("nickname");
				String password = resultSet.getString("password");
				System.out.println("login: " + login + ", nickname: " + nickname + ", password: " + password);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


}