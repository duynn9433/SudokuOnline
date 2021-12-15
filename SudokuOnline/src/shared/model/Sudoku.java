/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shared.model;

import java.io.Serializable;
import server.controller.DataCreateGame;
import shared.helper.CountDownTimer;

/**
 *
 * @author duynn
 */
public class Sudoku implements Serializable{

    public static final int ROW = 9, COL = 9;
    public static final int MATCH_TIME_LIMIT = 10;
//    public static final int MATCH_TIME_LIMIT = 30 * 60;

    Integer[][] board;
    String answer;
    boolean isSubmit;
    CountDownTimer matchTimer;
    int submitTime;

    public Sudoku() {
        isSubmit = false;
        answer = DataCreateGame.getRandomData();
        board = DataCreateGame.createBoard(answer);
        matchTimer = new CountDownTimer(MATCH_TIME_LIMIT);
    }

    public int getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(int submitTime) {
        this.submitTime = submitTime;
    }

    
    public Integer[][] getBoard() {
        return board;
    }

    public void setBoard(Integer[][] board) {
        this.board = board;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public boolean isIsSubmit() {
        return isSubmit;
    }

    public void setIsSubmit(boolean isSubmit) {
        this.isSubmit = isSubmit;
    }


    public void resumeTimer() {
        matchTimer.resume();
    }

    public void pauseTimer() {
        matchTimer.pause();
    }

    public void cancelTimer() {

        if (matchTimer != null) {
            matchTimer.cancel();
        }
    }

    public boolean CheckWin() {
        boolean check = true;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board[i][j] == 0) {
                    return false;
                }
            }
        }
        int A[][] = new int[9][10];
        int N = 0;
        for (int i = 0; i < 9; i++)
        	for (int j = 0; j < 9; j++)
        		A[i][j] = answer.charAt(i * 9 + j) - 48;
        
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board[i][j] != A[i][j]) {
                    check = false;
                }
            }
        }
        return check;
    }

    public CountDownTimer getMatchTimer() {
        return matchTimer;
    }

    public int getProgressMatchTimeValue() {
        return 100 * matchTimer.getCurrentTick() / MATCH_TIME_LIMIT;
    }
}
