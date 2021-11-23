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
public class LockSubmitMessage extends Message{
    public static final long serialVersionUID = 9L;
    private String status;

    public LockSubmitMessage() {
        super(StreamData.Type.LOCK_SUBMIT);
        this.status = "success";
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
}
