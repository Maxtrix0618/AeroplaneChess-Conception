package view.sub;

import javax.swing.*;
import java.awt.*;

public class RuyiDiceInterface extends JDialog {
    public int P = -1;

    public RuyiDiceInterface(JFrame JF) {
        super(JF, "Ruyi_Dice : Choice Point", true);

        setSize(64*6 + 13, 64 + 36);
        setResizable(false);
        setLayout(null);
        setLocationRelativeTo(null); // Center the window.

        addButtons();
        setVisible(true);
    }


    private void addButtons() {
        for (int i = 1; i <= 6; i++) {
            JButton button = new JButton(String.valueOf(i));
            button.setLocation((i-1)*64, 0);
            button.setSize(64, 64);
            button.setContentAreaFilled(false);
            button.setFocusPainted(false);
            button.setFont(new Font("", Font.BOLD, 20));
            add(button);
            int I = i;
            button.addActionListener((e) -> {
                P = I;
                dispose();
            });

        }
    }




}
