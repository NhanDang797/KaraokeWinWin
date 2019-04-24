/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import dao.ConnectDB;
import dao.ConnectDB;
import dao.DangNhap;
import io.FileFactory;
import java.io.File;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.util.Date;
import java.util.Vector;
import java.util.regex.Pattern;

/**
 *
 * @author Kan
 */
public class frmQL_KM extends javax.swing.JFrame {

    Connection con = ConnectDB.getConnect();
    PreparedStatement pstm;
    Statement stmt;
    String url, sql, ten_km, ngay_bd, ngay_kt, p, loai_km;
    ResultSet rs;
    Date date1, date2, today, tomorrow, d_after_tomorrow;
    SimpleDateFormat df;
    DefaultTableModel KM_Model;
    Vector header, data, row;
    int ma_km, dong, pt_km;
    Calendar cal;
    Boolean flag;
    ArrayList array;

    public frmQL_KM() {
        initComponents();
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        HienThiUser();
        data = new Vector();
        header = new Vector();
        header.add("Mã KM");
        header.add("Tên KM");
        header.add("Loại KM");
        header.add("Phần trăm KM");
        header.add("Ngày bắt đầu");
        header.add("Ngày kết thúc");
        KM_Model = new DefaultTableModel();

        df = new SimpleDateFormat("yyyy-MM-dd");
        loadData();
        p = "";
        today = new Date();
    }

    private void enableField() {
        txtTen_KM.grabFocus();
        txtTen_KM.setEditable(true);
        txtPt_KM.setEditable(true);

        jdcNgay_bd.setEnabled(true);
        jdcNgay_kt.setEnabled(true);
        cboLoai_km.setSelectedItem("Quán");

        //btnCancel.setEnabled(true);
    }

    private void clearField() {
        cal = Calendar.getInstance();
        cal.add(Calendar.DATE, +1);
        tomorrow = cal.getTime();
        Calendar cal1 = Calendar.getInstance();
        cal1.add(Calendar.DATE, +2);
        d_after_tomorrow = cal1.getTime();

        txtTen_KM.setText("");
        cboLoai_km.setSelectedItem("Quán");
        txtPt_KM.setText("");
        jdcNgay_bd.setDate(tomorrow);
        jdcNgay_kt.setDate(d_after_tomorrow);

        btnInsert.setEnabled(true);
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
        //btnCancel.setEnabled(false);
    }

