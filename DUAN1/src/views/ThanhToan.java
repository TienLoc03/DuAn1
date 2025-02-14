/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package views;

import application.NewClass;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Locale;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;
import services.KhuyenMaiService;
import services.impl.KhuyenMaiServiceImpl;
import utilies.DBConnection;
import viewModels.KhuyenMaiResponse;

/**
 *
 * @author ASUS
 */
public class ThanhToan extends javax.swing.JPanel {

    private KhuyenMaiService khuyenMaiService = new KhuyenMaiServiceImpl();
    private DefaultComboBoxModel boxModel = new DefaultComboBoxModel();
    private ArrayList<KhuyenMaiResponse> list = new ArrayList<>();
    public Connection conn = DBConnection.getConnection();

    /**
     * Creates new form HoaDon
     */
    public ThanhToan() {
        initComponents();
        list = khuyenMaiService.getAll();
        LayDuLieuHoaDon();

        loadCBB(list);
    }
    Calendar cal = Calendar.getInstance();
    int day = cal.get(Calendar.DAY_OF_MONTH);
    int month = cal.get(Calendar.MONTH) + 1;
    int year = cal.get(Calendar.YEAR);
    int hours = cal.get(Calendar.HOUR);
    int minutes = cal.get(Calendar.MINUTE);
    int second = cal.get(Calendar.SECOND);

    private void loadCBB(ArrayList<KhuyenMaiResponse> list) {
        boxModel = (DefaultComboBoxModel) cbbKM.getModel();
        for (KhuyenMaiResponse o : list) {
            cbbKM.addItem(o.getMaKhuyenMai() + "");
        }
    }

