/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author nhandang
 */
public class DangNhapService {
    public DangNhap Login(String tenDangNhap , String matkhau, String tc, String acc){
        DangNhap account = null;
        try {
                        // load driver sql
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            // tạo kết nối;
            String url = "jdbc:sqlserver://localhost:1433;databaseName=QLKaraoke";
            Connection con = DriverManager.getConnection(url,tc,acc);
            
            String sql = "select ten_dn , mat_khau, ho_ten, case quyen when 1 then N'Quản Lý' when 0 then N'Nhân Viên' end as quyen , tinh_trang from TaiKhoan where ten_dn = ? and mat_khau = ?";
            PreparedStatement pstm = con.prepareStatement(sql);
            pstm.setString(1, tenDangNhap);
            pstm.setString(2, matkhau);
            ResultSet rs = pstm.executeQuery();
            if (rs.next()){
                account = new DangNhap();
                account.setTen(rs.getString(1));
                account.setMatkhau(rs.getString(2));
                account.setHoten(rs.getString(3));
                account.setQuyen(rs.getString(4));
                account.setTinhTrang(rs.getString(5));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return account;
    }
}
