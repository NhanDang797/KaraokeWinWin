/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import dao.ConnectDB;
import dao.DSDichVu;
import dao.DSDichVuService;
import dao.DangNhap;
import io.FileFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import org.apache.poi.ss.usermodel.ExcelStyleDateFormatter;

/**
 *
 * @author Administrator
 */
public class ThanhToan extends javax.swing.JFrame {

    /**
     * Creates new form ThanhToan
     */
    DefaultTableModel dtmDSDivhVu;
    ArrayList<DSDichVu> danhSachDichVu;
    DSDichVuService dsDichVuService;
    DefaultListModel listKhuyenMai;
    String giohat;
    String maDDP, maP, gia, giovao_, maHoaDon;
    SimpleDateFormat df;
    String km = "";

    public ThanhToan(String maPhong, String giaphong, String maDDP_, String maHD, String giovao) {
        initComponents();
        df = new SimpleDateFormat("yyyy-MM-dd");
        HienThiUser();
        hienthiKhuyenMai();
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setResizable(false);
        tenp.setText(maPhong);
        maDDP = maDDP_;
        maP = maPhong;
        maHoaDon = maHD;

        gia = giaphong;
        giovao_ = giovao;
        System.out.println(giovao_);
        this.giaphong.setText(giaphong.replace(".000", ""));

        dtmDSDivhVu = new DefaultTableModel();
        dtmDSDivhVu.addColumn("STT");
        dtmDSDivhVu.addColumn("Tên Dịch Vụ");
        dtmDSDivhVu.addColumn("Số Lượng");
        dtmDSDivhVu.addColumn("Đơn Giá");

        dsDichVuService = new DSDichVuService();
        danhSachDichVu = dsDichVuService.hienThiDSDichVu(maHD);
        int stt = 0;
        for (DSDichVu dsDichVu : danhSachDichVu) {
            stt++;
            Vector<Object> vecDSDichVu = new Vector<>();
            vecDSDichVu.add(stt);
            vecDSDichVu.add(dsDichVu.getTenDichVu());
            vecDSDichVu.add(dsDichVu.getSoLuong());
            vecDSDichVu.add((dsDichVu.getGia().replace(".000", "") + "    vnd").trim());
            dtmDSDivhVu.addRow(vecDSDichVu);
        }
        tableDV.setModel(dtmDSDivhVu);

        // tien dv
        double giaDV, slDV, gia1dv = 0, tongtienPDV = 0;
        for (DSDichVu dsdv : danhSachDichVu) {
            giaDV = Double.parseDouble(dsdv.getGia());
            slDV = Double.parseDouble(dsdv.getSoLuong());
            gia1dv = giaDV * slDV;
            tongtienPDV = gia1dv + tongtienPDV;
            //System.out.println(gia1dv);
            //System.out.println(tongtienPDV);
            //System.out.println("----------------");
            tiendv.setText((tongtienPDV + "").replace(".0", ""));
        }

        // tien phong;
        try {
            Connection con = ConnectDB.getConnect();
            String sql = "select gio_hat from HoaDon where HoaDon.ma_HD = ?";
            PreparedStatement pstm = con.prepareStatement(sql);
            pstm.setString(1, maHD);
            ResultSet rs = pstm.executeQuery();
            while (rs.next()) {
                giohat = rs.getString(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String gio = giohat.substring(0, 2);
        String phut = giohat.substring(3, 5);

        float giohat = Float.parseFloat(gio);
        float phuthat = (Float.parseFloat(phut)) / 60;
        DecimalFormat dcf = new DecimalFormat("#.##");
        float tonggiohat = Float.parseFloat(dcf.format(giohat + phuthat));
        float tongtienP = Float.parseFloat(giaphong) * tonggiohat;
        tienP.setText((tongtienP + "").trim().replace(".0", ""));

        double tongthanhtoan = tongtienPDV + tongtienP;
        //double tongthanhtoanKM = tongthanhtoan - tongthanhtoan*
        tongtien.setText((tongthanhtoan + "").trim().replace(".0", "") + "   vnd");
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        giaphong = new javax.swing.JTextField();
        tiendv = new javax.swing.JTextField();
        tienP = new javax.swing.JTextField();
        tenp = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        listkm = new javax.swing.JList<>();
        tongtien = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableDV = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        btnThoat = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Thanh toán"));

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel1.setText("Tên phòng:");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel2.setText("Giá phòng:");

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel3.setText("Tổng tiền phòng:");

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel4.setText("Tiền dịch vụ:");

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel6.setText("Khuyến mãi: ");

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel7.setText("Tổng thanh toán:");

        giaphong.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                giaphongActionPerformed(evt);
            }
        });

        listkm.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(listkm);

        tongtien.setFont(new java.awt.Font("Tahoma", 3, 14)); // NOI18N
        tongtien.setText(" ");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tongtien, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel1)
                                    .addComponent(jLabel2))
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(58, 58, 58)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(giaphong, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(tenp, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(tiendv, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(tienP, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel6)
                            .addGap(50, 50, 50)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(19, 27, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(tenp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(giaphong, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(tiendv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(tienP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addComponent(jLabel6))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(25, 25, 25)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(tongtien, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Danh sách dịch vụ"));

        tableDV.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Tên dịch vụ", "Số lượng", "Đơn giá"
            }
        ));
        jScrollPane1.setViewportView(tableDV);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 317, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE)
                .addGap(40, 40, 40))
        );

        jButton1.setText("In Hóa Đơn");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        btnThoat.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnThoat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/thoat.png"))); // NOI18N
        btnThoat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThoatActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnThoat, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnThoat, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnThoatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThoatActionPerformed
        dispose();
        MenuChinh menuchinh = new MenuChinh(tenDN, hoTen, quyen, matkhau);
        menuchinh.setVisible(true);
    }//GEN-LAST:event_btnThoatActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        Date date = new Date();
        SimpleDateFormat sd = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
        SimpleDateFormat mat = new SimpleDateFormat(" HH:mm");
        try {
            // in hóa đơn

            PrintWriter writer = null;
            writer = new PrintWriter(new File("hoadon.txt"), "UTF-8");
            writer.println("");
            writer.println("\t\t    " + sd.format(date));
            writer.println("----------- KARAOKE_WINWIN ------------");

            writer.println("---------------------------------------");
            writer.println("");
            writer.println("\t   PHIẾU THANH TOÁN");
            writer.println("Tên Phòng :" + maP);
            writer.println("Giá Phòng :" + gia + "/h");
            writer.println("Tiền Phòng :" + tienP.getText().replace(".000", "  ") + " [ " + giovao_ + " - " + mat.format(date) + "]");
            writer.println("\t Tên DV     Số Lượng     Giá");
            for (DSDichVu dSDichVu : danhSachDichVu) {
                writer.println(" \t " + dSDichVu.getTenDichVu() + "    \t " + dSDichVu.getSoLuong() + " \t " + dSDichVu.getGia().replace(".000", ""));
            }
            writer.println("Tổng Tiền Dịch Vụ :" + tiendv.getText());
            writer.println("Tổng Tiền  :" + tongtien.getText()  );
            writer.flush();
            writer.close();

            
            
            
        try {
            Connection con = ConnectDB.getConnect();
            String sql = "update DonDatPhong set tinh_trang = '0' where DonDatPhong.ma_DDP = ?";
            PreparedStatement pstm = con.prepareStatement(sql);
            pstm.setString(1, maDDP);
            pstm.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Connection con = ConnectDB.getConnect();
            String sql = "update DMPhong set tinh_trang = 0 where DMPhong.ma_phong = ?";
            PreparedStatement pstm = con.prepareStatement(sql);
            pstm.setString(1, maP);
            pstm.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        try {
//            Connection con = ConnectDB.getConnect();
//            String sql = "insert into DSKM values(?,?)";
//            PreparedStatement pstm = con.prepareStatement(sql);
//            pstm.setString(1, maHoaDon);
//            pstm.setString(2, km);
//            pstm.executeUpdate();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        
        
        
            Runtime objr = Runtime.getRuntime();
            objr.exec("Notepad.exe " + "hoadon.txt");
            JOptionPane.showMessageDialog(null, "In Hóa Đơn Thành công");
            dispose();
            MenuChinh menuchinh = new MenuChinh(tenDN, hoTen, quyen, matkhau);
            menuchinh.setVisible(true);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ThanhToan.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ThanhToan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void giaphongActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_giaphongActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_giaphongActionPerformed
    String makm, phantram;

    public void hienthiKhuyenMai() {
        listKhuyenMai = new DefaultListModel();
        Date today = new Date();
        String ngay_bd, ngay_kt = "";
        try {
            Connection con = ConnectDB.getConnect();
            String sql = "select ma_KM , phan_tram, ngay_bd, ngay_kt, ten_KM from CTKM where tinh_trang = 0";
            PreparedStatement pstm = con.prepareStatement(sql);
            ResultSet rs = pstm.executeQuery();
            while (rs.next()) {
                ngay_bd = rs.getString(3);
                ngay_kt = rs.getString(4);
                if (!df.parse(ngay_bd).after(today) && df.parse(ngay_kt).after(today)) {
                    listKhuyenMai.addElement(rs.getString(5) +": "+rs.getString(2) + "%");
                    km = rs.getString(1) + "," + km;
                    makm = rs.getString(1);
                    phantram = rs.getString(2);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        listkm.setModel(listKhuyenMai);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ThanhToan(null, null, null, null, null).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnThoat;
    private javax.swing.JTextField giaphong;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JList<String> listkm;
    private javax.swing.JTable tableDV;
    private javax.swing.JTextField tenp;
    private javax.swing.JTextField tienP;
    private javax.swing.JTextField tiendv;
    private javax.swing.JLabel tongtien;
    // End of variables declaration//GEN-END:variables

    String tenDN, hoTen, quyen, matkhau;

    private void HienThiUser() {
        File file = new File("data.data");
        if (file.exists()) {
            Object data = FileFactory.ReadData("data.data");
            if (data != null) {
                DangNhap dn = (DangNhap) data; // ép kiểu về dạng đối tượng
                tenDN = dn.getTen();
                hoTen = dn.getHoten();
                quyen = dn.getQuyen();
                matkhau = dn.getMatkhau();
            }
        }
    }

}
