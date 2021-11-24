/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shared.message;

import shared.constant.StreamData;
import shared.model.ProfileData;

/**
 *
 * @author Duy
 */
public class ProfileMessage extends Message {
    private String email;
    private String status;
    private String codeMsg;
    private ProfileData profileData;

    public ProfileMessage(StreamData.Type type, String status, String codeMsg) {
        super(type);
        this.status = status;
        this.codeMsg = codeMsg;
    }
    
    
    public ProfileMessage(String email, StreamData.Type type) {
        super(type);
        this.email = email;
    }

    public ProfileMessage(String email) {
        this.email = email;
    }

    public ProfileMessage() {
    }
    

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public StreamData.Type getType() {
        return type;
    }

    public void setType(StreamData.Type type) {
        this.type = type;
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

    public ProfileData getProfileData() {
        return profileData;
    }

    public void setProfileData(ProfileData profileData) {
        this.profileData = profileData;
    }
    
    
}
