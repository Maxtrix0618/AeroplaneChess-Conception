package view.sub;

import view.GFrame;

import javax.swing.*;
import java.awt.event.ActionListener;

/**
 * Option Switch Interface
 */
public class AIPlayerOptionInterface extends JDialog {
    JPanel jp = new JPanel();

    public boolean RED;
    public boolean YELLOW;
    public boolean BLUE;
    public boolean GREEN;

    public AIPlayerOptionInterface(GFrame JF, boolean[] options) {
        super(JF,"Option",true);
        setTitle("AI Player(s)");
        setResizable(false);
        setSize(100, 200);
        setLocationRelativeTo(null); // Center the window.
//        setLayout(null);

        this.RED = options[0];
        this.YELLOW = options[1];
        this.BLUE = options[2];
        this.GREEN = options[3];

        JCheckBox CHKBox1 = new JCheckBox(" RED ", RED);
        JCheckBox CHKBox2 = new JCheckBox(" YELLOW ", YELLOW);
        JCheckBox CHKBox3 = new JCheckBox(" BLUE ", BLUE);
        JCheckBox CHKBox4 = new JCheckBox(" GREEN ", GREEN);

        ActionListener actionListener = e -> {
            RED = CHKBox1.isSelected();
            YELLOW = CHKBox2.isSelected();
            BLUE = CHKBox3.isSelected();
            GREEN = CHKBox4.isSelected();
        };
        CHKBox1.addActionListener(actionListener);
        CHKBox2.addActionListener(actionListener);
        CHKBox3.addActionListener(actionListener);
        CHKBox4.addActionListener(actionListener);
        CHKBox1.setFocusPainted(false);
        CHKBox2.setFocusPainted(false);
        CHKBox3.setFocusPainted(false);
        CHKBox4.setFocusPainted(false);

        jp.add(CHKBox1);
        jp.add(CHKBox2);
        jp.add(CHKBox3);
        jp.add(CHKBox4);

        add(jp);
        setVisible(true);
    }


    public boolean[] returnOPS() {
        boolean[] options = new boolean[4];
        options[0] = RED;
        options[1] = YELLOW;
        options[2] = BLUE;
        options[3] = GREEN;
        return options;
    }

}
