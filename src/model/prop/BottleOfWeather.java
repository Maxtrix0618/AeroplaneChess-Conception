package model.prop;

import controller.TextPrinter;
import model.Plane;
import model.Player;
import model.weather.Weather;
import sound.SoundPlayer;
import view.GField;
import view.sub.BottleOfWeatherInterface;

public class BottleOfWeather extends PropNatural {

    @Override
    protected void loadResource() {
        imageNumber = 2;
        formalName = "Bottle_Of_Weather";
        folderName = "bottle_of_weather";
        super.loadResource();
    }

    public BottleOfWeather(GField gameField) {
        super(gameField);
    }


    @Override
    public void acquired_by(Plane plane) {
        super.acquired_by(plane);
        plane.player.props[4] ++;
    }


    private Weather weather;
    private int U;

    public int used_by(Player player, Weather weather) {
        this.weather = weather;
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
            BottleOfWeatherInterface BWI = new BottleOfWeatherInterface(gameField.GFrame, player, weather);
            U = BWI.C;
        }
        if (U <= 0) return;
        TextPrinter.println("✦ Bottle_Of_Weather is Used by " + player.getName() + "!");
        player.usingProp[4] = true;
        player.props[4] --;
    }





}
