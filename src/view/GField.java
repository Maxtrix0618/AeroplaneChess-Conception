package view;

import controller.MouseController;
import controller.TextPrinter;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import model.BlockComponent;
import model.Plane;
import model.Player;
import model.prop.*;
import model.weather.Hurricane;
import model.weather.Rainfall;
import model.weather.Thunderstorm;
import model.weather.Weather;
import sound.SoundPlayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Panel that contains all components
 */
public class GField extends JComponent {
    public final view.GFrame GFrame;
    private final int BLOCKSIZE;
    public final MouseController mouseController = new MouseController(this);
    public JButton ColorButton;
    public StringBuilder ParameterText;
    public MissileComponent Missile;
    public boolean[] AI_Player;
    public boolean[] Rule_Set;
    public boolean[] WeatherSet;
    public boolean[] PropSet;
    public int Selection;
    public boolean CTRL;            // Ctrl key is pressed
    public int NumberPressed;       // Number key is pressed
    public int reserveDicePoint;
    public JButton DiceButton;

    public ArrayList<Weather> weathers;
    public ArrayList<PropNatural> props;

    public BlockComponent[][] Maze;
    public Plane[] Planes;
    public Player Current;
    public boolean END;
    public int ROUND;


    public GField(view.GFrame GFrame, int width, int height, int BlockSize, int n, int m) {
        setSize(width, height);
        setLayout(null);
        this.GFrame = GFrame;
        this.BLOCKSIZE = BlockSize;
        this.ParameterText = new StringBuilder();
        this.Selection = 1;
        this.AI_Player = new boolean[4];
        this.Rule_Set = new boolean[7];
        this.WeatherSet = new boolean[3];
        this.PropSet = new boolean[6];
        this.Current = null;

        weathers = new ArrayList<>();
        props = new ArrayList<>();

        Maze = new BlockComponent[n][m];
        Planes = new Plane[16];
        END = false;
        ROUND = 0;

        NumberPressed = -1;
        reserveDicePoint = -1;

        initialRules();
        addModels();
        createWeathers();
        createProps();
        createEmptyField();
        initiateField();
        addKeyListener();

        setVisible(true);
        requestFocus();

    }

    /**
     * 测试用，添加于构造函数末尾
     */
    private void testIn() {
        Arrays.fill(WeatherSet, true);
        Arrays.fill(PropSet, true);
        for (int i = 0; i < 4; i++) {
            weathers.get(i).capturedBy(Player.PlayerList[i]);
            weathers.get(i+10).capturedBy(Player.PlayerList[i]);
            weathers.get(i+17).capturedBy(Player.PlayerList[i]);

            for (Player player : Player.PlayerList) {
                Arrays.fill(player.props, 0, 5, 1);
            }
        }
    }

    private void addModels() {
        Missile = new MissileComponent();
        add(Missile);
    }



    public void updateColorButton() {
        if (Current != null) ColorButton.setBackground(Current.getColor());
    }

    void showParameters() {
        SoundPlayer.playSound("ting");
        String title = "RANK" + "  (Round = " + ROUND + ")";
        JOptionPane.showMessageDialog(this, ParameterText, title, JOptionPane.INFORMATION_MESSAGE);
    }


