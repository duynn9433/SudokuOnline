/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.view.scene;

import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileNotFoundException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 *
 * @author duynn 
 * Tham khảo: https://codelearn.io/sharing/lap-trinh-game-sudoku-bang-java
 */
public class SudokuGame extends javax.swing.JPanel {
    int I, J;  
    JButton bt[][] = new JButton[9][10];
    Integer[][] submit = new Integer[9][10];
    Integer board[][];
    /**
     * Creates new form NewJPanel
     */
    public SudokuGame() {
        initComponents();
        init();
    }

    public void init() {
        pn.setLayout(new GridLayout(9, 9));
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                bt[i][j] = new JButton() {
                    {
                        setSize(50, 50);
                        setMaximumSize(getSize());
                    }
                };
                bt[i][j].addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        for (int i = 0; i < 9; i++) {
                            for (int j = 0; j < 9; j++) {
                                bt[i][j].setBackground(Color.white);
                            }
                        }

                        String s = e.getActionCommand();
                        int k = s.indexOf(32);
                        int i = Integer.parseInt(s.substring(0, k));
                        int j = Integer.parseInt(s.substring(k + 1, s.length()));
                        I = i;
                        J = j;
                        if (submit[I][J] > 0) {
                            for (i = 0; i < 9; i++) {
                                for (j = 0; j < 9; j++) {
                                    if (submit[i][j] == submit[I][J]) {
                                        bt[i][j].setBackground(Color.gray);
                                    }
                                }
                            }
                        }

                    }
                });
                bt[i][j].addKeyListener(new KeyListener() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        int v = e.getKeyCode();
                        if ((v >= 49 && v <= 57) || (v >= 97 && v <= 105)) {
                            if (v >= 49 && v <= 57) {
                                v -= 48;
                            }
                            if (v >= 97 && v <= 105) {
                                v -= 96;
                            }
                            if (board[I][J] == 0) {
                                bt[I][J].setText(v + "");
                                submit[I][J] = v;
                            }
                        }
                    }

                    @Override
                    public void keyPressed(KeyEvent e) {
                        int v = e.getKeyCode();
                        if ((v >= 49 && v <= 57) || (v >= 97 && v <= 105)) {
                            if (v >= 49 && v <= 57) {
                                v -= 48;
                            }
                            if (v >= 97 && v <= 105) {
                                v -= 96;
                            }
                            if (board[I][J] == 0) {
                                bt[I][J].setText(v + "");
                                submit[I][J] = v;
                            }
                        }
                    }

                    @Override
                    public void keyReleased(KeyEvent e) {
                        int v = e.getKeyCode();
                        if ((v >= 49 && v <= 57) || (v >= 97 && v <= 105)) {
                            if (v >= 49 && v <= 57) {
                                v -= 48;
                            }
                            if (v >= 97 && v <= 105) {
                                v -= 96;
                            }
                            if (board[I][J] == 0) {
                                bt[I][J].setText(v + "");
                                submit[I][J] = v;
                            }
                        }
                    }
                });
                bt[i][j].setActionCommand(i + " " + j);
                bt[i][j].setBackground(Color.white);
                bt[i][j].setFont(new Font("UTM Micra", 1, 30));
                bt[i][j].setForeground(Color.black);
                pn.add(bt[i][j]);
                bt[i][j].setSize(50, 50);

            }
        }
        for (int i = 0; i < 9; i += 3) {
            for (int j = 0; j < 9; j += 3) {
                bt[i][j].setBorder(BorderFactory.createMatteBorder(3, 3, 1, 1, Color.black));
                bt[i][j + 2].setBorder(BorderFactory.createMatteBorder(3, 1, 1, 3, Color.black));
                bt[i + 2][j + 2].setBorder(BorderFactory.createMatteBorder(1, 1, 3, 3, Color.black));
                bt[i + 2][j].setBorder(BorderFactory.createMatteBorder(1, 3, 3, 1, Color.black));
                bt[i][j + 1].setBorder(BorderFactory.createMatteBorder(3, 1, 1, 1, Color.black));
                bt[i + 1][j + 2].setBorder(BorderFactory.createMatteBorder(1, 1, 1, 3, Color.black));
                bt[i + 2][j + 1].setBorder(BorderFactory.createMatteBorder(1, 1, 3, 1, Color.black));
                bt[i + 1][j].setBorder(BorderFactory.createMatteBorder(1, 3, 1, 1, Color.black));
//                bt[i + 1][j + 1].setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.black));
            }
        }
        this.setVisible(true);
        this.setSize(700, 700);
        pn.setSize(700, 700);

        return;
    }

    public Integer[][] getSubmit() {
        return submit;
    }

    public void setSubmit(Integer[][] submit) {
        this.submit = submit;
    }

    public Integer[][] getBoard() {
        return board;
    }

    public void setBoard(Integer[][] board) {
        this.board = board;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board[i][j] > 0) {
                    bt[i][j].setText(board[i][j] + "");
                    submit[i][j]=board[i][j];
                }
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pn = new javax.swing.JPanel();

        javax.swing.GroupLayout pnLayout = new javax.swing.GroupLayout(pn);
        pn.setLayout(pnLayout);
        pnLayout.setHorizontalGroup(
            pnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 635, Short.MAX_VALUE)
        );
        pnLayout.setVerticalGroup(
            pnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 517, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel pn;
    // End of variables declaration//GEN-END:variables
}
