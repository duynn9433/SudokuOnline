/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.controller;

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
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import shared.model.Sudoku;
import shared.constant.StreamData;
import shared.message.*;
import shared.model.Player;
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
        System.out.println("");
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
//            System.out.println("dis:" + dis);
//           dos = new DataOutputStream(s.getOutputStream());
//            System.out.println("dos:" + dos);
//            System.out.println("init ois oos");
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
                        onReceiveListRoom(message);    //can lien quan den phong
                        break;

                    case CREATE_ROOM:
                        onReceiveCreateRoom(message);  //can lien quan den phong
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
                        onReceiveChatRoom(message);
                        break;
                    case PLAY_AGAIN:
                        onReceivePlayAgian(message);
                        break;
                    case REFUSE_PLAY_AGAIN:
                        onReceiveRefusePlayAgain();
                        break;
                    case START_GAME_AGAIN:
                        onReceiveStatGameAgain(message);
                        break;
                    case CHAT_ALL:
                        onReceiveChatAll(message);
                        break;
                    case CHAT_WAITING_ROOM:
                        onReceiveChatWaitingRoom(message);
                        break;
                    case LEAVE_ROOM:
                        onReceiveLeaveRoom(message);
                        break;
                    case LEAVE_WAITING_ROOM:
                        onReceiveLeaveWaitingRoom(message);
                        break;
                    case READY:
                        onReceiveReady(message);
                        break;
                    case START_GAME_FROM_ROOM:
                        onReceiveStartGame(message);
                        break;
                    case CLOSE_ROOM:
                        onReceiveCloseRoom(received);
                        break;

                    case GET_PROFILE:
                        onReceiveGetProfile(message);
                        break;

                    case EDIT_PROFILE:
                        onReceivedEditProfile(message);
                        break;

                    case GET_LIST_RANK:
                        onReceiveGetRank(message);
                        break;
                    case CHANGE_PASSWORD:
                        onReceiveChangePassword(message);
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
        PlayerMessage msg = (PlayerMessage) message;
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
            this.loginEmail = msg.getMsg();

            // chuyển scene
            RunClient.closeScene(RunClient.SceneName.LOGIN);
            RunClient.openScene(RunClient.SceneName.MAINMENU);

            // tự động lấy danh sách phòng
            listRoom();
        }
    }

    private void onReceiveSignup(Object message) {
        // get status from data
        PlayerMessage msg = (PlayerMessage) message;
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
        Message msg = (Message) message;
        this.loginEmail = null;

        // chuyển scene
        RunClient.closeAllScene();
        RunClient.openScene(RunClient.SceneName.LOGIN);
    }

    // main menu
    private void onReceiveListRoom(Object message) {
        ListRoomMessage msg = (ListRoomMessage) message;
        String status = msg.getStatus();
        
        if (status.equals("failed")) {

        } else if (status.equals("success")) {
            int roomCount = msg.getRoomCount();

            Vector vheader = new Vector();
            vheader.add("Mã");
            vheader.add("Cặp đấu");

            Vector vdata = new Vector();
            
            for (int i = 0; i < roomCount; i ++) {

                String roomId = (String) msg.getRoomID().get(i);
                String title = (String) msg.getPairData().get(i);
                
                Vector vrow = new Vector();
                vrow.add(roomId);
                vrow.add(title);
                vdata.add(vrow);
            }

            RunClient.mainMenuScene.setListRoom(vdata, vheader);
        }
    }

    private void onReceiveCreateRoom(Object message) {
        Message msg = (Message) message;
//        JoinRoomMessage msg = (JoinRoomMessage) message;
        String status = msg.getStatus();
        if (status.equals("success")) {
            String roomId = msg.getMsg();

            // save room id
            this.roomId = roomId;
            System.out.println("Room id JOIN_ROOM:" + roomId);

            // change scene
            RunClient.closeScene(RunClient.SceneName.MAINMENU);
            RunClient.openScene(RunClient.SceneName.WAITINGROOM);
            RunClient.waitingRoom.setTitle("Phòng #" + roomId);
        } else if (status.equals("failed")) {
            String codeMsg = msg.getCodeMsg();
            JOptionPane.showMessageDialog(RunClient.mainMenuScene, codeMsg,
                    "Vào phòng không thành công", JOptionPane.ERROR_MESSAGE);
        }

    }

    private void onReceiveJoinRoom(Object message) {
        Message msg = (Message) message;
//        JoinRoomMessage msg = (JoinRoomMessage) message;
        String roomId = msg.getMsg();

        // save room id
        this.roomId = roomId;
        System.out.println("Room id JOIN_ROOM:" + roomId);

        // change scene
        RunClient.closeScene(RunClient.SceneName.MAINMENU);
        RunClient.openScene(RunClient.SceneName.INGAME);
        RunClient.inGameScene.setTitle("Phòng #" + roomId);

        // get room data
        dataRoom(roomId);
    }

    // pair match
    private void onReceiveFindMatch(Object message) {
        Message msg = (Message) message;
//        FindMatchMessage msg = (FindMatchMessage) message;
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
        Message msg = (Message) message;
//        FindMatchMessage msg = (FindMatchMessage) message;
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
        Message msg = (Message) message;
//        ResultPairMatchMessage msg = (ResultPairMatchMessage) message;
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
        System.out.println("DataRoom:" + msg.toString());
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

    private void onReceiveChatRoom(Object message) {
        ChatMessage msg = (ChatMessage) message;
        RunClient.inGameScene.addChat(msg.getChatItem());
    }

    private void onReceiveChatAll(Object message) {
        ChatMessage msg = (ChatMessage) message;
        RunClient.mainMenuScene.addChat(msg.getChatItem());
        RunClient.waitingRoom.addChatServer(msg.getChatItem());
    }

    private void onReceiveChatWaitingRoom(Object message) {
        ChatMessage msg = (ChatMessage) message;
        RunClient.waitingRoom.addChatRoom(msg.getChatItem());
    }

    private void onReceiveLeaveRoom(Object message) {
        Message msg = (Message) message;
//        LeaveRoomMessage msg = (LeaveRoomMessage) message;
        String status = msg.getStatus();
        System.out.println("status leave room:" + status);

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

    private void onReceiveLeaveWaitingRoom(Object message) {
        Message msg = (Message) message;
//        LeaveRoomMessage msg = (LeaveRoomMessage) message;
        String status = msg.getStatus();
        System.out.println("status leave room:" + status);

        if (status.equals("failed")) {
            String failedMsg = msg.getCodeMsg();
            JOptionPane.showMessageDialog(RunClient.waitingRoom, failedMsg, "Không thể thoát phòng", JOptionPane.ERROR_MESSAGE);

        } else if (status.equals("success")) {
            RunClient.closeScene(RunClient.SceneName.WAITINGROOM);
            RunClient.openScene(RunClient.SceneName.MAINMENU);
            // get list room again
            listRoom();
        }
    }

    private void onReceiveReady(Object message) {
        ReadyMessage msg = (ReadyMessage) message;
        boolean isReady = msg.isIsReady();
        RunClient.waitingRoom.ready(isReady);
    }

    private void onReceiveStartGame(Object message) {
        DataRoomMessage msg = (DataRoomMessage) message;
        String status = msg.getStatus();
        if (status.equals("success")) {
            RunClient.closeScene(RunClient.SceneName.WAITINGROOM);
            RunClient.openScene(RunClient.SceneName.INGAME);
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
    private void onReceiveGetProfile(Object message) {
        PlayerMessage p =  (PlayerMessage) message;

        // turn off loading
        RunClient.profileScene.setLoading(false);

        // show data to UI
        RunClient.profileScene.setProfileData(p);

    }

    private void onReceivedEditProfile(Object message) {
     //   EditProfileMessage msg = (EditProfileMessage) message;
        PlayerMessage msg = (PlayerMessage) message;
        String status = msg.getStatus();
        // turn off loading
        RunClient.profileScene.setProfileSaveLoading(false);

        if (status.equals("failed")) {
            String failedMsg = msg.getCodeMsg();
            JOptionPane.showMessageDialog(RunClient.profileScene, failedMsg, "Lỗi", JOptionPane.ERROR_MESSAGE);

        } else if (status.equals("success")) {
            JOptionPane.showMessageDialog(RunClient.profileScene, "Đổi thông tin thành công", "Thành công", JOptionPane.INFORMATION_MESSAGE);

            // lưu lại email
            this.loginEmail = msg.getPlayer().getEmail();

            // load lại thông tin cá nhân mới - có thể ko cần! nhưng cứ load lại cho chắc
            getProfile(this.loginEmail);
        }
    }

    private void onReceiveChangePassword(Object message) {
        ChangePasswordMessage msg = (ChangePasswordMessage) message;
        String status = msg.getStatus();
        // turn off loading
        RunClient.changePasswordScene.setLoading(false);

        // check status
        if (status.equals("failed")) {
            String failedMsg = msg.getCodeMsg();
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

    private void onReceiveLockSubmit(Object message) {
        Message msg = (Message) message;
//        LockSubmitMessage msg = (LockSubmitMessage) message;
        String status = msg.getStatus();
        if (status.equals("success")) {
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
        PlayerMessage data = new PlayerMessage();
        data.setType(StreamData.Type.LOGIN);
        Player p = new Player();
        p.setEmail(email);
        p.setPassword(password);
        data.setPlayer(p);

        // send data
        sendObject(data);
        //sendData(data);
    }

    public void signup(String email, String password, String name, String gender, int yearOfBirth, String avatar) {
        // prepare data
//        SignupMessage data = new SignupMessage(StreamData.Type.SIGNUP, email, password, name, gender, yearOfBirth, avatar);
        PlayerMessage data = new PlayerMessage();
        Player p = new Player();
        p.setEmail(email);
        p.setPassword(password);
        p.setName(name);
        p.setGender(gender);
        p.setYearOfBirth(yearOfBirth);
        p.setAvatar(avatar);
        data.setPlayer(p);
        data.setType(StreamData.Type.SIGNUP);
        // send data
        sendObject(data);
    }

    public void logout() {
        // prepare data
        Message data = new Message(StreamData.Type.LOGOUT);

        // send data
        sendObject(data);
    }

    // main menu
    public void listRoom() {
        sendObject(new ListRoomMessage(StreamData.Type.LIST_ROOM));
    }

    // pair match
    public void findMatch() {
        Message msg = new Message();
        msg.setType(StreamData.Type.FIND_MATCH);
        sendObject(msg);
    }

    public void cancelFindMatch() {
        Message msg = new Message();
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
        ChangePasswordMessage msg = new ChangePasswordMessage();
        msg.setOldPassword(oldPassword);
        msg.setNewPassword(newPassword);
        msg.setType(StreamData.Type.CHANGE_PASSWORD);

        sendObject(msg);
    }

    public void getProfile(String email) {
        Message msg = new Message(StreamData.Type.GET_PROFILE);
        msg.setCodeMsg(email);
        sendObject(msg);
    }

    public void editProfile(String newEmail, String name, String avatar, String yearOfBirth, String gender) {
        // prepare data
        PlayerMessage msg = new PlayerMessage(StreamData.Type.EDIT_PROFILE);
        Player p = new Player();
        p.setEmail(newEmail);
        p.setName(name);
        p.setAvatar(avatar);
        p.setYearOfBirth(Integer.parseInt(yearOfBirth));
        p.setGender(gender);
        msg.setPlayer(p);
        sendObject(msg);
    }

    // send data
    public void sendObject(Object object) {
        try {
            oos.reset();
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

    public void sendMsgRank() {
        Message data = new Message(StreamData.Type.GET_LIST_RANK);
        sendObject(data);
    }

    private void onReceiveGetRank(Object message) {
        GetListRankMessage msg = (GetListRankMessage) message;
        ArrayList<Player> listPlayer = msg.getListPalyer();
        RunClient.rankview.setListRank(listPlayer);
    }

    public void leaveWaitingRoom() {
        Message msg = new Message();
        msg.setType(StreamData.Type.LEAVE_WAITING_ROOM);
        sendObject(msg);
    }

    public void invitePlayAgain() {
       Message msg = new Message(StreamData.Type.PLAY_AGAIN);
        sendObject(msg);
    }
    private void onReceivePlayAgian(Object message) {
        Message msg = (Message) message;
        String stt = msg.getStatus();
        if(stt.equals("failed")) {
            JOptionPane.showMessageDialog(RunClient.inGameScene, "Đối thủ đã thoát khỏi phòng");
        }
        else if (stt.equals("success")) {
            RunClient.inGameScene.dialogInvitePlayAgian();
        }
         
    }

    public void refusePlayAgain() {
        Message msg = new Message(StreamData.Type.REFUSE_PLAY_AGAIN);
        sendObject(msg);
    }

    private void onReceiveRefusePlayAgain() {
       RunClient.inGameScene.dialogRefusePlayAgain();
    }

    public void aceptPlayAgain() {
        Message msg = new Message(StreamData.Type.ACCEPT_PALY_AGAIN);
        sendObject(msg);
    }

    private void onReceiveStatGameAgain(Object message) {
      DataRoomMessage msg = (DataRoomMessage) message;
        String status = msg.getStatus();
        if (status.equals("success")) {
            
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
}
