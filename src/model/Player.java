package model;

import controller.TextPrinter;

import java.awt.*;

public enum Player {
    RED("Red", BlockState.RED.getColor(), 1),
    YELLOW("Yellow", BlockState.YELLOW.getColor(), 2),
    BLUE("Blue", BlockState.BLUE.getColor(), 3),
    GREEN("Green", BlockState.GREEN.getColor(), 4);

    public static final Player[] PlayerList = new Player[]{RED, YELLOW, BLUE, GREEN};
    public static final boolean[] InGame = new boolean[]{true, true, true, true};
    public final int[] props;
    public final boolean[] usingProp;
    private final String name;
    private final Color color;
    private final int order;
    public boolean INGAME;
    public boolean AI;

    Player(String name, Color color, int order) {
        this.name = name;
        this.color = color;
        this.order = order;
        this.INGAME = true;
        this.props = new int[8];
        this.usingProp = new boolean[8];
    }

    public String getName() {
        return name;
    }
    public Color getColor() {
        return color;
    }
    public int getOrder() {
        return order;
    }


    public Player Next() {
        Player next = this;
        for (int i = 0; i < 4; i++) {
            next = PlayerList[((next.order) % 4)];
            if (InGame[next.order - 1])
                return next;
        }
        return null;
    }

    public static void removePlayer(Player player) {
        Player.InGame[player.getOrder() - 1] = false;
    }

    public static int numberOfInGamePlayer() {
        int number = 0;
        for (boolean b : InGame)
            if (b) number ++;
        return number;
    }

    public static void printInGamePlayers() {
        StringBuilder str = new StringBuilder("[ ");
        for (int i = 0; i < 4; i++)
            if (InGame[i])
                str.append(PlayerList[i].getName()).append(" ");
        str.append("]");
        TextPrinter.println(str.toString());
    }



}
