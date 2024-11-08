package model.prop;

import controller.TextPrinter;
import model.Plane;
import model.Player;
import view.GField;
import view.sub.RuyiDiceInterface;

public class RuyiDice extends PropNatural {

    @Override
    protected void loadResource() {
        imageNumber = 6;
        formalName = "Ruyi_Dice";
        folderName = "Ruyi_Dice";
        super.loadResource();
    }

    public RuyiDice(GField gameField) {
        super(gameField);
    }


    @Override
    public void acquired_by(Plane plane) {
        super.acquired_by(plane);
        plane.player.props[0] ++;

    }

    @Override
    public void used_by(Player player) {
        RuyiDiceInterface RDI = new RuyiDiceInterface(gameField.GFrame);
        if (RDI.P == -1) return;

        super.used_by(player);
        TextPrinter.println("✦ Dice Reserve : " + RDI.P);
        player.usingProp[0] = true;
        player.props[0] --;
        gameField.reserveDicePoint = RDI.P;
        gameField.Dice();
    }


}
