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
import server.DAO.controller.RoomController;
import shared.model.RoomInDatabase;
import shared.constant.StreamData;
import shared.message.ChatMessage;
import shared.message.Message;

/**
 *
 * @author duynn
 */
public class Room {

    String id;
    Sudoku sudoku1;
    Sudoku sudoku2;
    Client client1 = null, client2 = null;
    //ArrayList<Client> clients = new ArrayList<>();
    boolean gameStarted = false;

    public LocalDateTime startedTime;
    public void resetRoom(){
        sudoku1 = new Sudoku();
        sudoku2 = new Sudoku();
        gameStarted = false;
    }
    public Room(String id) {
        // room id
        this.id = id;

        // create game logic
        sudoku1 = new Sudoku();
        sudoku2 = new Sudoku();
    }

    public boolean isFull(){
        if(client1!=null && client2!=null){
            return true;
        }
        return false;
    }
    
    public LocalDateTime getStartedTime() {
        return startedTime;
    }

    public void setStartedTime(LocalDateTime startedTime) {
        this.startedTime = startedTime;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public void startGame() {
        startedTime = LocalDateTime.now();
        gameStarted = true;
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
    public void broadcast(Object message) {
        ChatMessage msg = (ChatMessage) message;
        System.out.println("CHat1:"+msg);
        if(client1 != null){
            client1.sendObject(msg);
        }
        
        System.out.println("CHat2:"+msg);
        if(client2 != null){
            client2.sendObject(msg);
        }
        
//        clients.forEach((c) -> {
//            c.sendData(msg);
//        });
    }

    public void leaveRoom(Client c){
        if(c.equals(client1)){
            client1=null;
        }else if(c.equals(client2)){
            client2=null;
        }
        if(client1 == null && client2==null){
            RunServer.roomManager.remove(this);
        }
    }
    public void close() {
        // remove reference
        client1.setJoinedRoom(null);
        client2.setJoinedRoom(null);
        client1=null;
        client2=null;
        // remove room
        RunServer.roomManager.remove(this);
    }

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

    @Override
    public String toString() {
        return "Room{" + "id=" + id + ", sudoku1=" + sudoku1 + ", sudoku2=" + sudoku2 + ", client1=" + client1 + ", client2=" + client2 + ", gameStarted=" + gameStarted + ", startedTime=" + startedTime + '}';
    }

   void invitePlayAgain(String idInviter) {
        gameStarted = false;
        if(!client1.getLoginPlayer().getNameId().equals(idInviter)){
            Message msg = new Message(StreamData.Type.PLAY_AGAIN);
            client1.sendObject(msg);
        }
        if(!client2.getLoginPlayer().getNameId().equals(idInviter)){
            Message msg = new Message(StreamData.Type.PLAY_AGAIN);
            client2.sendObject(msg);
        }
    }

    void refusePlayAgian(String idInviter) {
        gameStarted = false;
        if(!client1.getLoginPlayer().getNameId().equals(idInviter)){
            Message msg = new Message(StreamData.Type.REFUSE_PLAY_AGAIN);
            client1.sendObject(msg);
        }
        if(!client2.getLoginPlayer().getNameId().equals(idInviter)){
            Message msg = new Message(StreamData.Type.REFUSE_PLAY_AGAIN);
            client2.sendObject(msg);
        }
    }

    
}
