package form;

import koneksi.Koneksi;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.*;

public class FormLogin extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    public FormLogin() {
        setTitle("BI-KANTIN - Login System");
        setSize(350, 220);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel lblUser = new JLabel("Username:");
        lblUser.setBounds(30, 30, 80, 25);
        add(lblUser);

        txtUsername = new JTextField();
        txtUsername.setBounds(120, 30, 170, 25);
        add(txtUsername);

        JLabel lblPass = new JLabel("Password:");
        lblPass.setBounds(30, 70, 80, 25);
        add(lblPass);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(120, 70, 170, 25);
        add(txtPassword);

        btnLogin = new JButton("Login");
        btnLogin.setBounds(120, 110, 170, 30);
        add(btnLogin);

        btnLogin.addActionListener(e -> prosesLogin());
    }

    private void prosesLogin() {
        String user = txtUsername.getText();
        String pass = new String(txtPassword.getPassword());

        try {
            Connection conn = Koneksi.getKoneksi();
            String sql = "SELECT * FROM users WHERE username=? AND password=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, user);
            pst.setString(2, pass);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                int idUser = rs.getInt("id");
                
                // Gunakan username sebagai pengganti nama_lengkap
                String nama = rs.getString("username"); 
                
                // Cek apakah kolom 'role' ada, jika tidak ada gunakan default "admin"
                String role = "admin"; 
                try {
                    role = rs.getString("role");
                } catch (Exception e) {
                    role = "admin"; // Fallback jika kolom role belum dibuat
                }

                JOptionPane.showMessageDialog(this, "Login Berhasil sebagai " + role);

                if (role.equalsIgnoreCase("admin") || role.equalsIgnoreCase("kasir")) {
                    new FormAdmin().setVisible(true);
                } else if (role.equalsIgnoreCase("siswa")) {
                    new FormSiswa(idUser, nama).setVisible(true);
                }
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Username atau Password Salah!");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}