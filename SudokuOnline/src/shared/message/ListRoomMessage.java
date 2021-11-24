/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shared.message;

import shared.constant.StreamData;

/**
 *
 * @author Bui Quang Dam
 */
public class ListRoomMessage extends Message{
    private int roomCount;
    private String pairData;
    private String roomID;
    private String status;

    public ListRoomMessage(StreamData.Type type) {
        super(type);
    }

    public ListRoomMessage() {
    }

    public ListRoomMessage(int roomCount, String pairData, String roomID, String status, StreamData.Type type) {
        super(type);
        this.roomCount = roomCount;
        this.pairData = pairData;
        this.roomID = roomID;
        this.status = status;
    }

    public int getRoomCount() {
        return roomCount;
    }

    public void setRoomCount(int roomCount) {
        this.roomCount = roomCount;
    }

    public String getPairData() {
        return pairData;
    }

    public void setPairData(String pairData) {
        this.pairData = pairData;
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
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
