/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import model.SinhVien;
import utils.ConnectionUtils;

/**
 *
 * @author duynn
 */
public class SinhVienDAO {
    public int insert(SinhVien sv) throws SQLException, ClassNotFoundException{
//        System.out.println("insert:"+sv.toString());
        int insertRow = 0;
        Connection con = ConnectionUtils.getMyConnection();
        String sql = "insert into sinh_vien (hoTen, ngaySinh, lop) value (?,?,?)";
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, sv.getHoTen());
        ps.setString(2,sv.getNgaySinh().toString());
        ps.setString(3,sv.getLop());
        insertRow = ps.executeUpdate();
        con.close();
        return insertRow;
    }
    
    public int update(SinhVien sv) throws Exception {
        Connection con = ConnectionUtils.getMyConnection();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        int insertRow = 0;
        String sql = "update sinh_vien sv set sv.hoTen = ?, sv.ngaySinh = ?, sv.lop = ? " 
                + " where sv.id = ? ";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, sv.getHoTen());
        ps.setString(2, dtf.format(sv.getNgaySinh()));
        ps.setString(3, sv.getLop());
        ps.setInt(4, sv.getId());
        insertRow = ps.executeUpdate();
        con.close();
        return insertRow;
    }
    
    public int delete(int id) throws Exception {
        int rowCount = 0;
        Connection con = ConnectionUtils.getMyConnection();
        String sql = "DELETE FROM sinh_vien sv WHERE sv.id = ?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, id);
        rowCount = ps.executeUpdate();
        con.close();
        return rowCount;
    }
    
    public SinhVien findById(int id) throws Exception {
        Connection conn = ConnectionUtils.getMyConnection();
        SinhVien sv = new SinhVien();
        String sql = "select * from sinh_vien sv where sv.id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
//            int id = rs.getInt(1);
            String hoTen = rs.getString("hoTen");
            Timestamp ngaySinh = rs.getTimestamp("ngaySinh");
            String lop = rs.getString("lop");
            sv.setId(id);
            sv.setHoTen(hoTen);
            sv.setNgaySinh(ngaySinh.toLocalDateTime().toLocalDate());
            sv.setLop(lop);
        }
        conn.close();
        return sv;
    }
    public List<SinhVien> findAll() throws Exception {
        Connection con = ConnectionUtils.getMyConnection();
        List<SinhVien> list = new ArrayList<>();
        String sql = "select * from sinh_vien ";
        PreparedStatement ps = con.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            int id = rs.getInt("id");
            String hoTen = rs.getString("hoTen");
            LocalDate ngaySinh = rs.getTimestamp("ngaySinh").toLocalDateTime().toLocalDate();
            String lop = rs.getString("lop");
            list.add(new SinhVien(id,hoTen,ngaySinh,lop));
        }
        con.close();
        return list;
    }
}
