package view;

import edu.princeton.cs.algs4.In;
import view.sub.AIPlayerOptionInterface;
import view.sub.RWPInterface;
import view.sub.SelectionInterface;

import javax.swing.*;
import java.awt.*;

/**
 * Setting Interface
 */
public class SettingInterface extends JDialog {
    private final int WIDTH;
    private final int HEIGHT;
    private final int ButtonWidth;
    private final int ButtonHeight;
    private final GFrame JF;

    public boolean[] AIPlayers;
    public boolean[] RuleSet;
    public boolean[] WeatherSet;
    public boolean[] PropSet;
    public int Selection;


    public SettingInterface(GFrame JF, boolean[] AIPlayers, boolean[] RuleSet, boolean[] WeatherSet, boolean[] PropSet, int Selection) {
        super(JF,"Setting",true);
        this.JF = JF;
        this.WIDTH = 180;
        this.HEIGHT = 280;
        this.ButtonWidth = 120;
        this.ButtonHeight = 36;
        this.AIPlayers = AIPlayers;
        this.RuleSet = RuleSet;
        this.WeatherSet = WeatherSet;
        this.PropSet = PropSet;
        this.Selection = Selection;
        setResizable(false);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null); // Center the window.
        setLayout(null);

        addAISButton();
        addRWPSButton();
//        addSelButton();
        addInformationButton();

        setVisible(true);
    }


    private void addAISButton() {
        JButton button = new JButton("AI.");
        button.setLocation(WIDTH / 8, HEIGHT / 40 * 2);
        button.setSize(ButtonWidth, ButtonHeight);
        button.setFocusPainted(false);
        button.setFont(new Font("", Font.PLAIN, 16));
        add(button);
        button.addActionListener((e) -> {
            AIPlayerOptionInterface AOI = new AIPlayerOptionInterface(JF, AIPlayers);
            AIPlayers = AOI.returnOPS();
        });
    }

    private void addRWPSButton() {
        JButton button = new JButton("R.W.P.");
        button.setLocation(WIDTH / 8, HEIGHT / 40 * 9);
        button.setSize(ButtonWidth, ButtonHeight);
        button.setFocusPainted(false);
        button.setFont(new Font("", Font.PLAIN, 16));
        add(button);
        button.addActionListener((e) -> {
            RWPInterface RWP_I = new RWPInterface(JF, RuleSet, WeatherSet, PropSet);
            RuleSet = RWP_I.RuleSet;
            WeatherSet = RWP_I.WeatherSet;
            PropSet = RWP_I.PropSet;
        });
    }



    private void addSelButton() {
        JButton button = new JButton("Sel.");
        button.setLocation(WIDTH / 8, HEIGHT / 40 * 20);
        button.setSize(ButtonWidth, ButtonHeight);
        button.setFocusPainted(false);
        button.setFont(new Font("", Font.PLAIN, 16));
        add(button);
        button.addActionListener((e) -> {
            SelectionInterface SEI = new SelectionInterface(JF);
            if (SEI.Selection != -1)
                Selection = SEI.Selection;
        });
    }


    private void addInformationButton() {
        JButton button = new JButton("Hint");
        button.setLocation(WIDTH / 8, HEIGHT / 40 * 26);
        button.setSize(ButtonWidth, ButtonHeight);
        button.setFocusPainted(false);
        button.setFont(new Font("", Font.BOLD, 16));
        button.setContentAreaFilled(false);
        add(button);
        button.addActionListener((e) -> {
            try {
                In fin = new In("./resource/text/Hint.txt");
                String message = fin.readAll();
                JOptionPane.showMessageDialog(null, message, "Hint", JOptionPane.INFORMATION_MESSAGE);

            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(null, "Cannot find the Hint file!\nPath: \n    ./resource/text/Hint.txt    ", "File loss", JOptionPane.INFORMATION_MESSAGE);
//                ex.printStackTrace();
            }
        });
    }



}
