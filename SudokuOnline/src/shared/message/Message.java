/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shared.message;

import java.io.Serializable;
import shared.constant.StreamData.Type;

/**
 *
 * @author duynn
 */
public class Message implements Serializable{
    public static final long serialVersionUID = 1L;
    protected Type type;
    protected String msg;
    protected String status;
    protected String codeMsg;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
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

    
    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Message(Type type) {
        this.type = type;
    }

    public Message() {
    }
    
    
}
