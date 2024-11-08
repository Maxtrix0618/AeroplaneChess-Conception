package model.prop;

import model.Plane;
import model.Player;
import view.GField;

public abstract class PropManual extends Prop {

    public PropManual(GField gameField) {
        super(gameField);
    }

    @Override
    public void acquired_by(Plane plane) {
        super.acquired_by(plane);
    }

    @Override
    public void used_by(Player player) {
        super.used_by(player);
    }


}
