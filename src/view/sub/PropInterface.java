package view.sub;

import model.Player;
import view.GFrame;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class PropInterface extends JDialog {
    private final GFrame GF;
    private final Player player;

    private final int WIDTH;
    private final int HEIGHT;

    private final boolean dicePropTime;
    private final boolean bottlePropTime;

    public int Use;

    public PropInterface(GFrame GF, Player player, boolean dicePropTime, boolean bottlePropTime) {
        super(GF, "Props", true);     //继承法，阻塞父窗体
        setTitle(player.getName() + " | Props");

        this.GF = GF;
        this.player = player;
        this.dicePropTime = dicePropTime;
        this.bottlePropTime = bottlePropTime;
        this.Use = -1;
        WIDTH = 280;
        HEIGHT = 360;
        setSize(WIDTH, HEIGHT);
        setResizable(false); // 不可缩放大小
        setLocationRelativeTo(null); // Center the window.
        setLayout(null);

        paintImages();
        addTexts();
        addButtons();

        setVisible(true);
    }

    private void paintImages() {
        ImagePainter painter = new ImagePainter(WIDTH, HEIGHT);
        add(painter);
        painter.paintImages();

    }

    private final String[] Names = new String[]{
            " [如意骰子] ×",
            " [制导飞弹] ×",
            " [辅助引擎] ×",
            " [偏转力场] ×",
            " [气象之瓶] ×",
            " [瓶中天气] ×"
    };
    private void addTexts() {
        for (int i = 0; i < 6; i++) {
            JLabel HintLabel = new JLabel();
            int NProp = (i != 5) ? player.props[i] : player.props[5] + player.props[6] + player.props[7];
            HintLabel.setText(Names[i] + NProp);
            HintLabel.setLocation(64, i*48);
            HintLabel.setSize(WIDTH/2, HEIGHT/5);
            HintLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
            add(HintLabel);
        }
    }


    private void addButtons() {
        for (int i = 0; i < 6; i++) {
            if (player.AI)
                continue;
            if (i != 5) {
                if (player.usingProp[i])
                    continue;   // 一回合内使用过一次的道具不能再次使用.
                if (i == 3 || i == 4 || player.props[i] <= 0)
                    continue;   // 偏转力场和气象瓶不能直接用，故不显示按钮. 0个道具时亦不显示按钮.
                if (i == 0 && !dicePropTime)
                    continue;   // 骰子道具只能在“准备”和“思考骰子”时机使用
            }
            else {
                boolean noWeatherBottle = player.props[5] <= 0 && player.props[6] <= 0 && player.props[7] <= 0;
                if (!bottlePropTime || noWeatherBottle)
                    continue;   // 气象道具只能在除了“移动棋子”以外的时机使用.
            }

            JButton button = new JButton("➜");
            button.setLocation(200, 18 + i*48);
            button.setSize(42, 30);
            button.setContentAreaFilled(false);
            button.setFocusPainted(false);
            button.setFont(new Font("", Font.PLAIN, 8));
            add(button);

            if (i == 5) {
                button.addActionListener((e) -> {
                    BWPropInterface BWP_I = new BWPropInterface(GF, player);
                    Use = BWP_I.Use;
                    if (Use != -1)
                        dispose();
                });
            }
            else {
                int I = i;
                button.addActionListener((e) -> {
                    Use = I;
                    dispose();
                });
            }
        }

    }

    /**
     * 图片绘制器
     */
    private static class ImagePainter extends JComponent {
        private final Image[] images = new Image[6];

        private void loadImages() {
            String imageFolder = "./resource/image/prop/";
            try {
                images[0] = ImageIO.read(new File(imageFolder + "/Ruyi_dice/0.png"));
                images[1] = ImageIO.read(new File(imageFolder + "/guided_missile/0.png"));
                images[2] = ImageIO.read(new File(imageFolder + "/auxiliary_engine/0.png"));
                images[3] = ImageIO.read(new File(imageFolder + "/deflection_field/0.png"));
                images[4] = ImageIO.read(new File(imageFolder + "/bottle_of_weather/0.png"));
                images[5] = ImageIO.read(new File(imageFolder + "/bottle_of_weather/X.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public ImagePainter(int width, int height) {
            setSize(width, height);
            loadImages();
        }

        public void paintImages() {
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponents(g);
            int imageSize = 48;
            for (int i = 0; i < images.length; i++)
                g.drawImage(images[i], 16, 8 + i*48, imageSize, imageSize, this);
        }

    }

    /**
     * 瓶中天气-专用道具界面
     */
    public static class BWPropInterface extends JDialog {
        private final int propsN = 3;
        private final Player player;

        private final int WIDTH;
        private final int HEIGHT;

        public int Use;

        public BWPropInterface(GFrame JF, Player player) {
            super(JF, "BWProps", true);
            setTitle("Prop | Bottle_Weather");

            this.player = player;
            this.Use = -1;
            WIDTH = 280;
            HEIGHT = 220;
            setSize(WIDTH, HEIGHT);
            setResizable(false); // 不可缩放大小
            setLocationRelativeTo(null); // Center the window.
            setLayout(null);

            paintImages();
            addTexts();
            addButtons();

            setVisible(true);
        }

        private void paintImages() {
            ImagePainter2 painter = new ImagePainter2(WIDTH, HEIGHT);
            add(painter);
            painter.paintImages();
        }

        private final String[] Names = new String[]{
                " [瓶中阵雨]  ×",
                " [瓶中雷暴]  ×",
                " [瓶中飓风]  ×"};
        private void addTexts() {
            for (int i = 0; i < propsN; i++) {
                JLabel HintLabel = new JLabel();
                HintLabel.setText(Names[i] + player.props[i + 5]);
                HintLabel.setLocation(64, 10 + i*48);
                HintLabel.setSize(WIDTH/2, HEIGHT/5);
                HintLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
                add(HintLabel);
            }
        }

        private void addButtons() {
            for (int i = 0; i < propsN; i++) {
                if (player.props[i + 5] <= 0)
                    continue;   // 0个道具时不显示按钮.

                JButton button = new JButton("➜");
                button.setLocation(200, 18 + i*48);
                button.setSize(42, 30);
                button.setContentAreaFilled(false);
                button.setFocusPainted(false);
                button.setFont(new Font("", Font.PLAIN, 8));
                add(button);
                int I = i;
                button.addActionListener((e) -> {
                    Use = I + 5;
                    dispose();
                });
            }
        }

        private class ImagePainter2 extends JComponent {
            private final Image[] images = new Image[propsN];

            private void loadImages() {
                String imageFolder = "./resource/image/prop/";
                try {
                    images[0] = ImageIO.read(new File(imageFolder + "/bottle_of_weather/R.png"));
                    images[1] = ImageIO.read(new File(imageFolder + "/bottle_of_weather/T.png"));
                    images[2] = ImageIO.read(new File(imageFolder + "/bottle_of_weather/H.png"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            public ImagePainter2(int width, int height) {
                setSize(width, height);
                loadImages();
            }

            public void paintImages() {
                repaint();
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponents(g);
                int imageSize = 48;
                for (int i = 0; i < images.length; i++)
                    g.drawImage(images[i], 16, 8 + i*48, imageSize, imageSize, this);
            }

        }

    }


}
