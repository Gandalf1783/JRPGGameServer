package de.gandalf1783.gameserver.MySQL;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SQLUtils {

    public static String receiveUUID(String username) {
        try {
            java.sql.Connection conn = MySQL.getConn();
            if(conn == null)
                return "ERR";
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM accounts WHERE Account_Name = ?");
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                return rs.getString("Account_UUID");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return "ERR";
    }

    public static String receiveName(String uuid) {
        try {
            java.sql.Connection conn = MySQL.getConn();
            if(conn == null)
                return "ERR";
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM accounts WHERE Account_UUID = ?");
            pstmt.setString(1,uuid.toUpperCase());
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                return rs.getString("Account_Name");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return "ERR";
    }

    public static boolean doesUserExist(String uuid) {
        try {
            java.sql.Connection conn = MySQL.getConn();
            if(conn == null)
                return false;
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM accounts WHERE Account_UUID = ?");
            pstmt.setString(1,uuid.toUpperCase());
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                return true;
            }
            return false;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }
}
