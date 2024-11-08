package view.sub;

import javax.swing.*;
import java.awt.*;

public class SelectionInterface extends JDialog {
    private final JTextField textField = new JTextField(20);
    private final JComboBox<String> comboBox = new JComboBox<>();

    public int Selection = -1;    // -1 is exiting without choosing.

    public SelectionInterface(JFrame JF){
        super(JF,"Selection",true);
        setTitle("Selection");
        comboBox.addItem(" Selection-1 ");
        comboBox.addItem(" Selection-2 ");

        JButton button = new JButton("Confirm");
        button.addActionListener(e -> {
            String Algo = (String) comboBox.getSelectedItem();
            textField.setText("Now Selection: " + Algo);

            if (Algo != null) {
                switch (Algo) {
                    case " Selection-1 ":   Selection = 1; break;
                    case " Selection-2 ":  Selection = 2; break;
                }
            }

            JOptionPane.showMessageDialog(this, "Setting successfully!");
        });
        comboBox.addActionListener(e -> textField.setText("index:" + comboBox.getSelectedIndex() + " " + comboBox.getSelectedItem()));

        comboBox.setEditable(true);
        textField.setEditable(false);
        setLocationRelativeTo(null);

        setLayout(new FlowLayout());
        add(comboBox);
        add(button);
        add(textField);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        pack();
        setVisible(true);
    }


}

