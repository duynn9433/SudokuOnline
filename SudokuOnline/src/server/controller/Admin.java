/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.controller;

import java.io.IOException;
import server.RunServer;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.DAO.controller.RoomController;
import server.DAO.controller.PlayerController;
import shared.model.RoomInDB;
import shared.model.Player;

/**
 *
 * @author duynn
 */
public class Admin implements Runnable {
    Scanner s = new Scanner(System.in);

    @Override
    public void run() {

        String inp;

        while (!RunServer.isShutDown) {
            System.out.print("AdminCommand> ");
            inp = s.nextLine();
            try {
                if (inp.equalsIgnoreCase("set-level")) {
                    setLevel();

                } else if (inp.equalsIgnoreCase("shutdown")) {
                    System.out.println("shuting down...");
                    RunServer.isShutDown = true;

                    try {
                        RunServer.ss.close();
                    } catch (IOException ex) {
                        Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("Thiếu tham số !!!");
            }

            if (inp.equalsIgnoreCase("help")) {
                System.out.println("===[List commands]======================\n"
                        + "set-level:        level\n"
                        + "=======================================");
            }
        }
    }

    // Get player with the most win count
    private void setLevel() {
        System.out.println("Level=?(0->2)");
        int in = s.nextInt();
        DataCreateGame.setLV(in);
    }

    public static void main(String[] args) {
        Admin ad = new Admin();
        ad.run();
    }
}
