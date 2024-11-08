package model.weather;

import controller.TextPrinter;
import model.BlockComponent;
import model.Player;
import sound.SoundPlayer;


public class Rainfall extends Weather {

    @Override
    protected void loadResource() {
        imageNumber = 3;
        weatherName = "rainfall";
        super.loadResource();
    }

    public Rainfall() {}

    @Override
    public void start(BlockComponent block) {
        TextPrinter.println("🌧 Rainfall has Set In on " + block.getFieldPoint() + "!");
        super.start(block);
    }
    @Override
    public void stop() {
        if (block == null)
            return;
        TextPrinter.println("🌧 Rainfall Stop on " + block.getFieldPoint() + ".");
        super.stop();
    }

    @Override
    public int event() {
        TextPrinter.println("🌧 Rainfall Encountered!");
        SoundPlayer.playSound("rainfall");
        return 1;
    }

    @Override
    public void capturedBy(Player player) {
        super.capturedBy(player);
        player.props[5] ++;
    }

    @Override
    public void returnBackProp(Player player) {
        TextPrinter.println("✦🌧 Bottle_Rainfall is Returned.");
        player.props[5] ++;
    }

    @Override
    public void startSound() {
        SoundPlayer.playSound("rainfall");
    }




}
