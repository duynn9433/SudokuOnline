/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.controller;

import shared.model.ProfileData;
import shared.model.ChatItem;
import client.RunClient;
import client.view.scene.MainMenu;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.*;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import shared.model.Sudoku;
import shared.constant.StreamData;
import shared.message.*;
import shared.model.PlayerInGame;

/**
 *
 * @author duynn
 */
public class SocketHandler {

    Socket s;
    DataInputStream dis;
    DataOutputStream dos;
    ObjectInputStream ois = null;
    ObjectOutputStream oos = null;

    String loginEmail = null; // lưu tài khoản đăng nhập hiện tại
    String roomId = null; // lưu room hiện tại

    Thread listener = null;

    public String connect(String addr, int port) {
        try {
            // getting ip 
            InetAddress ip = InetAddress.getByName(addr);

            // establish the connection with server port 
            s = new Socket();
            s.connect(new InetSocketAddress(ip, port), 4000);
            System.out.println("Connected to " + ip + ":" + port + ", localport:" + s.getLocalPort());
            System.out.println(s);
            // obtaining input and output streams
//            dis = new DataInputStream(s.getInputStream());
            System.out.println("dis:" + dis);
//            dos = new DataOutputStream(s.getOutputStream());
            System.out.println("dos:" + dos);
            System.out.println("init ois oos");
            try {
                oos = new ObjectOutputStream(s.getOutputStream());
                System.out.println("oos:" + oos);
                oos.flush();
                ois = new ObjectInputStream(s.getInputStream());
                System.out.println("ois:" + ois);
            } catch (IOException e) {
                System.out.println(e.getMessage());

            }

            System.out.println("end inti");

            // close old listener
            if (listener != null && listener.isAlive()) {
                listener.interrupt();
            }

            // listen to server
            listener = new Thread(this::listen);
            listener.start();

            // connnect request 
            System.out.println("initConnect");
            initConnect();

            // connect success
            return "success";

        } catch (IOException e) {

            // connect failed
            return "failed;" + e.getMessage();
        }
    }

