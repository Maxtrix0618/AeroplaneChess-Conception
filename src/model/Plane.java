package model;

import controller.TextPrinter;
import edu.princeton.cs.algs4.StdRandom;
import model.weather.Weather;
import sound.SoundPlayer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 飞机（棋子）
 */
public class Plane extends JComponent {
    public Plane[] AllPlanes;
    public BlockComponent block;
    public Player player;

    private int dicePoint;
    private int passedPoint;
    private int extraPoint;

    private boolean allowJump;
    private boolean normalStep; // whether the movement isn't the forced movement

    private Image U, R, D, L;
    private Image image;

    private boolean circleReverse;
    private boolean finalReverse;    // 仅用于终程回返
    public boolean active_camouflage;
    private final ArrayList<BlockComponent> blocksToRepaint;

    private void loadResource() {
        String folderPath = "./resource/image/plane/";
        folderPath += player.getName().charAt(0);
        try {
            U = ImageIO.read(new File(folderPath + "U.png"));
            R = ImageIO.read(new File(folderPath + "R.png"));
            D = ImageIO.read(new File(folderPath + "D.png"));
            L = ImageIO.read(new File(folderPath + "L.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Plane(BlockComponent block, Player player, Plane[] planes) {
        this.AllPlanes = planes;
        this.player = player;
        this.block = block;
        this.active_camouflage = false;
        this.blocksToRepaint = new ArrayList<>();
        block.planes.add(this);
        setSize(48, 48);
        setVisible(false);
        loadResource();
    }
    public void initial() {
        setVisible(true);
        setLocation(block.getLocation());
        changePosture(block);
    }

    private void changePosture(BlockComponent block) {
        int p = block.planePosture;
        if ((block.EntryPoint || block.FlyPoint) && block.player == player)
            p = (p == 4) ? 1 : p+1;
        switch (p) {
            case 1: image = U; break;
            case 2: image = R; break;
            case 3: image = D; break;
            case 4: image = L; break;
        }
        repaint();
    }

    public void GoFor(int point) {
        normalStep = true;
        finalReverse = false;
        circleReverse = false;
        dicePoint = point;
        passedPoint = 0;
        extraPoint = 0;
        GoAHead();
    }

    public void GoBackFor(int point) {
        this.normalStep = false;
        turnBackFor(point);
        GoAHead();
    }
    public void turnBackFor(int point) {
        finalReverse = false;
        circleReverse = true;
        dicePoint = point;
        passedPoint = 0;
        extraPoint = 0;
    }

    public void GoAHead() {
        allowJump = true;
        if (finalReverse) {
            GoReversely(); return;
        }
        if (circleReverse && block.CircleLast != null) {
            moveTo(block.CircleLast); return;
        }
        if (block.ColorNext != null && player == block.ColorNext.player) {
            moveTo(block.ColorNext); return;
        }
        if (block.CircleNext != null) {
            moveTo(block.CircleNext); return;
        }
        TextPrinter.println("× No next position.");
    }

    /**
     * 强制跳跃到某格
     * @param normalStep 执行完此次跳跃后是否进入下一轮
     */
    public void forceTo(BlockComponent position, boolean normalStep) {
        this.normalStep = normalStep;
        if (normalStep)
            extraPoint ++;
        moveTo(position);
    }


    /**
     * 制造动效，其后调用ComeTo()，最后检查是否有剩余步数（有则再调GoToNext()）
     */
    public void moveTo(BlockComponent position) {
        Point I = block.getLocation();
        Point F = position.getLocation();
        int dx = (F.x - I.x) / 20;
        int dy = (F.y - I.y) / 20;
        Timer timer = new Timer();
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
                comeTo(position);
                if (passedPoint < dicePoint)
                    GoAHead();
            }
        };
        timer.scheduleAtFixedRate(task, 0, 10);
    }


    /**
     * 使棋子移动到position
     */
    public void comeTo(BlockComponent position) {
        boolean backToTarmac = (!block.Destination && position.Tarmac);
        blocksToRepaint.add(block);
        block.planes.remove(this);
        block = position;
        block.planes.add(this);
        passedPoint ++;
        setLocation(block.getLocation());
        changePosture(block);
        if (passedPoint >= dicePoint + extraPoint) {
            SoundPlayer.playSound("dong");
            if (backToTarmac) TextPrinter.println("✈ One Plane of " + player.getName() + " returns to the Tarmac!");
            else TextPrinter.println("✈ One " + player.getName() + " Plane goes to " + block);

            if (block.Destination) {
                TextPrinter.println("✈ One " + player.getName() + " Plane reaches the Destination!");
                setActiveCamouflage(false);
                if (block.planes.size() < 4)
                    SoundPlayer.playSound("5tone_2");
            }
        }
        else {
            if (block.mouseController.gameField.Rule_Set[3] && position.Destination)
                finalReverse = true;
        }
        block.reserve = false;      // reserve isn't reverse!
        block.mouseController.gameField.tryAddWeatherToField();
        tryAcquireProp();
        tryWeather();
        tryCrashPlane();
        tryWin();
        tryJump_Fly();
        tryGoNextRound();
    }

    private void tryGoNextRound() {
//        StdOut.println(normalStep + " | " + passedPoint + " | " + dicePoint + " | " + extraPoint);
        if (!normalStep || passedPoint < dicePoint + extraPoint)
            return;
        repaintBlocksThatToRepaint();
        block.mouseController.tryGoToTheNextRound();
    }

    private void tryAcquireProp() {
        if (passedPoint < dicePoint && !block.mouseController.gameField.Rule_Set[6]) return;
        if (block.prop != null) {
            block.prop.acquired_by(this);
        }
    }


    public void tryWeather() {
        if (block.weather == null)
            return;
        Weather weather = block.weather;
        if (block.mouseController.gameField.tryUseBottle(weather) > 0) {
            block.weather.capturedBy(player);
            return;
        }

        switch (weather.weatherName) {
            case "rainfall": dicePoint = passedPoint; extraPoint = 0; allowJump = false; weather.event(); break;
            case "thunderstorm": dicePoint = passedPoint; extraPoint = 0; allowJump = false; returnToTarmac(true); weather.event(); break;
            case "hurricane": if (passedPoint == dicePoint || passedPoint == dicePoint + extraPoint)
                                    {allowJump = false; randomlyTransferToCircle(); weather.event(); break;}
        }
    }

    private void randomlyTransferToCircle() {
        ArrayList<BlockComponent> circleBlocks = block.mouseController.gameField.CircleBlocks;
        forceTo(circleBlocks.get(StdRandom.uniformInt(0, circleBlocks.size())), true);
    }


    private void tryJump_Fly() {
        if (passedPoint < dicePoint || circleReverse) return;
        if (block.player != null && block.player == player && block.CircleBlock) {
            if (block.FlyPoint) {
                goFly();
            }
            else if (allowJump) {
                allowJump = false;
                tryJump();
            }
        }
    }


    private static final int[] ria = new int[]{0, -1, 0, 1};
    private static final int[] cia = new int[]{-1, 0, 1, 0};

    /**
     * 入轨航路的终程回返
     */
    private void GoReversely() {
        int order = (block.getSTATE() - 1) % 10;
        moveTo(block.Maze[block.getFieldPoint().getX() + ria[order]][block.getFieldPoint().getY() + cia[order]]);
    }

    private static final int[] ris = new int[]{3, 0, -3, 0};
    private static final int[] cis = new int[]{0, -3, 0, 3};

    /**
     * 飞跃跳跃线
     */
    private void goFly() {
        int ri = ris[block.getSTATE()-1];
        int ci = cis[block.getSTATE()-1];
        BlockComponent middleBlock = block.Maze[block.getFieldPoint().getX() + ri][block.getFieldPoint().getY()+ ci];
        BlockComponent finalBlock = block.Maze[block.getFieldPoint().getX() + 2*ri][block.getFieldPoint().getY()+ 2*ci];
        ArrayList<Plane> EPAs = middleBlock.EnemyPlanes(player);
        if (!block.mouseController.gameField.Rule_Set[2]) {
            forceTo(finalBlock, true);
            return;
        }
        if (EPAs.size() <= 1) {
            forceTo(finalBlock, true);
            for (Plane p : EPAs)
                p.returnToTarmac(false);
            TextPrinter.println("✈ Fly through the Air Line!");
        } else {
            TextPrinter.println("× Over 2 enemy planes(inclusive) stay on the cross road. Cannot fly through!");
            SoundPlayer.playSound("warn");
        }
    }

    private void tryJump() {
        if (!block.EntryPoint) {
            BlockComponent d = block;
            for (int i = 0; i < 4; i++)
                d = d.CircleNext;
            forceTo(d, true);
        }
    }

    /**
     * 撞子
     */
    private void tryCrashPlane() {
        if (passedPoint < dicePoint) return;
        ArrayList<Plane> EPAs = block.EnemyPlanes(player);
        if (EPAs.size() >= 2) {
            this.returnToTarmac(true);
            SoundPlayer.playSound("crash");
        }
        if (EPAs.size() >= 1 && EPAs.size() <= 2) {
            if (block.mouseController.gameField.tryUseDeflectionField(EPAs.get(0).player, block) > 0) {
                this.turnBackFor(1);
                return;
            }
            for (Plane p : EPAs) {
                p.returnToTarmac(false);
                SoundPlayer.playSound("crash");
            }
        }


    }


    /**
     * 回到停机坪
     * @param normalStep 执行完后是否进入下一回合
     */
    public void returnToTarmac(boolean normalStep) {
        setActiveCamouflage(false);
        if (!normalStep)
            extraPoint = 0;
        for (BlockComponent[] b_ : block.Maze)
            for (BlockComponent b : b_)
                if (b.Tarmac && b.player == player && b.planes.isEmpty() && !b.reserve) {
                    b.reserve = true;
                    forceTo(b, normalStep);
                    return;
                }
    }

    private void tryWin() {
        if (passedPoint < dicePoint) return;
        for (Plane p : AllPlanes)
            if (p.player == player && !p.block.Destination)
                return;
        player.INGAME = false;
    }



    private void repaintBlocksThatToRepaint() {
        for (BlockComponent b : blocksToRepaint)
            b.repaintPlanes();
        blocksToRepaint.clear();
    }

    public void setActiveCamouflage(boolean AC) {
        active_camouflage = AC;
        repaint();
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponents(g);
        if (active_camouflage && block.mouseController.gameField.Current != player)
            return;

        int a = block.planes.indexOf(this) * 5;
        int p = block.planePosture;
        if (block.EntryPoint && block.player == player)
            p = (p == 4) ? 1 : p+1;
        switch (p) {
            case 1: g.drawImage(image, 0, a, getWidth(), getHeight(), this); break;
            case 2: g.drawImage(image, a, 0, getWidth(), getHeight(), this); break;
            case 3: g.drawImage(image, 0, -a, getWidth(), getHeight(), this); break;
            case 4: g.drawImage(image, -a, 0, getWidth(), getHeight(), this); break;
        }
    }

    @Override
    public String toString() {
        return (player.getName().charAt(0) + "✈(" + block.getFieldPoint().getX() + "," + block.getFieldPoint().getY() + ")");
    }


}
