package form;

import koneksi.Koneksi;
import java.sql.*;
import java.util.HashMap;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class FormSiswa extends JFrame {
    private int idUser;
    private JComboBox<String> cbMenu;
    private JTextField txtJumlah;
    private JTable tblTracking;
    private DefaultTableModel modelTracking;
    private HashMap<String, Integer> mapMenuId = new HashMap<>();
    private HashMap<String, Integer> mapMenuHarga = new HashMap<>();

    public FormSiswa(int idUser, String namaSiswa) {
        this.idUser = idUser;
        setTitle("BI-KANTIN - Dashboard Siswa (" + namaSiswa + ")");
        setSize(700, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel lblTitle = new JLabel("Form Pemesanan Kantin");
        lblTitle.setBounds(20, 15, 200, 20);
        add(lblTitle);

        JLabel lblMenu = new JLabel("Pilih Menu:");
        lblMenu.setBounds(20, 50, 80, 25);
        add(lblMenu);

        cbMenu = new JComboBox<>();
        cbMenu.setBounds(100, 50, 180, 25);
        add(cbMenu);

        JLabel lblJml = new JLabel("Jumlah:");
        lblJml.setBounds(20, 90, 80, 25);
        add(lblJml);

        txtJumlah = new JTextField();
        txtJumlah.setBounds(100, 90, 180, 25);
        add(txtJumlah);

        JButton btnPesan = new JButton("Kirim Pesanan");
        btnPesan.setBounds(100, 130, 180, 30);
        add(btnPesan);
        btnPesan.addActionListener(e -> buatPesanan());

        JButton btnRefresh = new JButton("Refresh Status");
        btnRefresh.setBounds(530, 15, 130, 25);
        add(btnRefresh);
        btnRefresh.addActionListener(e -> loadTrackingPesanan());

        JLabel lblTrack = new JLabel("Status Pesanan Saya:");
        lblTrack.setBounds(310, 50, 200, 20);
        add(lblTrack);

        modelTracking = new DefaultTableModel(new String[]{"ID", "Menu", "Qty", "Total", "Status"}, 0);
        tblTracking = new JTable(modelTracking);
        JScrollPane sp = new JScrollPane(tblTracking);
        sp.setBounds(310, 75, 350, 310);
        add(sp);

        getComboDataMenu();
        loadTrackingPesanan();
    }

    private void getComboDataMenu() {
        try {
            Connection conn = Koneksi.getKoneksi();
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM menu");
            cbMenu.removeAllItems();
            mapMenuId.clear();
            mapMenuHarga.clear();

            while (rs.next()) {
                String name = rs.getString("nama_menu");
                int id = 0;
                try { id = rs.getInt("id_menu"); } catch (Exception e) { id = rs.getInt("id"); }
                int harga = rs.getInt("harga");

                cbMenu.addItem(name);
                mapMenuId.put(name, id);
                mapMenuHarga.put(name, harga);
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void buatPesanan() {
        try {
            if (cbMenu.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Pilih menu terlebih dahulu!");
                return;
            }

            String selectedMenu = cbMenu.getSelectedItem().toString();
            Integer idMenu = mapMenuId.get(selectedMenu);
            Integer harga = mapMenuHarga.get(selectedMenu);

            if (idMenu == null || harga == null) {
                JOptionPane.showMessageDialog(this, "Data menu tidak valid, silakan klik Refresh!");
                return;
            }

            int jumlah = Integer.parseInt(txtJumlah.getText());
            int total = harga * jumlah;

            Connection conn = Koneksi.getKoneksi();
            String sql = "INSERT INTO pesanan (id_user, id_menu, jumlah, total_harga) VALUES (?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, idUser);
            pst.setInt(2, idMenu);
            pst.setInt(3, jumlah);
            pst.setInt(4, total);
            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Pesanan Berhasil Dikirim!");
            txtJumlah.setText("");
            loadTrackingPesanan();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal Order: " + ex.getMessage());
        }
    }

    private void loadTrackingPesanan() {
        modelTracking.setRowCount(0);
        try {
            Connection conn = Koneksi.getKoneksi();
            String sql = "SELECT p.id_pesanan, m.nama_menu, p.jumlah, p.total_harga, p.status " +
                         "FROM pesanan p JOIN menu m ON p.id_menu = m.id_menu " +
                         "WHERE p.id_user=? ORDER BY p.id_pesanan DESC";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, idUser);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                modelTracking.addRow(new Object[]{
                    rs.getInt("id_pesanan"),
                    rs.getString("nama_menu"),
                    rs.getInt("jumlah"),
                    rs.getInt("total_harga"),
                    rs.getString("status")
                });
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}