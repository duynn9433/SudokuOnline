/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shared.message;

import shared.constant.StreamData;
import shared.model.ChatItem;

/**
 *
 * @author duynn
 */
public class ChatRoomMessage extends Message{
    public static final long serialVersionUID = 11L;
    private ChatItem chatItem;

    public ChatRoomMessage() {
        super(StreamData.Type.CHAT_ROOM);
    }

    public ChatRoomMessage(ChatItem chatItem) {
        super(StreamData.Type.CHAT_ROOM);
        this.chatItem = chatItem;
    }

    
    public ChatItem getChatItem() {
        return chatItem;
    }

    public void setChatItem(ChatItem chatItem) {
        this.chatItem = chatItem;
    }
    
    
    
}
