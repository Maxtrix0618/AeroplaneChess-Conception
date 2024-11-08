package controller;

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import model.BlockComponent;
import model.Dice;
import model.Plane;
import model.Player;
import sound.SoundPlayer;
import view.GField;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;


public class MouseController {
    public final GField gameField;
    private final Dice dice;
    private final Dice dice2;

    public boolean preparing;           // 轮到玩家回合，但还没有开始投掷骰子的时间段
    public boolean diceConsidering;     // 已经点击骰子按钮，但还没有点击界面投掷出去的时间段（双掷时改成第一按钮到第二投掷的时间段）
    public boolean chessMoving;         // 已经点击棋子，棋子正在移动的时间段
    public int dicePoint;               // 0 if not valid (preparing || diceConsidering).

    private boolean NoChessToMove;
    private int point_6_CN;
    public boolean SKIP_T6;

    public boolean oneMoreDice;
    public boolean missileAttack;

    public boolean Mouse_Pressed_L;
    public boolean Mouse_Pressed_R;

    public MouseController(GField gameField) {
        this.gameField = gameField;
        this.dice = new Dice(this);
        this.dice2 = new Dice(this);
        this.point_6_CN = 0;
        this.preparing = false;
        this.diceConsidering = false;
        this.chessMoving = false;
        this.oneMoreDice = false;
        this.missileAttack = false;
        gameField.add(dice);
        gameField.add(dice2);
    }

    public boolean consideringChess() {
        return (dicePoint > 0 && !chessMoving);
    }

    public void onTouch(BlockComponent block) {
        block.repaint();
        if (gameField.CTRL)
            block.printSelf();
    }
    public void offTouch(BlockComponent block) {
        block.repaint();
    }


    public void start() {
        preparing = true;
    }
    public void doDice() {
        preparing = false;
        diceConsidering = true;
    }

    public void OneMoreDice() {
        oneMoreDice = true;
        gameField.DiceButton.setText("2");
    }


    public void onClick_L(BlockComponent block) {
        Mouse_Pressed_L = true;
        if (dice.throwing || dice2.throwing || chessMoving) return;
        if (block.Destination) return;

        if (diceConsidering && block.getSTATE() == 0) {
            StdOut.println("▣ Now throwing Dice...");
            if (oneMoreDice) {
                dicePoint += dice2.throwDice(block);
                gameField.DiceButton.setText("1");
                return;
            }
            dicePoint += dice.throwDice(block);
            diceConsidering = false;
            gameField.DiceButton.setText("➜");
            return;
        }

        // Move Chess
        if (!diceConsidering && dicePoint > 0 && !block.planes.isEmpty() && block.getFirstPlane().player == gameField.Current) {
            Plane plane = block.getFirstPlane();
            if (!planeCanMove(plane, true))
                return;

            if (block.Tarmac) {
                if (dicePoint == 6 || (dicePoint == 5 && !gameField.Rule_Set[0])) {
                    planeGo(plane, 1);
                }
            }
            else {
                gameField.B_BFE = plane.block;
                planeGo(plane, dicePoint);
            }
        }
    }

    private void planeGo(Plane plane, int point) {
        chessMoving = true;
        tryMissileAttack(plane);
        plane.GoFor(point);
    }

    private void tryMissileAttack(Plane attacker) {
        if (!missileAttack)
            return;
        missileAttack = false;
        for (int l = 0; l < 6; l++) {
            ArrayList<Plane> enemyPlanes = attacker.block.followCircle(l).EnemyPlanes(attacker.player);
            if (enemyPlanes.size() > 0) {
                gameField.Missile.attack(attacker, enemyPlanes.get(0));
                return;
            }
        }
        TextPrinter.println("✦ Guided_Missile is Returned.");
        attacker.player.props[1] ++;
    }

    private boolean planeCanMove(Plane p, boolean hint) {
        if (p.player != gameField.Current)
            return false;
        if (p.block.Destination)
            return false;
        if (p.block.Tarmac) {
            return (dicePoint == 6 || (dicePoint == 5 && !gameField.Rule_Set[0]));
        }
        for (int n = 1; n <= dicePoint; n++) {
            BlockComponent follow = p.block.followCircle(n);
            // the enemy can reach the bigeminal planes even if not cross it.
            if (gameField.Rule_Set[1] && follow.EnemyPlanes(p.player).size() >= 2 && n != dicePoint) {
                if (hint) {
                    TextPrinter.println("× Over 2 enemy planes(inclusive) stay on the road. Cannot go through!");
                    SoundPlayer.playSound("warn");
                }
                return false;
            }
            // play weather sound if detect weather in the next point-number squares.
            if (follow.weather != null) {
                follow.weather.startSound();
            }
        }
        return true;
    }

    private boolean NoChessToMove() {
        for (Plane p : gameField.Planes)
            if (planeCanMove(p, false))
                return false;
        return true;
    }

