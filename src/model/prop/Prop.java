package model.prop;

import model.BlockComponent;
import model.Plane;
import model.Player;
import sound.SoundPlayer;
import view.GField;

import javax.swing.*;

public abstract class Prop extends JComponent {
    public GField gameField;
    public BlockComponent block;

    public String formalName = "";
    public String folderName = "";

    public Prop(GField gameField) {
        this.gameField = gameField;
        this.block = null;
    }

    /**
     * 获取道具
     */
    public void acquired_by(Plane plane) {
        this.block = null;
        SoundPlayer.playSound("3tone");
    }

    /**
     * 使用道具
     */
    public void used_by(Player player) {
        SoundPlayer.playSound("3tone_2");
    }



}
