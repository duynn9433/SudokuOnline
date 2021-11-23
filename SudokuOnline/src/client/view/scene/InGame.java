/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.view.scene;

import client.RunClient;
import client.model.ChatItem;
import shared.constant.Avatar;
import java.awt.event.KeyEvent;
import java.util.concurrent.Callable;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.text.DefaultCaret;
import shared.model.Sudoku;
import shared.constant.StreamData;
import shared.helper.CountDownTimer;
import shared.helper.CustumDateTimeFormatter;
import shared.message.SubmitMessage;
import shared.model.PlayerInGame;

/**
 *
 * @author duynn
 */
public class InGame extends javax.swing.JFrame {

    final ImageIcon p1Icon = new ImageIcon(Avatar.ASSET_PATH + "icons8_round_24px.png");
    final ImageIcon p2Icon = new ImageIcon(Avatar.ASSET_PATH + "icons8_delete_24px_1.png");

    PlayerInGame player1;
    PlayerInGame player2;

    int[][] board;

    JButton btnOnBoard[][];
    CountDownTimer matchTimer;
    SudokuGame sudokuGame;

    /**
     * Creates new form InGame
     */
    public InGame() {
        initComponents();
        this.setLocationRelativeTo(null);

        // board
        // plBoardContainer.setLayout(new GridLayout(ROW, COLUMN));
        //initBoard();
        sudokuGame = new SudokuGame();
        plBoardContainer.add(sudokuGame);
        plBoardContainer.setVisible(true);

        // https://stackoverflow.com/a/1627068
        ((DefaultCaret) txaChat.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        // close window event
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (JOptionPane.showConfirmDialog(InGame.this,
                        "Bạn có chắc muốn thoát phòng?", "Thoát phòng?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                    RunClient.socketHandler.leaveRoom();
                }
            }
        });
    }

    public void setPlayerInGame(PlayerInGame p1, PlayerInGame p2) {
        // save data
        player1 = p1;
        player2 = p2;

        // player 1
        lbPlayerNameId1.setText(p1.getNameId());
        if (p1.getAvatar().equals("")) {
            lbAvatar1.setIcon(new ImageIcon(Avatar.PATH + Avatar.EMPTY_AVATAR));
        } else {
            lbAvatar1.setIcon(new ImageIcon(Avatar.PATH + p1.getAvatar()));
        }

        // player 2
        lbPlayerNameId2.setText(p2.getNameId());
        lbAvatar2.setIcon(new ImageIcon(Avatar.PATH + Avatar.EMPTY_AVATAR));
        if (p2.getAvatar().equals("")) {
            lbAvatar2.setIcon(new ImageIcon(Avatar.PATH + Avatar.EMPTY_AVATAR));
        } else {
            lbAvatar2.setIcon(new ImageIcon(Avatar.PATH + p2.getAvatar()));
        }

        // reset turn
//        lbActive1.setVisible(false);
//        lbActive2.setVisible(false);
    }

    public void setWin(String winEmail) {
        // pause timer
        matchTimer.pause();

        // tie
        if (winEmail == null) {
            addChat(new ChatItem("[]", "KẾT QUẢ", "HÒA"));
            JOptionPane.showMessageDialog(this, "Trận đấu kết thúc với tỉ số HÒA.", "HÒA", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // get myEmail
        String myEmail = RunClient.socketHandler.getLoginEmail();

        if (winEmail.equals(myEmail)) {
            // là email của mình thì win
            addChat(new ChatItem("[]", "KẾT QUẢ", "Bạn đã thắng"));
            JOptionPane.showMessageDialog(this, "Chúc mừng. Bạn đã chiến thắng.", "Chiến thắng", JOptionPane.INFORMATION_MESSAGE);

        } else if (myEmail.equals(player1.getEmail()) || myEmail.equals(player2.getEmail())) {
            // nếu mình là 1 trong 2 người chơi, mà winEmail ko phải mình => thua
            addChat(new ChatItem("[]", "KẾT QUẢ", "Bạn đã thua"));
            JOptionPane.showMessageDialog(this, "Rất tiếc. Bạn đã thua cuộc.", "Thua cuộc", JOptionPane.INFORMATION_MESSAGE);

        } else {
            // còn lại là viewers
            String nameId = "";
            if (player1.getEmail().equals(winEmail)) {
                nameId = player1.getNameId();
            } else {
                nameId = player2.getNameId();
            }
            addChat(new ChatItem("[]", "KẾT QUẢ", "Người chơi " + nameId + " đã thắng"));
            JOptionPane.showMessageDialog(this, "Người chơi " + nameId + " đã thắng", "Kết quả", JOptionPane.INFORMATION_MESSAGE);
        }

        // thoát phòng sau khi thua 
        // TODO sau này sẽ cho tạo ván mới, hiện tại cho thoát để tránh lỗi
        // RunClient.socketHandler.leaveRoom();
    }

    public void startGame(int matchTimeLimit) {
        matchTimer = new CountDownTimer(matchTimeLimit);
        matchTimer.setTimerCallBack(
                // end match callback
                (Callable) () -> {
                    endGameTimeout();
                    return null;
                },
                // tick match callback
                (Callable) () -> {
                    pgbMatchTimer.setValue(100 * matchTimer.getCurrentTick() / matchTimer.getTimeLimit());
                    pgbMatchTimer.setString("" + CustumDateTimeFormatter.secondsToMinutes(matchTimer.getCurrentTick()));
                    return null;
                },
                // tick interval
                1
        );
    }

    public void endGameTimeout() {
        matchTimer.pause();
        JOptionPane.showConfirmDialog(rootPane, "Hết thời gian");
        int time = matchTimer.getCurrentTick();
        SubmitMessage msg = new SubmitMessage();
        msg.setType(StreamData.Type.SUBMIT);
        msg.setSubmit(sudokuGame.getSubmit());
        msg.setCurrentTick(time);
        RunClient.socketHandler.sendObject(msg);
    }
    
    public void lockSubmit(){
        btnSubmit.setEnabled(false);
    }
    public void setMatchTimerTick(int value) {
        matchTimer.setCurrentTick(value);
    }

    public void addChat(ChatItem c) {
        txaChat.append(c.toString() + "\n");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        plRightContainer = new javax.swing.JPanel();
        plToolContainer = new javax.swing.JPanel();
        btnNewGame = new javax.swing.JButton();
        btnUndo = new javax.swing.JButton();
        btnLeaveRoom = new javax.swing.JButton();
        plPlayerContainer = new javax.swing.JPanel();
        plPlayer = new javax.swing.JPanel();
        lbAvatar1 = new javax.swing.JLabel();
        lbPlayerNameId1 = new javax.swing.JLabel();
        lbVersus = new javax.swing.JLabel();
        lbAvatar2 = new javax.swing.JLabel();
        lbPlayerNameId2 = new javax.swing.JLabel();
        btnSubmit = new javax.swing.JButton();
        plTimer = new javax.swing.JPanel();
        pgbMatchTimer = new javax.swing.JProgressBar();
        tpChatAndViewerContainer = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        txChatInput = new javax.swing.JTextField();
        btnSendMessage = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        txaChat = new javax.swing.JTextArea();
        plBoardContainer = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Caro Game");
        setResizable(false);

        plToolContainer.setBorder(javax.swing.BorderFactory.createTitledBorder("Chức năng"));

        btnNewGame.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        btnNewGame.setIcon(new javax.swing.ImageIcon(getClass().getResource("/client/view/asset/icons8_new_file_24px.png"))); // NOI18N
        btnNewGame.setText("Ván mới");

        btnUndo.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        btnUndo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/client/view/asset/icons8_undo_24px.png"))); // NOI18N
        btnUndo.setText("Đánh lại");

        btnLeaveRoom.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        btnLeaveRoom.setIcon(new javax.swing.ImageIcon(getClass().getResource("/client/view/asset/icons8_exit_sign_24px.png"))); // NOI18N
        btnLeaveRoom.setText("Thoát phòng");
        btnLeaveRoom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLeaveRoomActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout plToolContainerLayout = new javax.swing.GroupLayout(plToolContainer);
        plToolContainer.setLayout(plToolContainerLayout);
        plToolContainerLayout.setHorizontalGroup(
            plToolContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, plToolContainerLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(plToolContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addGroup(plToolContainerLayout.createSequentialGroup()
                        .addComponent(btnNewGame)
                        .addGap(6, 6, 6)
                        .addComponent(btnUndo))
                    .addComponent(btnLeaveRoom))
                .addGap(42, 42, 42))
        );
        plToolContainerLayout.setVerticalGroup(
            plToolContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, plToolContainerLayout.createSequentialGroup()
                .addContainerGap(14, Short.MAX_VALUE)
                .addGroup(plToolContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnNewGame)
                    .addComponent(btnUndo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnLeaveRoom)
                .addGap(18, 18, 18))
        );

        plPlayer.setBorder(javax.swing.BorderFactory.createTitledBorder("Người chơi"));

        lbAvatar1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbAvatar1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/client/view/asset/avatar/icons8_circled_user_male_skin_type_7_96px.png"))); // NOI18N
        lbAvatar1.setBorder(javax.swing.BorderFactory.createTitledBorder("..."));
        lbAvatar1.setOpaque(true);

        lbPlayerNameId1.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N
        lbPlayerNameId1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbPlayerNameId1.setText("Hoang");

        lbVersus.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbVersus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/client/view/asset/icons8_sword_48px.png"))); // NOI18N

        lbAvatar2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbAvatar2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/client/view/asset/avatar/icons8_circled_user_female_skin_type_7_96px.png"))); // NOI18N
        lbAvatar2.setBorder(javax.swing.BorderFactory.createTitledBorder("..."));

        lbPlayerNameId2.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N
        lbPlayerNameId2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbPlayerNameId2.setText("Hien");

        btnSubmit.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        btnSubmit.setText("Kết thúc");
        btnSubmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSubmitActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout plPlayerLayout = new javax.swing.GroupLayout(plPlayer);
        plPlayer.setLayout(plPlayerLayout);
        plPlayerLayout.setHorizontalGroup(
            plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(plPlayerLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lbAvatar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbPlayerNameId1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(plPlayerLayout.createSequentialGroup()
                        .addComponent(btnSubmit)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(plPlayerLayout.createSequentialGroup()
                        .addComponent(lbVersus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lbAvatar2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lbPlayerNameId2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        plPlayerLayout.setVerticalGroup(
            plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(plPlayerLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(plPlayerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(plPlayerLayout.createSequentialGroup()
                        .addComponent(lbAvatar1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbPlayerNameId1, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(plPlayerLayout.createSequentialGroup()
                        .addComponent(lbAvatar2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbPlayerNameId2, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lbVersus, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnSubmit)
                .addContainerGap(13, Short.MAX_VALUE))
        );

        plTimer.setBorder(javax.swing.BorderFactory.createTitledBorder("Thời gian"));

        pgbMatchTimer.setValue(100);
        pgbMatchTimer.setString("Đang đợi nước đi đầu tiên..");
        pgbMatchTimer.setStringPainted(true);

        javax.swing.GroupLayout plTimerLayout = new javax.swing.GroupLayout(plTimer);
        plTimer.setLayout(plTimerLayout);
        plTimerLayout.setHorizontalGroup(
            plTimerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, plTimerLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(pgbMatchTimer, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32))
        );
        plTimerLayout.setVerticalGroup(
            plTimerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(plTimerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pgbMatchTimer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(24, 24, 24))
        );

        javax.swing.GroupLayout plPlayerContainerLayout = new javax.swing.GroupLayout(plPlayerContainer);
        plPlayerContainer.setLayout(plPlayerContainerLayout);
        plPlayerContainerLayout.setHorizontalGroup(
            plPlayerContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(plPlayer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(plTimer, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        plPlayerContainerLayout.setVerticalGroup(
            plPlayerContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(plPlayerContainerLayout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addComponent(plPlayer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(plTimer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        txChatInput.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txChatInputKeyPressed(evt);
            }
        });

        btnSendMessage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/client/view/asset/icons8_paper_plane_24px.png"))); // NOI18N
        btnSendMessage.setText("Gửi");
        btnSendMessage.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnSendMessageMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txChatInput, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSendMessage, javax.swing.GroupLayout.DEFAULT_SIZE, 93, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnSendMessage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txChatInput))
                .addContainerGap())
        );

        txaChat.setEditable(false);
        txaChat.setColumns(20);
        txaChat.setRows(5);
        jScrollPane3.setViewportView(txaChat);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        tpChatAndViewerContainer.addTab("Nhắn tin", jPanel3);

        javax.swing.GroupLayout plRightContainerLayout = new javax.swing.GroupLayout(plRightContainer);
        plRightContainer.setLayout(plRightContainerLayout);
        plRightContainerLayout.setHorizontalGroup(
            plRightContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(plPlayerContainer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(plToolContainer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(tpChatAndViewerContainer)
        );
        plRightContainerLayout.setVerticalGroup(
            plRightContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, plRightContainerLayout.createSequentialGroup()
                .addComponent(plToolContainer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(plPlayerContainer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tpChatAndViewerContainer, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        plBoardContainer.setLayout(new java.awt.GridLayout(1, 0));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(plBoardContainer, javax.swing.GroupLayout.PREFERRED_SIZE, 700, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(plRightContainer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(plBoardContainer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(plRightContainer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnLeaveRoomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLeaveRoomActionPerformed
        // https://stackoverflow.com/a/8689130
        if (JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn thoát phòng?", "Warning",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
            RunClient.socketHandler.leaveRoom();
        }
    }//GEN-LAST:event_btnLeaveRoomActionPerformed

    private void btnSubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSubmitActionPerformed
        // TODO add your handling code here:
        matchTimer.pause();
        int time = matchTimer.getCurrentTick();
        SubmitMessage msg = new SubmitMessage();
        msg.setType(StreamData.Type.SUBMIT);
        msg.setSubmit(sudokuGame.getSubmit());
        msg.setCurrentTick(time);
        RunClient.socketHandler.sendObject(msg);
    }//GEN-LAST:event_btnSubmitActionPerformed

    private void btnSendMessageMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSendMessageMouseClicked
        String chatMsg = txChatInput.getText();
        txChatInput.setText("");

        if (!chatMsg.equals("")) {
            RunClient.socketHandler.chatRoom(chatMsg);
        }
    }//GEN-LAST:event_btnSendMessageMouseClicked

    private void txChatInputKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txChatInputKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            btnSendMessageMouseClicked(null);
        }
    }//GEN-LAST:event_txChatInputKeyPressed
    public int[][] getBoard() {
        return board;
    }

    public void setBoard(int[][] board) {
        this.board = board;
        sudokuGame.setBoard(board);
        startGame(Sudoku.MATCH_TIME_LIMIT);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(InGame.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(InGame.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(InGame.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(InGame.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new InGame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLeaveRoom;
    private javax.swing.JButton btnNewGame;
    private javax.swing.JButton btnSendMessage;
    private javax.swing.JButton btnSubmit;
    private javax.swing.JButton btnUndo;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lbAvatar1;
    private javax.swing.JLabel lbAvatar2;
    private javax.swing.JLabel lbPlayerNameId1;
    private javax.swing.JLabel lbPlayerNameId2;
    private javax.swing.JLabel lbVersus;
    private javax.swing.JProgressBar pgbMatchTimer;
    private javax.swing.JPanel plBoardContainer;
    private javax.swing.JPanel plPlayer;
    private javax.swing.JPanel plPlayerContainer;
    private javax.swing.JPanel plRightContainer;
    private javax.swing.JPanel plTimer;
    private javax.swing.JPanel plToolContainer;
    private javax.swing.JTabbedPane tpChatAndViewerContainer;
    private javax.swing.JTextField txChatInput;
    private javax.swing.JTextArea txaChat;
    // End of variables declaration//GEN-END:variables
}
