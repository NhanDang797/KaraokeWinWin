/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import dao.ConnectDB;
import dao.DangNhap;
import io.FileFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.util.Date;
import java.io.FileNotFoundException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 *
 * @author Kan
 */
public class frmHoaDon extends javax.swing.JFrame {

    Connection con = ConnectDB.getConnect();
    PreparedStatement pstm;
    Statement stmt;
    String url, sql, ten_kh, ma_phong, ngay_lap, ten_km;
    ResultSet rs;
    DefaultTableModel HD_Model;
    Vector header, data, row;
    SimpleDateFormat df;
    Calendar cal;
    Date yesterday, tomorrow, date1, date2;
    int ma_kh, ma_km, ma_hd, phan_tram;
    double tien_hat, tien_dv, tong_tien, gio_hat, gia_phong, tong_ds, binh_quan;
    BigDecimal tienhat, tiendv, tongtien, giaphong, tongds;
    Locale localeEN;
    NumberFormat en;
    DecimalFormat dcf;
    SimpleDateFormat timeFormat;

    String giohat, gio, phut;
    double _gio, _phut;

    public frmHoaDon() {
        initComponents();
        HienThiUser();
        this.setLocationRelativeTo(null);
        this.setResizable(false);

        data = new Vector();
        header = new Vector();
        header.add("Mã Phòng");
        header.add("Tên KH");
        header.add("Ngày lập");
        header.add("Giờ hát");
        header.add("Tiền hát");
        header.add("Tiền Dịch vụ");
        header.add("Tên Khuyến Mãi");
        header.add("Tổng tiền");
        HD_Model = new DefaultTableModel();

        df = new SimpleDateFormat("yyyy-MM-dd");
        dcf = new DecimalFormat("#.##");
        timeFormat = new SimpleDateFormat("HH:mm");
        timeFormat.setTimeZone(TimeZone.getDefault().getTimeZone("UTC"));
        gia_phong = tien_hat = gio_hat = tien_dv = tong_tien = tong_ds = binh_quan = 0;
        ten_kh = ten_km = ma_phong = ngay_lap = url = sql = "";
        ma_km = ma_kh = ma_hd = 0;
        phan_tram = 1;
        giohat = gio = phut = "";
        _gio = _phut = 0;
        localeEN = new Locale("en", "EN");
        en = NumberFormat.getInstance(localeEN);
        loadData();
    }

    private void initField() {
        cal = Calendar.getInstance();
        cal.add(Calendar.DATE, +1);
        tomorrow = cal.getTime();

        txtTong_ds.setEditable(false);
        txtBinh_quan.setEditable(false);
        jdcNgay_bd.setDate(new Date());
        jdcNgay_kt.setDate(tomorrow);
    }

