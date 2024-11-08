package view.sub;

import javax.swing.*;
import java.awt.*;

public class TextFrame extends JFrame {

    public TextFrame(String FrameName, int width, int height, String text) {
        setTitle(FrameName);
        setSize(width, height);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

        JTextArea textArea = new JTextArea(32, 128);
        textArea.setFont(new Font("", Font.PLAIN, 14));
        textArea.setText(text);
        this.getContentPane().add(new JScrollPane(textArea));

        this.setVisible(true);
    }

    public static void main(String[] args) {
        String text = "This is a test sentence.";
        new TextFrame("Text Demo", 480, 360, text);
    }


}
