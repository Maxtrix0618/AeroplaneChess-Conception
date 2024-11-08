package model;

import edu.princeton.cs.algs4.StdOut;
import model.prop.PropNatural;
import model.weather.Weather;
import view.FieldPoint;
import controller.MouseController;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


/**
 * Block
 */
public class BlockComponent extends JComponent {
    public final MouseController mouseController;
    private final FieldPoint fieldPoint;
    public final BlockComponent[][] Maze;
    private BlockState blockState;
    public Player player;

    public BlockComponent CircleLast;
    public BlockComponent CircleNext;
    public BlockComponent ColorNext;
    public boolean Tarmac;      // 停机坪
    public boolean TakePoint;   // 起飞点
    public boolean EntryPoint;  // 入轨点
    public boolean CircleBlock; // 循环航线
    public boolean FlyPoint;    // 飞行点
    public boolean Destination; // 终点
    public Color AirLine;       // 航线格（承色）

    private int STATE;
    public int planePosture;    // 飞行器姿态：1-U,2-R,3-D,4-L.
    public boolean reserve;     // 预留的位置
    private boolean isSP;       // 局部填充格
    public boolean touched;
    private final boolean mouseValid;

    public Weather weather;             // 格子上的天气
    public PropNatural prop;            // 格子上的道具
    public ArrayList<Plane> planes;     // 格子上的飞行器集合


    public BlockComponent(FieldPoint fieldPoint, Point location, MouseController mouseController, int State, int size, BlockComponent[][] Maze) {
        enableEvents(AWTEvent.MOUSE_EVENT_MASK);
        setLocation(location);
        setSize(size, size);

        this.mouseController = mouseController;
        this.fieldPoint = fieldPoint;
        this.STATE = State;
        this.Maze = Maze;
        this.touched = false;
        this.mouseValid = true;
        this.isSP = false;

        this.CircleLast = this;
        this.CircleNext = this;
        this.ColorNext = null;
        this.Tarmac = this.TakePoint = this.EntryPoint = this.Destination = this.CircleBlock = this.FlyPoint = false;
        this.AirLine = null;
        this.player = null;
        this.reserve = false;
        this.weather = null;
        this.prop = null;
        this.planes = new ArrayList<>();

        loadImage();
        defineSP();
        refreshBlockState();
    }

    private void defineSP() {
        for (int k = 0; k < 9; k++) {
            if (getFieldPoint().getX() == sp_xs[k] && getFieldPoint().getY() == sp_ys[k]) {
                isSP = true;
                break;
            }
        }
    }

    private void refreshBlockState() {
        switch (STATE) {
            case 0: blockState = BlockState.Blank; player = null; break;
            case -1: blockState = BlockState.DENY; player = null; break;
            case -2: blockState = BlockState.LineBlock; player = null; break;
            case 1: blockState = BlockState.RED; player = Player.RED; break;
            case 2: blockState = BlockState.YELLOW; player = Player.YELLOW; break;
            case 3: blockState = BlockState.BLUE; player = Player.BLUE; break;
            case 4: blockState = BlockState.GREEN; player = Player.GREEN; break;
            case 11: blockState = BlockState.RED_H; player = Player.RED; break;
            case 12: blockState = BlockState.YELLOW_H; player = Player.YELLOW; break;
            case 13: blockState = BlockState.BLUE_H; player = Player.BLUE; break;
            case 14: blockState = BlockState.GREEN_H; player = Player.GREEN; break;
            case 21: blockState = BlockState.RED_L; player = Player.RED; break;
            case 22: blockState = BlockState.YELLOW_L; player = Player.YELLOW; break;
            case 23: blockState = BlockState.BLUE_L; player = Player.BLUE; break;
            case 24: blockState = BlockState.GREEN_L; player = Player.GREEN; break;
        }
    }

    public void setSTATE(int state) {
        STATE = state;
        refreshBlockState();
        repaint();
    }
    public int getSTATE() {
        return STATE;
    }

    public BlockState getBlockState() {return blockState;}
    public FieldPoint getFieldPoint() {
        return fieldPoint;
    }


    /**
     * @param e response for mouse event <br>
     */
    @Override
    protected void processMouseEvent(MouseEvent e) {
        super.processMouseEvent(e);
        if (!mouseValid) return;

        if (e.getID() == MouseEvent.MOUSE_PRESSED && e.getButton() == MouseEvent.BUTTON1)   // BUTTON1 左键
            tryOnClick_L();
        if (e.getID() == MouseEvent.MOUSE_PRESSED && e.getButton() == MouseEvent.BUTTON3)   // BUTTON3 右键
            mouseController.onClick_R(this);
        if (e.getID() == MouseEvent.MOUSE_RELEASED && e.getButton() == MouseEvent.BUTTON1)
            mouseController.Mouse_Pressed_L = false;
        if (e.getID() == MouseEvent.MOUSE_RELEASED && e.getButton() == MouseEvent.BUTTON3)
            mouseController.Mouse_Pressed_R = false;

        if (e.getID() == MouseEvent.MOUSE_ENTERED) {
            touched = true;
            mouseController.onTouch(this);
        } else if ((e.getID() == MouseEvent.MOUSE_EXITED)) {
            touched = false;
            mouseController.offTouch(this);
        }
    }

    private void tryOnClick_L() {
        if (mouseController.gameField.Current != null && !mouseController.gameField.Current.AI)
            mouseController.onClick_L(this);
    }


    public BlockComponent followCircle(int f) {
        BlockComponent b = this;
        for (int i = 0; i < f; i++)
            b = b.CircleNext;
        return b;
    }


