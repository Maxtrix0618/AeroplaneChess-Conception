package sound;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;


public class BackgroundMusic implements Runnable {
    Thread musicThread = new Thread(this);
    public BackgroundMusic() {}

    public void play() {
        musicThread.start();
    }
    public void stop() {
        musicThread.stop();
    }

    public void run() {
        String fileLocation = "./resource/sounds/From_the_New_World[CUT].wav";
        for (int i = 0; i < 4; i++)     // 最多连续播放4次
            playSound(fileLocation);
    }

    private void playSound(String fileName) {
        File soundFile = new File(fileName);
        AudioInputStream audioInputStream = null;
        try {
            audioInputStream = AudioSystem.getAudioInputStream(soundFile);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assert audioInputStream != null;
        AudioFormat audioFormat = audioInputStream.getFormat();
        SourceDataLine line = null;
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
        try {
            line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(audioFormat);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assert line != null;
        line.start();
        int nBytesRead = 0;
        byte[] abData = new byte[128000];
        while (nBytesRead != -1) {
            try {
                nBytesRead = audioInputStream.read(abData, 0, abData.length);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            if (nBytesRead >= 0) {
                line.write(abData, 0, nBytesRead);
//                int nBytesWritten = line.write(abData, 0, nBytesRead);
            }
        }
        line.drain();
        line.close();
    }


    /**
     * 测试
     */
    public static void main(String[] args) {
        BackgroundMusic bgm = new BackgroundMusic();
        bgm.play();
    }

}