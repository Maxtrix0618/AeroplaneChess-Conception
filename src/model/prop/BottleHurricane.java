package model.prop;

import controller.TextPrinter;
import model.Plane;
import model.Player;
import view.GField;

public class BottleHurricane extends PropManual {

    public BottleHurricane(GField gameField) {
        super(gameField);
        formalName = "Bottle_Hurricane";
        folderName = "bottle_hurricane";
    }

    @Override
    public void acquired_by(Plane plane) {
        super.acquired_by(plane);
        plane.player.props[7] ++;
    }

    @Override
    public void used_by(Player player) {
        super.used_by(player);
        TextPrinter.println("✦🌀 Bottle_Hurricane is Used by " + player.getName() + "!");
        player.usingProp[7] = true;
        player.props[7] --;
    }


}
