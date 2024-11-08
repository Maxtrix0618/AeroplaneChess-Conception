package view;

import controller.TextPrinter;
import model.Player;
import view.sub.PropInterface;
import view.sub.TextFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;

/**
 * Main Interface
 */
public class GFrame extends JFrame {
    private GField gameField;
    private JButton ColorButton;

    public int WIDTH;
    public int HEIGHT;
    public int BlockSize;
    public int n = 15;  // row number
    public int m = 15;  // column number

    private boolean FirstStep = true;


    public GFrame(String FrameName) {
        setTitle(FrameName);
        calculateSIZE_Fix();
        setSize(WIDTH, HEIGHT);
        getContentPane().setBackground(new Color(90, 90, 90));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(null);

        initializeTextPrinter();
        addHeadline();
        addSettingButton();
        addParameterButton();
        addColorButton();
        addField();
        addDiceButton();
        refreshAI();

    }

    private void initializeTextPrinter() {
        TextPrinter.initialize();
    }


    /**
     * the size of window will change to contain all blocks.
     */
    private void calculateSIZE_Fix() {
        BlockSize = 48;
        WIDTH = BlockSize * m +128;
        HEIGHT = BlockSize * n +64;
    }


    private void addField() {
        gameField = new GField(this, WIDTH, HEIGHT, BlockSize, n, m);
        gameField.setLocation(16, 16);
        gameField.ColorButton = this.ColorButton;
        add(gameField);
    }


    /**
     * Static Headline Text.
     */
    private void addHeadline() {
        JLabel statusLabel = new JLabel("<html>Aeroplane<br/>Chess<br/>");
        statusLabel.setLocation(WIDTH - 100, 10);
        statusLabel.setSize(80, 60);
        statusLabel.setForeground(Color.LIGHT_GRAY);
        statusLabel.setFont(new Font("Rockwell", Font.BOLD, 18));
        add(statusLabel);
    }


    /**
     * Button to Dice.
     */
    private void addDiceButton() {
        JButton button = new JButton("▶");
        button.setLocation(WIDTH -86, 140);
        button.setSize(54, 54);
        button.setFont(new Font("", Font.PLAIN, 20));
        button.setForeground(Color.LIGHT_GRAY);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.addActionListener((e) -> {
            if (FirstStep) {
                gameField.Start();
                FirstStep = false;
                button.setText("▣");
            }
            else {
                if (!gameField.END && gameField.mouseController.dicePoint == 0) {
                    gameField.Dice();
                    button.setText("▩");
                }
            }
            gameField.requestFocus();
        });
        add(button);
        gameField.DiceButton = button;
    }

    /**
     * Button for Setting.
     */
    private void addSettingButton() {
        JButton Button = new JButton("⚙");
        Button.setLocation(WIDTH -86, 80);
        Button.setSize(54, 54);
        Button.setFont(new Font("", Font.PLAIN, 24));
        Button.setForeground(Color.LIGHT_GRAY);
        Button.setContentAreaFilled(false);
        Button.setFocusPainted(false);
        Button.addActionListener((e) -> {
            SettingInterface SI = new SettingInterface(this, gameField.AI_Player, gameField.Rule_Set, gameField.WeatherSet, gameField.PropSet, gameField.Selection);
            gameField.AI_Player = SI.AIPlayers;
            gameField.Rule_Set = SI.RuleSet;
            gameField.WeatherSet = SI.WeatherSet;
            gameField.PropSet = SI.PropSet;
            gameField.Selection = SI.Selection;
            refreshAI();
            gameField.requestFocus();
        });
        add(Button);
    }

    private void refreshAI() {
        for (Player player : Player.PlayerList)
            player.AI = gameField.AI_Player[player.getOrder() - 1];
    }


    /**
     * Text for showing parameters(messages).
     */
    private void addColorButton() {
        ColorButton = new JButton("");
        ColorButton.setLocation(WIDTH -86, HEIGHT - 160);
        ColorButton.setSize(48, 48);
        ColorButton.setBackground(Color.LIGHT_GRAY);
        ColorButton.setFocusPainted(false);
        ColorButton.setFont(new Font("", Font.PLAIN, 40));
        ColorButton.addActionListener((e) -> {
            if (gameField.Current != null) {
                boolean diceProperTime = gameField.mouseController.preparing || gameField.mouseController.diceConsidering;
                boolean bottleProperTime = !gameField.mouseController.chessMoving;
                PropInterface PI = new PropInterface(this, gameField.Current, diceProperTime, bottleProperTime);
                gameField.useProp(PI.Use);
            }
            gameField.requestFocus();
        });
        add(ColorButton);
    }


    /**
     * Button for showing more detailed information.
     */
    private void addParameterButton() {
        JButton button = new JButton("RANK");
        button.setLocation(WIDTH -92, HEIGHT - 80);
        button.setSize(64, 24);
        button.setFont(new Font("", Font.BOLD, 10));
        button.setForeground(Color.LIGHT_GRAY);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.addActionListener((e) -> {
            if (!gameField.CTRL) {
                gameField.showParameters();
            }
            else {
                gameField.CTRL = false;
                new TextFrame("LOG", 480, 360, TextPrinter.read());
            }
            gameField.requestFocus();
        });
        add(button);
    }


    /**
     * Text for showing parameters(messages).
     */
    private void addParameterText() {
        JLabel ParameterText = new JLabel("■");
        ParameterText.setLocation(WIDTH -84, HEIGHT - 160);
        ParameterText.setSize(80, 80);
        ParameterText.setForeground(Color.LIGHT_GRAY);
        ParameterText.setFont(new Font("", Font.PLAIN, 40));
        add(ParameterText);
    }



    @Override
    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            if (JOptionPane.showConfirmDialog(this, "确定要退出游戏吗？", "EXIT", JOptionPane.OK_CANCEL_OPTION) != 0)
                return;
            TextPrinter.println(">_ Program Terminated.");
            TextPrinter.terminate();
        }
        super.processWindowEvent(e);
    }



}
