/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 *
 * @author nhandang
 */
public class TaiKhoanService  {
    Connection con = ConnectDB.getConnect();
    // hiển thị toàn bộ tài khoản 
    public ArrayList<TaiKhoan> HienThiTaiKhoan() {
        ArrayList<TaiKhoan> dsTaiKhoan = new  ArrayList<>();
        try {
            String sql = "select ten_dn, mat_khau, ho_ten, case quyen when 1 then N'Quản Lý'  when 0 then N'Nhân Viên' end as quyen, case tinh_trang when 1 then N'Đã Khóa' when 0 then N'Hoạt Động' end as tinh_trang  from TaiKhoan ";
            PreparedStatement pstm = con.prepareStatement(sql);
            ResultSet rs = pstm.executeQuery();
            while (rs.next()) {                
               TaiKhoan taikhoan = new TaiKhoan();
               taikhoan.setTenDangNhap(rs.getString(1));
               taikhoan.setMatKhau(rs.getString(2));
               taikhoan.setHoTen(rs.getString(3));
               taikhoan.setQuyen(rs.getString(4));
               taikhoan.setTinhTrang(rs.getString(5));
               dsTaiKhoan.add(taikhoan);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  dsTaiKhoan;
    }
    // thêm tài khoản
    public int themTaiKhoan(TaiKhoan taiKhoan){
        try {
            String sql = "insert into TaiKhoan values(?,?,?,?,?)";
            PreparedStatement pstm = con.prepareStatement(sql);
            pstm.setString(1, taiKhoan.getTenDangNhap());
            pstm.setString(2, taiKhoan.getMatKhau());
            pstm.setString(3, taiKhoan.getHoTen());
            pstm.setString(4, taiKhoan.getQuyen());
            pstm.setString(5, taiKhoan.getTinhTrang());
            return pstm.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
    // kiểm tra tồn tại tài khoản
    public boolean kiemTraTonTai(String tenDangNhap){
        try {
            String sql = "select ten_dn from TaiKhoan where ten_dn = ?";
            PreparedStatement pstm = con.prepareStatement(sql);
            pstm.setString(1, tenDangNhap);
            ResultSet rs = pstm.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    // cập nhật tài khoản;
     public int suaTaiKhoan(TaiKhoan taiKhoan){
        try {
            String sql = "update TaiKhoan set ho_ten = ?, mat_khau = ?, quyen = ? , tinh_trang = ? where ten_dn = ?";
            PreparedStatement pstm = con.prepareStatement(sql);
            pstm.setString(1, taiKhoan.getHoTen());
            pstm.setString(2, taiKhoan.getMatKhau());
            pstm.setString(3, taiKhoan.getQuyen());
            pstm.setString(4, taiKhoan.getTinhTrang());
            pstm.setString(5, taiKhoan.getTenDangNhap());
            return pstm.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
    public int khoaTaiKhoan(TaiKhoan taiKhoan){
        try {
            String sql = "update TaiKhoan set tinh_trang = ? where ten_dn = ?";
            PreparedStatement pstm = con.prepareStatement(sql);
            pstm.setString(1, taiKhoan.getTinhTrang());
            pstm.setString(2, taiKhoan.getTenDangNhap());
            return pstm.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
    //tim kiem
    public ArrayList<TaiKhoan> timKiemTaiKhoan(String text) {
        ArrayList<TaiKhoan> dsTaiKhoan = new  ArrayList<>();
        try {
            String sql = "select ten_dn, mat_khau, ho_ten, case quyen when 1 then N'Quản Lý' when 3 then N'Admin' when 0 then N'Nhân Viên' end as quyen, case tinh_trang when 1 then N'Đã Khóa' when 0 then N'Hoạt Động' end as tinh_trang  from TaiKhoan where ten_dn LIKE ? or ho_ten LIKE ?";
            PreparedStatement pstm = con.prepareStatement(sql);
            pstm.setString(1, "%" +text+ "%");
            pstm.setString(2, "%" +text+ "%");
            ResultSet rs = pstm.executeQuery();
            while (rs.next()) {                
               TaiKhoan taikhoan = new TaiKhoan();
               taikhoan.setTenDangNhap(rs.getString(1));
               taikhoan.setMatKhau(rs.getString(2));
               taikhoan.setHoTen(rs.getString(3));
               taikhoan.setQuyen(rs.getString(4));
               taikhoan.setTinhTrang(rs.getString(5));
               dsTaiKhoan.add(taikhoan);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  dsTaiKhoan;
    }
}
