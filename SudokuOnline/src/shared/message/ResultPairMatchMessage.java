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
public class ResultPairMatchMessage extends Message{
    public static final long serialVersionUID = 5L;
    private String codeMsg;
    private String status;

    public ResultPairMatchMessage() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    
    public String getCodeMsg() {
        return codeMsg;
    }

    public void setCodeMsg(String codeMsg) {
        this.codeMsg = codeMsg;
    }

    @Override
    public String toString() {
        return "ResultPairMatchMessage{" + "codeMsg=" + codeMsg + ", status=" + status + '}';
    }
    
    
}