    private final int[] wNs = new int[]{10, 7, 8};
    private void createWeathers() {
        for (int i = 0; i < wNs[0]; i++) {
            Rainfall w = new Rainfall();
            weathers.add(w);
            add(w);
        }
        for (int i = 0; i < wNs[1]; i++) {
            Thunderstorm w = new Thunderstorm();
            weathers.add(w);
            add(w);
        }
        for (int i = 0; i < wNs[2]; i++) {
            Hurricane w = new Hurricane();
            weathers.add(w);
            add(w);
        }
    }
    private final int[] pNs = new int[]{6, 7, 4, 4, 3, 6};
    private void createProps() {
        for (int i = 0; i < pNs[0]; i++) {
            RuyiDice P = new RuyiDice(this);
            props.add(P);
            add(P);
        }
        for (int i = 0; i < pNs[1]; i++) {
            GuidedMissile P = new GuidedMissile(this);
            props.add(P);
            add(P);
        }
        for (int i = 0; i < pNs[2]; i++) {
            AuxiliaryEngine P = new AuxiliaryEngine(this);
            props.add(P);
            add(P);
        }
        for (int i = 0; i < pNs[3]; i++) {
            DeflectionField P = new DeflectionField(this);
            props.add(P);
            add(P);
        }
        for (int i = 0; i < pNs[4]; i++) {
            ActiveCamouflage P = new ActiveCamouflage(this);
            props.add(P);
            add(P);
        }
        for (int i = 0; i < pNs[5]; i++) {
            BottleOfWeather P = new BottleOfWeather(this);
            props.add(P);
            add(P);
        }
    }

    /**
     * Establish Maze on Empty Field.
     */
    public void createEmptyField() {
        for (int i = 0; i < Maze.length; i++) {
            for (int j = 0; j < Maze[0].length; j++) {
                appendBlock(new BlockComponent(new FieldPoint(i, j), calculatePoint(i, j), mouseController, 0, BLOCKSIZE, Maze));
            }
        }
        // 由于遮盖关系，所有飞行器添加完毕后才能开始添加格块，这样飞行器才能显示在格块上层
        for (BlockComponent[] b_ : Maze)
            for (BlockComponent b : b_)
                add(b);
    }
    public void appendBlock(BlockComponent block) {
        int row = block.getFieldPoint().getX();
        int col = block.getFieldPoint().getY();
        if (Maze[row][col] != null) {
            remove(Maze[row][col]);
        }
        Maze[row][col] = block;
        tryAddPlane(row, col);
    }


    private int ips = 0;
    private final int[] r1 = new int[]{1, 1, 12, 12};
    private final int[] r2 = new int[]{2, 2, 13, 13};
    private final int[] c1 = new int[]{1, 12, 12, 1};
    private final int[] c2 = new int[]{2, 13, 13, 2};
    private final Player[] ps = new Player[]{Player.RED, Player.YELLOW, Player.BLUE, Player.GREEN};
    private void tryAddPlane(int row, int col) {
        for (int x = 0; x < 4; x++) {
            if ((row == r1[x] || row == r2[x]) && (col == c1[x] || col == c2[x])) {
                Planes[ips] = new Plane(Maze[row][col], ps[x], Planes);
                add(Planes[ips++]);
            }
        }
    }

    private Point calculatePoint(int row, int col) {
        return new Point(col * BLOCKSIZE, row * BLOCKSIZE);
    }

    /**
     * Initiate field based on specific rule.
     */
    private void initiateField() {
        buildBlocksRelationship();
        fillState();
    }