    public void LayDuLieuHoaDon() {
        String cautruyvan = "";
        cautruyvan = "SELECT dbo.HoaDon.MaHoaDon, dbo.NhanVien.TenNhanVien, dbo.HoaDon.TenKhachHang, dbo.HoaDon.NgayTao, dbo.HoaDon.NgayThanhToan, dbo.HoaDon.MaKhuyenMai, dbo.HoaDon.TongTien, dbo.HoaDon.TrangThai, dbo.HoaDon.GhiChu\n"
                + "FROM     dbo.HoaDon INNER JOIN\n"
                + "                  dbo.NhanVien ON dbo.HoaDon.MaNhanVien = dbo.NhanVien.MaNhanVien where HoaDon.TrangThai = N'Chưa thanh toán'";
        ResultSet rs = NewClass.connection.ExcuteQueryGetTable(cautruyvan);
        Object[] obj = new Object[]{"Mã hóa đơn", "Nhân viên", "Khách hàng", "Ngày tạo", "Ngày thanh toán", "Khuyến mãi", "Tổng tiền", "Trạng thái","Ghi chú"};
        DefaultTableModel tableModel = new DefaultTableModel(obj, 0);
        tblHoaDon.setModel(tableModel);
        try {
            while (rs.next()) {
                Object[] item = new Object[9];
                item[0] = rs.getInt("MaHoaDon");
                item[1] = rs.getString("TenNhanVien");
                item[2] = rs.getString("TenKhachHang");
                item[3] = rs.getString("NgayTao");
                item[4] = rs.getString("NgayThanhToan");
                item[5] = rs.getString("MaKhuyenMai");
                item[6] = rs.getBigDecimal("TongTien");
                item[7] = rs.getString("TrangThai");
                item[8] = rs.getString("GhiChu");
                tableModel.addRow(item);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void SetTongTien(String MaHoaDon, String MaKM) {
        String cautruyvan = "select (HoaDon.TongTien-KhuyenMai.TienGiam) as TongTienHienTai from HoaDon,KhuyenMai where HoaDon.MaHoaDon ="+MaHoaDon+" and KhuyenMai.MaKhuyenMai = "+MaKM+"";
        ResultSet rs = application.NewClass.connection.ExcuteQueryGetTable(cautruyvan);
        String ttht = "";
        try {
            if (rs.next()) {
                ttht = rs.getString("TongTienHienTai");

                String ctv = "update HoaDon set TongTien= " + ttht + "where MaHoaDon=" + MaHoaDon;
                NewClass.connection.ExcuteQueryUpdateDB(ctv);
                LayDuLieuHoaDon();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void setCBB(String MaKM) {
        String sql = "SELECT TienGiam FROM KhuyenMai WHERE MaKhuyenMai =" + MaKM;
        ResultSet rs = application.NewClass.connection.ExcuteQueryGetTable(sql);
        try {
            if (rs.next()) {
                txtTienKM.setText(rs.getString("TienGiam"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    Locale lc = new Locale("nv", "VN");
    NumberFormat nf = NumberFormat.getInstance(lc);

    public void ThongBao(String noiDungThongBao, String tieuDeThongBao, int icon) {
        JOptionPane.showMessageDialog(this, noiDungThongBao,
                tieuDeThongBao, icon);
    }

    private void clear() {
        lbTongTien.setText("");
        lbTienthua.setText("");
        txtMaHD.setText("");
        txtNgayThanhToan.setText("");
        txtTienNhan.setText("");
        cbbKM.setSelectedIndex(0);
    }

    private void xuatHoaDon(int idHD) {
        try {
            Hashtable map = new Hashtable();
            JasperReport report = JasperCompileManager.compileReport("src/report/RPHoaDon.jrxml");
            map.put("MaHoaDon", idHD);
            JasperPrint p = JasperFillManager.fillReport(report, map, DBConnection.getConnection());
            JasperViewer.viewReport(p, false);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        lbTongTien = new javax.swing.JLabel();
        txtTienNhan = new javax.swing.JTextField();
        jLabel35 = new javax.swing.JLabel();
        cbbKM = new javax.swing.JComboBox<>();
        lbTienthua = new javax.swing.JLabel();
        btnThanhToan = new javax.swing.JButton();
        jLabel36 = new javax.swing.JLabel();
        txtTienKM = new javax.swing.JTextField();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        txtNgayThanhToan = new javax.swing.JTextField();
        txtMaHD = new javax.swing.JTextField();
        btnInHoaDon = new javax.swing.JButton();
        jLabel39 = new javax.swing.JLabel();
        lbTienPhaiTra = new javax.swing.JLabel();
        txtNgay = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblHoaDon = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));
        jPanel8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel8.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel32.setBackground(new java.awt.Color(49, 139, 130));
        jLabel32.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel32.setForeground(new java.awt.Color(49, 139, 130));
        jLabel32.setText("Tiền phải trả");
        jPanel8.add(jLabel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 10, -1, -1));

        jLabel33.setBackground(new java.awt.Color(49, 139, 130));
        jLabel33.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel33.setForeground(new java.awt.Color(49, 139, 130));
        jLabel33.setText("Tiền nhận của khách:");
        jPanel8.add(jLabel33, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 130, -1, -1));

        jLabel34.setBackground(new java.awt.Color(49, 139, 130));
        jLabel34.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel34.setForeground(new java.awt.Color(49, 139, 130));
        jLabel34.setText("Tiền thừa:");
        jPanel8.add(jLabel34, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 10, -1, -1));

        lbTongTien.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lbTongTien.setForeground(new java.awt.Color(49, 139, 130));
        lbTongTien.setText("0 VNĐ");
        lbTongTien.setEnabled(false);
        jPanel8.add(lbTongTien, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 10, 210, -1));

        txtTienNhan.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        txtTienNhan.setForeground(new java.awt.Color(49, 139, 130));
        txtTienNhan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtTienNhanKeyReleased(evt);
            }
        });
        jPanel8.add(txtTienNhan, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 130, 183, -1));

        jLabel35.setBackground(new java.awt.Color(49, 139, 130));
        jLabel35.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel35.setForeground(new java.awt.Color(49, 139, 130));
        jLabel35.setText("Mã khuyến mãi:");
        jPanel8.add(jLabel35, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, -1, -1));

        cbbKM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbbKMActionPerformed(evt);
            }
        });
        jPanel8.add(cbbKM, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 50, 183, -1));

        lbTienthua.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lbTienthua.setForeground(new java.awt.Color(49, 139, 130));
        lbTienthua.setText("0 VNĐ");
        lbTienthua.setEnabled(false);
        jPanel8.add(lbTienthua, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 10, 210, 20));

        btnThanhToan.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnThanhToan.setForeground(new java.awt.Color(49, 139, 130));
        btnThanhToan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/IMG/cash_register_56px.png"))); // NOI18N
        btnThanhToan.setText("Thanh toán");
        btnThanhToan.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnThanhToan.setDebugGraphicsOptions(javax.swing.DebugGraphics.NONE_OPTION);
        btnThanhToan.setHideActionText(true);
        btnThanhToan.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnThanhToan.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnThanhToan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThanhToanActionPerformed(evt);
            }
        });
        jPanel8.add(btnThanhToan, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 60, 124, 100));

        jLabel36.setBackground(new java.awt.Color(49, 139, 130));
        jLabel36.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel36.setForeground(new java.awt.Color(49, 139, 130));
        jLabel36.setText("Tiền giảm giá");
        jPanel8.add(jLabel36, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 90, -1, -1));

        txtTienKM.setEditable(false);
        jPanel8.add(txtTienKM, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 90, 183, -1));

        jLabel37.setBackground(new java.awt.Color(49, 139, 130));
        jLabel37.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel37.setForeground(new java.awt.Color(49, 139, 130));
        jLabel37.setText("Mã hóa đơn");
        jPanel8.add(jLabel37, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 170, -1, -1));

        jLabel38.setBackground(new java.awt.Color(49, 139, 130));
        jLabel38.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel38.setForeground(new java.awt.Color(49, 139, 130));
        jLabel38.setText("Ngày thanh toán");
        jPanel8.add(jLabel38, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 210, -1, -1));

        txtNgayThanhToan.setEditable(false);
        txtNgayThanhToan.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        txtNgayThanhToan.setForeground(new java.awt.Color(49, 139, 130));
        jPanel8.add(txtNgayThanhToan, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 210, 210, 30));

        txtMaHD.setEditable(false);
        jPanel8.add(txtMaHD, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 170, 180, -1));

        btnInHoaDon.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnInHoaDon.setForeground(new java.awt.Color(49, 139, 130));
        btnInHoaDon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/IMG/recipt.png"))); // NOI18N
        btnInHoaDon.setText("In hóa đơn");
        btnInHoaDon.setHideActionText(true);
        btnInHoaDon.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnInHoaDon.setMaximumSize(new java.awt.Dimension(101, 84));
        btnInHoaDon.setMinimumSize(new java.awt.Dimension(101, 84));
        btnInHoaDon.setPreferredSize(new java.awt.Dimension(101, 84));
        btnInHoaDon.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnInHoaDon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInHoaDonActionPerformed(evt);
            }
        });
        jPanel8.add(btnInHoaDon, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 60, 120, 100));

        jLabel39.setBackground(new java.awt.Color(49, 139, 130));
        jLabel39.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel39.setForeground(new java.awt.Color(49, 139, 130));
        jLabel39.setText("Tổng tiền:");
        jPanel8.add(jLabel39, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, -1, -1));

        lbTienPhaiTra.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lbTienPhaiTra.setForeground(new java.awt.Color(49, 139, 130));
        lbTienPhaiTra.setText("0 VNĐ");
        lbTienPhaiTra.setEnabled(false);
        jPanel8.add(lbTienPhaiTra, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 10, 210, -1));
        jPanel8.add(txtNgay, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 110, 0, -1));

        jPanel2.add(jPanel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 490, 940, 250));

        tblHoaDon.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        tblHoaDon.setForeground(new java.awt.Color(49, 139, 130));
        tblHoaDon.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Mã hóa đơn", "Tên nhân viên", "Tên khách hàng", "Ngày tạo", "Ngày thanh toán", "Mã khuyến mãi", "Tổng tiền", "Trạng thái", "Chú thích"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblHoaDon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblHoaDonMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblHoaDon);

        jPanel2.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 240, 1010, 229));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/IMG/Logo2png.png"))); // NOI18N
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 10, 650, -1));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/IMG/logo.jpg"))); // NOI18N
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -60, 540, 580));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 1243, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 802, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnThanhToanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThanhToanActionPerformed
        String MaHD, tt, ngaytt, MaKM,ngay;
        double tn = Double.valueOf(txtTienNhan.getText());
        double tongt = Double.valueOf(lbTongTien.getText());
        if(txtTienNhan.getText().equals("")){
            JOptionPane.showMessageDialog(this, "Nhập tiền khách trả");
            return;
        }
        if(tn<tongt){
            JOptionPane.showMessageDialog(this, "Tiền khách trả chưa đủ");
            return;
        }
        
        MaHD = txtMaHD.getText();
        MaKM = cbbKM.getSelectedItem().toString();
        txtNgay.setText(year + "-" + month + "-" + day);
        txtNgayThanhToan.setText(year + "-" + month + "-" + day + " " + hours + ":" + minutes + ":" + second);
        ngaytt = txtNgayThanhToan.getText();
        ngay = txtNgay.getText();
        tt = "Đã thanh toán";
        String cautruyvan = "update HoaDon set MaKhuyenMai=" + MaKM + ",TrangThai=N'" + tt + "',NgayThanhToan=N'" + ngaytt + "',Ngay=N'" + ngay + "' where MaHoaDon=" + MaHD;
        if (txtMaHD.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Mã hóa đơn không được để trống");
            return;
        }
        NewClass.connection.ExcuteQueryUpdateDB(cautruyvan);
        JOptionPane.showMessageDialog(this, "Thanh toán thành công");
        LayDuLieuHoaDon();
        SetTongTien(txtMaHD.getText(), (String) cbbKM.getSelectedItem());
        clear();
    }//GEN-LAST:event_btnThanhToanActionPerformed

    private void tblHoaDonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblHoaDonMouseClicked
        int row = tblHoaDon.getSelectedRow();
        txtMaHD.setText(tblHoaDon.getValueAt(row, 0).toString());
        lbTongTien.setText(tblHoaDon.getValueAt(row, 6).toString());
    }//GEN-LAST:event_tblHoaDonMouseClicked

    private void txtTienNhanKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTienNhanKeyReleased
        if (txtTienNhan.getText().isEmpty()) {
            lbTienthua.setText("");
            return;
        } else {
            lbTienPhaiTra.setText(nf.format(Float.valueOf(lbTongTien.getText()) - Float.valueOf(txtTienKM.getText())));
            lbTienthua.setText(nf.format(Float.valueOf(txtTienNhan.getText()) - Float.valueOf(lbTongTien.getText()) + Float.valueOf(txtTienKM.getText())));
        }
    }//GEN-LAST:event_txtTienNhanKeyReleased

    private void cbbKMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbbKMActionPerformed
        setCBB(cbbKM.getSelectedItem().toString());
    }//GEN-LAST:event_cbbKMActionPerformed

    private void btnInHoaDonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInHoaDonActionPerformed
        if (txtMaHD.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Mã hóa đơn không được để trống");
            return;
        }
        xuatHoaDon(Integer.parseInt(txtMaHD.getText()));
    }//GEN-LAST:event_btnInHoaDonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnInHoaDon;
    private javax.swing.JButton btnThanhToan;
    private javax.swing.JComboBox<String> cbbKM;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbTienPhaiTra;
    private javax.swing.JLabel lbTienthua;
    private javax.swing.JLabel lbTongTien;
    private javax.swing.JTable tblHoaDon;
    private javax.swing.JTextField txtMaHD;
    private javax.swing.JTextField txtNgay;
    private javax.swing.JTextField txtNgayThanhToan;
    private javax.swing.JTextField txtTienKM;
    private javax.swing.JTextField txtTienNhan;
    // End of variables declaration//GEN-END:variables
}
