package model.weather;

import controller.TextPrinter;
import model.BlockComponent;
import model.Player;
import sound.SoundPlayer;


public class Thunderstorm extends Weather {

    @Override
    protected void loadResource() {
        imageNumber = 3;
        weatherName = "thunderstorm";
        super.loadResource();
    }

    public Thunderstorm() {}

    @Override
    public void start(BlockComponent block) {
        TextPrinter.println("⚡ Thunderstorm has set in on " + block.getFieldPoint() + "!");
        super.start(block);
    }
    @Override
    public void stop() {
        if (block == null)
            return;
        TextPrinter.println("⚡ Thunderstorm Stop on " + block.getFieldPoint() + ".");
        super.stop();
    }

    @Override
    public int event() {
        TextPrinter.println("⚡ Thunderstorm Encountered!");
        SoundPlayer.playSound("thunderstorm_event");
        return 2;
    }

    @Override
    public void capturedBy(Player player) {
        super.capturedBy(player);
        player.props[6] ++;
    }

    @Override
    public void returnBackProp(Player player) {
        TextPrinter.println("✦⚡ Bottle_Thunderstorm is Returned.");
        player.props[6] ++;
    }

    @Override
    public void startSound() {
        SoundPlayer.playSound("thunderstorm");
    }


}