    private void buildBlocksRelationship() {
        for (int i = 0; i < 6; i++) {
            setCircleNext(Maze[0][4+i], Maze[0][4+i +1]);
            setCircleNext(Maze[14][10-i], Maze[14][10-i - 1]);
            setCircleNext(Maze[4+i][14], Maze[4+i +1][14]);
            setCircleNext(Maze[10-i][0], Maze[10-i - 1][0]);
            Maze[0][4+i].planePosture = 2;
            Maze[14][10-i].planePosture = 4;
            Maze[4+i][14].planePosture = 3;
            Maze[10-i][0].planePosture = 1;
        }
        for (int i = 0; i < 3; i++) {
            setCircleNext(Maze[i][10], Maze[i +1][10]);
            setCircleNext(Maze[11+i][10], Maze[11+i +1][10]);
            setCircleNext(Maze[14-i][4], Maze[14-i -1][4]);
            setCircleNext(Maze[3-i][4], Maze[3-i -1][4]);
            setCircleNext(Maze[4][i], Maze[4][i +1]);
            setCircleNext(Maze[4][11+i], Maze[4][11+i +1]);
            setCircleNext(Maze[10][14-i], Maze[10][14-i -1]);
            setCircleNext(Maze[10][3-i], Maze[10][3-i -1]);
            Maze[i][10].planePosture = 3;
            Maze[11+i][10].planePosture = 3;
            Maze[14-i][4].planePosture = 1;
            Maze[3-i][4].planePosture = 1;
            Maze[4][i].planePosture = 2;
            Maze[4][11+i].planePosture = 2;
            Maze[10][14-i].planePosture = 4;
            Maze[10][3-i].planePosture = 4;
        }
        setCircleNext(Maze[4][3], Maze[3][4]);
        setCircleNext(Maze[3][10], Maze[4][11]);
        setCircleNext(Maze[10][11], Maze[11][10]);
        setCircleNext(Maze[11][4], Maze[10][3]);
        Maze[4][3].planePosture = 2;
        Maze[3][10].planePosture = 3;
        Maze[10][11].planePosture = 4;
        Maze[11][4].planePosture = 1;

        for (int i = 0; i < 6; i++) {
            Maze[i][7].ColorNext = Maze[i +1][7];
            Maze[7][i].ColorNext = Maze[7][i +1];
            Maze[14-i][7].ColorNext = Maze[14-i -1][7];
            Maze[7][14-i].ColorNext = Maze[7][14-i -1];
        }
        Maze[3][0].CircleNext = Maze[4][0];
        Maze[0][11].CircleNext = Maze[0][10];
        Maze[11][14].CircleNext = Maze[10][14];
        Maze[14][3].CircleNext = Maze[14][4];
    }

    private void setCircleNext(BlockComponent S, BlockComponent O) {
        S.CircleNext = O;
        O.CircleLast = S;
    }

    private void fillState() {
        // 初始化停机坪
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                Maze[i][j].setSTATE(21);
        for (int i = 1; i < 3; i++)
            for (int j = 1; j < 3; j++) {
                Maze[i][j].setSTATE(11);
                Maze[i][j].Tarmac = true;
                Maze[i][j].planePosture = 3;
                Maze[i][j].ColorNext = Maze[3][0];
            }
        for (int i = 0; i < 4; i++)
            for (int j = 11; j < 15; j++)
                Maze[i][j].setSTATE(22);
        for (int i = 1; i < 3; i++)
            for (int j = 12; j < 14; j++) {
                Maze[i][j].setSTATE(12);
                Maze[i][j].Tarmac = true;
                Maze[i][j].planePosture = 4;
                Maze[i][j].ColorNext = Maze[0][11];
            }
        for (int i = 11; i < 15; i++)
            for (int j = 11; j < 15; j++)
                Maze[i][j].setSTATE(23);
        for (int i = 12; i < 14; i++)
            for (int j = 12; j < 14; j++) {
                Maze[i][j].setSTATE(13);
                Maze[i][j].Tarmac = true;
                Maze[i][j].planePosture = 1;
                Maze[i][j].ColorNext = Maze[11][14];
            }
        for (int i = 11; i < 15; i++)
            for (int j = 0; j < 4; j++)
                Maze[i][j].setSTATE(24);
        for (int i = 12; i < 14; i++)
            for (int j = 1; j < 3; j++) {
                Maze[i][j].setSTATE(14);
                Maze[i][j].Tarmac = true;
                Maze[i][j].planePosture = 2;
                Maze[i][j].ColorNext = Maze[14][3];
            }

        // 初始化循环轨道
        int c = 2;
        BlockComponent block = Maze[0][4];
        do {
            int state = (c++) %4 +1;
            block.setSTATE(state);
            block.CircleBlock = true;
            CircleBlocks.add(block);
            block = block.CircleNext;
        } while (block != Maze[0][4]);

        // 初始化入轨轨道
        int[] i_s = new int[]{0, 7, 7, 0, 14, 7, 7, 14};
        for (int k = 0; k < 8; k+=2) {
            block = Maze[i_s[k]][i_s[k+1]];
            block.EntryPoint = true;
            int linePosition = (block.planePosture == 4) ? 1 : block.planePosture +1;
            for (; block.ColorNext != null; block = block.ColorNext) {
                block.ColorNext.setSTATE(block.getSTATE());
                block.ColorNext.planePosture = linePosition;
            }
            block.setSTATE(block.getSTATE()+10);
            block.Destination = true;
        }

        Maze[3][0].TakePoint = true;
        Maze[0][11].TakePoint = true;
        Maze[11][14].TakePoint = true;
        Maze[14][3].TakePoint = true;
        Maze[3][0].planePosture = 2;
        Maze[0][11].planePosture = 3;
        Maze[11][14].planePosture = 4;
        Maze[14][3].planePosture = 1;

        Maze[3][4].FlyPoint = true;
        Maze[4][11].FlyPoint = true;
        Maze[10][3].FlyPoint = true;
        Maze[11][10].FlyPoint = true;

        for (int i = 0; i < 5; i++) {
            Maze[3][5+i].AirLine = Maze[3][4].getBlockState().getColor();
            Maze[11][9-i].AirLine = Maze[11][10].getBlockState().getColor();
            Maze[5+i][11].AirLine = Maze[4][11].getBlockState().getColor();
            Maze[9-i][3].AirLine = Maze[10][3].getBlockState().getColor();
            if (Maze[3][5+i].getSTATE() == 0) Maze[3][5+i].setSTATE(-2);
            if (Maze[11][9-i].getSTATE() == 0) Maze[11][9-i].setSTATE(-2);
            if (Maze[5+i][11].getSTATE() == 0) Maze[5+i][11].setSTATE(-2);
            if (Maze[9-i][3].getSTATE() == 0) Maze[9-i][3].setSTATE(-2);
        }

    }

