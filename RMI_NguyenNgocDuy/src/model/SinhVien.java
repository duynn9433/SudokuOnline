/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import java.time.LocalDate;

/**
 *
 * @author duynn
 */
public class SinhVien implements Serializable{
    public static final long serialVersionUID = 1L;
    private int id;
    private String hoTen;
    private LocalDate ngaySinh;
    private String lop;

    public SinhVien() {
    }

    public SinhVien(int id, String hoTen, LocalDate ngaySinh, String lop) {
        this.id = id;
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.lop = lop;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public LocalDate getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(LocalDate ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public String getLop() {
        return lop;
    }

    public void setLop(String lop) {
        this.lop = lop;
    }

    @Override
    public String toString() {
        return "SinhVien{" + "id=" + id + ", hoTen=" + hoTen + ", ngaySinh=" + ngaySinh + ", lop=" + lop + '}';
    }
    
    
}
