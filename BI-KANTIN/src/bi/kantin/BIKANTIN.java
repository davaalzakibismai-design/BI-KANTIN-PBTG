package bi.kantin;

import form.FormLogin;

public class BIKANTIN {

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            FormLogin login = new FormLogin();
            login.setVisible(true);
        });
    }
}