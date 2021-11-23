/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shared.message;

import shared.constant.Code;
import shared.constant.StreamData;

/**
 *
 * @author Duy
 */
public class SignupMessage extends Message {
    public static final long serialVersionUID = 2L;
    private String email;
    private String password;
    private String name;
    private String gender;
    private int yearOfBirth; 
    private String avatar;
    
    
    private String status; //faile or success
    private String codeMsg;
    
    public SignupMessage(String email, String password, String status, String codeMsg, StreamData.Type type) {
        super(type);
        this.email = email;
        this.password = password;
        this.status = status;
        this.codeMsg = codeMsg;
    }

    public SignupMessage(String email, String password, String status, String codeMsg) {
        this.email = email;
        this.password = password;
        this.status = status;
        this.codeMsg = codeMsg;
    }

    public SignupMessage(StreamData.Type type, String email, String password, String name, String gender, int yearOfBirth, String avatar) {
        super(type);
        this.email = email;
        this.password = password;
        this.name = name;
        this.gender = gender;
        this.yearOfBirth = yearOfBirth;
        this.avatar = avatar;
    }
    
    public SignupMessage(String status, String code, StreamData.Type type) {
        super(type);
        this.status = status;
        this.codeMsg = code;
    }

    public SignupMessage() {
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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public StreamData.Type getType() {
        return type;
    }

    public void setType(StreamData.Type type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "SignupMessage{" + "email=" + email + ", password=" + password + ", status=" + status + ", codeMsg=" + codeMsg + '}';
    }
    
 
}