    public final ArrayList<BlockComponent> CircleBlocks = new ArrayList<>();

    private void initialRules() {
        Rule_Set[1] = true;
        Rule_Set[2] = true;
        Rule_Set[3] = true;
        Rule_Set[5] = true;
    }

    public void Start() {
        TextPrinter.println("☈ Game Start.\n");
        SoundPlayer.playSound("blank");
        for (BlockComponent[] b_ : Maze)
            for (BlockComponent b : b_)
                for (Plane p : b.planes)
                    p.initial();
        TextPrinter.println("⚪ Red first.");
        Current = Player.RED;
        nextRound();
        mouseController.start();
        mouseController.tryAI();
    }

    public void Dice() {
        DiceButton.setText("▩");
        mouseController.doDice();
    }


    public void nextRound() {
        ROUND ++;
        updateColorButton();
        randomWeather();
        randomProp();
//        SoundPlayer.stopAllSound();
    }



    private void randomWeather() {
        ArrayList<Weather> WeaOnField = new ArrayList<>();
        for (Weather w : weathers) {
            if (w.block != null)
                WeaOnField.add(w);
        }

        for (Weather w : WeaOnField)
            w.goTime();

        if (WeaOnField.size() >= 6)
            return;
        if (StdRandom.uniformInt(0, 10) > WeaOnField.size() + 4) {
            BlockComponent b;
            do {
                b = CircleBlocks.get(StdRandom.uniformInt(0, CircleBlocks.size()));
            } while (b.weather != null || (Rule_Set[5] && !b.planes.isEmpty()));

            int u = StdRandom.uniformInt(0, 26);
            if (WeatherSet[0] && u < wNs[0])
                for (Weather w : weathers)
                    if (w instanceof Rainfall && w.player == null && w.block == null) {
                        w.start(b);
                        return;
                    }
            if (WeatherSet[1] && wNs[0] <= u && u < array_accumulate(wNs, 1))
                for (Weather w : weathers)
                    if (w instanceof Thunderstorm && w.player == null && w.block == null) {
                        w.start(b);
                        return;
                    }
            if (WeatherSet[2] && array_accumulate(wNs, 1) <= u && u < array_accumulate(wNs, 2))
                for (Weather w : weathers)
                    if (w instanceof Hurricane && w.player == null && w.block == null) {
                        w.start(b);
                        return;
                    }
        }
    }

