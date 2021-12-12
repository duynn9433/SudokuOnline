/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.SinhVien;
import utils.ISinhVien;

/**
 *
 * @author duynn
 */
public class Client {

    public static void main(String[] args) {
        ISinhVien sv = null;
        try {
            Registry reg = LocateRegistry.getRegistry("localhost", 1303);
            sv = (ISinhVien) reg.lookup("sinhvien");
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            Scanner sc = new Scanner(System.in);
            boolean isWorking = true;
            while (isWorking) {
                System.out.println("1. Them moi");
                System.out.println("2. Cap nhat thong tin sinh vien");
                System.out.println("3. Xoa sinh vien");
                System.out.println("4. Xem thong tin cua 1 sinh vien");
                System.out.println("5. Xem tat ca thong tin sinh vien ");
                System.out.println("0. Thoat");
                int menu = Integer.parseInt(sc.nextLine());
                SinhVien sinhVien = new SinhVien();
                List<SinhVien> list = new ArrayList<>();
                switch (menu) {
                    case 1:
                        System.out.println("Nhap ho ten:");
                        sinhVien.setHoTen(sc.nextLine());
                        System.out.println("Nhap ngay sinh (yyyy-mm-dd):");
                        sinhVien.setNgaySinh(LocalDate.parse(sc.nextLine()));
                        System.out.println("Nhap lop: ");
                        sinhVien.setLop(sc.nextLine());
                        //System.out.println("sv:"+sinhVien.toString());
                        int res1 = sv.insert(sinhVien);
                        if (res1 != 0) {
                            System.out.println("Them thanh cong");
                        } else {
                            System.out.println("Them that bai");
                        }
                        System.out.println("--------------------------------");
                        break;
                    case 2:
                        System.out.println("Nhap id:");
                        sinhVien.setId(Integer.parseInt(sc.nextLine()));
                        System.out.println("Nhap ho ten:");
                        sinhVien.setHoTen(sc.nextLine());
                        System.out.println("Nhap ngay sinh (yyyy-mm-dd):");
                        sinhVien.setNgaySinh(LocalDate.parse(sc.nextLine()));
                        System.out.println("Nhap lop: ");
                        sinhVien.setLop(sc.nextLine());

                        int res2 = sv.update(sinhVien);
                        if (res2 != 0) {
                            System.out.println("Chinh sua thanh cong\n");
                        } else {
                            System.out.println("Chinh sua that bai\n");
                        }
                        System.out.println("--------------------------------");
                        break;
                    case 3:
                        System.out.println("Nhap id sinh vien can xoa:");
                        int res3 = sv.delete(Integer.parseInt(sc.nextLine()));
                        if (res3 != 0) {
                            System.out.println("Xoa thanh cong");
                        } else {
                            System.out.println("Khong ton tai id sinh vien can xoa");
                        }
                        System.out.println("--------------------------------");
                        break;
                    case 4:
                        System.out.println("Nhap id sinh vien can xem thong tin:");
                        int id = Integer.parseInt(sc.nextLine());
                        SinhVien foundSv = sv.findById(id);
                        if (foundSv.getId() == 0) {
                            System.out.println("Khong ton tai id sinh vien can xem");
                        } else {
                            System.out.println("Thonng tin sinh vien:");
                            System.out.println("Id:" + foundSv.getId()
                                    + " |Ho Ten: " + foundSv.getHoTen()
                                    + " |Ngay sinh: " + foundSv.getNgaySinh()
                                    + " |Lop: " + foundSv.getLop()
                            );
                        }
                        System.out.println("--------------------------------");
                        break;
                    case 5:
                        System.out.println("Thong tin tat ca sinh vien:");
                        list = (ArrayList<SinhVien>) sv.findAll();
                        for (SinhVien i : list) {
                            System.out.println("Id:" + i.getId()
                                    + " |Ho Ten: " + i.getHoTen()
                                    + " |Ngay sinh: " + i.getNgaySinh()
                                    + " |Lop: " + i.getLop()
                            );
                            System.out.println("--------------------------------");
                        }
                        break;
                    case 0:
                        isWorking = false;
                        break;
                }
            }
        } catch (RemoteException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NotBoundException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
