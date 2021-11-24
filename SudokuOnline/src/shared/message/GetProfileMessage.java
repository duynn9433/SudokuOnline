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
public class GetProfileMessage extends Message{
     public static final long serialVersionUID = 20L;
     private int id;
     private String email;
     private String name;
     private String avatar;
     private String gender;
     private String yearOfBirth;
     private int score;
     private int matchCount;
     private int win;
     private int tie;
     private int lose;
     private int winRate;

    public GetProfileMessage() {
    }

    public GetProfileMessage(int id, String email, String name, String avatar, String gender, String yearOfBirth, int score, int matchCount, int win, int tie, int lose, int winRate, StreamData.Type type) {
        super(type);
        this.id = id;
        this.email = email;
        this.name = name;
        this.avatar = avatar;
        this.gender = gender;
        this.yearOfBirth = yearOfBirth;
        this.score = score;
        this.matchCount = matchCount;
        this.win = win;
        this.tie = tie;
        this.lose = lose;
        this.winRate = winRate;
    }
    
    public GetProfileMessage(int id, String email, String name, String avatar, String gender, String yearOfBirth, int score, int matchCount, int win, int tie, int lose, int winRate) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.avatar = avatar;
        this.gender = gender;
        this.yearOfBirth = yearOfBirth;
        this.score = score;
        this.matchCount = matchCount;
        this.win = win;
        this.tie = tie;
        this.lose = lose;
        this.winRate = winRate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getYearOfBirth() {
        return yearOfBirth;
    }

    public void setYearOfBirth(String yearOfBirth) {
        this.yearOfBirth = yearOfBirth;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getMatchCount() {
        return matchCount;
    }

    public void setMatchCount(int matchCount) {
        this.matchCount = matchCount;
    }

    public int getWin() {
        return win;
    }

    public void setWin(int win) {
        this.win = win;
    }

    public int getTie() {
        return tie;
    }

    public void setTie(int tie) {
        this.tie = tie;
    }

    public int getLose() {
        return lose;
    }

    public void setLose(int lose) {
        this.lose = lose;
    }

    public int getWinRate() {
        return winRate;
    }

    public void setWinRate(int winRate) {
        this.winRate = winRate;
    }

    public StreamData.Type getType() {
        return type;
    }

    public void setType(StreamData.Type type) {
        this.type = type;
    }
     
}
