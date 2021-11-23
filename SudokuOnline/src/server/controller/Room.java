/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.controller;

import java.time.LocalDateTime;
import shared.model.Sudoku;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import server.RunServer;
import server.DAO.controller.GameMatchController;
import shared.model.GameMatch;
import shared.constant.StreamData;

/**
 *
 * @author duynn
 */
public class Room {

    String id;
    Sudoku sudoku1;
    Sudoku sudoku2;
    Client client1 = null, client2 = null;
    ArrayList<Client> clients = new ArrayList<>();
    boolean gameStarted = false;

    public LocalDateTime startedTime;

    public Room(String id) {
        // room id
        this.id = id;

        // create game logic
        sudoku1 = new Sudoku();
        sudoku2 = new Sudoku();
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public void startGame() {
        startedTime = LocalDateTime.now();
        gameStarted = true;
//        sudoku1.getMatchTimer()
//                .setTimerCallBack(// end match callback
//                        (Callable) () -> {
//
//                            // tinh diem hoa
//                            new GameMatchController().add(new GameMatch(
//                                    client1.getLoginPlayer().getId(),
//                                    client1.getLoginPlayer().getId(),
//                                    -1,
//                                    sudoku1.getMatchTimer().getCurrentTick(),
//                                    startedTime
//                            ));
//
//                            broadcast(
//                                    StreamData.Type.GAME_EVENT + ";"
//                                    + StreamData.Type.MATCH_TIMER_END.name()
//                            );
//                            return null;
//                        },
//                        // tick match callback
//                        (Callable) () -> {
//                            broadcast(StreamData.Type.GAME_EVENT + ";"
//                                    + StreamData.Type.MATCH_TICK.name() + ";"
//                                    + sudoku1.getMatchTimer().getCurrentTick()
//                            );
//                            return null;
//                        },
//                        // tick interval
//                        Sudoku.MATCH_TIME_LIMIT / 10
//                );
    }

    // add/remove client
    public boolean addClient(Client c) {
        if (client1==null || client2==null) {
            if (client1 == null) {
                client1 = c;
            } else if (client2 == null) {
                client2 = c;
            }
            return true;
        }
        return false;
    }
//
//    public boolean removeClient(Client c) {
//        if (clients.contains(c)) {
//            clients.remove(c);
//            return true;
//        }
//        return false;
//    }

    // broadcast messages
    public void broadcast(String msg) {
        clients.forEach((c) -> {
            c.sendData(msg);
        });
    }

    public void close(String reason) {
        // notify all client in room
        broadcast(StreamData.Type.CLOSE_ROOM.name() + ";" + reason);

//        // remove reference
//        clients.forEach((client) -> {
//            client.setJoinedRoom(null);
//        });
//
//        // remove all clients
//        clients.clear();

        // remove room
        RunServer.roomManager.remove(this);
    }

    // get room data
//    public String getFullData() {
//        String data = "";
//
//        // player data
//        data += getClient12InGameData() + ";";
//        data += getListClientData() + ";";
//        // timer
//       // data += getTimerData() + ";";
//        // board
////        data += getBoardData();
//
//        return data;
//    }

//    public String getTimerData() {
//        String data = "";
//
//        data += Sudoku.MATCH_TIME_LIMIT + ";" + sudoku1.getMatchTimer().getCurrentTick() + ";";
//        data += Sudoku.TURN_TIME_LIMIT + ";" + sudoku1.getTurnTimer().getCurrentTick();
//
//        return data;
//    }

//    public String getBoardData() {
//        ArrayList<History> history = sudoku1.getHistory();
//
//        String data = history.size() + ";";
//        for (History his : history) {
//            data += his.getRow() + ";" + his.getColumn() + ";" + his.getPlayerEmail() + ";";
//        }
//
//        return data.substring(0, data.length() - 1); // bỏ dấu ; ở cuối
//    }

//    public String getClient12InGameData() {
//        String data = "";
//
//        data += (client1 == null ? Client.getEmptyInGameData() : client1.getInGameData() + ";");
//        data += (client2 == null ? Client.getEmptyInGameData() : client2.getInGameData());
//
//        return data;
//    }
//
//    public String getListClientData() {
//        // kết quả trả về có dạng playerCount;player1_data;player2_data;...;playerN_data
//
//        String data = clients.size() + ";";
//
//        for (Client c : clients) {
//            data += c.getInGameData() + ";";
//        }
//
//        return data.substring(0, data.length() - 1); // bỏ dấu ; ở cuối
//    }

    // gets sets
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public Sudoku getSudoku1() {
        return sudoku1;
    }

    public void setSudoku1(Sudoku sudoku1) {
        this.sudoku1 = sudoku1;
    }

    public Sudoku getSudoku2() {
        return sudoku2;
    }

    public void setSudoku2(Sudoku sudoku2) {
        this.sudoku2 = sudoku2;
    }

    
    public Client getClient1() {
        return client1;
    }

    public void setClient1(Client client1) {
        this.client1 = client1;
    }

    public Client getClient2() {
        return client2;
    }

    public void setClient2(Client client2) {
        this.client2 = client2;
    }

    public ArrayList<Client> getClients() {
        return clients;
    }

    public void setClients(ArrayList<Client> clients) {
        this.clients = clients;
    }

    @Override
    public String toString() {
        return "Room{" + "id=" + id + ", sudoku1=" + sudoku1 + ", sudoku2=" + sudoku2 + ", client1=" + client1 + ", client2=" + client2 + ", clients=" + clients + ", gameStarted=" + gameStarted + ", startedTime=" + startedTime + '}';
    }

    
}
