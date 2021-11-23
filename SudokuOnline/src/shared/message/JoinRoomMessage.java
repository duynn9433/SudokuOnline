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
public class JoinRoomMessage extends Message{
    public static final long serialVersionUID = 6L;
    private String status;
    private String codeMsg;
    private String idRoom;

    public JoinRoomMessage() {
        super(StreamData.Type.JOIN_ROOM);
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

    @Override
    public String toString() {
        return "JoinRoomMessage{" + "status=" + status + ", codeMsg=" + codeMsg + ", idRoom=" + idRoom + '}';
    }
    
    
    
}
