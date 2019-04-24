/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import gui.Datphong;
import gui.frmHoaDon;
import dao.ConnectDB;
import dao.DMPhong;
import dao.DMPhongService;
import dao.DSDichVu;
import dao.DSDichVuService;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author nhandang
 */
public class MenuChinh extends javax.swing.JFrame {

    // PopupMenu
    JPopupMenu popupDSPhong;
    JMenuItem datPhong, datDichVu, doiPhong, donPhong;

    //table Danh Sách Phòng Hát;
    DefaultTableModel dtmDMPhong;
    ArrayList<DMPhong> dsPhong;
    DMPhongService phongService;
    DMPhong phongSlected;

    int row;
    SimpleDateFormat df;
    // danh sách dịch vụ
    DefaultTableModel dtmDSDivhVu;
    DefaultListModel listKhuyenMai;
    ArrayList<DSDichVu> danhSachDichVu;
    DSDichVuService dsDichVuService;
    Locale localeEN;
    NumberFormat en;

    public MenuChinh(String tenDangNhap, String hoten, String quyen, String matkhau) {
        initComponents();
        localeEN = new Locale("en", "EN");
        en = NumberFormat.getInstance(localeEN);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        btnThanhToan.setEnabled(false);
        df = new SimpleDateFormat("yyyy-MM-dd");
        /*
            Đã Xong  quản lý tài khoản.
         */
        // xử  ly quyền đăng nhập
        if (quyen.equals("Nhân Viên")) {
            mnuitemPhongHat.setEnabled(false);
            mnuitemKhachHang.setEnabled(false);
            mnuitemCTKM.setEnabled(false);
            mnuitemTaiKhoan.setEnabled(false);
            btnTaiKhoan.setEnabled(false);
            btnKhachHang.setEnabled(false);

        }
// goi form doi mat khau
        mnuitemDoiMatKhau.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DoiMatKhau doimatkhau = new DoiMatKhau(null, true, tenDangNhap, hoten, matkhau);
                doimatkhau.setVisible(true);
            }
        });
        btnDoiMatKhau.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DoiMatKhau doimatkhau = new DoiMatKhau(null, true, tenDangNhap, hoten, matkhau);
                doimatkhau.setVisible(true);
            }
        });
// goi form tai khoan
        mnuitemTaiKhoan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                QuanLyTaiKhoan obj = new QuanLyTaiKhoan(null, true, tenDangNhap, quyen);
                obj.setVisible(true);
            }
        });
        btnTaiKhoan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                QuanLyTaiKhoan obj = new QuanLyTaiKhoan(null, true, tenDangNhap, quyen);
                obj.setVisible(true);
            }
        });

        //  giovao = mat.parse("00:00");
