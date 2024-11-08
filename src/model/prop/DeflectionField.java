package model.prop;

import controller.TextPrinter;
import model.BlockComponent;
import model.Plane;
import model.Player;
import sound.SoundPlayer;
import view.GField;
import view.sub.PropUseInterface;

public class DeflectionField extends PropNatural {

    @Override
    protected void loadResource() {
        imageNumber = 2;
        formalName = "Deflection_Field";
        folderName = "deflection_field";
        super.loadResource();
    }

    public DeflectionField(GField gameField) {
        super(gameField);
    }


    @Override
    public void acquired_by(Plane plane) {
        super.acquired_by(plane);
        plane.player.props[3] ++;

    }

    private BlockComponent spot;
    private int U;

    public int used_by(Player player, BlockComponent block) {
        this.spot = block;
        used_by(player);
        return U;
    }

    @Override
    public void used_by(Player player) {
        SoundPlayer.playSound("rime");
        if (player.AI) {
            U = 1;
        }
        else {
            PropUseInterface PUI = new PropUseInterface(gameField.GFrame, player, formalName, folderName);
            U = PUI.C;
        }
        if (U <= 0) return;
        SoundPlayer.playSound("3tone_2");
        TextPrinter.println("✦ Deflection_Field is Used by " + player.getName() + " on " + spot + "!");
        player.usingProp[3] = true;
        player.props[3] --;
    }


}
