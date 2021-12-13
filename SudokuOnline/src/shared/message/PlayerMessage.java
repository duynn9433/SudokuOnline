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
 * @author duynn
 */
public class PlayerMessage extends Message{
    private Player player;

    public PlayerMessage() {
    }

    public PlayerMessage(StreamData.Type type) {
        super(type);
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
    
    
    
}
