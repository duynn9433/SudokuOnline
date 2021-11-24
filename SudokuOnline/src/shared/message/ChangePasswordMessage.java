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
public class ChangePasswordMessage extends Message {
    private String oldPassword;
    private String newPassword;
    private String status;
    private String codeMsg;

    public ChangePasswordMessage(String oldPassword, String newPassword, StreamData.Type type) {
        super(type);
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

    public ChangePasswordMessage(String oldPassword, String newPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

    public ChangePasswordMessage() {
    }
    
    

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public StreamData.Type getType() {
        return type;
    }

    public void setType(StreamData.Type type) {
        this.type = type;
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
