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
public class FindMatchMessage extends Message{
    public static final long serialVersionUID = 3L;
    private String status;
    private String codeMsg;
    private String idRoom;

    public FindMatchMessage() {
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

    public String getIdRoom() {
        return idRoom;
    }

    public void setIdRoom(String idRoom) {
        this.idRoom = idRoom;
    }

    public StreamData.Type getType() {
        return type;
    }

    public void setType(StreamData.Type type) {
        this.type = type;
    }
    
    
}
