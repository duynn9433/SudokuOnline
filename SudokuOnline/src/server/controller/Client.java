/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.controller;

import client.RunClient;
import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.DAO.controller.GameMatchController;
import server.RunServer;
import server.DAO.controller.PlayerController;
import shared.model.Player;
import shared.constant.*;
import shared.helper.CustomDateTimeFormatter;
import shared.message.*;
import shared.model.ChatItem;
import shared.model.GameMatch;
import shared.model.PlayerInGame;
import shared.model.ProfileData;

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
    boolean isReady = false; //san sang de vào phòng

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
                        onReceiveListRoom(message);
                        break;

                    case CREATE_ROOM:
                        onReceiveCreateRoom();
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
                        // type này có 1 chiều server->client
                        // gửi khi ghép cặp bị đối thủ từ chối
                        // nếu ghép cặp được đồng ý thì server gửi type join-room luôn chứ ko cần gửi type này
                        // client không gửi type này cho server
                        break;

                    case DATA_ROOM:
                        onReceiveDataRoom(message);
                        break;

                    case CHAT_ROOM:
                        onReceiveChatRoom(message);
                        break;

                    case CHAT_ALL:
                        onReceiveChatAll(message);
                        break;

                    case CHAT_WAITING_ROOM:
                        onReceiveChatWaitingRoom(message);
                        break;

                    case LEAVE_ROOM:
                        onReceiveLeaveRoom();
                        break;
                    case LEAVE_WAITING_ROOM:
                        onReceiveLeaveWaitingRoom();
                        break;

                    case READY:
                        onReceiveReady();
                        break;

                    case GET_PROFILE:
                        onReceiveGetProfile(message);
                        break;

                    case EDIT_PROFILE:
                        onReceiveEditProfile(message);
                        break;
                    case GET_LIST_RANK:
                        onReceiveGetRank();
                        break;
                    case CHANGE_PASSWORD:
                        onReceiveChangePassword(message);
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
                // onReceiveLeaveRoom();
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
        LoginMessage msg = (LoginMessage) message;
        // get email / password from data
        String email = msg.getEmail();
        String password = msg.getPassword();

        // check đã được đăng nhập ở nơi khác
        if (RunServer.clientManager.find(email) != null) {
//            sendObject(new LoginMessage(StreamData.Type.LOGIN, "failed", Code.ACCOUNT_LOGEDIN));
            sendObject(new LoginMessage("failed", Code.ACCOUNT_LOGEDIN, StreamData.Type.LOGIN));
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
    private void onReceiveListRoom(Object message) {
        // prepare data
        ListRoomMessage msg = (ListRoomMessage) message;
        msg.setStatus("success");
        ArrayList<Room> listRoom = RunServer.roomManager.getRooms();

        msg.setRoomCount(listRoom.size());
        ArrayList listID = new ArrayList();
        ArrayList listData = new ArrayList();
        for (Room r : listRoom) {
            String pairData
                    = ((r.getClient1() != null) ? r.getClient1().getLoginPlayer().getNameId() : "_")
                    + " VS "
                    + ((r.getClient2() != null) ? r.getClient2().getLoginPlayer().getNameId() : "_");

            listID.add(r.getId());
            listData.add(pairData);
            msg.setRoomID(listID);
            msg.setPairData(listData);
        }

        // send data
        msg.setType(StreamData.Type.LIST_ROOM);
        sendObject(msg);
    }

    private void onReceiveCreateRoom() {
        Room newRoom = RunServer.roomManager.createRoom();
        JoinRoomMessage msg = this.joinRoom(newRoom);
        msg.setType(StreamData.Type.CREATE_ROOM);
        sendObject(msg);

    }

    private void onReceiveJoinRoom(Object message) {
        JoinRoomMessage msg = (JoinRoomMessage) message;
        String roomID = msg.getIdRoom();
        Room room = RunServer.roomManager.find(roomID);
        if (room != null) {
            if (!room.isFull()) {
                JoinRoomMessage send = this.joinRoom(room);
                joinedRoom.getClient1().setcCompetitor(joinedRoom.getClient2());
                joinedRoom.getClient2().setcCompetitor(joinedRoom.getClient1());
                send.setType(StreamData.Type.CREATE_ROOM);
                sendObject(send);
            } else {
                JoinRoomMessage send = new JoinRoomMessage();
                send.setType(StreamData.Type.CREATE_ROOM);
                send.setStatus("failed");
                send.setCodeMsg("Phòng đã đầy");
                sendObject(send);
            }
        } else {
            JoinRoomMessage send = new JoinRoomMessage();
            send.setType(StreamData.Type.CREATE_ROOM);
            send.setStatus("failed");
            send.setCodeMsg("Phòng không tồn tại");
            sendObject(send);
        }

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

            System.out.println("Room(receivedRequestPairMatch:" + newRoom.toString());
            String temp = DataCreateGame.getRandomData();
            newRoom.getSudoku1().setAnswer(temp);
            newRoom.getSudoku1().setBoard(DataCreateGame.createBoard(temp));
            temp = DataCreateGame.getRandomData();
            newRoom.getSudoku2().setAnswer(temp);
            newRoom.getSudoku2().setBoard(DataCreateGame.createBoard(temp));
            // join room
            JoinRoomMessage send1 = this.joinRoom(newRoom);
            JoinRoomMessage send2 = cCompetitor.joinRoom(newRoom);
            System.out.println("JoinRoomMsg1:" + send1);
            System.out.println("JoinRoomMsg1:" + send2);
            System.out.println("Room(receivedRequestPairMatch) after:" + newRoom.toString());
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

    private void onReceiveChatRoom(Object message) {
        ChatMessage msg = (ChatMessage) message;
        ChatItem chatItem = msg.getChatItem();

        if (joinedRoom != null) {
            joinedRoom.broadcast(msg);
        }
    }

    private void onReceiveChatAll(Object message) {
        ChatMessage msg = (ChatMessage) message;
        RunServer.clientManager.broadcast(msg);
    }

    private void onReceiveChatWaitingRoom(Object message) {
        ChatMessage msg = (ChatMessage) message;
        msg.setType(StreamData.Type.CHAT_WAITING_ROOM);
        joinedRoom.broadcast(msg);
    }

    private void onReceiveLeaveRoom() {
        System.out.println("joinedRoom:" + joinedRoom);
        if (joinedRoom == null) {
            LeaveRoomMessage send = new LeaveRoomMessage();
            send.setStatus("failed");
            send.setCodeMsg(Code.CANT_LEAVE_ROOM);
            System.out.println("LeaveMsg:" + send);
            sendObject(send);
            return;
        }
        if (joinedRoom.gameStarted == true) {
            Player p1 = loginPlayer;
            p1.setMatchCount(p1.getMatchCount() + 1);
            p1.setLoseCount(p1.getLoseCount() + 1);
            p1.setScore(p1.getScore() - 1);
            new PlayerController().update(p1);
        }
        // nếu là người chơi thì đóng room luôn
        //            joinedRoom.close("Người chơi " + this.loginPlayer.getNameId() + " đã thoát phòng.");
        joinedRoom.leaveRoom(this);
        System.out.println("1");

        // broadcast to all clients in room
//        String data = CustomDateTimeFormatter.getCurrentTimeFormatted() + ";"
//                + "SERVER" + ";"
//                + loginPlayer.getNameId() + " đã thoát";
//
//        joinedRoom.broadcast(StreamData.Type.CHAT_ROOM + ";" + data);
        // delete refernce to room
//        joinedRoom.removeClient(this);
        joinedRoom = null;
        System.out.println("2");
        // TODO if this client is player -> close room
        // send result
        LeaveRoomMessage send = new LeaveRoomMessage();
        send.setStatus("success");
        sendObject(send);

    }

    private void onReceiveLeaveWaitingRoom() {
        System.out.println("joinedRoom:" + joinedRoom);
        if (joinedRoom == null) {
            LeaveRoomMessage send = new LeaveRoomMessage();
            send.setStatus("failed");
            send.setCodeMsg(Code.CANT_LEAVE_ROOM);
            System.out.println("LeaveMsg:" + send);
            sendObject(send);
            return;
        }

        // nếu là người chơi thì đóng room luôn
        //            joinedRoom.close("Người chơi " + this.loginPlayer.getNameId() + " đã thoát phòng.");
        joinedRoom.leaveRoom(this);
        System.out.println("1");

        // broadcast to all clients in room
//        String data = CustomDateTimeFormatter.getCurrentTimeFormatted() + ";"
//                + "SERVER" + ";"
//                + loginPlayer.getNameId() + " đã thoát";
//
//        joinedRoom.broadcast(StreamData.Type.CHAT_ROOM + ";" + data);
        // delete refernce to room
//        joinedRoom.removeClient(this);
        joinedRoom = null;
        System.out.println("2");
        // TODO if this client is player -> close room
        // send result
        LeaveRoomMessage send = new LeaveRoomMessage();
        send.setType(StreamData.Type.LEAVE_WAITING_ROOM);
        send.setStatus("success");
        sendObject(send);
    }

    private void onReceiveReady() {
        if (!isReady) {
            isReady = true;
            //send msg da san sang -> chuyen trang thai thanh huy san sang
            ReadyMessage send = new ReadyMessage();
            send.setIsReady(isReady);
            send.setStatus("success");
            sendObject(send);
            //check xem 2 th da san sang chua
            if (joinedRoom != null) {
                boolean is1Ready = false;
                if (joinedRoom.getClient1() != null) {
                    is1Ready = joinedRoom.getClient1().isIsReady();
                }
                boolean is2Ready = false;
                if (joinedRoom.getClient2() != null) {
                    is2Ready = joinedRoom.getClient2().isIsReady();
                }
                if (is1Ready && is2Ready) {
                    //bat dau game
                    joinedRoom.getClient1().setIsReady(false);
                    joinedRoom.getClient2().setIsReady(false);
                    String s = DataCreateGame.getRandomData();
                    joinedRoom.getSudoku1().setAnswer(s);
                    joinedRoom.getSudoku1().setBoard(DataCreateGame.createBoard(s));
                    s = DataCreateGame.getRandomData();
                    joinedRoom.getSudoku2().setAnswer(s);
                    joinedRoom.getSudoku2().setBoard(DataCreateGame.createBoard(s));
                    //gui de
                    DataRoomMessage sendRoom = new DataRoomMessage();
                    sendRoom.setType(StreamData.Type.START_GAME_FROM_ROOM);
                    sendRoom.setStatus("success");
                    sendRoom.setIdRoom(joinedRoom.getId());
                    sendRoom.setPlayer1(joinedRoom.getClient1().getLoginPlayer().toPlayerInGame());
                    sendRoom.setPlayer2(joinedRoom.getClient2().getLoginPlayer().toPlayerInGame());

                    boolean amIPlayer1 = false;
                    if (joinedRoom.getClient1().getLoginPlayer().getEmail().equals(loginPlayer.getEmail())) {
                        sendRoom.setSudokuBoard(joinedRoom.getSudoku1().getBoard());
                        System.out.println("sudoku:");
                        sendRoom.printBoard();
                        amIPlayer1 = true;
                        sendObject(sendRoom);
                    } else {
                        sendRoom.setSudokuBoard(joinedRoom.getSudoku2().getBoard());
                        System.out.println("sudoku:");
                        sendRoom.printBoard();
                        sendObject(sendRoom);
                    }
                    //gui de cho doi thu
                    if (amIPlayer1) {
                        sendRoom.setSudokuBoard(joinedRoom.getSudoku2().getBoard());
                        cCompetitor.sendObject(sendRoom);
                    } else {
                        sendRoom.setSudokuBoard(joinedRoom.getSudoku1().getBoard());
                        cCompetitor.sendObject(sendRoom);
                    }
                    joinedRoom.startGame();
                }
            }

        } else {
            isReady = false;
            //send msg huy san sang -> chuyen thanh nut san sang
            ReadyMessage send = new ReadyMessage();
            send.setIsReady(isReady);
            send.setStatus("success");
            sendObject(send);
        }
    }

    // profile
    private void onReceiveGetProfile(Object message) {
        SendEmailMessage msg = (SendEmailMessage) message;
        Player p = new PlayerController().getByEmail(msg.getEmail());
        GetProfileMessage data = new GetProfileMessage(p, StreamData.Type.GET_PROFILE);
        sendObject(data);
    }

    private void onReceiveEditProfile(Object message) {
        EditProfileMessage msg = (EditProfileMessage) message;
        try {
            // get data from received

            String newEmail = msg.getEmail();
            String name = msg.getName();
            String avatar = msg.getAvatar();
            int yearOfBirth = Integer.parseInt(msg.getYearOfBirth());
            String gender = msg.getGender();

            // edit profile
            EditProfileMessage result = new PlayerController().editProfile(loginPlayer.getEmail(), newEmail, name, avatar, yearOfBirth, gender);

            // lưu lại newEmail vào Client nếu cập nhật thành công
            String status = result.getStatus();
            if (status.equals("success")) {
                loginPlayer = new PlayerController().getByEmail(newEmail);
            }

            // send result
            result.setType(StreamData.Type.EDIT_PROFILE);
            sendObject(result);

        } catch (NumberFormatException e) {
            // send failed format
            msg.setType(StreamData.Type.EDIT_PROFILE);
            msg.setStatus("failed");
            msg.setCodeMsg("Năm sinh phải là số nguyên");
            sendObject(msg);
        }
    }

    private void onReceiveChangePassword(Object message) {
        // get old pass, new pass from data
        ChangePasswordMessage msg = (ChangePasswordMessage) message;
        String oldPassword = msg.getOldPassword();
        String newPassword = msg.getNewPassword();
        // check change pass
        ChangePasswordMessage result = new PlayerController().changePassword(loginPlayer.getEmail(), oldPassword, newPassword);

        // send result
        result.setType(StreamData.Type.CHANGE_PASSWORD);
        sendObject(result);
    }

    // game event
    private void onReceiveSubmit(Object message) {
        SubmitMessage msg = (SubmitMessage) message;
        boolean isPlayer1 = false;
        if (joinedRoom.getClient1() == null && joinedRoom.getClient2() == null) {
            return;
        }
        if (joinedRoom.getClient1() == null || joinedRoom.getClient2() == null) {
            joinedRoom.gameStarted = false;
            //chien thang +3diem
            Player p1 = loginPlayer;
            p1.setMatchCount(p1.getMatchCount() + 1);
            p1.setWinCount(p1.getWinCount() + 1);
            p1.setScore(p1.getScore() + 3);
            new PlayerController().update(p1);
            //gui thong bao
            SubmitMessage send1 = new SubmitMessage();
            send1.setType(StreamData.Type.SUBMIT);
            send1.setResult(loginPlayer.getEmail());
            sendObject(send1);

        }
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
            joinedRoom.gameStarted = false;
            boolean check1 = joinedRoom.getSudoku1().CheckWin();
            boolean check2 = joinedRoom.getSudoku2().CheckWin();
            SubmitMessage send1 = new SubmitMessage();
            boolean isPlayer1Win = false;
            boolean isPlayer2Win = false;
            if (check1 && !check2) {
                //1win
                send1.setType(StreamData.Type.SUBMIT);
                send1.setResult(joinedRoom.getClient1().getLoginPlayer().getEmail());
                //them 3 diem cho 1
                firstWin();
                isPlayer1Win = true;
                isPlayer2Win = false;
            } else if (!check1 && check2) {
                //2win
                send1.setType(StreamData.Type.SUBMIT);
                send1.setResult(joinedRoom.getClient2().getLoginPlayer().getEmail());
                //them 3 diem cho 2
                secondWin();
                isPlayer1Win = false;
                isPlayer2Win = true;
            } else if (!check1 && !check2) {
                //hoa
                send1.setType(StreamData.Type.SUBMIT);

                //moi th 1 diem
                draw();
                isPlayer1Win = false;
                isPlayer2Win = false;
            } else if (check1 && check2) {
                if (joinedRoom.getSudoku1().getSubmitTime() > joinedRoom.getSudoku2().getSubmitTime()) {
                    //1win
                    send1.setType(StreamData.Type.SUBMIT);
                    send1.setResult(joinedRoom.getClient1().getLoginPlayer().getEmail());
                    //them 3 dierm cho 1
                    firstWin();
                    isPlayer1Win = true;
                    isPlayer2Win = false;
                } else if (joinedRoom.getSudoku1().getSubmitTime() < joinedRoom.getSudoku2().getSubmitTime()) {
                    //2win
                    send1.setType(StreamData.Type.SUBMIT);
                    send1.setResult(joinedRoom.getClient2().getLoginPlayer().getEmail());

                    //them 3 diem cho 2
                    secondWin();
                    isPlayer1Win = false;
                    isPlayer2Win = true;
                } else {
                    //hoa
                    send1.setType(StreamData.Type.SUBMIT);

                    //moi th them 1 diem
                    draw();
                    isPlayer1Win = false;
                    isPlayer2Win = false;
                }
            }
            sendObject(send1);
            cCompetitor.sendObject(send1);
            //TODO luu game match
            new GameMatchController().add(
                    new GameMatch(0, joinedRoom.getClient1().getLoginPlayer().getId(),
                            joinedRoom.getClient2().getLoginPlayer().getId(),
                            getWinnerID(isPlayer1Win, isPlayer2Win),
                            joinedRoom.getStartedTime()));
        }
    }

    private int getWinnerID(boolean isPlayer1Win, boolean isPlayer2Win) {
        if (isPlayer1Win && !isPlayer2Win) {
            return joinedRoom.getClient1().getLoginPlayer().getId();
        }
        if (!isPlayer1Win && isPlayer2Win) {
            return joinedRoom.getClient2().getLoginPlayer().getId();
        }
        if (!isPlayer1Win && !isPlayer2Win) {
            return 0;
        }
        return 0;
    }

    private void firstWin() {
        PlayerController playerController = new PlayerController();
        Player p1 = joinedRoom.getClient1().getLoginPlayer();
        p1.setMatchCount(p1.getMatchCount() + 1);
        p1.setWinCount(p1.getWinCount() + 1);
        p1.setScore(p1.getScore() + 3);
        playerController.update(p1);

        Player p2 = joinedRoom.getClient2().getLoginPlayer();
        p2.setMatchCount(p2.getMatchCount() + 1);
        p2.setLoseCount(p2.getLoseCount() + 1);
        playerController.update(p2);
    }

    private void secondWin() {
        PlayerController playerController = new PlayerController();
        Player p2 = joinedRoom.getClient2().getLoginPlayer();
        p2.setMatchCount(p2.getMatchCount() + 1);
        p2.setWinCount(p2.getWinCount() + 1);
        p2.setScore(p2.getScore() + 3);
        playerController.update(p2);

        Player p1 = joinedRoom.getClient1().getLoginPlayer();
        p1.setMatchCount(p1.getMatchCount() + 1);
        p1.setLoseCount(p1.getLoseCount() + 1);
        playerController.update(p1);
    }

    private void draw() {
        PlayerController playerController = new PlayerController();
        Player p2 = joinedRoom.getClient2().getLoginPlayer();
        p2.setMatchCount(p2.getMatchCount() + 1);
        p2.setScore(p2.getScore() + 1);
        playerController.update(p2);

        Player p1 = joinedRoom.getClient1().getLoginPlayer();
        p1.setMatchCount(p1.getMatchCount() + 1);
        p1.setScore(p1.getScore() + 1);
        playerController.update(p1);
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
//            ChatMessage sendChat = new ChatMessage(
//                    new ChatItem(CustomDateTimeFormatter.getCurrentTimeFormatted(),
//                            "SERVER", loginPlayer.getNameId()));
//            this.joinedRoom.broadcast(sendChat);
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

    public boolean isIsReady() {
        return isReady;
    }

    public void setIsReady(boolean isReady) {
        this.isReady = isReady;
    }

    public String getAcceptPairMatchStatus() {
        return acceptPairMatchStatus;
    }

    public void setAcceptPairMatchStatus(String acceptPairMatchStatus) {
        this.acceptPairMatchStatus = acceptPairMatchStatus;
    }

    private void onReceiveGetRank() {
        ArrayList<Player> listPlayer = new PlayerController().getList();
        GetListRankMessage data = new GetListRankMessage(listPlayer, StreamData.Type.GET_LIST_RANK);
        sendObject(data);
    }

}
