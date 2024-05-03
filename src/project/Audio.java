package project;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Audio {

    private Clip clip;
    private static HashMap<String, Clip> playlist = new HashMap(), playlist2 = new HashMap();
    private static HashMap<String, String> pl2 = new HashMap();

    Audio(String filename){
        try{
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(new File("res/audio/"+filename)));
            this.clip = clip;
        } catch (Exception e){}
    }

    /**@param filename: default file path must be res/audio/ */
    public static void play(String filename){
        try{

            if(filename.equals("none")) return;
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(new File("res/audio/"+filename)));
            clip.start();
        } catch (Exception e){}
    }
    public static void loop(String filename, int numcycle){
        try{
            if(filename.equals("none")) return;
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(new File("res/audio/"+filename)));
            clip.loop(numcycle);
        } catch (Exception e){}
    }
    public static void loopforever(String filename){
        try{
            if(filename.equals("none")) return;
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(new File("res/audio/"+filename)));
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e){}
    }

    public static void playlist(String filename){
        try{
            if(filename.equals("none")) return;
            if(!playlist.containsKey(filename)) {
                Clip clip = AudioSystem.getClip();
                clip.open(AudioSystem.getAudioInputStream(new File("res/audio/"+filename)));
                playlist.put(filename, clip);
                clip.start();
            } else if (playlist.get(filename).getMicrosecondLength() == playlist.get(filename).getMicrosecondPosition()){
                playlist.remove(filename);
            }
        } catch (Exception e){}
    }

    static boolean used = false;
    public static void playlist2(String filename, String index){
        try{
            if(used) return;
            used = true;
            if(filename.equals("none")) {
                if(pl2.get(index) != null){
                    stop(playlist2.get(index));
                    playlist2.remove(index);
                    pl2.remove(index);
                }
                used = false;
                return;
            }
            if(pl2.get(index) != filename){
                if(pl2.get(index) != null){
                    stop(playlist2.get(index));
                    playlist2.remove(index);
                    pl2.remove(index);
                }
                Clip clip = AudioSystem.getClip();
                clip.open(AudioSystem.getAudioInputStream(new File("res/audio/"+filename)));
                playlist2.put(index, clip);
                pl2.put(index, filename);
                clip.start();
            } else if (playlist2.get(index).getMicrosecondLength() == playlist2.get(index).getMicrosecondPosition()){
                stop(playlist2.get(index));
                playlist2.remove(index);
                pl2.remove(index);
            }
            used = false;
        } catch (Exception e){}
    }

    public void play(){ clip.start(); }
    public void loop(int numcycle){ clip.loop(numcycle); }
    public void loopforever(){ clip.loop(Clip.LOOP_CONTINUOUSLY); }
    public void stop(){ clip.stop(); }
    public static void stopAll(){
        for(Clip c : playlist.values()){
            stop(c);
        }
        playlist = new HashMap();
    }

    public static void stop(Clip c){
        c.stop(); c.flush(); c.close();
    }
}
