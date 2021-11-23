/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shared.message;

import shared.constant.StreamData;

/**
 *
 * @author Duy
 */
public class LogoutMessage extends Message{
    private String status;

    public LogoutMessage(String status, StreamData.Type type) {
        super(type);
        this.status = status;
    }

    public LogoutMessage(String status) {
        this.status = status;
    }

    public LogoutMessage(StreamData.Type type) {
        super(type);
    }
    

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public StreamData.Type getType() {
        return type;
    }

    public void setType(StreamData.Type type) {
        this.type = type;
    }
    

}
