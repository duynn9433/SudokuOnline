/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shared.model;

import java.io.Serializable;
import shared.model.PlayerInGame;

/**
 *
 * @author duynn
 */
public class Player implements Serializable{

    int id;
    String email;
    String password;
    String avatar;
    String name;
    String gender;
    int yearOfBirth;
    int score = 0; // gia tri mac dinh
    int matchCount = 0;
    int winCount = 0;
    int loseCount = 0;
    boolean blocked = false;

    public Player() {

    }

    public void addScore(int toAdd) {
        this.score += toAdd;
    }

    public Player(int id, String email, String password, String avatar, String name, String gender, int yearOfBirth, int score, int matchCount, int winCount, int loseCount, boolean blocked) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.avatar = avatar;
        this.name = name;
        this.gender = gender;
        this.yearOfBirth = yearOfBirth;
        this.score = score;
        this.matchCount = matchCount;
        this.winCount = winCount;
        this.loseCount = loseCount;
        this.blocked = blocked;
    }

    public Player(String email, String password, String avatar, String name, String gender, int yearOfBirth) {
        this.email = email;
        this.password = password;
        this.avatar = avatar;
        this.name = name;
        this.gender = gender;
        this.yearOfBirth = yearOfBirth;
    }

    public Player(Player p) {
        this.id = p.id;
        this.email = p.email;
        this.password = p.password;
        this.avatar = p.avatar;
        this.name = p.name;
        this.gender = p.gender;
        this.yearOfBirth = p.yearOfBirth;
        this.score = p.score;
        this.matchCount = p.matchCount;
        this.winCount = p.winCount;
        this.loseCount = p.loseCount;
        this.blocked = p.blocked;
    }

    public int calculateTieCount() {
        return matchCount - winCount - loseCount;
    }
    public float calWinRate(){
        if(matchCount==0) return 0;
        return (float)(winCount/matchCount)*100;
    }

    public float calculateWinRate() {
        if (this.matchCount == 0) {
            return 0;
        }

        return (float) (100.0 * winCount / matchCount);
    }

    public String getNameId() {
        return name + " #" + id;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getYearOfBirth() {
        return yearOfBirth;
    }

    public void setYearOfBirth(int yearOfBirth) {
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

    public int getWinCount() {
        return winCount;
    }

    public void setWinCount(int winCount) {
        this.winCount = winCount;
    }

    public int getLoseCount() {
        return loseCount;
    }

    public void setLoseCount(int loseCount) {
        this.loseCount = loseCount;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }
    public PlayerInGame toPlayerInGame(){
        return new PlayerInGame(email, name, avatar);
    }
}
