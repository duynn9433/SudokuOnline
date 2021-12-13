/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.DAO.controller;

import server.DAO.DAOClass.RoomDAO;
import shared.model.RoomInDatabase;
import java.util.ArrayList;

/**
 *
 * @author duynn
 */
public class RoomController {

    ArrayList<RoomInDatabase> listGameMatch = new ArrayList<>();
    RoomDAO roomDAO = new RoomDAO();

    public RoomController() {
        readDB();
    }

    public void readDB() {
        listGameMatch = roomDAO.readDB();
    }

    public boolean add(RoomInDatabase g) {
        boolean status = roomDAO.add(g);

        if (status == true) {
            listGameMatch.add(g);
        }

        return status;
    }

    public boolean delete(int id) {
        boolean status = roomDAO.delete(id);

        if (status == true) {
            for (int i = (listGameMatch.size() - 1); i >= 0; i--) {
                if (listGameMatch.get(i).getId() == id) {
                    listGameMatch.remove(i);
                }
            }
        }

        return status;
    }

    public boolean update(RoomInDatabase g) {
        boolean status = roomDAO.update(g);

        if (status == true) {
            listGameMatch.forEach((gm) -> {
                gm = new RoomInDatabase(g);
            });
        }

        return status;
    }

    public RoomInDatabase getById(int id) {
        for (RoomInDatabase g : listGameMatch) {
            if (g.getId() == id) {
                return g;
            }
        }
        return null;
    }

    // ================ utils ===================
    public int calculateTotalMatch(int playerId) {
        int result = 0;

        for (RoomInDatabase m : listGameMatch) {
            if (m.getPlayerID1() == playerId || m.getPlayerID2() == playerId) {
                result++;
            }
        }
        return result;
    }

    public int calculateWinCount(int playerId) {
        int result = 0;

        for (RoomInDatabase m : listGameMatch) {
            if (m.getWinnerID() == playerId) {
                result++;
            }
        }
        return result;
    }

    public int calculateLongestWinStreak(int playerId) {
        int longest = 0;
        int current = 0;

        for (RoomInDatabase m : listGameMatch) {
            if (m.getPlayerID1() == playerId || m.getPlayerID2() == playerId) {
                if (m.getWinnerID() == playerId) {
                    current++;
                } else {
                    if (current > longest) {
                        longest = current;
                    }
                    current = 0;
                }
            }
        }

        return longest;
    }

    public float calculateWinRate(int playerId) {
        return (float) (100.00 * (calculateWinCount(playerId) / calculateTotalMatch(playerId)));
    }

    public ArrayList<RoomInDatabase> getList() {
        return listGameMatch;
    }
}
