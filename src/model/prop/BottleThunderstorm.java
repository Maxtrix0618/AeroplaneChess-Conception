package model.prop;

import controller.TextPrinter;
import model.Plane;
import model.Player;
import view.GField;

public class BottleThunderstorm extends PropManual {

    public BottleThunderstorm(GField gameField) {
        super(gameField);
        folderName = "bottle_thunderstorm";
    }

    @Override
    public void acquired_by(Plane plane) {
        super.acquired_by(plane);
        plane.player.props[6] ++;
    }

    @Override
    public void used_by(Player player) {
        super.used_by(player);
        TextPrinter.println("✦⚡ Bottle_Thunderstorm is Used by " + player.getName() + "!");
        player.usingProp[6] = true;
        player.props[6] --;
    }


}
