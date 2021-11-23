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
public class LeaveRoomMessage extends Message{
    public static final long serialVersionUID = 10L;
    private String status;
    private String codeMsg;

    public LeaveRoomMessage() {
        super(StreamData.Type.LEAVE_ROOM);
        
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
    
    
    
}