    public ArrayList<Plane> EnemyPlanes(Player self) {
        ArrayList<Plane> enemyPlanes = new ArrayList<>();
        for (Plane p : planes)
            if (p != null && p.player != self)
                enemyPlanes.add(p);
        return enemyPlanes;
    }


    private final int[] sp_xs = new int[]{7, 4, 4, 10, 10, 6, 6, 8, 8};
    private final int[] sp_ys = new int[]{7, 4, 10, 4, 10, 6, 8, 6, 8};
    private final Image[] sp_Is = new Image[9];


    /**
     * repaint this block
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponents(g);

        g.setColor(blockState.getColor());
        if (touched) {
            if (blockState == BlockState.DENY)
                g.setColor(new Color(30, 30, 30, 255));
            else if (blockState == BlockState.Blank) {
                g.setColor(new Color(210, 210, 210));
            }
        }

//        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        g.fillRect(1, 1, this.getWidth() -1, this.getHeight() -1);

        if (isSP)
            for (int k = 0; k < 9; k++) {
                if (getFieldPoint().getX() == sp_xs[k] && getFieldPoint().getY() == sp_ys[k])
                    g.drawImage(sp_Is[k], 1, 1, getWidth(), getHeight(), this);
            }

        if (AirLine != null) {
            g.setColor(AirLine);
            if (AirLine == BlockState.BLUE.getColor() || AirLine == BlockState.RED.getColor())
                g.drawLine(getWidth()/2, 0, getWidth()/2, getHeight());
            if (AirLine == BlockState.YELLOW.getColor() || AirLine == BlockState.GREEN.getColor())
                g.drawLine(0, getHeight()/2, getWidth(), getHeight()/2);
        }


        if (STATE > 0 && STATE < 20) {
            g.setColor(new Color(210, 210, 210));
            g.fillOval(4, 4, this.getWidth()-8, this.getHeight()-8);
        }
        if (TakePoint) {
            g.setColor(new Color(236, 236, 236));
            g.fillOval(12, 12, this.getWidth()-24, this.getHeight()-24);
        }

        if (touched && STATE > 0) {
            g.setColor(new Color(210, 210, 210));
            drawBox(g);
        }

    }

    private void drawBox(Graphics g) {
        g.drawLine(this.getWidth() / 16, this.getHeight() / 16, this.getWidth() / 16, this.getHeight() / 16 * 5);
        g.drawLine(this.getWidth() / 16, this.getHeight() / 16, this.getWidth() / 16 * 5, this.getHeight() / 16);
        g.drawLine(this.getWidth() / 16 * 15, this.getHeight() / 16, this.getWidth() / 16 * 11, this.getHeight() / 16);
        g.drawLine(this.getWidth() / 16 * 15, this.getHeight() / 16, this.getWidth() / 16 * 15, this.getHeight() / 16 * 5);
        g.drawLine(this.getWidth() / 16, this.getHeight() / 16 * 15, this.getWidth() / 16, this.getHeight() / 16 * 11);
        g.drawLine(this.getWidth() / 16, this.getHeight() / 16 * 15, this.getWidth() / 16 * 5, this.getHeight() / 16 * 15);
        g.drawLine(this.getWidth() / 16 * 15, this.getHeight() / 16 * 15, this.getWidth() / 16 * 11, this.getHeight() / 16 * 15);
        g.drawLine(this.getWidth() / 16 * 15, this.getHeight() / 16 * 15, this.getWidth() / 16 * 15, this.getHeight() / 16 * 11);
    }


    public void loadImage() {
        try {
            sp_Is[0] = ImageIO.read(new File("./resource/image/block/C.png"));
            sp_Is[1] = ImageIO.read(new File("./resource/image/block/LU.png"));
            sp_Is[2] = ImageIO.read(new File("./resource/image/block/RU.png"));
            sp_Is[3] = ImageIO.read(new File("./resource/image/block/LD.png"));
            sp_Is[4] = ImageIO.read(new File("./resource/image/block/RD.png"));

            sp_Is[5] = ImageIO.read(new File("./resource/image/block/lus.png"));
            sp_Is[6] = ImageIO.read(new File("./resource/image/block/rus.png"));
            sp_Is[7] = ImageIO.read(new File("./resource/image/block/lds.png"));
            sp_Is[8] = ImageIO.read(new File("./resource/image/block/rds.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void repaintPlanes() {
        for (Plane p : planes)
            if (p != null)
                p.repaint();
    }



    public void printSelf() {
        StdOut.println(this);
        StdOut.print("  Planes [ ");
        for (Plane p : planes) {
            StdOut.print(p);
            StdOut.print(" | ");
        }
        StdOut.println(" ]");
        String W = (weather == null) ? "null" : weather.weatherName;
        String P = (prop == null) ? "null" : prop.folderName;
        StdOut.println("  {Weather: " + W + " | Prop: " + P + "}");
        String ColorNextPoint = (ColorNext == null) ? "null" : String.valueOf(ColorNext.getFieldPoint());
        StdOut.println("  {CircleLast:(" + CircleLast.getFieldPoint() + "), CircleNext:(" + CircleNext.getFieldPoint() + "), ColorNext:(" + ColorNextPoint + ")}");
    }



    @Override
    public String toString() {
        return "[" +  STATE + "](" + getFieldPoint().getX() + "," + getFieldPoint().getY() + ")";
    }


    public Plane getFirstPlane() {
        while (planes.get(0) == null)
            planes.remove(null);
        return planes.get(0);
    }
}
