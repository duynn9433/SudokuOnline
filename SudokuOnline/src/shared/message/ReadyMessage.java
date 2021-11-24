/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shared.message;

import shared.constant.StreamData;

/**
 *
 * @author duynn
 */
public class ReadyMessage extends Message{
    private String status;
    private boolean isReady;
    public ReadyMessage() {
        super(StreamData.Type.READY);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isIsReady() {
        return isReady;
    }

    public void setIsReady(boolean isReady) {
        this.isReady = isReady;
    }
    
    
    
}
