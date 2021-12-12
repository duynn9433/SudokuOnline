/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.ISinhVien;
import utils.SinhVienImp;

/**
 *
 * @author duynn
 */
public class Sever {
    public static void main(String[] args) {
        try {
            ISinhVien sv = new SinhVienImp();
            System.out.println("server is running !!!");
            
            Registry reg = LocateRegistry.createRegistry(1303);
            reg.rebind("sinhvien", sv);
        } catch (RemoteException ex) {
            Logger.getLogger(Sever.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
}
