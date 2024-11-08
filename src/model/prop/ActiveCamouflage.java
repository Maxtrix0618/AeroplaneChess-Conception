package model.prop;

import model.Plane;
import model.Player;
import view.GField;

public class ActiveCamouflage extends PropNatural {

    @Override
    protected void loadResource() {
        imageNumber = 2;
        formalName = "Active_Camouflage";
        folderName = "active_camouflage";
        super.loadResource();
    }

    public ActiveCamouflage(GField gameField) {
        super(gameField);
    }


    @Override
    public void acquired_by(Plane plane) {
        super.acquired_by(plane);
        used_by(plane);
    }

    public void used_by(Plane plane) {
        super.used_by(plane.player);
        plane.setActiveCamouflage(true);
    }

    @Override
    public void used_by(Player player) {
        super.used_by(player);
    }


}
