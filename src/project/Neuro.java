package project;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.net.URI;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class Neuro extends JFrame implements Runnable
{
    Graphics g; neuron[][] st; int sc = 10, siz = 2, space = 5;
    BufferStrategy bs;

    Neuro(){
        setTitle("Neuro");
        setSize(900,600);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        neuron a = new neuron(new long[]{0,0}), b = new neuron(new long[]{0,1});
        b.addParent(a);
        st = new neuron[][]{{a}, {b}};
        run();
    }

  /*  public static void main(String[] args)
    {
        try
        {
            new Neuro();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }*/

    public void run(){
        bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(3);
            bs = getBufferStrategy();
        }
        g = bs.getDrawGraphics();
        while(true) {
            visualize();
            bs.show();
        }
    }

    public void visualize(){
        //g.drawOval(100,100,100,100);
        for(int i = 0; i < st.length; i++){
            for(int j = 0; j < st[i].length; j++){
                neuron p = st[i][j];
                g.setColor(Color.BLACK);
                g.fillOval((i+1)*space*sc, (j+1)*space*sc, siz*sc, siz*sc);
                g.setColor(Color.WHITE);
                g.drawString(p.ind+"", (i+1)*space*sc+siz*sc/3, (j+1)*space*sc+siz*sc*2/3);
                g.setColor(Color.BLACK);
                for(int k = 0; k < p.parent.size(); k++){
                    g.drawLine((i+1)*space*sc+siz*sc/2, (j+1)*space*sc+siz*sc/2,
                            (int) ((p.parent.get(k).cords[0]+1)*space*sc+siz*sc/2), (int) ((p.parent.get(k).cords[1]+1)*space*sc+siz*sc/2));
                }
            }
        }
    }
}

class neuron {
    LinkedList<neuron> parent = new LinkedList(), offs = new LinkedList();
    public double val; public long ind; public static long maxind = 0;
    public long[] cords;

    neuron(){
        this.ind = ++maxind;
    }
    neuron(long[] cords){ this.ind = ++maxind; this.cords = cords; }

    public void addParent(neuron parent){
        this.parent.add(parent); parent.addOffs(this);}

    public void addOffs(neuron offs){this.offs.add(offs);}

    public void setVal(double val){this.val = val;}

    public double sigmoid(double val){
        return 1./(1.+Math.exp(-val));
    }
}