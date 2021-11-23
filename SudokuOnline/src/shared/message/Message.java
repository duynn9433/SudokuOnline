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
