package shared.model;

import java.io.Serializable;

/**
 *
 * @author duynn
 * Thể hiện thông tin player không có thông tin nhạy cảm
 */
public class PlayerInGame implements Serializable{

    String email;
    String nameId;
    String avatar;

    public PlayerInGame(String email, String nameId, String avatar) {
        this.email = email;
        this.nameId = nameId;
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return nameId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNameId() {
        return nameId;
    }

    public void setNameId(String nameId) {
        this.nameId = nameId;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

}
