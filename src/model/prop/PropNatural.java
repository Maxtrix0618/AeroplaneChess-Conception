package model.prop;

import controller.TextPrinter;
import edu.princeton.cs.algs4.StdRandom;
import model.BlockComponent;
import model.Plane;
import model.Player;
import view.GField;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 可以自然生成的 Prop <br/>
 * 当玩家拾取此道具时，该实例从格子上消失，但并未与玩家建立联系（玩家有变量记录格道具数量）<br/>
 * 当玩家使用道具时，新建一个实例来执行效果
 */
public abstract class PropNatural extends Prop {
    public BlockComponent block;

    private Timer imageTimer;
    private Image[] imageSet;
    private int imageOrder;

    private int durationTime;
    private int passedTime;

    protected int imageNumber = 0;


    protected void loadResource() {
        imageSet = new Image[imageNumber];
        String folderPath = "./resource/image/prop/" + folderName + "/";
        try {
            for (int i = 0; i < imageSet.length; i++) {
                imageSet[i] = ImageIO.read(new File(folderPath + i + ".png"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public PropNatural(GField gameField) {
        super(gameField);
        setSize(48, 48);
        setVisible(false);
        loadResource();
    }


    public void start(BlockComponent block) {
        TextPrinter.println("✦ A " + formalName + " Appears On " + block.getFieldPoint() + "!");
        this.block = block;
        block.prop = this;
        setLocation(block.getLocation());
        setVisible(true);
        playImages();
        durationTime = StdRandom.uniformInt(6, 16);
        passedTime = 0;
    }

    public void stop() {
        if (block != null && block.prop != null)
            block.prop = null;
        this.block = null;
        if (imageTimer != null)
            imageTimer.cancel();
        setVisible(false);
    }

    public void goTime() {
        if (++ passedTime >= durationTime)
            stop();
    }

    @Override
    public void acquired_by(Plane plane) {
        TextPrinter.println("✦ " + formalName + " is Acquired by " + plane.player.getName() + "!");
        super.acquired_by(plane);
        stop();
    }

    @Override
    public void used_by(Player player) {
        super.used_by(player);
        TextPrinter.println("✦ " + formalName + " is Used by " + player.getName() + "!");
    }

    private void playImages() {
        imageTimer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                imageOrder = (imageOrder + 1) % imageSet.length;
                repaint();
            }
        };
        imageTimer.scheduleAtFixedRate(task, 0, 400);
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponents(g);
        g.drawImage(imageSet[imageOrder], 0, 0, getWidth(), getHeight(), this);
    }


}
