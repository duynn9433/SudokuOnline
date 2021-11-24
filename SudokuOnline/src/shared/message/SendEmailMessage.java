/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shared.message;

import shared.constant.StreamData;

/**
 *
 * @author Truong
 */
public class SendEmailMessage extends Message{
    public static final long serialVersionUID = 23L;
    private String email;

    public SendEmailMessage() {
    }

    public SendEmailMessage(String email, StreamData.Type type) {
        super(type);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public StreamData.Type getType() {
        return type;
    }

    public void setType(StreamData.Type type) {
        this.type = type;
    }
    
}
