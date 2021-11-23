/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shared.message;

/**
 *
 * @author duynn
 */
public class SubmitMessage extends Message{
    private int[][] submit;
    private String status;
    private String result;//win / lose/ draw
    int currentTick;

    public int getCurrentTick() {
        return currentTick;
    }

    public void setCurrentTick(int currentTick) {
        this.currentTick = currentTick;
    }
    
    public SubmitMessage() {
    }

    public int[][] getSubmit() {
        return submit;
    }

    public void setSubmit(int[][] submit) {
        this.submit = submit;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
    
    
    
    
}