    /**
     * 判断并执行先行事件：无子可走以及三个连续6点
     */
    public void tryEvents() {
        if (oneMoreDice) {
            oneMoreDice = false;
            return;
        }
        NoChessToMove = false;
        if (gameField.Rule_Set[4] && point_6_CN == 2 && dicePoint == 6) {
            TextPrinter.println("☢ Roll three six-point dice in a row! All free planes go back to the Tarmac!");
            SoundPlayer.playSound("5tone");
            for (Plane p : gameField.Planes)
                if (p.player == gameField.Current && !p.block.Destination && !p.block.Tarmac)
                    p.returnToTarmac(false);
            dicePoint = 0;
            SKIP_T6 = true;
            tryGoToTheNextRound();
            return;
        }
        if (NoChessToMove()) {
            TextPrinter.println("× No chess to move.");
            NoChessToMove = true;
            tryGoToTheNextRound();
        }
    }


    public void tryGoToTheNextRound() {
        chessMoving = false;
        preparing = true;
        Arrays.fill(gameField.Current.usingProp, false);
        if (!tryPlayerWin() && dicePoint == 6) {
            TextPrinter.println("➜ Continue to Go!");
            gameField.DiceButton.setText("▣");
            point_6_CN ++;
            dicePoint = 0;
            tryAI();
            return;
        }
        goToTheNextRound();
        gameField.nextRound();
        tryAI();
    }

    private void goToTheNextRound() {
        dice.hideDice();
        dice2.hideDice();
        gameField.DiceButton.setText("▣");
        gameField.Current = gameField.Current.Next();
        repaintAllPlanes();
        if (gameField.Current == null) {
            gameField.FinalEnd();
            return;
        }
        point_6_CN = 0;
        dicePoint = 0;
        TextPrinter.print("\n⚪ Turn to: " + gameField.Current.getName());
        TextPrinter.print((gameField.Current.AI) ? "[AI]" : "");
        TextPrinter.println(" (" + gameField.ROUND + ") ");
    }

    private boolean tryPlayerWin() {
        if (!gameField.Current.INGAME) {
            Player.removePlayer(gameField.Current);
            Player.printInGamePlayers();

            int rank = 4 - Player.numberOfInGamePlayer();
            SoundPlayer.playSound("expansion");
            String info = "🏆 " + gameField.Current.getName() +  " win the " + rank + " place!";
            TextPrinter.println(info);
            JOptionPane.showMessageDialog(gameField, info, "WIN", JOptionPane.INFORMATION_MESSAGE);
            gameField.ParameterText.append("🏆 ").append(rank).append(" ").append(gameField.Current.getName()).append(" | r:").append(gameField.ROUND).append("\n");
            return true;
        }
        return false;
    }

    private void repaintAllPlanes() {
        for (Plane plane : gameField.Planes)
            plane.repaint();
    }

    public void tryAI() {
        if (gameField.END || !gameField.Current.AI)
            return;
        diceConsidering = true;

        // AI 等待1.5s后掷骰子
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            int r = 0;
            @Override
            public void run() {
                if (r < 15) {
                    r ++;
                    return;
                }
                timer.cancel();
                throwAIDice();

                // AI 等待骰子结果（2.5s）后行棋
                Timer timer = new Timer();
                TimerTask task = new TimerTask() {
                    int r = 0;
                    @Override
                    public void run() {
                        if (r < 20) {
                            r ++;
                            return;
                        }
                        timer.cancel();
                        if (NoChessToMove || SKIP_T6)
                            return;
                        tryAIChess();
                    }
                };
                timer.scheduleAtFixedRate(task, 0, 100);
            }
        };
        timer.scheduleAtFixedRate(task, 0, 100);

    }

    private void throwAIDice() {
        BlockComponent block;
        do {
            block = gameField.Maze[StdRandom.uniformInt(0, 15)][StdRandom.uniformInt(0, 15)];
        } while (block.getSTATE() != 0);
        onClick_L(block);
    }

    private void tryAIChess() {
        ArrayList<Plane> planesCanMove = new ArrayList<>();
        for (Plane p : gameField.Planes)
            if (planeCanMove(p, false)) {
                planesCanMove.add(p);
            }

//        StdOut.println(planesCanMove.toString());
        for (Plane p : planesCanMove) {
            BlockComponent target = p.block.followCircle(dicePoint);
            ArrayList<Plane> enemyPlanes = target.EnemyPlanes(p.player);
            if (enemyPlanes.size() == 1 && !enemyPlanes.get(0).active_camouflage) {
                moveAIChess(p);
                return;
            }
        }
        for (Plane p : planesCanMove) {
            if (p.block.Tarmac) {
                moveAIChess(p);
                return;
            }
        }
        for (Plane p : planesCanMove) {
            BlockComponent target = p.block.followCircle(dicePoint);
            if (target.player == gameField.Current && target.CircleBlock) {
                moveAIChess(p);
                return;
            }
        }
        for (Plane p : planesCanMove) {
            if (p.block.CircleBlock) {
                moveAIChess(p);
                return;
            }
        }
        moveAIChess(planesCanMove.get(0));
    }

    private void moveAIChess(Plane p) {
        onClick_L(p.block);
    }



    public void onClick_R(BlockComponent block) {
        Mouse_Pressed_R = true;
        // Click_R events.
    }


}
