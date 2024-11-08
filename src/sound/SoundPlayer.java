package sound;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.applet.AudioClip;
import java.io.File;
import java.util.ArrayList;

/**
 * 游戏音效播放器
 * 将读取./resource/sounds/目录下的文件
 * 不要使用 mp3，请使用 wav或 au格式
 */

public class SoundPlayer implements AudioClip {

    final private Clip clip;
    private SoundPlayer(final Clip clip) {
            this.clip = clip;
    }

    @Override
    public void play() {
        clip.setFramePosition(0);  //把指针移到开始
        clip.start();
    }
    @Override
    public void loop() {
        clip.setFramePosition(0);
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }
    @Override
    public void stop() {
        clip.stop();
    }


    /**
     * 通过文件获取已加载音乐的AudioClip
     * @param audioFile 音乐文件 官方推荐wav和au格式
     * @return 已加载音乐的AudioClip
     **/
    public static AudioClip createAudioClip(final File audioFile){
        try{
            final Clip clip = AudioSystem.getClip(); //获取Clip对象
            AudioInputStream ais = AudioSystem.getAudioInputStream(audioFile);    //获取音乐输入流

            clip.open(ais); //加载音乐
            clip.setLoopPoints(0, -1);   //设置循环播放范围是整个音频
            return new SoundPlayer(clip);   //返回clip

        } catch (UnsupportedAudioFileException e){
            e.printStackTrace();
            System.err.println("音乐文件：" + audioFile.getAbsolutePath() + "的格式不合法（建议格式：wav, au）" );
        } catch (Exception e){
            e.printStackTrace();
            System.err.println("加载音乐文件：" + audioFile.getAbsolutePath() + "时发生系统错误" );
        }

        return null;
    }

    /**
     *关闭由上面生成的AudioClip释放资源
     **/
    public static void closeAudioClip(final AudioClip audioClip){
        if(audioClip instanceof SoundPlayer){
            try{ ((SoundPlayer)audioClip).clip.close();}catch(Exception ignored){}
        }
    }

    private static final ArrayList<AudioClip> CPS = new ArrayList<>();

    /**
     * 播放音效，根据sounds目录下的文件
     * @param filename 音效文件名，无后缀
     * */
    public static void playSound(String filename) {
        AudioClip cp = createAudioClip(new File("./resource/sound/" + filename + ".wav"));
        playS(cp);
    }

    private static void playS(AudioClip cp) {
        if (cp != null) {
            CPS.add(cp);
            cp.play();
            try {Thread.sleep(1);}
            catch (InterruptedException ignored) {}
//            closeAudioClip(cp);
        }
    }


    /**
     * 循环播放音效，根据sounds目录下的文件
     * @param filename 音效文件名，无后缀
     * @param time 循环时间，到点停止播放（单位：毫秒[ms]）
     */
    public static void playSound_Keeping(String filename, int time) {
        File sound = new File("./resource/sound/" + filename + ".wav");

        AudioClip cp = createAudioClip(sound);
        CPS.add(cp);
        if(cp != null){
            cp.loop();
            try {
                Thread.sleep(time);     // 循环时间，到点停止
            }
            catch (InterruptedException ignored) {
            }
            closeAudioClip(cp);  //如果close成功，音乐就停止
        }
    }

    /**
     * 停止所有音轨的播放
     */
    public static void stopAllSound() {
        if (CPS.isEmpty())
            return;
        for (AudioClip a : CPS)
            closeAudioClip(a);
        CPS.clear();
    }


    private static final AudioClip[] Tracks = new AudioClip[12];

    public static void playSoundOnTrack(String filename, int track) {
        AudioClip cp = createAudioClip(new File("./resource/sound/" + filename + ".wav"));
        Tracks[track] = cp;
        playS(cp);
    }
    public static void stopSoundOnTrack(int track) {
        closeAudioClip(Tracks[track]);
    }


    /**
     * 测试
     */
    public static void main(String[] args) {
        SoundPlayer.playSound("dong");
        SoundPlayer.playSound_Keeping("rainfall", 5000);
    }


}




