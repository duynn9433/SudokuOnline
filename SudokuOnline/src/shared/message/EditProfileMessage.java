package shared.message;


import shared.constant.StreamData;
import shared.message.Message;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Duy
 */
public class EditProfileMessage extends Message {
    private String email;
    private String name;
    private String avatar;
    private String yearOfBirth;
    private String gender;
    private String status;
    private String codeMsg;

    public EditProfileMessage(StreamData.Type type, String email, String name, String avatar, String yearOfBirth, String gender, String status, String codeMsg) {
        super(type);
        this.email = email;
        this.name = name;
        this.avatar = avatar;
        this.yearOfBirth = yearOfBirth;
        this.gender = gender;
        this.status = status;
        this.codeMsg = codeMsg;
    }

    
    public EditProfileMessage() {
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

    public String getYearOfBirth() {
        return yearOfBirth;
    }

    public void setYearOfBirth(String yearOfBirth) {
        this.yearOfBirth = yearOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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

    public StreamData.Type getType() {
        return type;
    }

    public void setType(StreamData.Type type) {
        this.type = type;
    }
    
    
}
