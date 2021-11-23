/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.DAO.controller;

import server.DAO.DAOClass.PlayerDAO;
import server.DAO.model.Player;
import java.util.ArrayList;
import shared.constant.Code;
import shared.constant.StreamData;
import shared.message.LoginMessage;

/**
 *
 * @author duynn
 */
public class PlayerController {

    ArrayList<Player> listPlayer = new ArrayList<>();
    PlayerDAO playerDAL = new PlayerDAO();

    public PlayerController() {
        readDB();
    }

    public void readDB() {
        listPlayer = playerDAL.readDB();
    }

    public boolean add(Player p) {
        boolean status = playerDAL.add(p);

        if (status == true) {
            listPlayer.add(p);
        }

        return status;
    }

    public boolean delete(int id) {
        boolean status = playerDAL.delete(id);

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
        boolean status = playerDAL.update(p);

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

    public LoginMessage checkLogin(String email, String password) {
        // code vòng for như getByEmail là được, nhưng netbeans nó hiện bóng đèn sáng ấn vào thì ra code này
        // thấy "ngầu" nên để lại :))
        // return listPlayer.stream().anyMatch((p) -> (p.getEmail().equals(email) && p.getPassword().equals(password)));
        // nhưng chợt nhận ra có block player nữa, nên phải trả về String chứ ko được boolean :(

        // check email
        LoginMessage msg = new LoginMessage();
        Player p = getByEmail(email);
        if (p == null) {
            msg.setStatus("failed");
            msg.setCodeMsg(Code.ACCOUNT_NOT_FOUND);
            //return "failed;" + Code.ACCOUNT_NOT_FOUND;
            return msg;
        }

        // check password
        if (!p.getPassword().equals(password)) {
            msg.setStatus("failed");
            msg.setCodeMsg(Code.WRONG_PASSWORD);
            //return "failed;" + Code.WRONG_PASSWORD;
            return msg;
        }

        // check blocked
        if (p.isBlocked()) {
            msg.setStatus("failed");
            msg.setCodeMsg(Code.ACCOUNT_BLOCKED);
//            return "failed;" + Code.ACCOUNT_BLOCKED;
            return msg;
        }

        msg.setStatus("success");
        msg.setEmail(email);
        return msg;
    }

    public String changePassword(String email, String oldPassword, String newPassword) {
        // check email
        Player p = getByEmail(email);
        if (p == null) {
            return "failed;" + Code.ACCOUNT_NOT_FOUND;
        }

        // check password
        if (!p.getPassword().equals(oldPassword)) {
            return "failed;" + Code.WRONG_PASSWORD;
        }

        // đặt pass mới
        p.setPassword(newPassword);
        boolean status = update(p);
        if (!status) {
            // lỗi không xác định
            return "failed;Lỗi khi đổi mật khẩu";
        }

        return "success";
    }

    public String signup(String email, String password, String avatar, String name, String gender, int yearOfBirth) {

        // check email 
        Player p = getByEmail(email);
        if (p != null) {
            return "failed;" + Code.EMAIL_EXISTED;
        }

        // thêm vào db
        boolean status = add(new Player(email, password, avatar, name, gender, yearOfBirth));
        if (!status) {
            // lỗi ko xác định
            return "failed;Lỗi khi đăng ký";
        }

        return "success";
    }

    public String editProfile(String email, String newEmail, String name, String avatar, int yearOfBirth, String gender) {
        // check trung email
        if (!newEmail.equals(email) && getByEmail(newEmail) != null) {
            return "failed;" + Code.EMAIL_EXISTED;
        }

        // check email
        Player p = getByEmail(email);
        if (p == null) {
            return "failed;" + Code.ACCOUNT_NOT_FOUND;
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
            return "failed;Lỗi khi cập nhật thông tin cá nhân";
        }

        return "success;" + newEmail;
    }
}
