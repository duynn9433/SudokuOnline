/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.controller;


import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.RunServer;
import server.DAO.controller.PlayerController;
import server.DAO.model.Player;
import shared.constant.*;
import shared.helper.CustumDateTimeFormatter;
import shared.message.*;
import shared.model.PlayerInGame;

/**
 *
 * @author duynn
 */
public class Client implements Runnable {

    Socket s;
    DataInputStream dis;
    DataOutputStream dos;
    ObjectInputStream ois;
    ObjectOutputStream oos;

    Player loginPlayer;
    Client cCompetitor;
    Room joinedRoom; // if == null => chua vao phong nao het

    boolean findingMatch = false;
    String acceptPairMatchStatus = "_"; // yes/no/_

    public Client(Socket s) throws IOException {
        this.s = s;

        // obtaining input and output streams 
        this.dis = new DataInputStream(this.s.getInputStream());
        this.dos = new DataOutputStream(this.s.getOutputStream());
        ois = new ObjectInputStream(this.s.getInputStream());
        oos = new ObjectOutputStream(this.s.getOutputStream());
        System.out.println("ois:" + ois);
        System.out.println("oos:" + oos);
    }

    @Override
    public void run() {

        Message message = new Message();
        String received;
        boolean running = true;

        while (!RunServer.isShutDown) {
            try {
                // receive the request from client
                message = (Message) ois.readObject();
                received = message.getType().name();
                System.out.println("S:" + received);
                // process received data
                StreamData.Type type = StreamData.getTypeFromData(received);

                switch (type) {
                    case CONNECT:
                        onReceiveConnect();
                        break;

                    case LOGIN:
                        onReceiveLogin(message);
                        break;

                    case SIGNUP:
                        onReceiveSignup(message);
                        break;

                    case LOGOUT:
                        onReceiveLogout(message);
                        break;

                    case LIST_ROOM:
                        onReceiveListRoom(received);
                        break;

                    case LIST_ONLINE:
                        onReceiveListOnline(received);
                        break;

                    case CREATE_ROOM:
                        onReceiveCreateRoom(received);
                        break;

                    case JOIN_ROOM:
                        onReceiveJoinRoom(received);
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
                        // type này có 1 chiều server->client
                        // gửi khi ghép cặp bị đối thủ từ chối
                        // nếu ghép cặp được đồng ý thì server gửi type join-room luôn chứ ko cần gửi type này
                        // client không gửi type này cho server
                        break;

                    case DATA_ROOM:
                        onReceiveDataRoom(message);
                        break;

                    case CHAT_ROOM:
                        onReceiveChatRoom(received);
                        break;

                    case LEAVE_ROOM:
                        onReceiveLeaveRoom(received);
                        break;

                    case GET_PROFILE:
                        onReceiveGetProfile(received);
                        break;

                    case EDIT_PROFILE:
                        onReceiveEditProfile(received);
                        break;

                    case CHANGE_PASSWORD:
                        onReceiveChangePassword(received);
                        break;

                    case SUBMIT:
                        onReceiveSubmit(message);
                        break;
                    case EXIT:
                        running = false;
                }

            } catch (IOException ex) {
                // System.out.println("Connection lost with " + s.getPort());

                // leave room if needed
                onReceiveLeaveRoom("");
                break;
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        try {
            // closing resources 
            this.s.close();
            this.dis.close();
            this.dos.close();

            // remove from clientManager
            RunServer.clientManager.remove(this);

        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // listen events
    // auth
    private void onReceiveConnect() {
        // notify client
        sendObject(new Message(StreamData.Type.CONNECT));
    }

    private void onReceiveLogin(Object message) {
        LoginMessage msg =(LoginMessage) message;
        // get email / password from data
        String email = msg.getEmail();
        String password = msg.getPassword();

        // check đã được đăng nhập ở nơi khác
        if (RunServer.clientManager.find(email) != null) {
            sendObject(new LoginMessage(StreamData.Type.LOGIN, "failed", Code.ACCOUNT_LOGEDIN));
            return;
        }

        // check login
        LoginMessage result = new PlayerController().checkLogin(email, password);

        if (result.getStatus().equals("success")) {
            // set login email
            this.loginPlayer = new PlayerController().getByEmail(email);
        }

        // send result
        result.setType(StreamData.Type.LOGIN);
        sendObject(result);
    }

    private void onReceiveSignup(Object message) {
        // get data from received
        SignupMessage msg = (SignupMessage) message;
        String email = msg.getEmail();
        String password = msg.getPassword();
        String avatar = msg.getAvatar();
        String name = msg.getName();
        String gender = msg.getGender();
        int yearOfBirth = msg.getYearOfBirth();

        // sign up
        SignupMessage result = new PlayerController().signup(email, password, avatar, name, gender, yearOfBirth);

        // send data
        result.setType(StreamData.Type.SIGNUP);
        sendObject(result);
    }

    private void onReceiveLogout(Object message) {
        // log out now
        this.loginPlayer = null;
        this.findingMatch = false;

        LogoutMessage msg = (LogoutMessage) message;
        msg.setStatus("success");
        msg.setType(StreamData.Type.LOGOUT);
        // TODO leave room
        // TODO broadcast to all clients
        // send status
        sendObject(msg);
    }

    // main menu
    private void onReceiveListRoom(String received) {
        // prepare data
        String result = "success;";
        ArrayList<Room> listRoom = RunServer.roomManager.getRooms();
        int roomCount = listRoom.size();

        result += roomCount + ";";

        for (Room r : listRoom) {
            String pairData
                    = ((r.getClient1() != null) ? r.getClient1().getLoginPlayer().getNameId() : "_")
                    + " VS "
                    + ((r.getClient2() != null) ? r.getClient2().getLoginPlayer().getNameId() : "_");

            result += r.getId() + ";"
                    + pairData + ";"
                    + r.clients.size() + ";";
        }

        // send data
        sendData(StreamData.Type.LIST_ROOM.name() + ";" + result);
    }

    private void onReceiveListOnline(String received) {

    }

    private void onReceiveCreateRoom(String received) {

    }

    private void onReceiveJoinRoom(String received) {

    }

    // pair match
    private void onReceiveFindMatch(Object message) {
        FindMatchMessage msg = (FindMatchMessage) message;
        // nếu đang trong phòng rồi thì báo lỗi ngay
        FindMatchMessage sendMsg = new FindMatchMessage();
        if (this.joinedRoom != null) {
            sendMsg.setType(StreamData.Type.FIND_MATCH);
            sendMsg.setStatus("failed");
            sendMsg.setCodeMsg(Code.ALREADY_INROOM);
            sendMsg.setIdRoom(this.joinedRoom.getId());
            sendObject(sendMsg);
            return;
        }

        // kiểm tra xem có ai đang tìm phòng không
        Client cCompetitor = RunServer.clientManager.findClientFindingMatch();

        if (cCompetitor == null) {
            // đặt cờ là đang tìm phòng
            this.findingMatch = true;
            // trả về success để client hiển thị giao diện Đang tìm phòng
            sendMsg.setType(StreamData.Type.FIND_MATCH);
            sendMsg.setStatus("success");
            sendObject(sendMsg);
        } else {
            // nếu có người cũng đang tìm trận thì hỏi ghép cặp pairMatch
            // trong lúc hỏi thì phải tắt tìm trận bên đối thủ đi (để nếu client khác tìm trận thì ko bị ghép đè)
            cCompetitor.findingMatch = false;
            this.findingMatch = false;

            // lưu email đối thủ để dùng khi server nhận được result-pair-match
            this.cCompetitor = cCompetitor;
            cCompetitor.cCompetitor = this;

            // trả thông tin đối thủ về cho 2 clients
            RequestPairMatchMessage sendMsg2 = new RequestPairMatchMessage();
            sendMsg2.setType(StreamData.Type.REQUEST_PAIR_MATCH);
            sendMsg2.setIdPlayer(cCompetitor.loginPlayer.getId());
            sendMsg2.setNamePlayer(cCompetitor.loginPlayer.getName());
            this.sendObject(sendMsg2);

            RequestPairMatchMessage sendMsg3 = new RequestPairMatchMessage();
            sendMsg3.setType(StreamData.Type.REQUEST_PAIR_MATCH);
            sendMsg3.setIdPlayer(this.loginPlayer.getId());
            sendMsg3.setNamePlayer(this.loginPlayer.getName());
            cCompetitor.sendObject(sendMsg3);
        }
    }

    private void onReceiveCancelFindMatch(Object message) {
        FindMatchMessage msg = (FindMatchMessage) message;
        // gỡ cờ đang tìm phòng
        this.findingMatch = false;

        // báo cho client để tắt giao diện đang tìm phòng
        FindMatchMessage send = new FindMatchMessage();
        send.setType(StreamData.Type.CANCEL_FIND_MATCH);
        send.setStatus("success");
        sendObject(send);
    }

    private void onReceiveRequestPairMatch(Object message) {
        RequestPairMatchMessage msg = (RequestPairMatchMessage) message;
        System.out.println("request:" + msg.toString());
        String requestResult = msg.getAnswer();

        // save accept pair status
        this.acceptPairMatchStatus = requestResult;

        // get competitor
        if (cCompetitor == null) {
            System.out.println("null");
            ResultPairMatchMessage send = new ResultPairMatchMessage();
            send.setType(StreamData.Type.RESULT_PAIR_MATCH);
            send.setStatus("failed");
            send.setCodeMsg(Code.COMPETITOR_LEAVE);
            sendObject(send);
            return;
        }

        // if once say no
        if (requestResult.equals("no")) {
            System.out.println("no");
            // tru diem
            this.loginPlayer.setScore(this.loginPlayer.getScore() - 1);
            new PlayerController().update(this.loginPlayer);

            // send data
            ResultPairMatchMessage send1 = new ResultPairMatchMessage();
            send1.setType(StreamData.Type.RESULT_PAIR_MATCH);
            send1.setStatus("failed");
            send1.setCodeMsg(Code.YOU_CHOOSE_NO);
            this.sendObject(send1);

            ResultPairMatchMessage send2 = new ResultPairMatchMessage();
            send2.setType(StreamData.Type.RESULT_PAIR_MATCH);
            send2.setStatus("failed");
            send2.setCodeMsg(Code.COMPETITOR_CHOOSE_NO);
            cCompetitor.sendObject(send2);

            // reset acceptPairMatchStatus
            this.acceptPairMatchStatus = "_";
            cCompetitor.acceptPairMatchStatus = "_";
        }

        // if both say yes
        if (requestResult.equals("yes") && cCompetitor.acceptPairMatchStatus.equals("yes")) {
            ResultPairMatchMessage send = new ResultPairMatchMessage();
            send.setType(StreamData.Type.RESULT_PAIR_MATCH);
            send.setStatus("success");
            System.out.println("result1:" + send.toString());
            this.sendObject(send);
            ResultPairMatchMessage send0 = new ResultPairMatchMessage();
            send0.setType(StreamData.Type.RESULT_PAIR_MATCH);
            send0.setStatus("success");
            System.out.println("result2:" + send0.toString());
            cCompetitor.sendObject(send0);

            // create new room
            Room newRoom = RunServer.roomManager.createRoom();
            System.out.println("Room(receivedRequestPairMatch:"+newRoom.toString());
            String temp = DataCreateGame.getRandomData();
            newRoom.getSudoku1().setAnswer(temp);
            newRoom.getSudoku1().setBoard(DataCreateGame.createBoard(temp));
            temp = DataCreateGame.getRandomData();
            newRoom.getSudoku2().setAnswer(temp);
            newRoom.getSudoku2().setBoard(DataCreateGame.createBoard(temp));
            // join room
            JoinRoomMessage send1 = this.joinRoom(newRoom);
            JoinRoomMessage send2 = cCompetitor.joinRoom(newRoom);
            System.out.println("JoinRoomMsg1:"+send1);
            System.out.println("JoinRoomMsg1:"+send2);
            System.out.println("Room(receivedRequestPairMatch) after:"+newRoom.toString());
            // send join room status to client
            sendObject(send1);
            cCompetitor.sendObject(send2);

            // TODO update list room to all client
            // reset acceptPairMatchStatus
            this.acceptPairMatchStatus = "_";
            cCompetitor.acceptPairMatchStatus = "_";
        }
    }

    // in game
    private void onReceiveDataRoom(Object message) {
        DataRoomMessage msg = (DataRoomMessage) message;
        // get room id
        String roomId = msg.getIdRoom();

        // check roomid is valid
        Room room = RunServer.roomManager.find(roomId);
        if (room == null) {
            DataRoomMessage send = new DataRoomMessage();
            send.setStatus("failed");
            send.setCodeMsg(Code.ROOM_NOTFOUND);
            send.setIdRoom(roomId);
            sendObject(send);
            return;
        }

        // prepare data
        // send data
        //sendData(StreamData.Type.DATA_ROOM.name() + ";success;" + data);
        DataRoomMessage send = new DataRoomMessage();
        send.setStatus("success");
        send.setIdRoom(roomId);
        //send.setRoom(room);
        if (room.getClient1().getLoginPlayer().getEmail().equals(loginPlayer.getEmail())) {
            send.setSudokuBoard(room.getSudoku1().getBoard());
            System.out.println("sudoku:");
            send.printBoard();
        } else {
            send.setSudokuBoard(room.getSudoku2().getBoard());
            System.out.println("sudoku:");
            send.printBoard();

        }
        send.setPlayer1(room.getClient1().getLoginPlayer().toPlayerInGame());
        send.setPlayer2(room.getClient2().getLoginPlayer().toPlayerInGame());
        sendObject(send);
        room.startGame();
    }

    private void onReceiveChatRoom(String received) {
        String[] splitted = received.split(";");
        String chatMsg = splitted[1];

        if (joinedRoom != null) {
            String data = CustumDateTimeFormatter.getCurrentTimeFormatted() + ";"
                    + loginPlayer.getNameId() + ";"
                    + chatMsg;

            joinedRoom.broadcast(StreamData.Type.CHAT_ROOM.name() + ";" + data);
        }
    }

    private void onReceiveLeaveRoom(String received) {
        if (joinedRoom == null) {
            sendData(StreamData.Type.LEAVE_ROOM.name() + ";failed" + Code.CANT_LEAVE_ROOM);
            return;
        }

        // nếu là người chơi thì đóng room luôn
        if (joinedRoom.getClient1().equals(this) || joinedRoom.getClient2().equals(this)) {
            joinedRoom.close("Người chơi " + this.loginPlayer.getNameId() + " đã thoát phòng.");
            return;
        }

        // broadcast to all clients in room
        String data = CustumDateTimeFormatter.getCurrentTimeFormatted() + ";"
                + "SERVER" + ";"
                + loginPlayer.getNameId() + " đã thoát";

        joinedRoom.broadcast(StreamData.Type.CHAT_ROOM + ";" + data);

        // delete refernce to room
//        joinedRoom.removeClient(this);
        joinedRoom = null;

        // TODO if this client is player -> close room
        // send result
        sendData(StreamData.Type.LEAVE_ROOM.name() + ";success");
    }

    // profile
    private void onReceiveGetProfile(String received) {
        String result;

        // get email from data
        String email = received.split(";")[1];

        // get player data
        Player p = new PlayerController().getByEmail(email);
        if (p == null) {
            result = "failed;" + Code.ACCOUNT_NOT_FOUND;
        } else {
            result = "success;"
                    + p.getId() + ";"
                    + p.getEmail() + ";"
                    + p.getName() + ";"
                    + p.getAvatar() + ";"
                    + p.getGender() + ";"
                    + p.getYearOfBirth() + ";"
                    + p.getScore() + ";"
                    + p.getMatchCount() + ";"
                    + p.getWinCount() + ";"
                    + p.calculateTieCount() + ";"
                    + p.getLoseCount() + ";"
                    + p.getCurrentStreak() + ";"
                    + p.calculateWinRate();
        }

        // send result
        sendData(StreamData.Type.GET_PROFILE.name() + ";" + result);
    }

    private void onReceiveEditProfile(String received) {
        try {
            // get data from received
            String[] splitted = received.split(";");
            String newEmail = splitted[1];
            String name = splitted[2];
            String avatar = splitted[3];
            int yearOfBirth = Integer.parseInt(splitted[4]);
            String gender = splitted[5];

            // edit profile
            String result = new PlayerController().editProfile(loginPlayer.getEmail(), newEmail, name, avatar, yearOfBirth, gender);

            // lưu lại newEmail vào Client nếu cập nhật thành công
            String status = result.split(";")[0];
            if (status.equals("success")) {
                loginPlayer = new PlayerController().getByEmail(newEmail);
            }

            // send result
            sendData(StreamData.Type.EDIT_PROFILE + ";" + result);

        } catch (NumberFormatException e) {
            // send failed format
            sendData(StreamData.Type.EDIT_PROFILE + ";failed;Năm sinh phải là số nguyên");
        }
    }

    private void onReceiveChangePassword(String received) {
        // get old pass, new pass from data
        String[] splitted = received.split(";");
        String oldPassword = splitted[1];
        String newPassword = splitted[2];

        // check change pass
        String result = new PlayerController().changePassword(loginPlayer.getEmail(), oldPassword, newPassword);

        // send result
        sendData(StreamData.Type.CHANGE_PASSWORD.name() + ";" + result);
    }

    // game event
    private void onReceiveSubmit(Object message) {
        SubmitMessage msg = (SubmitMessage) message;
        boolean isPlayer1 = false;
        if (loginPlayer.getEmail().equals(joinedRoom.getClient1().getLoginPlayer().getEmail())) {
            joinedRoom.getSudoku1().setIsSubmit(true);
            joinedRoom.getSudoku1().setBoard(msg.getSubmit());
            joinedRoom.getSudoku1().setSubmitTime(msg.getCurrentTick());
            isPlayer1 = true;
            //gui da nhan kq 
            LockSubmitMessage send = new LockSubmitMessage();
            sendObject(send);
        } else {
            joinedRoom.getSudoku2().setIsSubmit(true);
            joinedRoom.getSudoku2().setBoard(msg.getSubmit());
            joinedRoom.getSudoku2().setSubmitTime(msg.getCurrentTick());
            //gui da nhan kq 
            LockSubmitMessage send = new LockSubmitMessage();
            sendObject(send);
        }

        if (joinedRoom.getSudoku1().isIsSubmit() && joinedRoom.getSudoku2().isIsSubmit()) {
            boolean check1 = joinedRoom.getSudoku1().CheckWin();
            boolean check2 = joinedRoom.getSudoku2().CheckWin();
            SubmitMessage send1 = new SubmitMessage();
            if (check1 && !check2) {
                //1win
                send1.setType(StreamData.Type.SUBMIT);
                send1.setResult(joinedRoom.getClient1().getLoginPlayer().getEmail());
                //them 3 diem 1
            } else if (!check1 && check2) {
                //2win
                send1.setType(StreamData.Type.SUBMIT);
                send1.setResult(joinedRoom.getClient2().getLoginPlayer().getEmail());
                //them 3 diem cho 2
            } else if (!check1 && !check2) {
                //hoa
                send1.setType(StreamData.Type.SUBMIT);

                //moi th 1 diem
            } else if (check1 && check2) {
                if (joinedRoom.getSudoku1().getSubmitTime() > joinedRoom.getSudoku2().getSubmitTime()) {
                    //1win
                    send1.setType(StreamData.Type.SUBMIT);
                    send1.setResult(joinedRoom.getClient1().getLoginPlayer().getEmail());
                    //them 3 dierm cho 1
                } else if (joinedRoom.getSudoku1().getSubmitTime() < joinedRoom.getSudoku2().getSubmitTime()) {
                    //2win
                    send1.setType(StreamData.Type.SUBMIT);
                    send1.setResult(joinedRoom.getClient2().getLoginPlayer().getEmail());

                    //them 3 diem cho 2
                } else {
                    //hoa
                    send1.setType(StreamData.Type.SUBMIT);

                    //moi th them 1 diem
                }
            }
            sendObject(send1);
            cCompetitor.sendObject(send1);
            //TODO luu game match
            //                        // TODO luu game match
//                        new GameMatchBUS().add(new GameMatch(
//                                winner.getId(),
//                                loser.getId(),
//                                winner.getId(),
//                                Sudoku.MATCH_TIME_LIMIT - ((Sudoku) joinedRoom.getGamelogic()).getMatchTimer().getCurrentTick(),
//                                joinedRoom.startedTime
//                        ));
        }
    }

    // send data fucntions
    public String sendObject(Object object) {
        try {
            oos.writeObject(object);
            return "success";
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            return "failed;" + ex.getMessage();
        }
    }

    public String sendData(String data) {
        try {

            this.dos.writeUTF(data);
            return "success";
        } catch (IOException e) {
            System.err.println("Send data failed to " + this.loginPlayer.getEmail());
            return "failed;" + e.getMessage();
        }
    }

    public String sendPureData(String data) {
        try {
            this.dos.writeUTF(data);
            return "success";
        } catch (IOException e) {
            System.err.println("Send data failed to " + this.loginPlayer.getEmail());
            return "failed;" + e.getMessage();
        }
    }

    // room handlers
    public JoinRoomMessage joinRoom(String id) {
        Room found = RunServer.roomManager.find(id);

        JoinRoomMessage msg = new JoinRoomMessage();
        // không tìm thấy phòng cần join ?
        if (found == null) {
            msg.setStatus("failed");
            msg.setCodeMsg("Không tìm thấy phòng" + id);
            return msg;
        }

        return joinRoom(found);
    }

    public JoinRoomMessage joinRoom(Room room) {
        JoinRoomMessage msg = new JoinRoomMessage();
        // đang trong phòng rồi ?
        if (this.joinedRoom != null) {
            msg.setStatus("failed");
            msg.setCodeMsg(Code.ALREADY_INROOM);
            msg.setIdRoom(this.joinedRoom.getId());
            return msg;
        }

        // join vào phòng thanh cong hay khong ?
        if (room.addClient(this)) {
            this.joinedRoom = room;

            // thông báo với mọi người trong phòng
            this.joinedRoom.broadcast(StreamData.Type.CHAT_ROOM + ";"
                    + CustumDateTimeFormatter.getCurrentTimeFormatted()
                    + ";SERVER;"
                    + loginPlayer.getNameId() + " đã vào phòng."
            );

            msg.setStatus("success");
            msg.setIdRoom(room.getId());
            return msg;
        }

        msg.setStatus("failed");
        msg.setCodeMsg(Code.CANNOT_JOINROOM);
        return msg;
    }

    // get set
    public static String getEmptyInGameData() {
        return ";;";
    }

    public boolean isFindingMatch() {
        return findingMatch;
    }

    public void setFindingMatch(boolean findingMatch) {
        this.findingMatch = findingMatch;
    }

    public Player getLoginPlayer() {
        return loginPlayer;
    }

    public void setLoginPlayer(Player loginPlayer) {
        this.loginPlayer = loginPlayer;
    }

    public Client getcCompetitor() {
        return cCompetitor;
    }

    public void setcCompetitor(Client cCompetitor) {
        this.cCompetitor = cCompetitor;
    }

    public Room getJoinedRoom() {
        return joinedRoom;
    }

    public void setJoinedRoom(Room room) {
        this.joinedRoom = room;
    }

    public String getAcceptPairMatchStatus() {
        return acceptPairMatchStatus;
    }

    public void setAcceptPairMatchStatus(String acceptPairMatchStatus) {
        this.acceptPairMatchStatus = acceptPairMatchStatus;
    }

}
