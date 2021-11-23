/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shared.message;

/**
 *
 * @author duynn
 */
public class RequestPairMatchMessage extends Message{
    public static final long serialVersionUID = 4L;
    private int idPlayer;
    private String namePlayer;
    private String answer;  //no or yes

    public RequestPairMatchMessage() {
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    
    public int getIdPlayer() {
        return idPlayer;
    }

    public void setIdPlayer(int idPlayer) {
        this.idPlayer = idPlayer;
    }

    public String getNamePlayer() {
        return namePlayer;
    }

    public void setNamePlayer(String namePlayer) {
        this.namePlayer = namePlayer;
    }

    @Override
    public String toString() {
        return "RequestPairMatchMessage{" + "idPlayer=" + idPlayer + ", namePlayer=" + namePlayer + ", answer=" + answer + '}';
    }
    
    
}
