package model.prop;

import model.Plane;
import model.Player;
import view.GField;

public class AuxiliaryEngine extends PropNatural {

    @Override
    protected void loadResource() {
        imageNumber = 2;
        formalName = "Auxiliary_Engine";
        folderName = "auxiliary_engine";
        super.loadResource();
    }

    public AuxiliaryEngine(GField gameField) {
        super(gameField);
    }


    @Override
    public void acquired_by(Plane plane) {
        super.acquired_by(plane);
        plane.player.props[2] ++;

    }

    @Override
    public void used_by(Player player) {
        if (!gameField.mouseController.preparing) return;

        super.used_by(player);
        player.usingProp[2] = true;
        player.props[2] --;
        gameField.mouseController.OneMoreDice();
    }


}
