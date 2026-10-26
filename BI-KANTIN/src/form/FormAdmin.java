package form;

import koneksi.Koneksi;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class FormAdmin extends JFrame {
    private JTextField txtNamaMenu, txtHarga, txtStok, txtIdPesanan;
    private JComboBox<String> cbKategori, cbStatusPesanan;
    private JTable tblPesanan, tblMenu;
    private DefaultTableModel modelPesanan, modelMenu;

    public FormAdmin() {
        setTitle("BI-KANTIN - Dashboard Admin");
        setSize(850, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        // --- Panel Input Menu ---
        JLabel lblInput = new JLabel("--- Kelola Menu Makanan ---");
        lblInput.setBounds(20, 10, 200, 20);
        add(lblInput);

        JLabel lblNama = new JLabel("Nama Menu:");
        lblNama.setBounds(20, 40, 80, 25);
        add(lblNama);
        txtNamaMenu = new JTextField();
        txtNamaMenu.setBounds(100, 40, 150, 25);
        add(txtNamaMenu);

        JLabel lblKat = new JLabel("Kategori:");
        lblKat.setBounds(20, 75, 80, 25);
        add(lblKat);
        cbKategori = new JComboBox<>(new String[]{"Makanan", "Minuman"});
        cbKategori.setBounds(100, 75, 150, 25);
        add(cbKategori);

        JLabel lblHarga = new JLabel("Harga:");
        lblHarga.setBounds(20, 110, 80, 25);
        add(lblHarga);
        txtHarga = new JTextField();
        txtHarga.setBounds(100, 110, 150, 25);
        add(txtHarga);

        JLabel lblStok = new JLabel("Stok:");
        lblStok.setBounds(20, 145, 80, 25);
        add(lblStok);
        txtStok = new JTextField();
        txtStok.setBounds(100, 145, 150, 25);
        add(txtStok);

        JButton btnSimpanMenu = new JButton("Simpan Menu");
        btnSimpanMenu.setBounds(100, 180, 150, 30);
        add(btnSimpanMenu);
        btnSimpanMenu.addActionListener(e -> simpanMenu());

        // Tabel Menu
        modelMenu = new DefaultTableModel(new String[]{"ID", "Nama", "Kategori", "Harga", "Stok"}, 0);
        tblMenu = new JTable(modelMenu);
        JScrollPane spMenu = new JScrollPane(tblMenu);
        spMenu.setBounds(270, 30, 540, 180);
        add(spMenu);

        // --- Panel Status Pesanan ---
        JLabel lblStatusTitle = new JLabel("--- Update Status Pesanan Masuk ---");
        lblStatusTitle.setBounds(20, 230, 250, 20);
        add(lblStatusTitle);

        JLabel lblIdP = new JLabel("ID Pesanan:");
        lblIdP.setBounds(20, 260, 80, 25);
        add(lblIdP);
        txtIdPesanan = new JTextField();
        txtIdPesanan.setBounds(100, 260, 150, 25);
        add(txtIdPesanan);

        JLabel lblSts = new JLabel("Status Baru:");
        lblSts.setBounds(20, 295, 80, 25);
        add(lblSts);
        cbStatusPesanan = new JComboBox<>(new String[]{"Menunggu Konfirmasi", "Sedang Dimasak", "Siap Diambil", "Selesai"});
        cbStatusPesanan.setBounds(100, 295, 150, 25);
        add(cbStatusPesanan);

        JButton btnUpdateStatus = new JButton("Update Status");
        btnUpdateStatus.setBounds(100, 330, 150, 30);
        add(btnUpdateStatus);
        btnUpdateStatus.addActionListener(e -> updateStatus());

        // Tabel Pesanan
        modelPesanan = new DefaultTableModel(new String[]{"ID Pesanan", "Siswa", "Menu", "Jumlah", "Total", "Status"}, 0);
        tblPesanan = new JTable(modelPesanan);
        JScrollPane spPesanan = new JScrollPane(tblPesanan);
        spPesanan.setBounds(270, 240, 540, 230);
        add(spPesanan);

        loadMenu();
        loadPesanan();
    }

    private void simpanMenu() {
        try {
            Connection conn = Koneksi.getKoneksi();
            String sql = "INSERT INTO menu (nama_menu, kategori, harga, stok) VALUES (?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, txtNamaMenu.getText());
            pst.setString(2, cbKategori.getSelectedItem().toString());
            pst.setInt(3, Integer.parseInt(txtHarga.getText()));
            pst.setInt(4, Integer.parseInt(txtStok.getText()));
            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Menu Berhasil Ditambahkan!");
            loadMenu();
            txtNamaMenu.setText(""); txtHarga.setText(""); txtStok.setText("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error Simpan: " + ex.getMessage());
        }
    }

    private void loadMenu() {
        modelMenu.setRowCount(0);
        try {
            Connection conn = Koneksi.getKoneksi();
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM menu");
            while (rs.next()) {
                int id = 0;
                try { id = rs.getInt("id_menu"); } catch (Exception e) { id = rs.getInt("id"); }
                int stokVal = 0;
                try { stokVal = rs.getInt("stok"); } catch (Exception e) { stokVal = 0; }
                
                modelMenu.addRow(new Object[]{
                    id, rs.getString("nama_menu"),
                    rs.getString("kategori"), rs.getInt("harga"), stokVal
                });
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void loadPesanan() {
        modelPesanan.setRowCount(0);
        try {
            Connection conn = Koneksi.getKoneksi();
            String sql = "SELECT p.id_pesanan, u.username, m.nama_menu, p.jumlah, p.total_harga, p.status " +
                         "FROM pesanan p JOIN users u ON p.id_user = u.id " +
                         "JOIN menu m ON p.id_menu = m.id_menu ORDER BY p.id_pesanan DESC";
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                modelPesanan.addRow(new Object[]{
                    rs.getInt("id_pesanan"), rs.getString("username"),
                    rs.getString("nama_menu"), rs.getInt("jumlah"),
                    rs.getInt("total_harga"), rs.getString("status")
                });
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void updateStatus() {
        try {
            Connection conn = Koneksi.getKoneksi();
            String sql = "UPDATE pesanan SET status=? WHERE id_pesanan=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, cbStatusPesanan.getSelectedItem().toString());
            pst.setInt(2, Integer.parseInt(txtIdPesanan.getText()));
            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Status Pesanan Diperbarui!");
            loadPesanan();
            txtIdPesanan.setText("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error Update: " + ex.getMessage());
        }
    }
}