    private void randomProp() {
        ArrayList<PropNatural> PropOnField = new ArrayList<>();
        for (PropNatural p : props) {
            if (p.block != null)
                PropOnField.add(p);
        }

        for (PropNatural p : PropOnField)
            p.goTime();

        if (PropOnField.size() >= 8)
            return;
        if (StdRandom.uniformInt(0, 10) > PropOnField.size() + 3) {
            BlockComponent b;
            do {
                b = CircleBlocks.get(StdRandom.uniformInt(0, CircleBlocks.size()));
            } while (b.prop != null || !b.planes.isEmpty());

            int u = StdRandom.uniformInt(0, 31);
            if (PropSet[0] && u < pNs[0])
                for (PropNatural p : props)
                    if (p instanceof RuyiDice && p.block == null) {
                        p.start(b);
                        return;
                    }
            if (PropSet[1] && pNs[0] <= u && u < array_accumulate(pNs, 1))
                for (PropNatural p : props)
                    if (p instanceof GuidedMissile && p.block == null) {
                        p.start(b);
                        return;
                    }
            if (PropSet[2] && array_accumulate(pNs, 1) <= u && u < array_accumulate(pNs, 2))
                for (PropNatural p : props)
                    if (p instanceof AuxiliaryEngine && p.block == null) {
                        p.start(b);
                        return;
                    }
            if (PropSet[3] && array_accumulate(pNs, 2) <= u && u < array_accumulate(pNs, 3))
                for (PropNatural p : props)
                    if (p instanceof DeflectionField && p.block == null) {
                        p.start(b);
                        return;
                    }
            if (PropSet[4] && array_accumulate(pNs, 3) <= u && u < array_accumulate(pNs, 4))
                for (PropNatural p : props)
                    if (p instanceof ActiveCamouflage & p.block == null) {
                        p.start(b);
                        return;
                    }
            if (PropSet[5] && array_accumulate(pNs, 4) <= u && u < array_accumulate(pNs, 5))
                for (PropNatural p : props)
                    if (p instanceof BottleOfWeather && p.block == null) {
                        p.start(b);
                        return;
                    }
        }
    }

    private static int array_accumulate(int[] array, int index) {
        if (index >= array.length || index < 0)
            return -1;
        int N = 0;
        for (int i = 0; i <= index; i++)
            N += array[i];
        return N;
    }


    public Weather W_TCP = null;
    public BlockComponent B_BFE = null;

    public void tryAddWeatherToField() {
        if (W_TCP == null || B_BFE == null)
            return;
        if (B_BFE.Tarmac || B_BFE.TakePoint || B_BFE.Destination)  {
            W_TCP.returnBackProp(Current);  // 使用气象道具后，所走棋子先前在不可生成天气的格子时，返还气象道具
        }
        else {
            W_TCP.start(B_BFE);
            W_TCP.startSound();
            SoundPlayer.playSound("glass_break");
        }
        W_TCP = null;
        B_BFE = null;
    }


