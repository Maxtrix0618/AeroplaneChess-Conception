package view.sub;

import model.Player;

import javax.swing.*;
import java.awt.*;

public class PropUseInterface extends JDialog {
    public int C = -1;

    public PropUseInterface(JFrame JF, Player player, String formalName, String folderName) {
        super(JF, " " + player + " | Confirm to Use : " + formalName, true);

        setSize(340, 200);
        setResizable(false);
        setLayout(null);
        setLocationRelativeTo(null); // Center the window.

        addImage(folderName);
        addButtons();
        setVisible(true);
    }



    private void addButtons() {
        JButton Cancel = new JButton("✖");
        Cancel.setLocation(200, 90);
        Cancel.setSize(72, 48);
        Cancel.setContentAreaFilled(false);
        Cancel.setFocusPainted(false);
        Cancel.setFont(new Font("", Font.PLAIN, 20));
        add(Cancel);
        Cancel.addActionListener((e) -> {
            C = 0;
            dispose();
        });

        JButton Confirm = new JButton("✔");
        Confirm.setLocation(200, 20);
        Confirm.setSize(72, 48);
        Confirm.setContentAreaFilled(false);
        Confirm.setFocusPainted(false);
        Confirm.setFont(new Font("", Font.PLAIN, 20));
        add(Confirm);
        Confirm.addActionListener((e) -> {
            C = 1;
            dispose();
        });
    }


    private void addImage(String folderName) {
        ImageIcon imageIcon = new ImageIcon("./resource/image/prop/" + folderName + "/0.png");
        JLabel imageLabel = new JLabel(imageIcon);
        imageLabel.setLocation(15, 10);
        imageLabel.setSize(imageIcon.getIconWidth(), imageIcon.getIconHeight());
        imageLabel.setVisible(true);
        add(imageLabel);
    }



}
