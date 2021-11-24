/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shared.message;

import java.util.ArrayList;
import shared.constant.StreamData;
import shared.model.Player;

/**
 *
 * @author Truong
 */
public class GetListRankMessage extends Message{
     public static final long serialVersionUID = 22L;
     private ArrayList<Player> listPalyer;

    public GetListRankMessage(ArrayList<Player> listPalyer, StreamData.Type type) {
        super(type);
        this.listPalyer = listPalyer;
    }
     
    public GetListRankMessage(ArrayList<Player> listPalyer) {
        this.listPalyer = listPalyer;
    }

    public GetListRankMessage() {
    }

    public ArrayList<Player> getListPalyer() {
        return listPalyer;
    }

    public void setListPalyer(ArrayList<Player> listPalyer) {
        this.listPalyer = listPalyer;
    }

    public StreamData.Type getType() {
        return type;
    }

    public void setType(StreamData.Type type) {
        this.type = type;
    }

    
     
}
