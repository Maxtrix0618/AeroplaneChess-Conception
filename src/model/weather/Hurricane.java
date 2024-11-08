package model.weather;

import controller.TextPrinter;
import model.BlockComponent;
import model.Player;
import sound.SoundPlayer;


public class Hurricane extends Weather {

    @Override
    protected void loadResource() {
        imageNumber = 2;
        weatherName = "hurricane";
        super.loadResource();
    }

    public Hurricane() {}

    @Override
    public void start(BlockComponent block) {
        TextPrinter.println("🌀 Hurricane has set in on " + block.getFieldPoint() + "!");
        super.start(block);
    }
    @Override
    public void stop() {
        if (block == null)
            return;
        TextPrinter.println("🌀 Hurricane Stop on " + block.getFieldPoint() + ".");
        super.stop();
    }

    @Override
    public int event() {
        TextPrinter.println("🌀 Hurricane Encountered!");
        SoundPlayer.playSound("hurricane_event");
        return 3;
    }

    @Override
    public void capturedBy(Player player) {
        super.capturedBy(player);
        player.props[7] ++;
    }

    @Override
    public void returnBackProp(Player player) {
        TextPrinter.println("✦🌀 Bottle_Hurricane is Returned.");
        player.props[7] ++;
    }

    @Override
    public void startSound() {
        SoundPlayer.playSound("hurricane");
    }


}
