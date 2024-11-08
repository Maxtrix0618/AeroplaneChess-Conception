package view.sub;

import view.GFrame;

import javax.swing.*;
import java.awt.*;


public class RWPInterface extends JDialog {
    private final int WIDTH;
    private final int HEIGHT;
    private final int ButtonWidth;
    private final int ButtonHeight;
    private final GFrame JF;

    public boolean[] RuleSet;
    public boolean[] WeatherSet;
    public boolean[] PropSet;


    public RWPInterface(GFrame JF, boolean[] RuleSet, boolean[] WeatherSet, boolean[] PropSet) {
        super(JF,"RWP",true);
        this.JF = JF;
        this.WIDTH = 180;
        this.HEIGHT = 220;
        this.ButtonWidth = 120;
        this.ButtonHeight = 36;
        this.RuleSet = RuleSet;
        this.WeatherSet = WeatherSet;
        this.PropSet = PropSet;
        setTitle("Rule | Weather | Prop");
        setResizable(false);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null); // Center the window.
        setLayout(null);


        addRuleSButton();
        addWeatherSButton();
        addPropSButton();

        setVisible(true);
    }



    private static final String[] RuleNames = new String[]{
            " 仅6点允许起飞 ",
            " 禁敌机越2迭子 ",
            " 交叉线允许撞子 ",
            " 终点按余数回返 ",
            " 连三6点己机全返 ",
            " 机占格不生成天气 ",
            " 经过时拾取道具 "
    };
    private void addRuleSButton() {
        JButton button = new JButton("Rule");
        button.setLocation(WIDTH / 8, HEIGHT / 40 * 4);
        button.setSize(ButtonWidth, ButtonHeight);
        button.setFocusPainted(false);
        button.setFont(new Font("", Font.PLAIN, 16));
        add(button);
        button.addActionListener((e) -> {
            OptionInterface ROI = new OptionInterface(JF, 180, 260, "Rule Set", RuleNames, RuleSet);
            RuleSet = ROI.returnOPS();
        });
    }


    private static final String[] WeatherNames = new String[]{
            "   🌧 降雨   ",
            "   ⚡ 雷暴   ",
            "   🌀 飓风   "
    };
    private void addWeatherSButton() {
        JButton button = new JButton("Weather");
        button.setLocation(WIDTH / 8, HEIGHT / 40 * 14);
        button.setSize(ButtonWidth, ButtonHeight);
        button.setFocusPainted(false);
        button.setFont(new Font("", Font.PLAIN, 16));
        add(button);
        button.addActionListener((e) -> {
            OptionInterface WOI = new OptionInterface(JF, 180, 150, "Weather Set", WeatherNames, WeatherSet);
            WeatherSet = WOI.returnOPS();
        });
    }

    private static final String[] PropNames = new String[]{
            " [如意骰子] ",
            " [制导飞弹] ",
            " [辅助引擎] ",
            " [偏转力场] ",
            " [光学迷彩] ",
            " [气象之瓶] "
    };
    private void addPropSButton() {
        JButton button = new JButton("Prop");
        button.setLocation(WIDTH / 8, HEIGHT / 40 * 24);
        button.setSize(ButtonWidth, ButtonHeight);
        button.setFocusPainted(false);
        button.setFont(new Font("", Font.PLAIN, 16));
        add(button);
        button.addActionListener((e) -> {
            OptionInterface POI = new OptionInterface(JF, 180, 240, "Prop Set", PropNames, PropSet);
            PropSet = POI.returnOPS();
        });
    }





}