    private void bindingData() {
        dong = tblKM.getSelectedRow();

        ten_km = KM_Model.getValueAt(dong, 1).toString();
        loai_km = KM_Model.getValueAt(dong, 2).toString();
        pt_km = (Integer) KM_Model.getValueAt(dong, 3);
        ngay_bd = KM_Model.getValueAt(dong, 4).toString();
        ngay_kt = KM_Model.getValueAt(dong, 5).toString();

        try {
            txtTen_KM.setText(ten_km);
            cboLoai_km.setSelectedItem(loai_km);
            txtPt_KM.setText(Integer.toString(pt_km));
            jdcNgay_bd.setDate(df.parse(ngay_bd));
            jdcNgay_kt.setDate(df.parse(ngay_kt));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateTinh_trang(int ma_km) {
        try {
            sql = "UPDATE CTKM SET tinh_trang = 1 WHERE ma_KM = ?";
            pstm = con.prepareStatement(sql);
            pstm.setInt(1, ma_km);
            pstm.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadData() {
        clearField();
        while (KM_Model.getRowCount() > 0) {
            KM_Model.removeRow(0);
        }
        try {
            sql = "SELECT * FROM CTKM WHERE tinh_trang = 0";
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                ngay_bd = rs.getString("ngay_bd").split("\\s", 0)[0];
                ngay_kt = rs.getString("ngay_kt").split("\\s", 0)[0];

                Date today = new Date();
                row = new Vector();
                row.add(rs.getInt("ma_KM"));
                row.add(rs.getString("ten_KM"));
                row.add(rs.getString("loai_KM"));
                row.add(rs.getInt("phan_tram"));
                row.add(ngay_bd);
                row.add(ngay_kt);
                if (!df.parse(ngay_kt).after(today)) {
                    updateTinh_trang(rs.getInt("ma_km"));
                } else {
                    data.add(row);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Có lỗi trong quá trình thực hiện. Vui lòng kiểm tra lại thông tin đã nhập!");
            e.printStackTrace();
        }

        KM_Model.setDataVector(data, header);
        tblKM.setModel(KM_Model);
    }

    private boolean validData() {
        flag = false;

        if (txtTen_KM.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Tên khuyến mãi!");
            txtTen_KM.setText("");
            txtTen_KM.grabFocus();
        } else if (txtPt_KM.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Phần trăm khuyến mãi!");
            txtPt_KM.setText("");
            txtPt_KM.grabFocus();
        } else if (!txtPt_KM.getText().matches("\\d{1,3}") || Integer.parseInt(txtPt_KM.getText()) < 1 || Integer.parseInt(txtPt_KM.getText()) > 100) {
            JOptionPane.showMessageDialog(this, "Phần trăm khuyến mãi là kiểu số nguyên từ 1 đến 100! Vui lòng nhập lại!");
            txtPt_KM.setText("");
            txtPt_KM.grabFocus();
        } else if (jdcNgay_bd.getDate() == null) {
            System.out.println(jdcNgay_bd.getDate());
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Ngày bắt đầu!");
            jdcNgay_bd.grabFocus();
        } else if (!jdcNgay_bd.getDate().after(today)) {
            JOptionPane.showMessageDialog(this, "Ngày bắt đầu phải từ ngày mai!");
        } else if (jdcNgay_kt.getDate() == null) {
            System.out.println(jdcNgay_kt.getDate());
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Ngày kết thúc!");
            jdcNgay_kt.grabFocus();
        } else if (!jdcNgay_kt.getDate().after(jdcNgay_bd.getDate())) {
            JOptionPane.showMessageDialog(this, "Ngày kết thúc phải sau Ngày bắt đầu!");
        } else {
            flag = true;
        }

        return flag;
    }

    private boolean insert(String ten_KM, String loai_KM, int phan_tram, String ngay_bd, String ngay_kt) {
        try {
            sql = "INSERT INTO CTKM (ten_KM, loai_KM, phan_tram, ngay_bd, ngay_kt, tinh_trang) VALUES (?, ?, ?, ?, ?, 0)";
            pstm = con.prepareStatement(sql);
            pstm.setString(1, ten_KM);
            pstm.setString(2, loai_KM);
            pstm.setInt(3, phan_tram);
            pstm.setString(4, ngay_bd);
            pstm.setString(5, ngay_kt);
            pstm.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean update(int ma_KM, String ten_KM, String loai_KM, int phan_tram, String ngay_bd, String ngay_kt) {
        //DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        try {
            sql = "UPDATE CTKM SET ten_KM = ?, loai_KM = ?, phan_tram = ?, ngay_bd = ?, ngay_kt = ? WHERE ma_KM = ?";
            pstm = con.prepareStatement(sql);
            pstm.setString(1, ten_KM);
            pstm.setString(2, loai_KM);
            pstm.setInt(3, phan_tram);
            pstm.setString(4, ngay_bd);
            pstm.setString(5, ngay_kt);
            pstm.setInt(6, ma_KM);
            pstm.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean delete(int ma_KM) {
        try {
            sql = "UPDATE CTKM SET tinh_trang = 1 WHERE ma_KM = ?";
            pstm = con.prepareStatement(sql);
            pstm.setInt(1, ma_KM);
            pstm.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean search(String ten_km, String loai_KM, String pt_km, String ngay_bd, String ngay_kt) {
        while (KM_Model.getRowCount() > 0) {
            KM_Model.removeRow(0);
        }
        try {
            int i = 0;
            sql = "SELECT * FROM CTKM WHERE tinh_trang = 0 ";

            if (!ten_km.equals("")) {
                sql += " AND ten_km LIKE N'%" + ten_km + "%'";
                i++;
            }
            if (!loai_KM.equals("")) {
                if (i == 0) {
                    sql += " loai_KM = " + loai_KM;
                } else {
                    sql += " AND loai_KM = " + loai_KM;
                }
                i++;
            }
            if (!pt_km.equals("")) {
                if (i == 0) {
                    sql += " phan_tram = " + pt_km;
                } else {
                    sql += " AND phan_tram = " + pt_km;
                }
                i++;
            }
            if (!ngay_bd.equals("")) {
                if (i == 0) {
                    sql += " ngay_bd >= '" + ngay_bd + "'";
                } else {
                    sql += " AND ngay_bd >= '" + ngay_bd + "'";
                }
                i++;
            }
            if (!ngay_kt.equals("")) {
                if (i == 0) {
                    sql += " ngay_kt <= '" + ngay_kt + "'";
                } else {
                    sql += " AND ngay_kt <= '" + ngay_kt + "'";
                }
            }

            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String ngaybd = rs.getString("ngay_bd").split("\\s", 0)[0];
                String ngaykt = rs.getString("ngay_kt").split("\\s", 0)[0];

                row = new Vector();
                row.add(rs.getInt("ma_KM"));
                row.add(rs.getString("ten_KM"));
                row.add(rs.getString("loai_KM"));
                row.add(rs.getInt("phan_tram"));
                row.add(ngaybd);
                row.add(ngaykt);
                data.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        KM_Model.setDataVector(data, header);
        tblKM.setModel(KM_Model);
        //btnCancel.setEnabled(false);
        return true;
    }

    private boolean checkTen_km(String ten_km) {
        int n = 0;
        array = new ArrayList();
        String tenkm = "";

        try {
            sql = "SELECT ten_KM FROM CTKM WHERE tinh_trang = 0";
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                tenkm = rs.getString("ten_KM");
                array.add(tenkm);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        for (int i = 0; i < data.size(); i++) {
            if (ten_km.equals(array.get(i).toString())) {
                JOptionPane.showMessageDialog(this, "Tên khuyến mãi đã có! Vui lòng chọn tên khác!");
                return false;
            }
        }
        return true;
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
        lblNgay_bd = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtTen_KM = new javax.swing.JTextField();
        btnInsert = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnSearch = new javax.swing.JButton();
        btnExit = new javax.swing.JButton();
        lblPt_km = new javax.swing.JLabel();
        txtPt_KM = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        lblNgay_kt = new javax.swing.JLabel();
        jdcNgay_bd = new com.toedter.calendar.JDateChooser();
        jdcNgay_kt = new com.toedter.calendar.JDateChooser();
        btnCancel = new javax.swing.JButton();
        cboLoai_km = new javax.swing.JComboBox<>();
        lblLoai_km = new javax.swing.JLabel();
        panelDanhsach = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblKM = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Quản lý Khuyến Mãi");
        setName("frmQLKho"); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Quản lý Khuyến mãi", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 24), new java.awt.Color(0, 0, 255))); // NOI18N

        lblNgay_bd.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Chi tiết", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14), new java.awt.Color(0, 0, 255))); // NOI18N

        jLabel1.setText("Tên Khuyến mãi:");

        btnInsert.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnInsert.setForeground(new java.awt.Color(102, 0, 0));
        btnInsert.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/addNew.png"))); // NOI18N
        btnInsert.setText("Thêm");
        btnInsert.setPreferredSize(new java.awt.Dimension(125, 45));
        btnInsert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInsertActionPerformed(evt);
            }
        });

        btnUpdate.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnUpdate.setForeground(new java.awt.Color(102, 0, 0));
        btnUpdate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/update.png"))); // NOI18N
        btnUpdate.setText("Sửa");
        btnUpdate.setPreferredSize(new java.awt.Dimension(125, 45));
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        btnDelete.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnDelete.setForeground(new java.awt.Color(102, 0, 0));
        btnDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/delete.png"))); // NOI18N
        btnDelete.setText("Xóa");
        btnDelete.setPreferredSize(new java.awt.Dimension(125, 45));
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        btnSearch.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnSearch.setForeground(new java.awt.Color(102, 0, 0));
        btnSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/search.png"))); // NOI18N
        btnSearch.setText("Tìm kiếm");
        btnSearch.setPreferredSize(new java.awt.Dimension(125, 45));
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        btnExit.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnExit.setForeground(new java.awt.Color(102, 0, 0));
        btnExit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/thoat.png"))); // NOI18N
        btnExit.setPreferredSize(new java.awt.Dimension(125, 45));
        btnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExitActionPerformed(evt);
            }
        });

        lblPt_km.setText("Phần trăm KM:");

        jLabel2.setText("Ngày bắt đầu:");

        lblNgay_kt.setText("Ngày kết thúc:");

        jdcNgay_bd.setDateFormatString("yyyy-MM-dd");

        jdcNgay_kt.setDateFormatString("yyyy-MM-dd");

        btnCancel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnCancel.setForeground(new java.awt.Color(102, 0, 0));
        btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/cancel.png"))); // NOI18N
        btnCancel.setText("Hủy");
        btnCancel.setPreferredSize(new java.awt.Dimension(125, 45));
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        cboLoai_km.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Quán", "Sản phẩm" }));

        lblLoai_km.setText("Loại KM:");

        javax.swing.GroupLayout lblNgay_bdLayout = new javax.swing.GroupLayout(lblNgay_bd);
        lblNgay_bd.setLayout(lblNgay_bdLayout);
        lblNgay_bdLayout.setHorizontalGroup(
            lblNgay_bdLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lblNgay_bdLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(lblNgay_bdLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(lblNgay_bdLayout.createSequentialGroup()
                        .addComponent(btnInsert, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(lblNgay_bdLayout.createSequentialGroup()
                        .addGroup(lblNgay_bdLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(lblLoai_km))
                        .addGap(18, 18, 18)
                        .addGroup(lblNgay_bdLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cboLoai_km, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(lblNgay_bdLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(txtTen_KM)
                                .addComponent(jdcNgay_bd, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)))))
                .addGroup(lblNgay_bdLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(lblNgay_bdLayout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addGroup(lblNgay_bdLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblPt_km, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblNgay_kt, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(lblNgay_bdLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtPt_KM)
                            .addComponent(jdcNgay_kt, javax.swing.GroupLayout.DEFAULT_SIZE, 282, Short.MAX_VALUE))
                        .addGap(45, 45, 45))
                    .addGroup(lblNgay_bdLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnExit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 43, Short.MAX_VALUE))))
        );
        lblNgay_bdLayout.setVerticalGroup(
            lblNgay_bdLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lblNgay_bdLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(lblNgay_bdLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtTen_KM, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPt_km)
                    .addComponent(txtPt_KM, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(lblNgay_bdLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(lblNgay_bdLayout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addComponent(jLabel2))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, lblNgay_bdLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(lblNgay_bdLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jdcNgay_bd, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblNgay_kt, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jdcNgay_kt, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(18, 18, 18)
                .addGroup(lblNgay_bdLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboLoai_km, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblLoai_km))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
                .addGroup(lblNgay_bdLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnInsert, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnExit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        panelDanhsach.setBorder(javax.swing.BorderFactory.createTitledBorder(null, " Danh sách CTKM", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14), new java.awt.Color(0, 0, 255))); // NOI18N

        tblKM.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblKMMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblKM);

        javax.swing.GroupLayout panelDanhsachLayout = new javax.swing.GroupLayout(panelDanhsach);
        panelDanhsach.setLayout(panelDanhsachLayout);
        panelDanhsachLayout.setHorizontalGroup(
            panelDanhsachLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDanhsachLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addGap(24, 24, 24))
        );
        panelDanhsachLayout.setVerticalGroup(
            panelDanhsachLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDanhsachLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelDanhsach, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblNgay_bd, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblNgay_bd, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panelDanhsach, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed
        dispose();
        MenuChinh menuchinh = new MenuChinh(tenDN, hoTen, quyen, matkhau);
        menuchinh.setVisible(true);
    }//GEN-LAST:event_btnExitActionPerformed

    private void btnInsertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInsertActionPerformed

        ten_km = "";
        loai_km = "";
        pt_km = 0;
        ngay_bd = "";
        ngay_kt = "";
        if (validData()) {
            date1 = jdcNgay_bd.getDate();
            date2 = jdcNgay_kt.getDate();

            ten_km = txtTen_KM.getText();
            loai_km = cboLoai_km.getSelectedItem().toString();
            pt_km = Integer.parseInt(txtPt_KM.getText());
            ngay_bd = df.format(date1);
            ngay_kt = df.format(date2);
            if (checkTen_km(ten_km)) {
                if (insert(ten_km, loai_km, pt_km, ngay_bd, ngay_kt)) {
                    JOptionPane.showMessageDialog(this, "Thêm thành công!");
                    loadData();
                }
            }
        }
    }//GEN-LAST:event_btnInsertActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        dong = tblKM.getSelectedRow();
        ma_km = Integer.parseInt(KM_Model.getValueAt(dong, 0).toString());

        if (JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa?", "Xóa?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
            KM_Model.removeRow(dong);
            if (delete(ma_km)) {
                JOptionPane.showMessageDialog(this, "Xóa thành công!");
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Có lỗi xảy ra!");
            }
        }
        loadData();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void tblKMMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblKMMouseClicked
        btnInsert.setEnabled(false);
        btnUpdate.setEnabled(true);
        btnDelete.setEnabled(true);
        enableField();
        bindingData();
    }//GEN-LAST:event_tblKMMouseClicked

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        ten_km = "";
        loai_km = "";
        pt_km = 0;
        ngay_bd = "";
        ngay_kt = "";
        if (validData()) {
            dong = tblKM.getSelectedRow();
            String ngaybd = "";
            String ngaykt = "";

            ma_km = Integer.parseInt(KM_Model.getValueAt(dong, 0).toString());
            String tenkm = KM_Model.getValueAt(dong, 1).toString();
            String loaikm = KM_Model.getValueAt(dong, 2).toString();
            String phantram = KM_Model.getValueAt(dong, 3).toString();
            try {
                ngaybd = KM_Model.getValueAt(dong, 4).toString().split("\\s", 0)[0];
                ngaykt = KM_Model.getValueAt(dong, 5).toString().split("\\s", 0)[0];
            } catch (Exception e) {
                e.printStackTrace();
            }

            date1 = jdcNgay_bd.getDate();
            date2 = jdcNgay_kt.getDate();

            ten_km = txtTen_KM.getText();
            loai_km = cboLoai_km.getSelectedItem().toString();
            pt_km = Integer.parseInt(txtPt_KM.getText());
            ngay_bd = df.format(date1);
            ngay_kt = df.format(date2);

            if (checkTen_km(ten_km)) {
                if (ten_km.equals(tenkm)
                        && loai_km.equals(loaikm)
                        && Integer.toString(pt_km).equals(phantram)
                        && ngay_bd.equals(ngaybd)
                        && ngay_kt.equals(ngaykt)) {
                    JOptionPane.showMessageDialog(this, "Không có thông tin mới để sửa! Vui lòng nhập lại nhập vào!");
                } else {
                    if (update(ma_km, ten_km, loai_km, pt_km, ngay_bd, ngay_kt)) {
                        JOptionPane.showMessageDialog(this, "Sửa thành công!");
                        loadData();
                    }
                }
            }
        }
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        loadData();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        //btnCancel.setEnabled(true);
        ten_km = "";
        loai_km = "";
        String phan_tram = "";
        ngay_bd = "";
        ngay_kt = "";

        if (!txtTen_KM.getText().equals("")) {
            if (!txtTen_KM.getText().matches(p)) {
                JOptionPane.showMessageDialog(this, "Tên khuyến mãi chứa ký tự không đúng! Vui lòng nhập lại!");
                txtTen_KM.setText("");
                txtTen_KM.grabFocus();
            } else {
                ten_km = txtTen_KM.getText();
            }
        }

        if (!txtPt_KM.getText().equals("")) {
            if (!txtPt_KM.getText().matches("\\d{1,3}") || Integer.parseInt(txtPt_KM.getText()) < 1 || Integer.parseInt(txtPt_KM.getText()) > 100) {
                JOptionPane.showMessageDialog(this, "Phần trăm khuyến mãi là kiểu số nguyên từ 1 đến 100! Vui lòng nhập lại!");
                txtPt_KM.setText("");
                txtPt_KM.grabFocus();
            } else {
                phan_tram = txtPt_KM.getText();
            }
        }

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

        if (!ten_km.equals("") || !phan_tram.equals("") || !ngay_bd.equals("") || !ngay_kt.equals("")) {
            if (search(ten_km, loai_km, phan_tram, ngay_bd, ngay_kt)) {
                JOptionPane.showMessageDialog(this, "Tìm kiếm thành công");
            }
        }
    }//GEN-LAST:event_btnSearchActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(frmQL_KM.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(frmQL_KM.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(frmQL_KM.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(frmQL_KM.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new frmQL_KM().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnExit;
    private javax.swing.JButton btnInsert;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JComboBox<String> cboLoai_km;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private com.toedter.calendar.JDateChooser jdcNgay_bd;
    private com.toedter.calendar.JDateChooser jdcNgay_kt;
    private javax.swing.JLabel lblLoai_km;
    private javax.swing.JPanel lblNgay_bd;
    private javax.swing.JLabel lblNgay_kt;
    private javax.swing.JLabel lblPt_km;
    private javax.swing.JPanel panelDanhsach;
    private javax.swing.JTable tblKM;
    private javax.swing.JTextField txtPt_KM;
    private javax.swing.JTextField txtTen_KM;
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
