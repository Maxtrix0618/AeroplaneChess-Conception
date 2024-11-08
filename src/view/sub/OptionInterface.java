package view.sub;

import javax.swing.*;
import java.awt.event.ActionListener;

/**
 * Option Switch Interface
 */
public class OptionInterface extends JDialog {
    JPanel jp = new JPanel();

    private final JCheckBox[] JCheckBoxes;
    public boolean[] OptionSet;

    public OptionInterface(JFrame JF, int width, int height, String title, String[] OptionNames, boolean[] options) {
        super(JF,"Option",true);
        setTitle(title);
        setResizable(false);
        setSize(width, height);
        setLocationRelativeTo(null); // Center the window.
//        setLayout(null);

        this.OptionSet = options;
        this.JCheckBoxes = new JCheckBox[options.length];

        for (int i = 0; i < options.length; i++) {
            JCheckBoxes[i] = new JCheckBox(OptionNames[i], OptionSet[i]);
        }

        ActionListener actionListener = e -> {
            for (int i = 0; i < options.length; i++)
                OptionSet[i] = JCheckBoxes[i].isSelected();
        };
        for (JCheckBox box : JCheckBoxes) {
            box.addActionListener(actionListener);
            box.setFocusPainted(false);
            jp.add(box);
        }

        add(jp);
        setVisible(true);
    }


    public boolean[] returnOPS() {
        return OptionSet;
    }

}
