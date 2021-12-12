/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author duynn
 */
public class MySqlConnector {

    public static Connection getMySQLConnection() throws ClassNotFoundException, SQLException {
        String host = "localhost";
        String dbName ="quanlisinhvien";
        String username = "root";
        String pass = "123456";
        return getMySqlConnector(host, dbName, username, pass);
    }
    private static Connection getMySqlConnector(String host, String dbName, String username, String pass) throws ClassNotFoundException, SQLException{
        Class.forName("com.mysql.cj.jdbc.Driver");
        String url = "jdbc:mysql://" + host + ":3306/" + dbName+"?zeroDateTimeBehavior=convertToNull";
        Connection con = DriverManager.getConnection(url,username,pass);
        return con;
    }
}
