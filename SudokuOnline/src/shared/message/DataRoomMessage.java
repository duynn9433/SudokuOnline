/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shared.message;

import shared.model.PlayerInGame;
import server.controller.Room;
import shared.model.Sudoku;
import shared.constant.StreamData;

/**
 *
 * @author duynn
 */
public class DataRoomMessage extends Message{
    public static final long serialVersionUID = 7L;

    private String idRoom;
    private String status;
    private String codeMsg;
    
    private int[][] sudokuBoard;
    private int currentTick;
    
    private PlayerInGame player1;
    private PlayerInGame player2;

    public void printBoard(){
        for(int i =0;i<9;i++){
            for(int j=0;j<9;j++){
                System.out.print(sudokuBoard[i][j]+" ");
            }
            System.out.println("");
        }
    }
    public PlayerInGame getPlayer1() {
        return player1;
    }

    public int[][] getSudokuBoard() {
        return sudokuBoard;
    }

    public void setSudokuBoard(int[][] sudokuBoard) {
        this.sudokuBoard = sudokuBoard;
    }

    public void setPlayer1(PlayerInGame player1) {
        this.player1 = player1;
    }

    public PlayerInGame getPlayer2() {
        return player2;
    }

    public void setPlayer2(PlayerInGame player2) {
        this.player2 = player2;
    }

    public int getCurrentTick() {
        return currentTick;
    }

    public void setCurrentTick(int currentTick) {
        this.currentTick = currentTick;
    }

    
    public DataRoomMessage() {
        super(StreamData.Type.DATA_ROOM);
    }

    public String getIdRoom() {
        return idRoom;
    }

    public void setIdRoom(String idRoom) {
        this.idRoom = idRoom;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCodeMsg() {
        return codeMsg;
    }

    public void setCodeMsg(String codeMsg) {
        this.codeMsg = codeMsg;
    }

    @Override
    public String toString() {
        return "DataRoomMessage{" + "idRoom=" + idRoom + ", status=" + status + ", codeMsg=" + codeMsg + ", sudokuBoard=" + sudokuBoard + ", currentTick=" + currentTick + ", player1=" + player1 + ", player2=" + player2 + '}';
    }
    
    
    
    
}
