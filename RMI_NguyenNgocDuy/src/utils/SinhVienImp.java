/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import dao.SinhVienDAO;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.SinhVien;

/**
 *
 * @author duynn
 */
public class SinhVienImp extends UnicastRemoteObject implements ISinhVien{

    public SinhVienImp() throws RemoteException {
        super();
    }

    
    @Override
    public int insert(SinhVien sv) throws RemoteException{
        SinhVienDAO svDao = new SinhVienDAO();
        try {
            return svDao.insert(sv);
        } catch (SQLException ex) {
            Logger.getLogger(SinhVienImp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SinhVienImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    @Override
    public int update(SinhVien sv) throws RemoteException{
        SinhVienDAO svDao = new SinhVienDAO();
        try {
            return svDao.update(sv);
        } catch (SQLException ex) {
            Logger.getLogger(SinhVienImp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SinhVienImp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(SinhVienImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
    
    @Override
    public int delete(int id) throws RemoteException{
        SinhVienDAO svDao = new SinhVienDAO();
        try {
            return svDao.delete(id);
        } catch (SQLException ex) {
            Logger.getLogger(SinhVienImp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SinhVienImp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(SinhVienImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    @Override
    public SinhVien findById(int id) throws RemoteException{
        SinhVienDAO svDao = new SinhVienDAO();
        try {
            return svDao.findById(id);
        } catch (Exception ex) {
            Logger.getLogger(SinhVienImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public List<SinhVien> findAll() throws RemoteException{
        SinhVienDAO svDao = new SinhVienDAO();
        try {
            return svDao.findAll();
        } catch (Exception ex) {
            Logger.getLogger(SinhVienImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
}
