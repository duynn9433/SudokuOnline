/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shared.message;

import java.io.Serializable;

/**
 *
 * @author duynn
 */
public class SubmitMessage extends Message implements Serializable{
    public static final long serialVersionUID = 8L;
    private Integer[][] submit;
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
    
    public void printSubmit(){
        for(int i=0;i<submit.length;i++){
            for(int j=0;j<submit[i].length;j++){
                System.out.print(submit[i][j]+ " ");
            }
            System.out.println("");
        }
    }
    public Integer[][] getSubmit() {
        return submit;
    }

    public void setSubmit(Integer[][] submit) {
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
