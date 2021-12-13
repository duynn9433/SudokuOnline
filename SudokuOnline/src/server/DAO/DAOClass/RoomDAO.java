/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.DAO.DAOClass;

import shared.model.RoomInDatabase;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author duynn
 */
public class RoomDAO {

    MysqlConnector connector;

    public RoomDAO() {

    }

    public ArrayList readDB() {
        ArrayList<RoomInDatabase> result = new ArrayList<>();
        connector = new MysqlConnector();

        try {
            String sql = "SELECT * FROM room;";
            PreparedStatement stm = connector.getConnection().prepareStatement(sql);
            ResultSet rs = connector.sqlQry(stm);

            if (rs != null) {
                while (rs.next()) {
                    RoomInDatabase g = new RoomInDatabase(
                            rs.getInt("ID"),
                            rs.getInt("PlayerID1"),
                            rs.getInt("PlayerID2"),
                            rs.getInt("WinnerID"),
                            LocalDateTime.parse(rs.getString("StartedTime"))
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

    public boolean add(RoomInDatabase m) {
        boolean result = false;
        connector = new MysqlConnector();

        try {
            String sql = "INSERT INTO room(PlayerID1,PlayerID2,WinnerID,StartedTime) "
                    + "VALUES(?,?,?,?)";
            PreparedStatement stm = connector.getConnection().prepareStatement(sql);
            stm.setInt(1, m.getPlayerID1());
            stm.setInt(2, m.getPlayerID2());
            stm.setInt(3, m.getWinnerID());
            stm.setString(4, m.getStartedTime().toString());

            result = connector.sqlUpdate(stm);
        } catch (SQLException ex) {
            Logger.getLogger(RoomDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            connector.closeConnection();
        }

        return result;
    }

    public boolean update(RoomInDatabase m) {
        boolean result = false;
        connector = new MysqlConnector();

        try {
            String sql = "UPDATE room SET "
                    + "PlayerID1=?,"
                    + "PlayerID2=?,"
                    + "WinnerID=?,"
                    + "StartedTime=?"
                    + " WHERE ID=?";

            PreparedStatement stm = connector.getConnection().prepareStatement(sql);
            stm.setInt(1, m.getPlayerID1());
            stm.setInt(2, m.getPlayerID2());
            stm.setInt(3, m.getWinnerID());
            stm.setString(4, m.getStartedTime().toString());
            stm.setInt(5, m.getId());

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
            String qry = "DELETE FROM room WHERE ID=?";

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
