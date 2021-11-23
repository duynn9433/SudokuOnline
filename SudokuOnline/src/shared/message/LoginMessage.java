/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shared.message;

import shared.constant.Code;
import shared.constant.StreamData;

/**
 *
 * @author duynn
 */
public class LoginMessage extends Message{
    public static final long serialVersionUID = 2L;
    private String email;
    private String password;
    
    private String status; //faile or success
    private String codeMsg;

    public LoginMessage(String email, String password, String status, String code, StreamData.Type type) {
        super(type);
        this.email = email;
        this.password = password;
        this.status = status;
        this.codeMsg = code;
    }

    public LoginMessage(String email, String password, String status, String code) {
        this.email = email;
        this.password = password;
        this.status = status;
        this.codeMsg = code;
    }
    

    
    public LoginMessage( StreamData.Type type,String email, String password) {
        super(type);
        this.email = email;
        this.password = password;
    }

    public LoginMessage(String status, String code, StreamData.Type type) {
        super(type);
        this.status = status;
        this.codeMsg = code;
    }

    public LoginMessage() {
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



    public StreamData.Type getType() {
        return type;
    }

    public void setType(StreamData.Type type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "LoginMessage{" + "email=" + email + ", password=" + password + ", status=" + status + ", codeMsg=" + codeMsg + '}';
    }

    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
}