    private void listen() {
        boolean running = true;
        Message message;
        while (running) {
            try {
                // receive the data from server
                message = (Message) ois.readObject();
                String received = message.getType().name();

                System.out.println("RECEIVED: " + received);

                // process received data
                StreamData.Type type = StreamData.getTypeFromData(received);
                System.out.println("C received:" + type);
                switch (type) {
                    case CONNECT:
                        onReceiveConnect();
                        break;

                    case LOGIN:
                        onReceiveLogin(message);
                        break;

                    case SIGNUP:
                        onReceiveSignup(message);  //chua sua
                        break;

                    case LOGOUT:
                        onReceiveLogout(message);  //chua sua
                        break;

                    case LIST_ROOM:
                        onReceiveListRoom(received);    //can lien quan den phong
                        break;

                    case LIST_ONLINE:
                        onReceiveListOnline(received);
                        break;

                    case CREATE_ROOM:
                        onReceiveCreateRoom(received);  //can lien quan den phong
                        break;

                    case JOIN_ROOM:
                        onReceiveJoinRoom(message);
                        break;

                    case FIND_MATCH:
                        onReceiveFindMatch(message);
                        break;

                    case CANCEL_FIND_MATCH:
                        onReceiveCancelFindMatch(message);
                        break;

                    case REQUEST_PAIR_MATCH:
                        onReceiveRequestPairMatch(message);
                        break;

                    case RESULT_PAIR_MATCH:
                        onReceiveResultPairMatch(message);
                        break;

                    case DATA_ROOM:
                        onReceiveDataRoom(message);
                        break;

                    case CHAT_ROOM:
                        onReceiveChatRoom(received);    //tinh nang chat
                        break;

                    case LEAVE_ROOM:
                        onReceiveLeaveRoom(message);
                        break;

                    case CLOSE_ROOM:
                        onReceiveCloseRoom(received);
                        break;

                    case GET_PROFILE:
                        onReceiveGetProfile(received);
                        break;

                    case EDIT_PROFILE:
                        onReceivedEditProfile(received);
                        break;

                    case CHANGE_PASSWORD:
                        onReceiveChangePassword(received);
                        break;

                    case SUBMIT:
                        onReceiveSubmit(message);
                        break;
                    case LOCK_SUBMIT:
                        onReceiveLockSubmit(message);
                        break;
                        
                    case EXIT:
                        running = false;
                }

            } catch (IOException ex) {
                Logger.getLogger(SocketHandler.class.getName()).log(Level.SEVERE, null, ex);
                running = false;
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(SocketHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        try {
            s.close();
//            dis.close();
        //    dos.close();
            ois.close();
            oos.close();
        } catch (IOException ex) {
            Logger.getLogger(SocketHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

        // alert if connect interup
        JOptionPane.showMessageDialog(null, "Mất kết nối tới server", "Lỗi", JOptionPane.ERROR_MESSAGE);
        RunClient.closeAllScene();
        RunClient.openScene(RunClient.SceneName.CONNECTSERVER);
    }

    // ================ listen events ============
    // auth
    private void onReceiveConnect() {
        // tắt scene connectServer
        RunClient.closeScene(RunClient.SceneName.CONNECTSERVER);
        // mở scene login
        RunClient.openScene(RunClient.SceneName.LOGIN);
    }

    private void onReceiveLogin(Object message) {
        LoginMessage msg = (LoginMessage) message;
        // get status from data
        String status = msg.getStatus();

        // turn off loading
        RunClient.loginScene.setLoading(false);

        if (status.equals("failed")) {
            // hiển thị lỗi
            String failedMsg = msg.getCodeMsg();
            JOptionPane.showMessageDialog(RunClient.loginScene, failedMsg, "Lỗi", JOptionPane.ERROR_MESSAGE);

        } else if (status.equals("success")) {
            // lưu email login
            this.loginEmail = msg.getEmail();

            // chuyển scene
            RunClient.closeScene(RunClient.SceneName.LOGIN);
            RunClient.openScene(RunClient.SceneName.MAINMENU);

            // tự động lấy danh sách phòng
//            listRoom();
        }
    }

    private void onReceiveSignup(Object message) {
        // get status from data
        SignupMessage msg = (SignupMessage) message;
        String status = msg.getStatus();

        // turn off loading
        RunClient.signupScene.setLoading(false);

        // check status
        if (status.equals("failed")) {
            String failedMsg = msg.getCodeMsg();
            JOptionPane.showMessageDialog(RunClient.signupScene, failedMsg, "Lỗi", JOptionPane.ERROR_MESSAGE);

        } else if (status.equals("success")) {
            JOptionPane.showMessageDialog(RunClient.signupScene, "Đăng ký thành công", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            RunClient.closeScene(RunClient.SceneName.SIGNUP);
            RunClient.openScene(RunClient.SceneName.LOGIN);
        }
    }

    private void onReceiveLogout(Object message) {
       // xóa email login
        LogoutMessage msg = (LogoutMessage) message;
        this.loginEmail = null;

        // chuyển scene
        RunClient.closeAllScene();
        RunClient.openScene(RunClient.SceneName.LOGIN);
    }

    // main menu
    private void onReceiveListRoom(String received) {
        String[] splitted = received.split(";");
        String status = splitted[1];

        if (status.equals("failed")) {

        } else if (status.equals("success")) {
            int roomCount = Integer.parseInt(splitted[2]);

            // https://niithanoi.edu.vn/huong-dan-thao-tac-voi-jtable-lap-trinh-java-swing.html
            Vector vheader = new Vector();
            vheader.add("Mã");
            vheader.add("Cặp đấu");
            vheader.add("Số người");

            Vector vdata = new Vector();

            // i += 3: 3 là số cột trong bảng
            // i = 3; i < roomCount + 3: dữ liệu phòng bắt đầu từ index 3 trong mảng splitted
            for (int i = 3; i < roomCount + 3; i += 3) {

                String roomId = splitted[i];
                String title = splitted[i + 1];
                String clientCount = splitted[i + 2];

                Vector vrow = new Vector();
                vrow.add(roomId);
                vrow.add(title);
                vrow.add(clientCount);

                vdata.add(vrow);
            }

            RunClient.mainMenuScene.setListRoom(vdata, vheader);
        }
    }

    private void onReceiveListOnline(String received) {

    }

    private void onReceiveCreateRoom(String received) {

    }

    private void onReceiveJoinRoom(Object message) {
        JoinRoomMessage msg = (JoinRoomMessage) message;
        msg.toString();
        String roomId = msg.getIdRoom();

        // save room id
        this.roomId = roomId;
        System.out.println("Room id JOIN_ROOM:" +roomId);

        // change scene
        RunClient.closeScene(RunClient.SceneName.MAINMENU);
        RunClient.openScene(RunClient.SceneName.INGAME);
        RunClient.inGameScene.setTitle("Phòng #" + roomId);

        // get room data
        dataRoom(roomId);
    }

    // pair match
    private void onReceiveFindMatch(Object message) {
        FindMatchMessage msg = (FindMatchMessage) message;
        String status = msg.getStatus();

        // check status
        if (status.equals("failed")) {
            String failedMsg = msg.getCodeMsg();
            JOptionPane.showMessageDialog(RunClient.mainMenuScene, failedMsg, "Lỗi", JOptionPane.ERROR_MESSAGE);

        } else if (status.equals("success")) {
            RunClient.mainMenuScene.setDisplayState(MainMenu.State.FINDING_MATCH);
        }
    }

    private void onReceiveCancelFindMatch(Object message) {
        FindMatchMessage msg = (FindMatchMessage) message;
        String status = msg.getStatus();

        // check status
        if (status.equals("failed")) {
            String failedMsg = msg.getCodeMsg();
            JOptionPane.showMessageDialog(RunClient.mainMenuScene, failedMsg, "Lỗi", JOptionPane.ERROR_MESSAGE);

        } else if (status.equals("success")) {
            RunClient.mainMenuScene.setDisplayState(MainMenu.State.DEFAULT);
        }
    }

    private void onReceiveRequestPairMatch(Object message) {
        RequestPairMatchMessage msg = (RequestPairMatchMessage) message;
        String competitorName = msg.getNamePlayer();
        int competitorID = msg.getIdPlayer();

        // show data
        RunClient.mainMenuScene.foundMatch((competitorName + "(" + competitorID + ")"));
    }

    private void onReceiveResultPairMatch(Object message) {
        ResultPairMatchMessage msg = (ResultPairMatchMessage) message;
        System.out.println("ResultPM:" + msg.toString());
        String status = msg.getStatus();

        // reset display state of main menu
        RunClient.mainMenuScene.setDisplayState(MainMenu.State.DEFAULT);

        if (status.equals("failed")) {
            String failedMsg = msg.getCodeMsg();
            JOptionPane.showMessageDialog(RunClient.mainMenuScene, failedMsg, "Không thể ghép trận", JOptionPane.ERROR_MESSAGE);

        } else if (status.equals("success")) {
            // System.out.println("Ghép trận thành công, đang chờ server cho vào phòng.");
        }
    }

    // in game
    private void onReceiveDataRoom(Object message) {
        DataRoomMessage msg = (DataRoomMessage) message;
        System.out.println("DataRoom:"+msg.toString());
        String status = msg.getStatus();

        if (status.equals("failed")) {
            String failedMsg = msg.getCodeMsg();
            JOptionPane.showMessageDialog(RunClient.mainMenuScene, failedMsg, "Không lấy được dữ liệu phòng", JOptionPane.ERROR_MESSAGE);

        } else if (status.equals("success")) {
            // player
            PlayerInGame p1 = msg.getPlayer1();
            PlayerInGame p2 = msg.getPlayer2();
            RunClient.inGameScene.setPlayerInGame(p1, p2);
            RunClient.inGameScene.setBoard(msg.getSudokuBoard());
            msg.printBoard();

            // timer data
            int matchTimerLimit = Sudoku.MATCH_TIME_LIMIT;

            RunClient.inGameScene.startGame(matchTimerLimit);

        }
    }

    private void onReceiveChatRoom(String received) {
        String[] splitted = received.split(";");
        ChatItem c = new ChatItem(splitted[1], splitted[2], splitted[3]);
        RunClient.inGameScene.addChat(c);
    }

    private void onReceiveLeaveRoom(Object message) {
        LeaveRoomMessage msg = (LeaveRoomMessage) message;
        String status = msg.getStatus();
        System.out.println("status leave room:"+status);

        if (status.equals("failed")) {
            String failedMsg = msg.getCodeMsg();
            JOptionPane.showMessageDialog(RunClient.inGameScene, failedMsg, "Không thể thoát phòng", JOptionPane.ERROR_MESSAGE);

        } else if (status.equals("success")) {
            RunClient.closeScene(RunClient.SceneName.INGAME);
            RunClient.openScene(RunClient.SceneName.MAINMENU);

            // get list room again
            listRoom();
        }
    }

    private void onReceiveCloseRoom(String received) {
        String[] splitted = received.split(";");
        String reason = splitted[1];

        // change scene
        RunClient.closeScene(RunClient.SceneName.INGAME);
        RunClient.openScene(RunClient.SceneName.MAINMENU);

        // show noti
        JOptionPane.showMessageDialog(
                RunClient.profileScene,
                "Phòng " + this.roomId + " đã đóng do " + reason, "Đóng",
                JOptionPane.INFORMATION_MESSAGE
        );

        // remove room id
        this.roomId = null;
    }

    // profile
    private void onReceiveGetProfile(String received) {
        String[] splitted = received.split(";");
        String status = splitted[1];

        // turn off loading
        RunClient.profileScene.setLoading(false);

        if (status.equals("failed")) {
            String failedMsg = splitted[2];
            JOptionPane.showMessageDialog(RunClient.profileScene, failedMsg, "Lỗi", JOptionPane.ERROR_MESSAGE);

        } else if (status.equals("success")) {
            // get player data from received
            ProfileData p = new ProfileData(
                    Integer.parseInt(splitted[2]), // id
                    splitted[3], // email
                    splitted[4], // name
                    splitted[5], // avatar
                    splitted[6], // gender
                    Integer.parseInt(splitted[7]), // year of birth
                    Integer.parseInt(splitted[8]), // score
                    Integer.parseInt(splitted[9]), // match count
                    Integer.parseInt(splitted[10]), // win count
                    Integer.parseInt(splitted[11]), // tie count
                    Integer.parseInt(splitted[12]), // lose count
                    Integer.parseInt(splitted[13]), // current streak
                    Float.parseFloat(splitted[14])); // win rate

            // show data to UI
            RunClient.profileScene.setProfileData(p);
        }
    }

    private void onReceivedEditProfile(String received) {
        String[] splitted = received.split(";");
        String status = splitted[1];

        // turn off loading
        RunClient.profileScene.setProfileSaveLoading(false);

        if (status.equals("failed")) {
            String failedMsg = splitted[2];
            JOptionPane.showMessageDialog(RunClient.profileScene, failedMsg, "Lỗi", JOptionPane.ERROR_MESSAGE);

        } else if (status.equals("success")) {
            JOptionPane.showMessageDialog(RunClient.profileScene, "Đổi thông tin thành công", "Thành công", JOptionPane.INFORMATION_MESSAGE);

            // lưu lại email
            this.loginEmail = splitted[2];

            // load lại thông tin cá nhân mới - có thể ko cần! nhưng cứ load lại cho chắc
            getProfile(this.loginEmail);
        }
    }

    private void onReceiveChangePassword(String received) {
        String[] splitted = received.split(";");
        String status = splitted[1];

        // turn off loading
        RunClient.changePasswordScene.setLoading(false);

        // check status
        if (status.equals("failed")) {
            String failedMsg = splitted[2];
            JOptionPane.showMessageDialog(RunClient.changePasswordScene, failedMsg, "Lỗi", JOptionPane.ERROR_MESSAGE);

        } else if (status.equals("success")) {
            RunClient.closeScene(RunClient.SceneName.CHANGEPASSWORD);
            JOptionPane.showMessageDialog(RunClient.changePasswordScene, "Đổi mật khẩu thành công", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // game events
    public void onReceiveSubmit(Object message) {
        SubmitMessage msg = (SubmitMessage) message;
        String result = msg.getResult();
        RunClient.inGameScene.setWin(result);
    }
    private void onReceiveLockSubmit(Object message){
        LockSubmitMessage msg = (LockSubmitMessage) message;
        String status = msg.getStatus();
        if(status.equals("success")){
            RunClient.inGameScene.lockSubmit();
        }
    }
    // ============= functions ===============
    // auth
    private void initConnect() {
        // send to server
        System.out.println("C send Connect");
        sendObject(new Message(StreamData.Type.CONNECT));
        //sendPureData(StreamData.Type.CONNECT.name());
    }

    public void login(String email, String password) {
        // prepare data
        LoginMessage data = new LoginMessage(StreamData.Type.LOGIN, email, password);

        // send data
        sendObject(data);
        //sendData(data);
    }

    public void signup(String email, String password, String name, String gender, int yearOfBirth, String avatar) {
        // prepare data
        SignupMessage data = new SignupMessage(StreamData.Type.SIGNUP, email, password, name, gender, yearOfBirth, avatar);
        // send data
        sendObject(data);
    }

    public void logout() {
        // prepare data
        LogoutMessage data = new LogoutMessage(StreamData.Type.LOGOUT);

        // send data
        sendObject(data);
    }

    // main menu
    public void listRoom() {
//        sendData(StreamData.Type.LIST_ROOM.name()); //sua
    }

    // pair match
    public void findMatch() {
        FindMatchMessage msg = new FindMatchMessage();
        msg.setType(StreamData.Type.FIND_MATCH);
        sendObject(msg);
    }

    public void cancelFindMatch() {
        FindMatchMessage msg = new FindMatchMessage();
        msg.setType(StreamData.Type.CANCEL_FIND_MATCH);
        sendObject(msg);
    }

    public void declinePairMatch() {
        RequestPairMatchMessage msg = new RequestPairMatchMessage();
        msg.setAnswer("no");
        msg.setType(StreamData.Type.REQUEST_PAIR_MATCH);
        sendObject(msg);
    }

    public void acceptPairMatch() {
        RequestPairMatchMessage msg = new RequestPairMatchMessage();
        msg.setType(StreamData.Type.REQUEST_PAIR_MATCH);
        msg.setAnswer("yes");
        sendObject(msg);
    }

    // in game
    public void dataRoom(String roomId) {
        DataRoomMessage msg = new DataRoomMessage();
        msg.setIdRoom(roomId);
        sendObject(msg);
    }

    public void chatRoom(String chatMsg) {
        sendData(StreamData.Type.CHAT_ROOM.name() + ";" + chatMsg);
    }

    public void leaveRoom() {
        Message msg = new Message(StreamData.Type.LEAVE_ROOM);
        sendObject(msg);
    }

    // profile
    public void changePassword(String oldPassword, String newPassword) {
        // hasing password
        String oldPasswordHash = oldPassword;
        String newPasswordHash = newPassword;

        // prepare data
        String data = StreamData.Type.CHANGE_PASSWORD.name() + ";" + oldPasswordHash + ";" + newPasswordHash;

        // send data
        sendData(data);
    }

    public void getProfile(String email) {
        // prepare data
        String data = StreamData.Type.GET_PROFILE.name() + ";" + email;

        // send data
        sendData(data);
    }

    public void editProfile(String newEmail, String name, String avatar, String yearOfBirth, String gender) {
        // prepare data
        String data = StreamData.Type.EDIT_PROFILE + ";"
                + newEmail + ";"
                + name + ";"
                + avatar + ";"
                + yearOfBirth + ";"
                + gender;

        // send data
        sendData(data);
    }

    // send data
    public void sendObject(Object object) {
        try {
            oos.writeObject(object);
        } catch (IOException ex) {
            Logger.getLogger(SocketHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void sendPureData(String data) {
        try {
            dos.writeUTF(data);

        } catch (IOException ex) {
            Logger.getLogger(SocketHandler.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void sendData(String data) {
        try {

            dos.writeUTF(data);

        } catch (IOException ex) {
            Logger.getLogger(SocketHandler.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    // get set
    public String getLoginEmail() {
        return loginEmail;
    }

    public void setLoginEmail(String email) {
        this.loginEmail = email;
    }
}