    private void loadData() {
        initField();
        while (HD_Model.getRowCount() > 0) {
            HD_Model.removeRow(0);
        }
        double t_tien, tien_km = 0;

        try {
            sql = "SELECT a.ma_HD, a.ma_KM, a.ngay_lap, a.gio_hat, b.ma_phong, b.ma_KH FROM HoaDon a, DonDatPhong b where a.ma_DDP = b.ma_DDP";
            pstm = con.prepareStatement(sql);
            ResultSet rs1 = pstm.executeQuery();

            while (rs1.next()) {
                ten_km = "";
                tien_dv = 0;
                phan_tram = 0;

                ma_hd = rs1.getInt(1);
                ma_km = rs1.getInt(2);
                ngay_lap = rs1.getString(3);

                giohat = rs1.getString(4);
                gio = giohat.substring(0, 2);
                phut = giohat.substring(3, 5);
                _gio = Double.parseDouble(gio);
                _phut = (Double.parseDouble(phut)) / 60;
                gio_hat = _gio + _phut;

                ma_phong = rs1.getString(5);
                ma_kh = rs1.getInt(6);
                tien_hat = gio_hat * getGia_phong(ma_phong);
                String[][] km;
                km = getTen_km(ma_hd);
                for (int i = 0; i < km.length; i++) {
                    if (km[i][0] != null) {
                        if (i == 0) {
                            ten_km += km[i][0];
                        } else {
                            ten_km += "," + km[i][0];
                        }
                    }
                }
                if (!ten_km.equals("")) {
                    for (int i = 0; i < km.length; i++) {
                        if (i == 0) {
                            if (!km[i][2].equals("Sản phẩm")) {
                                phan_tram += Integer.parseInt(km[i][1]);
                            }
                        }
                    }
                } else {
                    phan_tram = 0;
                }
                tien_dv = getTien_DV(ma_hd);
                t_tien = tien_hat + tien_dv;
                tien_km = phan_tram * (t_tien / 100);
                tong_tien = t_tien - tien_km;
                tong_ds += tong_tien;

                row = new Vector();
                row.add(ma_phong);
                row.add(getTen_kh(ma_kh));
                row.add(ngay_lap);
                row.add(giohat + "h");
                tienhat = new BigDecimal(tien_hat);
                row.add(en.format(tienhat));
                tiendv = new BigDecimal(tien_dv);
                row.add(en.format(tiendv));
                row.add(ten_km);
                tongtien = new BigDecimal(tong_tien);
                row.add(en.format(tongtien));

                data.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        HD_Model.setDataVector(data, header);
        tblHoa_don.setModel(HD_Model);
        tongds = new BigDecimal(tong_ds);
        txtTong_ds.setText(en.format(tongds) + " vnđ");
        txtBinh_quan.setText(dcf.format(calGio_hat(HD_Model)) + "h");
    }

    private double calGio_hat(DefaultTableModel model) {
        double tong_gio_hat = 0;
        model = (DefaultTableModel) tblHoa_don.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            giohat = model.getValueAt(i, 3).toString();
            gio = giohat.substring(0, 2);
            phut = giohat.substring(3, 5);
            _gio = Double.parseDouble(gio);
            _phut = (Double.parseDouble(phut)) / 60;
            gio_hat = _gio + _phut;
            tong_gio_hat += gio_hat;
        }
        binh_quan = tong_gio_hat / model.getRowCount();
        return binh_quan;
    }

    private double getGia_phong(String ma_phong) {
        try {
            sql = "SELECT gia FROM DMPhong WHERE ma_phong = N'" + ma_phong + "'";
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                gia_phong = Float.parseFloat(rs.getString("gia"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return gia_phong;
    }

    private String getTen_kh(int ma_kh) {
        try {
            sql = "SELECT ten_KH FROM KhachHang WHERE ma_kh = " + ma_kh;
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                ten_kh = rs.getString("ten_kh");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ten_kh;
    }

    private double getTien_DV(int ma_hd) {
        double t_tien, tien_km = 0;
        int phan_tram;
        String[][] km = new String[100][3];
        km = getTen_km(ma_hd);
        String ten_dv = "";
        try {
            sql = "SELECT k.gia, a.so_luong, k.ten_DV FROM CTHoaDon a LEFT JOIN Kho k ON a.ma_DV = k.ma_DV WHERE ma_HD = " + ma_hd;
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                ten_dv = rs.getString("ten_DV");
                phan_tram = 0;
                for (int i = 0; i < km.length; i++) {
                    if (ten_dv.equals(km[i][0])) {
                        phan_tram = Integer.parseInt(km[i][1]);
                    }
                }
                t_tien = (Double.parseDouble(rs.getString("gia")) * rs.getInt("so_luong"));
                tien_km = phan_tram * (t_tien / 100);
                tien_dv += t_tien - tien_km;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tien_dv;
    }

    private String[][] getTen_km(int ma_hd) {
        int i = 0;
        String[][] km = new String[100][3];
        try {
            sql = "SELECT k.Ten_KM, k.phan_tram, k.loai_KM  FROM DSKM a "
                    + "LEFT JOIN CTKM k ON a.ma_KM = k.ma_KM "
                    + "WHERE a.ma_HD = ?";
            pstm = con.prepareStatement(sql);
            pstm.setInt(1, ma_hd);
            rs = pstm.executeQuery();
            while (rs.next()) {
                km[i][0] = rs.getString(1);
                km[i][1] = rs.getString(2);
                km[i][2] = rs.getString(3);
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();;
        }
        return km;
    }

    private boolean filter(String ngay_bd, String ngay_kt) {
        while (HD_Model.getRowCount() > 0) {
            HD_Model.removeRow(0);
        }

        int i = 0;
        tong_ds = 0;
        double t_tien, tien_km = 0;

        try {
            sql = "SELECT a.ma_HD, a.ma_KM, a.ngay_lap, a.gio_hat, b.ma_phong, b.ma_KH \n"
                    + " FROM	HoaDon a, DonDatPhong b\n"
                    + " WHERE a.ma_DDP = b.ma_DDP";
            if (!ngay_bd.equals("")) {
                sql += " AND a.ngay_lap >= '" + ngay_bd + "'";
                i++;
            }
            if (!ngay_kt.equals("")) {
                if (i == 0) {
                    sql += " a.ngay_lap <= '" + ngay_kt + "'";
                } else {
                    sql += " AND a.ngay_lap <= '" + ngay_kt + "'";
                }
            }
            stmt = con.createStatement();
            ResultSet rs2 = stmt.executeQuery(sql);
            while (rs2.next()) {
                ten_km = "";
                tien_dv = 0;
                phan_tram = 0;

                ma_hd = rs2.getInt(1);
                ma_km = rs2.getInt(2);
                ngay_lap = rs2.getString(3);
                
                giohat = rs2.getString(4);
                gio = giohat.substring(0, 2);
                phut = giohat.substring(3, 5);
                _gio = Double.parseDouble(gio);
                _phut = (Double.parseDouble(phut)) / 60;
                gio_hat = _gio + _phut;
                
                ma_phong = rs2.getString(5);
                ma_kh = rs2.getInt(6);
                tien_hat = gio_hat * getGia_phong(ma_phong);
                String[][] km;
                km = getTen_km(ma_hd);
                for (int j = 0; j < km.length; j++) {
                    if (km[j][0] != null) {
                        if (j == 0) {
                            ten_km += km[j][0];
                        } else {
                            ten_km += ", " + km[j][0];
                        }
                    }
                }
                if (!ten_km.equals("")) {
                    for (int j = 0; j < km.length; j++) {
                        if (j == 0) {
                            if (!km[j][2].equals("Sản phẩm")) {
                                phan_tram += Integer.parseInt(km[j][1]);
                            }
                        }
                    }
                } else {
                    phan_tram = 0;
                }
                tien_dv = getTien_DV(ma_hd);

                t_tien = tien_hat + tien_dv;
                tien_km = phan_tram * (t_tien / 100);
                tong_tien = t_tien - tien_km;
                tong_ds += tong_tien;

                row = new Vector();
                row.add(ma_phong);
                row.add(getTen_kh(ma_kh));
                row.add(ngay_lap);
                row.add(giohat + "h");
                tienhat = new BigDecimal(tien_hat);
                row.add(en.format(tienhat));
                tiendv = new BigDecimal(tien_dv);
                row.add(en.format(tiendv));
                row.add(ten_km);
                tongtien = new BigDecimal(tong_tien);
                row.add(en.format(tongtien));

                data.add(row);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        BigDecimal tongds = new BigDecimal(tong_ds);
        HD_Model.setDataVector(data, header);
        tblHoa_don.setModel(HD_Model);
        txtTong_ds.setText(en.format(tongds) + " vnđ");
        txtBinh_quan.setText(dcf.format(calGio_hat(HD_Model)) + "h");
        return true;
    }

    private boolean exportExcel(DefaultTableModel model) {
        String ma_phong, ten_kh, ngay_lap, gio_hat, tien_hat, tien_dv, ten_km, tong_tien;
        Vector data1 = new Vector();
        DefaultTableModel hd_model = new DefaultTableModel();
        Vector row1;

        //Tạo model với các cột dữ liệu là String
        for (int i = 0; i < model.getRowCount(); i++) {
            ma_phong = model.getValueAt(i, 0).toString();
            ten_kh = model.getValueAt(i, 1).toString();
            ngay_lap = model.getValueAt(i, 2).toString();
            gio_hat = model.getValueAt(i, 3).toString();
            tien_hat = model.getValueAt(i, 4).toString();
            tien_dv = model.getValueAt(i, 5).toString();
            ten_km = model.getValueAt(i, 6).toString();
            tong_tien = model.getValueAt(i, 7).toString();

            row1 = new Vector();
            row1.add(ma_phong);
            row1.add(ten_kh);
            row1.add(ngay_lap);
            row1.add(gio_hat);
            row1.add(tien_hat);
            row1.add(tien_dv);
            row1.add(ten_km);
            row1.add(tong_tien);

            data1.add(row1);
        }
        hd_model.setDataVector(data1, header);

        try {
            //Exporting to Excel		   
            Workbook wb = new HSSFWorkbook();
            CreationHelper createhelper = wb.getCreationHelper();
            Sheet sheet = (Sheet) wb.createSheet("Hóa Đơn");
            Row row = null;
            Cell cell = null;
            int count = hd_model.getRowCount();
            int count1 = hd_model.getColumnCount();
            for (int i = 0; i <= count; i++) {
                row = sheet.createRow(i);
                if (i == 0) {
                    HSSFFont font = (HSSFFont) wb.createFont();
                    font.setBold(true);
                    HSSFCellStyle style = (HSSFCellStyle) wb.createCellStyle();
                    style.setFont(font);

                    cell = (Cell) row.createCell(0);
                    cell.setCellValue("Mã phòng");
                    cell.setCellStyle(style);
                    cell = (Cell) row.createCell(1);
                    cell.setCellValue("Tên KH");
                    cell.setCellStyle(style);
                    cell = (Cell) row.createCell(2);
                    cell.setCellValue("Ngày lập");
                    cell.setCellStyle(style);
                    cell = (Cell) row.createCell(3);
                    cell.setCellValue("Giờ hát");
                    cell.setCellStyle(style);
                    cell = (Cell) row.createCell(4);
                    cell.setCellValue("Tiền hát");
                    cell.setCellStyle(style);
                    cell = (Cell) row.createCell(5);
                    cell.setCellValue("Tiền dịch vụ");
                    cell.setCellStyle(style);
                    cell = (Cell) row.createCell(6);
                    cell.setCellValue("Tên Khuyến mãi");
                    cell.setCellStyle(style);
                    cell = (Cell) row.createCell(7);
                    cell.setCellValue("Tổng tiền");
                    cell.setCellStyle(style);
                } else {
                    for (int j = 0; j < count1; j++) {
                        cell = (Cell) row.createCell(j);
                        cell.setCellValue((String) hd_model.getValueAt(i - 1, j));
                    }
                }
            }

            File f = new File("D:\\Hóa Đơn.xls");
            FileOutputStream out = new FileOutputStream(f);
            wb.write(out);
            out.flush();
            out.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(frmHoaDon.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (IOException ex) {
            Logger.getLogger(frmHoaDon.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        buttonGroup3 = new javax.swing.ButtonGroup();
        jCheckBoxMenuItem1 = new javax.swing.JCheckBoxMenuItem();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblHoa_don = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        lblNgay_bd = new javax.swing.JLabel();
        jdcNgay_bd = new com.toedter.calendar.JDateChooser();
        jPanel4 = new javax.swing.JPanel();
        lblTong_ds = new javax.swing.JLabel();
        txtTong_ds = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        txtBinh_quan = new javax.swing.JTextField();
        lblNgay_kt = new javax.swing.JLabel();
        jdcNgay_kt = new com.toedter.calendar.JDateChooser();
        jPanel5 = new javax.swing.JPanel();
        btnExecute = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        btnExcel = new javax.swing.JButton();
        btnThoat = new javax.swing.JButton();

        jCheckBoxMenuItem1.setSelected(true);
        jCheckBoxMenuItem1.setText("jCheckBoxMenuItem1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Hóa Đơn - Thống Kê");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Hóa Đơn - Thống Kê", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 24), new java.awt.Color(0, 0, 255))); // NOI18N

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Danh sách Hóa đơn", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14), new java.awt.Color(0, 0, 255))); // NOI18N

        jScrollPane1.setViewportView(tblHoa_don);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 807, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 361, Short.MAX_VALUE)
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Thông tin lọc", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14), new java.awt.Color(0, 0, 255))); // NOI18N

        lblNgay_bd.setText("Từ Ngày:");

        jdcNgay_bd.setDateFormatString("yyyy-MM-dd");

        lblTong_ds.setText("Tổng doanh số :");

        jLabel1.setText("Bình quân giờ hát (/phòng):");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(lblTong_ds))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtBinh_quan, javax.swing.GroupLayout.DEFAULT_SIZE, 166, Short.MAX_VALUE)
                    .addComponent(txtTong_ds))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtBinh_quan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTong_ds, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTong_ds))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        lblNgay_kt.setText("Đến ngày:");

        jdcNgay_kt.setDateFormatString("yyyy-MM-dd");

        btnExecute.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnExecute.setForeground(new java.awt.Color(102, 0, 0));
        btnExecute.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/submit.png"))); // NOI18N
        btnExecute.setText("Lọc");
        btnExecute.setMaximumSize(new java.awt.Dimension(91, 41));
        btnExecute.setMinimumSize(new java.awt.Dimension(91, 41));
        btnExecute.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExecuteActionPerformed(evt);
            }
        });

        btnCancel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnCancel.setForeground(new java.awt.Color(102, 0, 0));
        btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/cancel.png"))); // NOI18N
        btnCancel.setText("Hủy");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        btnExcel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnExcel.setForeground(new java.awt.Color(102, 0, 0));
        btnExcel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/excel.png"))); // NOI18N
        btnExcel.setText("Excel");
        btnExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnExecute, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(39, 39, 39)
                .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 24, Short.MAX_VALUE)
                .addComponent(btnExcel, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnExecute, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnExcel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(13, 13, 13)
                        .addComponent(lblNgay_bd)
                        .addGap(40, 40, 40)
                        .addComponent(jdcNgay_bd, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(42, 42, 42)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(lblNgay_kt)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jdcNgay_kt, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(22, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblNgay_bd)
                    .addComponent(jdcNgay_bd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblNgay_kt)
                    .addComponent(jdcNgay_kt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(19, 19, 19))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

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
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 845, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnThoat, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnThoat, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnExecuteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExecuteActionPerformed
        String ngay_bd = "";
        String ngay_kt = "";
        cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        yesterday = cal.getTime();

        if (jdcNgay_bd.getDate() != null) {
            date1 = jdcNgay_bd.getDate();
            ngay_bd = df.format(date1);
        }

        if (jdcNgay_kt.getDate() != null) {
            if (!jdcNgay_kt.getDate().after(jdcNgay_bd.getDate())) {
                JOptionPane.showMessageDialog(this, "Ngày kết thúc phải sau Ngày bắt đầu!");
            } else {
                date2 = jdcNgay_kt.getDate();
                ngay_kt = df.format(date2);
            }
        }

        if (filter(ngay_bd, ngay_kt)) {
            JOptionPane.showMessageDialog(this, "Lọc thành công!");
        }
    }//GEN-LAST:event_btnExecuteActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        loadData();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcelActionPerformed
        DefaultTableModel model = (DefaultTableModel) tblHoa_don.getModel();
        if (!exportExcel(model)) {
            JOptionPane.showMessageDialog(this, "Có lỗi khi ghi file!");
        } else {
            JOptionPane.showMessageDialog(this, "Ghi file thành công!");
        }
    }//GEN-LAST:event_btnExcelActionPerformed

    private void btnThoatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThoatActionPerformed
        dispose();
        MenuChinh menuchinh = new MenuChinh(tenDN, hoTen, quyen, matkhau);
        menuchinh.setVisible(true);
    }//GEN-LAST:event_btnThoatActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new frmHoaDon().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnExcel;
    private javax.swing.JButton btnExecute;
    private javax.swing.JButton btnThoat;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private com.toedter.calendar.JDateChooser jdcNgay_bd;
    private com.toedter.calendar.JDateChooser jdcNgay_kt;
    private javax.swing.JLabel lblNgay_bd;
    private javax.swing.JLabel lblNgay_kt;
    private javax.swing.JLabel lblTong_ds;
    private javax.swing.JTable tblHoa_don;
    private javax.swing.JTextField txtBinh_quan;
    private javax.swing.JTextField txtTong_ds;
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
