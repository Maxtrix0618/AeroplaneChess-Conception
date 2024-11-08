package view.sub;

import edu.princeton.cs.algs4.StdOut;
import model.Player;
import model.weather.Rainfall;
import model.weather.Weather;
import sound.SoundPlayer;

import javax.swing.*;
import java.awt.*;

public class BottleOfWeatherInterface extends JDialog {
    private final ImageIcon[] imageIcons = new ImageIcon[4];
    private JLabel imageLabel;

    private final Weather weather;
    public int C = -1;

    public BottleOfWeatherInterface(JFrame JF, Player player, Weather weather) {
        super(JF, " " + player + " | Confirm to Use : Bottle_Of_Weather", true);

        setSize(340, 200);
        setResizable(false);
        setLayout(null);
        setLocationRelativeTo(null); // Center the window.

        this.weather = weather;

        loadImages();
        addImage();
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
            if (C == -1) {
                C = 1;
                SoundPlayer.playSound("3tone_2");
                setTitle("Bottle_Rainfall | Acquired");
                setImage(weather);
                Confirm.setLocation(200, 55);
                Cancel.setVisible(false);

            }
            else dispose();
        });
    }


    private void addImage() {
        imageLabel = new JLabel(imageIcons[0]);
        imageLabel.setLocation(15, 10);
        imageLabel.setSize(imageIcons[0].getIconWidth(), imageIcons[0].getIconHeight());
        imageLabel.setVisible(true);
        add(imageLabel);
    }

    private void setImage(Weather weather) {
        int I = 0;
        switch (weather.weatherName) {
            case "rainfall" : I = 1; break;
            case "thunderstorm" : I = 2; break;
            case "hurricane" : I = 3; break;
        }
        imageLabel.setIcon(imageIcons[I]);
        imageLabel.setSize(imageIcons[I].getIconWidth(), imageIcons[I].getIconHeight());
    }



    private void loadImages() {
        String imageFolder = "./resource/image/prop/";
        imageIcons[0] = new ImageIcon(imageFolder + "/bottle_of_weather/0.png");
        imageIcons[1] = new ImageIcon(imageFolder + "/bottle_of_weather/R.png");
        imageIcons[2] = new ImageIcon(imageFolder + "/bottle_of_weather/T.png");
        imageIcons[3] = new ImageIcon(imageFolder + "/bottle_of_weather/H.png");
    }


    public static void main(String[] args) {
        BottleOfWeatherInterface BWI = new BottleOfWeatherInterface(null, Player.RED, new Rainfall());
        StdOut.println(BWI.C);

    }



}
