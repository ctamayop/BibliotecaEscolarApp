package cl.biblioteca.app;

import cl.biblioteca.view.BibliotecaFrame;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            BibliotecaFrame ventana = new BibliotecaFrame();
            ventana.setVisible(true);
        });
    }
}
