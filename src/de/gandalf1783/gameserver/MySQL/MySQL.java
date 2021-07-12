package de.gandalf1783.gameserver.MySQL;

import de.gandalf1783.gameserver.threads.ConsoleRunnable;
import org.fusesource.jansi.Ansi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL {

    private static Connection conn;

    public static void initDB(String database) {
        try {
            Class.forName("com.mysql.jdbc.Driver");

            conn = DriverManager.getConnection("jdbc:mysql://192.168.178.45/"+database, "RPGServer", "T99MpBUJrHKHOZ1K");

        } catch (SQLException throwables) {
            ConsoleRunnable.println("Could not connect to the database. Please check your credentials.", Ansi.Color.RED);
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            ConsoleRunnable.println("MySQL Class not found.");
        }
    }

    public static Connection getConn() {
        return conn;
    }

}
