package model.prop;

import controller.TextPrinter;
import model.Plane;
import model.Player;
import view.GField;

public class BottleRainfall extends PropManual {

    public BottleRainfall(GField gameField) {
        super(gameField);
        folderName = "bottle_rainfall";
    }

    @Override
    public void acquired_by(Plane plane) {
        super.acquired_by(plane);
        plane.player.props[5] ++;
    }

    @Override
    public void used_by(Player player) {
        super.used_by(player);
        TextPrinter.println("✦🌧 Bottle_Rainfall is Used by " + player.getName() + "!");
        player.usingProp[5] = true;
        player.props[5] --;
    }


}