    public void useProp(int Use) {
        if (Use == -1)
            return;
        switch (Use) {
            case 0: {
                RuyiDice RD = new RuyiDice(this);
                RD.used_by(Current);
                break;
            }
            case 1: {
                GuidedMissile GM = new GuidedMissile(this);
                GM.used_by(Current);
                break;
            }
            case 2: {
                AuxiliaryEngine AE = new AuxiliaryEngine(this);
                AE.used_by(Current);
                break;
            }
            case 3: {
                DeflectionField DF = new DeflectionField(this);
                DF.used_by(Current);
                break;
            }
            case 5: {
                BottleRainfall BR = new BottleRainfall(this);
                BR.used_by(Current);
                useBottleWeather("rainfall");
                break;
            }
            case 6: {
                BottleThunderstorm BT = new BottleThunderstorm(this);
                BT.used_by(Current);
                useBottleWeather("thunderstorm");
                break;
            }
            case 7: {
                BottleHurricane BH = new BottleHurricane(this);
                BH.used_by(Current);
                useBottleWeather("hurricane");
                break;
            }
        }
    }

    private void useBottleWeather(String weatherName) {
        for (Weather w : weathers)
            if (w.weatherName.equals(weatherName) && w.player == Current) {
                W_TCP = w;
                return;
            }
    }



    public int tryUseBottle(Weather weather) {
        if (Current.props[4] <= 0)
            return -1;
        BottleOfWeather BW = new BottleOfWeather(this);
        return (BW.used_by(Current, weather));
    }

    public int tryUseDeflectionField(Player player, BlockComponent block) {
        if (player.props[3] <= 0)
            return -1;
        DeflectionField DF = new DeflectionField(this);
        return (DF.used_by(player, block));
    }



    private void addKeyListener() {
        addKeyListener(new KeyListener() {
            /**
             * @param e response for keyboard event <br>
             */
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_CONTROL) CTRL = true;
                if (e.getKeyCode() == KeyEvent.VK_1) numberPressed(1);
                if (e.getKeyCode() == KeyEvent.VK_2) numberPressed(2);
                if (e.getKeyCode() == KeyEvent.VK_3) numberPressed(3);
                if (e.getKeyCode() == KeyEvent.VK_4) numberPressed(4);
                if (e.getKeyCode() == KeyEvent.VK_5) numberPressed(5);
                if (e.getKeyCode() == KeyEvent.VK_6) numberPressed(6);
                if (e.getKeyCode() == KeyEvent.VK_ENTER) what();
                if (e.getKeyCode() == KeyEvent.VK_I) inquiryPlanes();
            }
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_CONTROL) CTRL = false;
                if (e.getKeyCode() == KeyEvent.VK_1) numberPressed(-1);
                if (e.getKeyCode() == KeyEvent.VK_2) numberPressed(-1);
                if (e.getKeyCode() == KeyEvent.VK_3) numberPressed(-1);
                if (e.getKeyCode() == KeyEvent.VK_4) numberPressed(-1);
                if (e.getKeyCode() == KeyEvent.VK_5) numberPressed(-1);
                if (e.getKeyCode() == KeyEvent.VK_6) numberPressed(-1);
            }
            @Override
            public void keyTyped(KeyEvent e) {}
        });
    }

    private void numberPressed(int n) {
        NumberPressed = n;
        reserveDicePoint = n;
    }

    private void inquiryPlanes() {
        for (Plane p : Planes)
            StdOut.println(p);
    }

    public void FinalEnd() {
        END = true;
        TextPrinter.println("\n☈ Game Over.");
    }

    private void what() {
        if (JOptionPane.showConfirmDialog(this, "!", "?", JOptionPane.YES_NO_CANCEL_OPTION) == 0)
            for (BlockComponent[] b_ : Maze)
                for (BlockComponent b : b_)
                    if (b.EntryPoint && b.player == Current) {
                        for (Plane p : Planes)
                            if (p.player == Current)
                                p.forceTo(b, false);
                        return;
                    }

    }


    public static String toString2(BlockComponent[][] maze) {
        StringBuilder str = new StringBuilder();
        for (BlockComponent[] maze0 : maze) {
            for (BlockComponent maze00 : maze0)
                str.append(maze00).append(" ");
            str.append("\n");
        }
        return str.toString();
    }


}
