/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.DAO.DAOClass;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import shared.model.GameData;

/**
 *
 * @author duynn
 */
public class GameDataDAO {

    MysqlConnector connector;

    public GameDataDAO() {
    }

    public ArrayList readDB() {
        ArrayList<GameData> result = new ArrayList<>();
        connector = new MysqlConnector();

        try {
            String sql = "SELECT * FROM gamedata;";
            PreparedStatement stm = connector.getConnection().prepareStatement(sql);
            ResultSet rs = connector.sqlQry(stm);

            if (rs != null) {
                while (rs.next()) {
                    GameData g = new GameData(
                            rs.getInt("ID"),
                            rs.getString("data")
                    );
                    result.add(g);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error while trying to read Matchs info from database!");
        } finally {
            connector.closeConnection();
        }

        return result;
    }

    public GameData getRandom() {
        GameData result = null;
        connector = new MysqlConnector();

        try {
            String sql = "SELECT * FROM gamedata ORDER BY RAND() LIMIT 1";
            PreparedStatement stm = connector.getConnection().prepareStatement(sql);
            ResultSet rs = connector.sqlQry(stm);
            rs.next();

            if (rs != null) {
                result = new GameData(rs.getInt("ID"), rs.getString("data"));
            }

        } catch (SQLException e) {
            System.err.println("Error while trying to read Matchs info from database!");
        } finally {
            connector.closeConnection();
        }

        return result;
    }

    public boolean add(String s) {
        boolean result = false;
        connector = new MysqlConnector();

        try {
            String sql = "INSERT INTO GameData(data) VALUES(?)";
            PreparedStatement stm = connector.getConnection().prepareStatement(sql);
            stm.setString(1, s);
            result = connector.sqlUpdate(stm);
        } catch (SQLException ex) {
            Logger.getLogger(RoomDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            connector.closeConnection();
        }

        return result;
    }

    public boolean update(GameData gd) {
        boolean result = false;
        connector = new MysqlConnector();

        try {
            String sql = "UPDATE GameData SET data=? WHERE ID=?";

            PreparedStatement stm = connector.getConnection().prepareStatement(sql);
            stm.setString(1, gd.getData());
            stm.setInt(2, gd.getID());

            result = connector.sqlUpdate(stm);
        } catch (SQLException ex) {
            Logger.getLogger(RoomDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            connector.closeConnection();
        }

        return result;
    }

    public boolean delete(int id) {
        boolean result = false;
        connector = new MysqlConnector();

        try {
            String qry = "DELETE FROM GameData WHERE ID=?";

            PreparedStatement stm = connector.getConnection().prepareStatement(qry);
            stm.setInt(1, id);

            result = connector.sqlUpdate(stm);
        } catch (SQLException ex) {
            Logger.getLogger(RoomDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            connector.closeConnection();
        }

        return result;
    }
}
