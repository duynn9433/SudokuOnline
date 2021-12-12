/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import model.SinhVien;

/**
 *
 * @author duynn
 */
public interface ISinhVien extends Remote{
    public int insert(SinhVien sv) throws RemoteException;
    public int update(SinhVien sv) throws RemoteException;
    public int delete(int id) throws RemoteException;
    public SinhVien findById(int id) throws RemoteException;
    public List<SinhVien> findAll() throws RemoteException;
}
