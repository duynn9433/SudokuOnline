/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.controller;

import server.DAO.DAOClass.GameDataDAO;

/**
 *
 * @author duynn
 */
public class DataCreateGame {
    private static int LV=0;
    private static int an[] = {0, 45, 52};

    public static int getLV() {
        return LV;
    }

    public static void setLV(int LV) {
        DataCreateGame.LV = LV;
    }
    
    public static String getRandomData() {
        String s = new GameDataDAO().getRandom().getData();
        return s;
    }

    public static Integer[][] createBoard(String data) {
        String str = data;
        int[][] a = new int[9][10];
        int[][] A = new int[9][10];
        Integer[][] aa = new Integer[9][10];
        int temp[] = new int[81];
        int x[] = new int[81];
        int y[] = new int[81];

        int N = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                a[i][j] = A[i][j] = str.charAt(i * 9 + j) - 48;
            }
        }

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                x[N] = i;
                y[N] = j;
                temp[N++] = (int) (10000 * Math.random());
            }
        }
        for (int i = 0; i < N - 1; i++) {
            for (int j = i + 1; j < N; j++) {
                if (temp[i] > temp[j]) {
                    int t = x[i];
                    x[i] = x[j];
                    x[j] = t;

                    t = y[i];
                    y[i] = y[j];
                    y[j] = t;

                    t = temp[i];
                    temp[i] = temp[j];
                    temp[j] = t;
                }
            }
        }
        for (int i = 0; i < an[LV]; i++) {
            a[x[i]][y[i]] = 0;
        }

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                aa[i][j] = a[i][j];
            }
        }
        return aa;
    }
    
}
