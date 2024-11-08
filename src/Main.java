import view.GFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GFrame mainFrame = new GFrame(" Aeroplane Chess : Conception | 飞行棋：概念 ");
            mainFrame.setVisible(true);
            mainFrame.requestFocus();
        });
    }
}

