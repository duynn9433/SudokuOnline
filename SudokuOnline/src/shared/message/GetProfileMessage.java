/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shared.message;

import shared.constant.StreamData;
import shared.model.Player;

/**
 *
 * @author Truong
 */
public class GetProfileMessage extends Message{
     public static final long serialVersionUID = 20L;
     private Player player;

    public GetProfileMessage(Player player, StreamData.Type type) {
        super(type);
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public StreamData.Type getType() {
        return type;
    }

    public void setType(StreamData.Type type) {
        this.type = type;
    }
     
     
}
