/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shared.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 *
 * @author duynn
 */
public class RoomInDatabase implements Serializable{
    int test;
    int id;
    int playerID1;
    int playerID2;
    int winnerID;
    LocalDateTime startedTime;

    public RoomInDatabase(int id, int playerID1, int playerID2, int winnerID, LocalDateTime startedTime) {
        this.id = id;
        this.playerID1 = playerID1;
        this.playerID2 = playerID2;
        this.winnerID = winnerID;
        this.startedTime = startedTime;
    }

    public RoomInDatabase(RoomInDatabase g) {
        this.id = g.id;
        this.playerID1 = g.playerID1;
        this.playerID2 = g.playerID2;
        this.winnerID = g.winnerID;
        this.startedTime = g.startedTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPlayerID1() {
        return playerID1;
    }

    public void setPlayerID1(int playerID1) {
        this.playerID1 = playerID1;
    }

    public int getPlayerID2() {
        return playerID2;
    }

    public void setPlayerID2(int playerID2) {
        this.playerID2 = playerID2;
    }

    public int getWinnerID() {
        return winnerID;
    }

    public void setWinnerID(int winnerID) {
        this.winnerID = winnerID;
    }

    public LocalDateTime getStartedTime() {
        return startedTime;
    }

    public void setStartedTime(LocalDateTime startedTime) {
        this.startedTime = startedTime;
    }

}
