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
public class DMPhongService {
    Connection con = ConnectDB.getConnect();
    // hàm lấy dữ liệu từ DMPhong
    public ArrayList<DMPhong> hienThiDMPhong (){
        ArrayList<DMPhong> dsPhong = new ArrayList<>();
        try {
            String sql = "select ma_phong , loai_phong , gia , case tinh_trang when 0 then N'Trống' when 1 then N'Đã đặt' when 2 then N'Sửa chửa' when 4 then N'Dọn dẹp' end as tinh_trang from DMPhong where tinh_trang not in (3)";
            PreparedStatement pstm = con.prepareStatement(sql);
            ResultSet rs = pstm.executeQuery();
            while (rs.next()) {                
                DMPhong dMPhong = new  DMPhong();
                dMPhong.setMaPhong(rs.getString(1));
                dMPhong.setLoaiPhong(rs.getString(2));
                dMPhong.setGiaPhong(rs.getString(3));
                dMPhong.setTinhTrang(rs.getString(4));
                dsPhong.add(dMPhong);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dsPhong;
    }
}
