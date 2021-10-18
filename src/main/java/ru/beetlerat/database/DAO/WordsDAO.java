package ru.beetlerat.database.DAO;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class WordsDAO {
    private Connection connection;
    private PrintWriter consoleOutput;
    private String tableName;

    public WordsDAO(String user, String password) throws SQLException {
        // Подключение к БД
        connection= DriverManager.getConnection("jdbc:mysql://localhost:3306/memorystick",user,password);

        consoleOutput=new PrintWriter(System.out,true);
    }

}
