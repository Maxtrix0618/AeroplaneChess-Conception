package model;

import controller.MouseController;
import controller.TextPrinter;
import edu.princeton.cs.algs4.StdRandom;
import sound.SoundPlayer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 骰子
 */
public class Dice extends JComponent {
    public final MouseController mouseController;
    private static Image P1, P2, P3, P4, P5, P6;
    private Image image;

    public boolean throwing;
    public int point;

    public void loadResource() throws IOException {
        point = StdRandom.uniformInt(1, 7);
        String folderPath = "./resource/image/dice/";
        P1 = ImageIO.read(new File(folderPath + "1.png"));
        P2 = ImageIO.read(new File(folderPath + "2.png"));
        P3 = ImageIO.read(new File(folderPath + "3.png"));
        P4 = ImageIO.read(new File(folderPath + "4.png"));
        P5 = ImageIO.read(new File(folderPath + "5.png"));
        P6 = ImageIO.read(new File(folderPath + "6.png"));
    }

    private static final int[] r_16 = new int[]{2, 3, 4, 5};
    private static final int[] r_25 = new int[]{1, 3, 4, 6};
    private static final int[] r_34 = new int[]{1, 2, 5, 6};

    public Dice(MouseController mouseController) {
        this.mouseController = mouseController;
        try {
            loadResource();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int SIZE = 48;
        setSize(SIZE, SIZE);
        setVisible(false);
    }


    /**
     * 投掷骰子
     * @param b 投掷骰子的棋盘位置对应格
     * @return 点数
     */
    public int throwDice(BlockComponent b) {
        throwing = true;
        setLocation(b.getLocation());
        setVisible(true);

        int rollTimes = StdRandom.uniformInt(3, 10);
        int[] rolls = new int[rollTimes];
        for (int i = 0; i < rollTimes; i++) {
            rolls[i] = roll();
        }
        // 预定的骰子点数
        int reservePoint = mouseController.gameField.reserveDicePoint;
        if (reservePoint > 0) {
            rolls[rollTimes - 1] = reservePoint;
            point = reservePoint;
            mouseController.gameField.reserveDicePoint = -1;
        }
        SoundPlayer.playSoundOnTrack("dice", 1);

        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            int r = 0;
            @Override
            public void run() {
                if (r < rollTimes) {
                    showPoint(rolls[r++]);
                    return;
                }
                timer.cancel();
                SoundPlayer.stopSoundOnTrack(1);
                TextPrinter.println("▩ Dice point: " + point);

                // 停滞一段时间以展示骰子点数
                Timer timer = new Timer();
                TimerTask task = new TimerTask() {
                    int t = 0;
                    @Override
                    public void run() {
                        if (t < 10) {
                            t ++;
                            return;
                        }
                        timer.cancel();
                        throwing = false;
                        mouseController.SKIP_T6 = false;
                        mouseController.tryEvents();
                    }
                };
                timer.scheduleAtFixedRate(task, 0, 50);
            }
        };
        timer.scheduleAtFixedRate(task, 0, 80);
        return point;
    }


    private int roll() {
        int[] possibleNextPoints = new int[]{0, 0, 0, 0};
        switch (point) {
            case 1: case 6: possibleNextPoints = r_16; break;
            case 2: case 5: possibleNextPoints = r_25; break;
            case 3: case 4: possibleNextPoints = r_34; break;
        }
        point = possibleNextPoints[StdRandom.uniformInt(0, 4)];
        return point;
    }

    private void showPoint(int thePoint) {
        switch (thePoint) {
            case 1: image = P1; break;
            case 2: image = P2; break;
            case 3: image = P3; break;
            case 4: image = P4; break;
            case 5: image = P5; break;
            case 6: image = P6; break;
        }
        repaint();
    }

    public void hideDice() {
        setVisible(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponents(g);
        g.drawImage(image, 2, 2, getWidth()-4, getHeight()-4, this);
    }

}
