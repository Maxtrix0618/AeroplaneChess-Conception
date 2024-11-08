package model.weather;

import edu.princeton.cs.algs4.StdRandom;
import model.BlockComponent;
import model.Plane;
import model.Player;
import sound.SoundPlayer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public abstract class Weather extends JComponent {
    public BlockComponent block;
    public Player player;

    private Timer imageTimer;
    private Image[] imageSet;
    private int imageOrder;

    private int durationTime;
    private int passedTime;

    protected int imageNumber = 0;
    public String weatherName = "";

    protected void loadResource() {
        imageSet = new Image[imageNumber];
        String folderPath = "./resource/image/weather/" + weatherName + "/";
        try {
            for (int i = 0; i < imageSet.length; i++) {
                imageSet[i] = ImageIO.read(new File(folderPath + i + ".png"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Weather() {
        this.player = null;
        this.block = null;
        setSize(48, 48);
        setVisible(false);
        loadResource();
    }

    /**
     * 遭遇天气事件
     */
    public abstract int event();

    private void playImages() {
        imageTimer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                imageOrder = (imageOrder + 1) % imageSet.length;
                repaint();
            }
        };
        imageTimer.scheduleAtFixedRate(task, 0, 200);
    }

    public void start(BlockComponent block) {
        this.block = block;
        block.weather = this;
        setLocation(block.getLocation());
        setVisible(true);
        playImages();
        tryFallOnPlane();
        durationTime = StdRandom.uniformInt(4, 13);
        passedTime = 0;
    }


    public void stop() {
        block.weather = null;
        this.block = null;
        setVisible(false);
        imageTimer.cancel();
    }

    public void goTime() {
        if (++ passedTime >= durationTime)
            stop();
    }

    private void tryFallOnPlane() {
        if (!block.planes.isEmpty())
            for (Plane p : block.planes)
                p.tryWeather();
    }

    public abstract void returnBackProp(Player player);

    public void capturedBy(Player player) {
        SoundPlayer.playSound("3tone");
        this.player = player;
        stop();
    }


    public abstract void startSound();

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponents(g);
        g.drawImage(imageSet[imageOrder], 2, 1, getWidth()-4, getHeight()-4, this);
    }


}
