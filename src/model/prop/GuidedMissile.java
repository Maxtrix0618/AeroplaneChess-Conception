package model.prop;

import model.Plane;
import model.Player;
import view.GField;

public class GuidedMissile extends PropNatural {

    @Override
    protected void loadResource() {
        imageNumber = 2;
        formalName = "Guided_Missile";
        folderName = "guided_missile";
        super.loadResource();
    }

    public GuidedMissile(GField gameField) {
        super(gameField);
    }


    @Override
    public void acquired_by(Plane plane) {
        super.acquired_by(plane);
        plane.player.props[1] ++;

    }

    @Override
    public void used_by(Player player) {
        if (!(gameField.mouseController.dicePoint == 0 || gameField.mouseController.consideringChess()))
            return;

        super.used_by(player);
        player.usingProp[1] = true;
        player.props[1] --;
        gameField.mouseController.missileAttack = true;
    }


}
