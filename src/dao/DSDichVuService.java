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
public class DSDichVuService {

    public ArrayList<DSDichVu> hienThiDSDichVu(String maHD ) {
        Connection con = ConnectDB.getConnect();
        ArrayList<DSDichVu> dsDichVu = new ArrayList<>();
        try {
            String sql = "SELECT DISTINCT HoaDon.ma_HD, CTHoaDon.ma_DV, CTHoaDon.ma_CTHD, CTHoaDon.so_luong ,  Kho.ten_DV , Kho.dvt , Kho.gia FROM HoaDon, CTHoaDon, Kho , DMPhong WHERE Kho.ma_DV = CTHoaDon.ma_DV AND CTHoaDon.tinh_trang = 0 AND HoaDon.ma_HD = CTHoaDon.ma_HD AND HoaDon.ma_HD = ? ";
            PreparedStatement pstm = con.prepareStatement(sql);
            pstm.setString(1, maHD);
            ResultSet rs = pstm.executeQuery();
            while (rs.next()) {                
                DSDichVu dsDV = new DSDichVu();
                dsDV.setMaHoaDon(rs.getString(1));
                dsDV.setMaDV(rs.getString(2));
                dsDV.setMaCTHD(rs.getString(3));
                dsDV.setSoLuong(rs.getString(4));
                dsDV.setTenDichVu(rs.getString(5));
                dsDV.setDonViTinh(rs.getString(6));
                dsDV.setGia(rs.getString(7));
                dsDichVu.add(dsDV);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dsDichVu;
    }
}
