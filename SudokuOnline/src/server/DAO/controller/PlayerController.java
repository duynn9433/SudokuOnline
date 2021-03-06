/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.DAO.controller;

import server.DAO.DAOClass.PlayerDAO;
import shared.model.Player;
import java.util.ArrayList;
import shared.constant.Code;
import shared.constant.StreamData;
import shared.message.ChangePasswordMessage;
//import shared.message.LoginMessage;
import shared.message.PlayerMessage;
//import shared.message.SignupMessage;

/**
 *
 * @author duynn
 */
public class PlayerController {

    ArrayList<Player> listPlayer = new ArrayList<>();
    PlayerDAO playerDAO = new PlayerDAO();

    public PlayerController() {
        readDB();
    }

    public void readDB() {
        listPlayer = playerDAO.readDB();
    }

    public boolean add(Player p) {
        boolean status = playerDAO.add(p);

        if (status == true) {
            listPlayer.add(p);
        }

        return status;
    }

    public boolean delete(int id) {
        boolean status = playerDAO.delete(id);

        if (status == true) {
            for (int i = (listPlayer.size() - 1); i >= 0; i--) {
                if (listPlayer.get(i).getId() == id) {
                    listPlayer.remove(i);
                }
            }
        }

        return status;
    }

    public boolean update(Player p) {
        boolean status = playerDAO.update(p);

        if (status == true) {
            listPlayer.forEach((pl) -> {
                pl = new Player(p);
            });
        }

        return status;
    }

    public Player getById(int id) {
        for (Player p : listPlayer) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    public Player getByEmail(String email) {
        for (Player p : listPlayer) {
            if (p.getEmail().equals(email)) {
                return p;
            }
        }
        return null;
    }

    public ArrayList<Player> getList() {
        return listPlayer;
    }

    public PlayerMessage checkLogin(String email, String password) {
        // check email
        PlayerMessage msg = new PlayerMessage();
        Player p = getByEmail(email);
        if (p == null) {
            msg.setStatus("failed");
            msg.setCodeMsg(Code.ACCOUNT_NOT_FOUND);
            return msg;
        }

        // check password
        if (!p.getPassword().equals(password)) {
            msg.setStatus("failed");
            msg.setCodeMsg(Code.WRONG_PASSWORD);
            return msg;
        }

        // check blocked
        if (p.isBlocked()) {
            msg.setStatus("failed");
            msg.setCodeMsg(Code.ACCOUNT_BLOCKED);
            return msg;
        }

        msg.setStatus("success");
        msg.setMsg(email);
        return msg;
    }

    public ChangePasswordMessage changePassword(String email, String oldPassword, String newPassword) {
        // check email
        ChangePasswordMessage msg = new ChangePasswordMessage();
        Player p = getByEmail(email);
        if (p == null) {
            msg.setStatus("failed");
            msg.setCodeMsg(Code.ACCOUNT_NOT_FOUND);
            return msg;
        }

        // check password
        if (!p.getPassword().equals(oldPassword)) {
            msg.setStatus("failed");
            msg.setCodeMsg(Code.WRONG_PASSWORD);
            return msg;
        }

        // ?????t pass m???i
        p.setPassword(newPassword);
        boolean status = update(p);
        if (!status) {
            // l???i kh??ng x??c ?????nh
            msg.setStatus("failed");
            msg.setCodeMsg("L???i khi ?????i m???t kh???u");
        }

        msg.setStatus("success");
        return msg;
    }

    public PlayerMessage signup(String email, String password, String avatar, String name, String gender, int yearOfBirth) {

        // check email 
        PlayerMessage msg = new PlayerMessage();
        Player p = getByEmail(email);
        if (p != null) {
            msg.setStatus("failed");
            msg.setCodeMsg(Code.EMAIL_EXISTED);
            return msg;
        }

        // th??m v??o db
        boolean status = add(new Player(email, password, avatar, name, gender, yearOfBirth));
        if (!status) {
            // l???i ko x??c ?????nh
            msg.setStatus("failed");
            msg.setCodeMsg("L???i khi ????ng k??");
            return msg;
        }

//        return "success";
        msg.setStatus("success");
        Player p1 = new Player();
        p1.setEmail(email);
        p1.setAvatar(avatar);
        p1.setName(name);
        p1.setGender(gender);
        p1.setYearOfBirth(yearOfBirth);
        msg.setPlayer(p1);
        return msg;
    }

    public PlayerMessage editProfile(String email, String newEmail, String name, String avatar, int yearOfBirth, String gender) {
        // check trung email
      //  EditProfileMessage msg = new EditProfileMessage();
        PlayerMessage msg = new PlayerMessage();
        if (!newEmail.equals(email) && getByEmail(newEmail) != null) {
            msg.setStatus("failed");
            msg.setCodeMsg(Code.EMAIL_EXISTED);
            return msg;
        }

        // check email
        Player p = getByEmail(email);
        if (p == null) {
            msg.setStatus("failed");
            msg.setCodeMsg(Code.ACCOUNT_NOT_FOUND);
            return msg;
        }

        // set data
        p.setEmail(newEmail);
        p.setName(name);
        p.setAvatar(avatar);
        p.setYearOfBirth(yearOfBirth);
        p.setGender(gender);

        // update
        boolean status = update(p);

        if (!status) {
            msg.setStatus("failed");
            msg.setCodeMsg("L???i khi c???p nh???t th??ng tin c?? nh??n");
            return msg;

        }

        msg.setStatus("success");
        msg.setPlayer(p);
        return msg;
    }
}
