package model;

import java.awt.*;

/**
 * Block State
 */
public enum BlockState {
    Blank("_Blank", new Color(235, 235, 235)),
    DENY("xDeny", new Color(20, 20, 20)),
    LineBlock("LineBlock", new Color(180, 180, 180)),

    RED_L("RED_L", new Color(255, 188, 188, 255)),
    YELLOW_L("YELLOW_L", new Color(255, 252, 185, 255)),
    BLUE_L("BLUE_L", new Color(182, 187, 255, 255)),
    GREEN_L("GREEN_L", new Color(190, 255, 189, 255)),

    RED("RED", new Color(255, 128, 128, 255)),
    YELLOW("YELLOW", new Color(255, 248, 127, 255)),
    BLUE("BLUE", new Color(128, 136, 255, 255)),
    GREEN("GREEN", new Color(129, 255, 127, 255)),

    RED_H("RED_H", new Color(255, 75, 75, 255)),
    YELLOW_H("YELLOW_H", new Color(255, 244, 74, 255)),
    BLUE_H("BLUE_H", new Color(79, 92, 255, 255)),
    GREEN_H("GREEN_H", new Color(81, 255, 77, 255));


    private final String name;
    private final Color color;

    BlockState(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }
    public Color getColor() {
        return color;
    }
}
