package project;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.util.Calendar;

import javax.swing.JFrame;

public class FPS{
    long tim; int i=0; double reliablefps;
    public void start(){
        tim = Calendar.getInstance().getTimeInMillis();
    }
    public double refresh(){
        long newt = Calendar.getInstance().getTimeInMillis();
        long timd = newt - tim;
        i++; double fps = Math.round(10000*i/((double)timd))/10.0;
        if (timd >= 100){tim = newt; i = 0; reliablefps=fps;}
        try{return reliablefps;}
        catch(Exception e){return fps;}
    }
}