//------------------------------------------------------------------------------------
        hienThiDMPhong();
        hienthiMa_DDP_HD("");
        hienthiKhuyenMai();

    }

    public void hienThiDMPhong() {
        phongService = new DMPhongService();
        dsPhong = phongService.hienThiDMPhong();
        dtmDMPhong = new DefaultTableModel();
        dtmDMPhong.addColumn("STT");
        dtmDMPhong.addColumn("Tên Phòng");
        dtmDMPhong.addColumn("Loại Phòng");
        dtmDMPhong.addColumn("Giá Phòng");
        dtmDMPhong.addColumn("Trạng Thái");
        int stt = 0;
        for (DMPhong dMPhong : dsPhong) {
            stt++;
            Vector<Object> vecDMPhong = new Vector<>();
            vecDMPhong.add(stt);
            vecDMPhong.add(dMPhong.getMaPhong());
            vecDMPhong.add(dMPhong.getLoaiPhong());
            vecDMPhong.add(en.format(Float.parseFloat(dMPhong.getGiaPhong())) + "    vnd");
            vecDMPhong.add(dMPhong.getTinhTrang());

            dtmDMPhong.addRow(vecDMPhong);
        }

        tableDangSachPhong.setModel(dtmDMPhong);

    }

    // hiển thị Đơn Đặt phòng theo mã phòng được truyền trong event Table DMPhong
    public void hienthiKhuyenMai() {
        listKhuyenMai = new DefaultListModel();
        Date today = new Date();
        String ngay_bd = "", ngay_kt = "";
        try {
            Connection con = ConnectDB.getConnect();
            String sql = "select ma_KM , ten_KM , phan_tram, ngay_bd, ngay_kt from CTKM where tinh_trang = 0";
            PreparedStatement pstm = con.prepareStatement(sql);
            ResultSet rs = pstm.executeQuery();
            while (rs.next()) {
                ngay_bd = rs.getString(4);
                ngay_kt = rs.getString(5);
                if (!df.parse(ngay_bd).after(today) && df.parse(ngay_kt).after(today)) {
                    listKhuyenMai.addElement(rs.getString(2) + "  ---  " + rs.getString(3) + "  %");
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        listKM.setModel(listKhuyenMai);
    }

    // hàm popupmenu.
    public void setPopupTable(String maPhong) {
        popupDSPhong = new JPopupMenu();
        datPhong = new JMenuItem("  Đặt Phòng", new ImageIcon(getClass().getResource("/image/datphong.png")));
        datDichVu = new JMenuItem("  Cập Nhật Dịch Vụ", new ImageIcon(getClass().getResource("/image/dichvu.png")));
        doiPhong = new JMenuItem("  Đổi Phòng", new ImageIcon(getClass().getResource("/image/doiphong.png")));
        donPhong = new JMenuItem("  Dọn Phòng");

        popupDSPhong.add(datPhong);
        popupDSPhong.addSeparator();
        popupDSPhong.add(datDichVu);

        tableDangSachPhong.setComponentPopupMenu(popupDSPhong);
        //______________________________________________
        //--Kiểm tra tình trạng phòng xử lý các popup
        if (phongSlected.getTinhTrang().equals("Đã đặt")) {
            datPhong.setEnabled(false);
            donPhong.setEnabled(false);
            btnThanhToan.setEnabled(true);
        }
        if (phongSlected.getTinhTrang().equals("Sửa chửa") || phongSlected.getTinhTrang().equals("Dọn dẹp")) {
            datPhong.setEnabled(false);
            datDichVu.setEnabled(false);
            donPhong.setEnabled(false);
            doiPhong.setEnabled(false);
            btnThanhToan.setEnabled(false);
        }
        if (phongSlected.getTinhTrang().equals("Trống")) {
            datDichVu.setEnabled(false);
            doiPhong.setEnabled(false);
            donPhong.setEnabled(false);
            btnThanhToan.setEnabled(false);
        }
        //______________________________________________

        // đặt phòng
        datPhong.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                Datphong datphong = new Datphong(null, true, maPhong);
                datphong.setVisible(true);
            }
        });
        //cập nhật dịch vụ 
        datDichVu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                DatDV datdv = new DatDV(null, true, maHD, maPhong);
                datdv.setVisible(true);
            }
        });
        // đổi phòng
        doiPhong.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tenPhong = new javax.swing.JLabel();
        gioVao = new javax.swing.JLabel();
        tongHop = new javax.swing.JLabel();
        btnThanhToan = new javax.swing.JButton();
        pnPhongHat = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tableDangSachPhong = new javax.swing.JTable();
        pnDichVu = new javax.swing.JPanel();
        tableDanhSachDichVu = new javax.swing.JScrollPane();
        tableDichVu = new javax.swing.JTable();
        lblTenPhong = new javax.swing.JLabel();
        lblGioVao = new javax.swing.JLabel();
        jToolBar = new javax.swing.JToolBar();
        btnTaiKhoan = new javax.swing.JButton();
        btnKhachHang = new javax.swing.JButton();
        btnThongKe = new javax.swing.JButton();
        btnDoiMatKhau = new javax.swing.JButton();
        btnDangXuat = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        listKM = new javax.swing.JList<>();
        mnubar = new javax.swing.JMenuBar();
        mnuHeThong = new javax.swing.JMenu();
        mnuitemDoiMatKhau = new javax.swing.JMenuItem();
        mnuitemDangXuat = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        mnuitemThoat = new javax.swing.JMenuItem();
        mnuQuanLy = new javax.swing.JMenu();
        mnuitemPhongHat = new javax.swing.JMenuItem();
        mnuitemKhachHang = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        mnuitemCTKM = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        mnuitemTaiKhoan = new javax.swing.JMenuItem();
        mnuBCTK = new javax.swing.JMenu();
        mnuitemKho = new javax.swing.JMenuItem();
        mnuitemThongKe = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("KARAOKE WinWin");

        tenPhong.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        tenPhong.setText("Phòng Hát :");
        tenPhong.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, java.awt.Color.white, new java.awt.Color(0, 153, 153), java.awt.Color.lightGray, java.awt.Color.lightGray));

        gioVao.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        gioVao.setText("Giờ Vào :");
        gioVao.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, java.awt.Color.white, new java.awt.Color(0, 102, 102), java.awt.Color.lightGray, java.awt.Color.lightGray));

        tongHop.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        tongHop.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        tongHop.setText("Chương Trình Khuyến Mãi");
        tongHop.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, java.awt.Color.white, new java.awt.Color(0, 102, 102), java.awt.Color.lightGray, java.awt.Color.lightGray));

        btnThanhToan.setFont(new java.awt.Font("Tahoma", 2, 16)); // NOI18N
        btnThanhToan.setForeground(new java.awt.Color(51, 51, 51));
        btnThanhToan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/thanhtoan.png"))); // NOI18N
        btnThanhToan.setText("Thanh Toán");
        btnThanhToan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThanhToanActionPerformed(evt);
            }
        });

        pnPhongHat.setBorder(javax.swing.BorderFactory.createTitledBorder(null, " Danh Sách Phòng Hát", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 2, 16), new java.awt.Color(20, 70, 70))); // NOI18N

        tableDangSachPhong.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, "p1", "thường", "5", "trống"},
                {null, "p2", "vip", "12", null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "STT", "Tên Phòng", "Loại Phòng", "Giá Phòng", "Trạng Thái"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tableDangSachPhong.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableDangSachPhongMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tableDangSachPhong);
        if (tableDangSachPhong.getColumnModel().getColumnCount() > 0) {
            tableDangSachPhong.getColumnModel().getColumn(0).setMinWidth(0);
            tableDangSachPhong.getColumnModel().getColumn(0).setPreferredWidth(50);
            tableDangSachPhong.getColumnModel().getColumn(0).setMaxWidth(100);
        }

        javax.swing.GroupLayout pnPhongHatLayout = new javax.swing.GroupLayout(pnPhongHat);
        pnPhongHat.setLayout(pnPhongHatLayout);
        pnPhongHatLayout.setHorizontalGroup(
            pnPhongHatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnPhongHatLayout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1108, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnPhongHatLayout.setVerticalGroup(
            pnPhongHatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnPhongHatLayout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
                .addContainerGap())
        );

        pnDichVu.setBorder(javax.swing.BorderFactory.createTitledBorder(null, " Danh Sách Dịch Vụ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 2, 16), new java.awt.Color(20, 70, 70))); // NOI18N

        tableDichVu.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "STT", "Tên Dịch Vụ", "Đơn Giá ", "Số Lượng"
            }
        ));
        tableDanhSachDichVu.setViewportView(tableDichVu);
        if (tableDichVu.getColumnModel().getColumnCount() > 0) {
            tableDichVu.getColumnModel().getColumn(0).setPreferredWidth(50);
            tableDichVu.getColumnModel().getColumn(0).setMaxWidth(100);
        }

        javax.swing.GroupLayout pnDichVuLayout = new javax.swing.GroupLayout(pnDichVu);
        pnDichVu.setLayout(pnDichVuLayout);
        pnDichVuLayout.setHorizontalGroup(
            pnDichVuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tableDanhSachDichVu, javax.swing.GroupLayout.DEFAULT_SIZE, 663, Short.MAX_VALUE)
        );
        pnDichVuLayout.setVerticalGroup(
            pnDichVuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tableDanhSachDichVu, javax.swing.GroupLayout.DEFAULT_SIZE, 184, Short.MAX_VALUE)
        );

        lblTenPhong.setFont(new java.awt.Font("Tahoma", 3, 14)); // NOI18N
        lblTenPhong.setForeground(new java.awt.Color(0, 102, 102));
        lblTenPhong.setText("...");

        lblGioVao.setFont(new java.awt.Font("Tahoma", 3, 14)); // NOI18N
        lblGioVao.setForeground(new java.awt.Color(0, 102, 102));
        lblGioVao.setText("...");

        jToolBar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));
        jToolBar.setRollover(true);

        btnTaiKhoan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/acc.png"))); // NOI18N
        btnTaiKhoan.setText("  ");
        btnTaiKhoan.setToolTipText("Quản Lý Tài Khoản");
        btnTaiKhoan.setFocusable(false);
        btnTaiKhoan.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToolBar.add(btnTaiKhoan);

        btnKhachHang.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/cus.png"))); // NOI18N
        btnKhachHang.setText("  ");
        btnKhachHang.setToolTipText("Quản Lý Khách Hàng");
        btnKhachHang.setFocusable(false);
        btnKhachHang.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnKhachHang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKhachHangActionPerformed(evt);
            }
        });
        jToolBar.add(btnKhachHang);

        btnThongKe.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/thongke1.png"))); // NOI18N
        btnThongKe.setText("  ");
        btnThongKe.setToolTipText("Thống kê ");
        btnThongKe.setFocusable(false);
        btnThongKe.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnThongKe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThongKeActionPerformed(evt);
            }
        });
        jToolBar.add(btnThongKe);

        btnDoiMatKhau.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/doimatkhau1.png"))); // NOI18N
        btnDoiMatKhau.setText("  ");
        btnDoiMatKhau.setToolTipText("Đổi Mật Khẩu");
        btnDoiMatKhau.setFocusable(false);
        btnDoiMatKhau.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToolBar.add(btnDoiMatKhau);

        btnDangXuat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/logout1.png"))); // NOI18N
        btnDangXuat.setText("  ");
        btnDangXuat.setToolTipText("Đăng Xuất");
        btnDangXuat.setFocusable(false);
        btnDangXuat.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnDangXuat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDangXuatActionPerformed(evt);
            }
        });
        jToolBar.add(btnDangXuat);

        listKM.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(listKM);

        mnuHeThong.setText("Hệ Thống ");

        mnuitemDoiMatKhau.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.SHIFT_MASK));
        mnuitemDoiMatKhau.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/doimatkhau.png"))); // NOI18N
        mnuitemDoiMatKhau.setText("Đổi Mật Khẩu");
        mnuHeThong.add(mnuitemDoiMatKhau);

        mnuitemDangXuat.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.ALT_MASK));
        mnuitemDangXuat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/dangxuat.png"))); // NOI18N
        mnuitemDangXuat.setText("Đăng Xuất");
        mnuitemDangXuat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuitemDangXuatActionPerformed(evt);
            }
        });
        mnuHeThong.add(mnuitemDangXuat);
        mnuHeThong.add(jSeparator1);

        mnuitemThoat.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, java.awt.event.InputEvent.CTRL_MASK));
        mnuitemThoat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/exit.png"))); // NOI18N
        mnuitemThoat.setText("Thoát");
        mnuitemThoat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuitemThoatActionPerformed(evt);
            }
        });
        mnuHeThong.add(mnuitemThoat);

        mnubar.add(mnuHeThong);

        mnuQuanLy.setText("Quản Lý ");

        mnuitemPhongHat.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        mnuitemPhongHat.setText("Phòng Hát");
        mnuitemPhongHat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuitemPhongHatActionPerformed(evt);
            }
        });
        mnuQuanLy.add(mnuitemPhongHat);

        mnuitemKhachHang.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_K, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        mnuitemKhachHang.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/khachhang.png"))); // NOI18N
        mnuitemKhachHang.setText("Khách Hàng");
        mnuitemKhachHang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuitemKhachHangActionPerformed(evt);
            }
        });
        mnuQuanLy.add(mnuitemKhachHang);
        mnuQuanLy.add(jSeparator2);

        mnuitemCTKM.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        mnuitemCTKM.setText("Chương Trình Khuyến Mãi");
        mnuitemCTKM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuitemCTKMActionPerformed(evt);
            }
        });
        mnuQuanLy.add(mnuitemCTKM);
        mnuQuanLy.add(jSeparator3);

        mnuitemTaiKhoan.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        mnuitemTaiKhoan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/taikhoan.png"))); // NOI18N
        mnuitemTaiKhoan.setText("Tài Khoản");
        mnuQuanLy.add(mnuitemTaiKhoan);

        mnubar.add(mnuQuanLy);

        mnuBCTK.setText(" Kho - Thống Kê  ");

        mnuitemKho.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_K, java.awt.event.InputEvent.ALT_MASK));
        mnuitemKho.setText("Kho Dịch Vụ");
        mnuitemKho.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuitemKhoActionPerformed(evt);
            }
        });
        mnuBCTK.add(mnuitemKho);

        mnuitemThongKe.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.ALT_MASK));
        mnuitemThongKe.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/thongke.png"))); // NOI18N
        mnuitemThongKe.setText("Thống Kê");
        mnuitemThongKe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuitemThongKeActionPerformed(evt);
            }
        });
        mnuBCTK.add(mnuitemThongKe);

        mnubar.add(mnuBCTK);

        setJMenuBar(mnubar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnPhongHat, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jToolBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnDichVu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(tenPhong)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblTenPhong, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(gioVao)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblGioVao, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnThanhToan)
                        .addGap(108, 108, 108))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jScrollPane1)
                            .addComponent(tongHop, javax.swing.GroupLayout.DEFAULT_SIZE, 318, Short.MAX_VALUE))
                        .addGap(22, 22, 22))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(pnPhongHat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTenPhong, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblGioVao, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(tongHop, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(gioVao, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(tenPhong, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addGap(18, 18, 18)
                        .addComponent(btnThanhToan, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(pnDichVu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // sự kiện thoát chương trình
    private void mnuitemThoatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuitemThoatActionPerformed
        System.exit(0);
    }//GEN-LAST:event_mnuitemThoatActionPerformed
    // sự kiện  Jtable ds phòng 
    private void tableDangSachPhongMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableDangSachPhongMouseClicked

        row = tableDangSachPhong.getSelectedRow();
        phongSlected = dsPhong.get(row);
        lblTenPhong.setText(phongSlected.getMaPhong());
        // gọi hàm hiển thị Đon đặt phòng
        if (!phongSlected.getTinhTrang().equals("Đã đặt")) {
            maHD = null;
            lblGioVao.setText("...");
        }
        hienthiMa_DDP_HD(phongSlected.getMaPhong());

        // gọi hàm popup
        setPopupTable(phongSlected.getMaPhong());
    }//GEN-LAST:event_tableDangSachPhongMouseClicked

    String maDDP, maHD;
    SimpleDateFormat mat = new SimpleDateFormat("HH:mm");

    public void hienthiMa_DDP_HD(String maphong) {

//        System.out.println(maHD);
//        System.out.println(maDDP);
        try {
            Connection con = ConnectDB.getConnect();
            String sql = "SELECT dbo.HoaDon.ma_HD , dbo.DonDatPhong.ma_DDP , dbo.DonDatPhong.gio_dat FROM dbo.HoaDon , dbo.DonDatPhong WHERE dbo.HoaDon.ma_DDP = dbo.DonDatPhong.ma_DDP AND dbo.DonDatPhong.tinh_trang = 1 AND dbo.DonDatPhong.ma_phong = ?";
            PreparedStatement pstm = con.prepareStatement(sql);
            pstm.setString(1, maphong);
            ResultSet rs = pstm.executeQuery();
            while (rs.next()) {
                maHD = rs.getString(1);
                maDDP = rs.getString(2);
                lblGioVao.setText(mat.format(rs.getTime(3)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        dtmDSDivhVu = new DefaultTableModel();
        dtmDSDivhVu.addColumn("STT");
        dtmDSDivhVu.addColumn("Tên Dịch Vụ");
        dtmDSDivhVu.addColumn("Đơn Giá");
        dtmDSDivhVu.addColumn("Số Lượng");
        dtmDSDivhVu.addColumn("Đơn Vị Tính");

        dsDichVuService = new DSDichVuService();
        danhSachDichVu = dsDichVuService.hienThiDSDichVu(maHD);
        int stt = 0;
        for (DSDichVu dsDichVu : danhSachDichVu) {
            stt++;
            Vector<Object> vecDSDichVu = new Vector<>();
            vecDSDichVu.add(stt);
            vecDSDichVu.add(dsDichVu.getTenDichVu());
            vecDSDichVu.add(en.format(Float.parseFloat(dsDichVu.getGia())) + "    vnd");
            vecDSDichVu.add(dsDichVu.getSoLuong());
            vecDSDichVu.add(dsDichVu.getDonViTinh());
            dtmDSDivhVu.addRow(vecDSDichVu);
        }
        tableDichVu.setModel(dtmDSDivhVu);

        //--------------------------------------------------------------------------------
        // TÍNH tiền dịch vụ của mỗi phòng
//        double giaDV, slDV, gia1dv = 0, tongtienPDV = 0;
//        double phuthu = 0;
//        if (!txtPhuThu.getText().isEmpty()) {
//           phuthu = Double.parseDouble(txtPhuThu.getText());
//        }
//        for (DSDichVu dsdv : danhSachDichVu) {
//            giaDV = Double.parseDouble(dsdv.getGia());
//            slDV = Double.parseDouble(dsdv.getSoLuong());
//            gia1dv = giaDV * slDV;
//            tongtienPDV = gia1dv + tongtienPDV;
//            //System.out.println(gia1dv);
//            //System.out.println(tongtienPDV);
//            //System.out.println("----------------");
//        }
//        lblTienDichVu.setText((tongtienPDV + "").replace(".0", "") + "   vnd");
//        if (lblTienDichVu.getText().equals("0   vnd")) {
//            lblTienDichVu.setText("...");
//        }
//            
//        lblTongTien.setText((tongtienPDV+phuthu + "").replace(".0", "") + "   vnd");
//        if (lblTongTien.getText().equals("0   vnd")) {
//            lblTongTien.setText("...");
//        }
        //--------------------------------------------------------------------------------
    }

    /*
        Đã Xong  đăng xuất , đổi mật khẩu .
     */
    private void mnuitemDangXuatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuitemDangXuatActionPerformed
        DangXuat();
    }//GEN-LAST:event_mnuitemDangXuatActionPerformed

    private void btnDangXuatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDangXuatActionPerformed
        DangXuat();
    }//GEN-LAST:event_btnDangXuatActionPerformed

    String maKhuyenMai;
    private void btnThanhToanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThanhToanActionPerformed
        try {
            Date date = new Date();

            String time1 = lblGioVao.getText();
            String time2 = mat.format(date);

            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            timeFormat.setTimeZone(TimeZone.getDefault().getTimeZone("UTC"));

            Date date1 = timeFormat.parse(time1);
            Date date2 = timeFormat.parse(time2);

            long sum = date2.getTime() - date1.getTime();

            String date3 = timeFormat.format(new Date(sum));
            // System.out.println("The sum is " + date3);

            try {
                Connection con = ConnectDB.getConnect();
                String sql = "select ma_KM from CTKM where tinh_trang = 0";
                PreparedStatement pstm = con.prepareStatement(sql);
                ResultSet rs = pstm.executeQuery();
                while (rs.next()) {
                    maKhuyenMai = rs.getString(1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
// cập nhật hóa đơn
            try {
                Connection con = ConnectDB.getConnect();
                String sql = "update HoaDon set ma_KM = ? , gio_hat = ? where HoaDon.ma_HD = ?";
                PreparedStatement pstm = con.prepareStatement(sql);
                pstm.setString(1, maKhuyenMai);
                pstm.setString(2, date3.toString());
                pstm.setString(3, maHD);
                pstm.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //System.out.println(maKhuyenMai);
            hienThiDMPhong();
            dispose();
            ThanhToan thanhtoan = new ThanhToan(phongSlected.getMaPhong(), phongSlected.getGiaPhong(), maDDP, maHD, lblGioVao.getText());
            thanhtoan.setVisible(true);
        } catch (ParseException ex) {
            Logger.getLogger(MenuChinh.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnThanhToanActionPerformed

    private void mnuitemCTKMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuitemCTKMActionPerformed
        dispose();
        frmQL_KM khuyenMai = new frmQL_KM();
        khuyenMai.setVisible(true);
    }//GEN-LAST:event_mnuitemCTKMActionPerformed

    private void mnuitemPhongHatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuitemPhongHatActionPerformed
        dispose();
        QLPhong phonghat = new QLPhong();
        phonghat.setVisible(true);
    }//GEN-LAST:event_mnuitemPhongHatActionPerformed

    private void mnuitemKhachHangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuitemKhachHangActionPerformed
        dispose();
        QLkhachhang khachhang = new QLkhachhang();
        khachhang.setVisible(true);
    }//GEN-LAST:event_mnuitemKhachHangActionPerformed

    String maKM;
    private void mnuitemKhoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuitemKhoActionPerformed
        dispose();
        frmQLKho kho = new frmQLKho();
        kho.setVisible(true);
    }//GEN-LAST:event_mnuitemKhoActionPerformed

    private void mnuitemThongKeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuitemThongKeActionPerformed
        dispose();
        frmHoaDon hoadon = new frmHoaDon();
        hoadon.setVisible(true);
    }//GEN-LAST:event_mnuitemThongKeActionPerformed

    private void btnKhachHangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKhachHangActionPerformed
        dispose();
        QLkhachhang khachhang = new QLkhachhang();
        khachhang.setVisible(true);
    }//GEN-LAST:event_btnKhachHangActionPerformed

    private void btnThongKeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThongKeActionPerformed
        dispose();
        frmHoaDon hoadon = new frmHoaDon();
        hoadon.setVisible(true);
    }//GEN-LAST:event_btnThongKeActionPerformed

    private void DangXuat() {
        dispose();
        Login login = new Login();
        login.setVisible(true);
    }

    public void exitSystem() {
        System.exit(0);
    }

    // lâý mã đơn đặt phòng , mã hóa đơn từ from đặt phòng.
    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MenuChinh(null, null, null, null).setVisible(true);
            }
        });
    }
    /*
        Đã Xong 
     */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDangXuat;
    private javax.swing.JButton btnDoiMatKhau;
    private javax.swing.JButton btnKhachHang;
    private javax.swing.JButton btnTaiKhoan;
    private javax.swing.JButton btnThanhToan;
    private javax.swing.JButton btnThongKe;
    private javax.swing.JLabel gioVao;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JToolBar jToolBar;
    private javax.swing.JLabel lblGioVao;
    private javax.swing.JLabel lblTenPhong;
    private javax.swing.JList<String> listKM;
    private javax.swing.JMenu mnuBCTK;
    private javax.swing.JMenu mnuHeThong;
    private javax.swing.JMenu mnuQuanLy;
    private javax.swing.JMenuBar mnubar;
    private javax.swing.JMenuItem mnuitemCTKM;
    private javax.swing.JMenuItem mnuitemDangXuat;
    private javax.swing.JMenuItem mnuitemDoiMatKhau;
    private javax.swing.JMenuItem mnuitemKhachHang;
    private javax.swing.JMenuItem mnuitemKho;
    private javax.swing.JMenuItem mnuitemPhongHat;
    private javax.swing.JMenuItem mnuitemTaiKhoan;
    private javax.swing.JMenuItem mnuitemThoat;
    private javax.swing.JMenuItem mnuitemThongKe;
    private javax.swing.JPanel pnDichVu;
    private javax.swing.JPanel pnPhongHat;
    private javax.swing.JTable tableDangSachPhong;
    private javax.swing.JScrollPane tableDanhSachDichVu;
    private javax.swing.JTable tableDichVu;
    private javax.swing.JLabel tenPhong;
    private javax.swing.JLabel tongHop;
    // End of variables declaration//GEN-END:variables

}
