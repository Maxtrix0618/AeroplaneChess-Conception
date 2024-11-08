package model.prop;

import controller.TextPrinter;
import model.Plane;
import sound.SoundPlayer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MissileComponent extends JComponent {
    private Plane user;
    private Plane target;

    private Image U, R, D, L;
    private Image image;

    private void loadResource() {
        String folderPath = "./resource/image/model/missile/";
        try {
            U = ImageIO.read(new File(folderPath + "U.png"));
            R = ImageIO.read(new File(folderPath + "R.png"));
            D = ImageIO.read(new File(folderPath + "D.png"));
            L = ImageIO.read(new File(folderPath + "L.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public MissileComponent() {
        this.user = null;
        this.target = null;
        setSize(36, 36);
        setVisible(false);
        loadResource();
    }


    public void attack(Plane user, Plane target) {
        this.user = user;
        this.target = target;
        setVisible(true);
        setLocation(user.block.getLocation());
        changePosture();
        missileMove();
    }

    private void missileMove() {
        Point I = user.getLocation();
        Point F = target.getLocation();
        int dx = (F.x - I.x) / 20;
        int dy = (F.y - I.y) / 20;
        java.util.Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            int t = 0;
            @Override
            public void run() {
                if (t < 20) {
                    t ++;
                    setLocation(getX() + dx, getY() + dy);
                    return;
                }
                timer.cancel();
                strike();
            }
        };
        timer.scheduleAtFixedRate(task, 0, 5);
    }


    private void strike() {
        setVisible(false);
        TextPrinter.println("✦ One plane of " + target.player.getName() + " is hit by " + user.player.getName() + "!");
        SoundPlayer.playSound("hit");
        target.GoBackFor(8);
        this.user = null;
        this.target = null;
    }


    private void changePosture() {
        int Dx = target.getX() - user.getX();
        int Dy = target.getY() - user.getY();
        if (Dy >= Dx) {
            if (Dy >= 0) image = U;
            else image = D;
        } else {
            if (Dx >= 0) image = R;
            else image = L;
        }
        repaint();
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponents(g);
        g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
    }


}
