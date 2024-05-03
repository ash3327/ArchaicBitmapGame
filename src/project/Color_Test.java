package project;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import javax.swing.JFrame;

public class Color_Test extends JFrame implements Runnable, MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {

    double X = 0, Y = 0, Z = 0, Zoom = 1; int mouseX, mouseY; boolean Ctrl = false;

    Display d; Graphics g; FPS f = new FPS();

    Color_Test(){
        setSize(500,500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setFocusable(true);
        setLocationRelativeTo(null);
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        addKeyListener(this);

        d = new Display(500, 500);
        add(d);

        setVisible(true);
        run();
    }

    public void run(){
        g = getGraphics();
        f.start();
        Perlin p[] = new Perlin[4]; for(int i = 0; i < 4; i++) p[i] = new Perlin();
        while(true){
            for(int j = 0; j < d.h-1; j++){
                for(int i = 0; i < d.w-1; i++){
                    //int color = colormix(colormix(Color.RED, Color.BLUE, ((double)i)/d.w),
                    //        colormix(Color.GREEN, Color.CYAN, ((double)i)/d.w), ((double)j)/d.h).getRGB();
                    int color = 0; double rat = 0.5; double xx = (X+1.*i/d.w)*Zoom, yy = (Y+1.*j/d.h)*Zoom;
                    for(int s = 0; s < 4; s++){
                        color += (int)((p[s].noise(xx*(1<<s),yy*(1<<s),System.currentTimeMillis()*0.001+Z)+2)*rat*128);
                        rat *= 0.5;
                    } color *= 0.75; color *= 65793;/**/
                    //int r = (int)((p.noise((X+1.*i/d.w)*Zoom,(Y+1.*j/d.h)*Zoom,Z)+1)*/*/16843009);/*/128);/**/
                    //int g = (int)((p2.noise((X+1.*i/d.w)*Zoom,(Y+1.*j/d.h)*Zoom,Z)+1)*/*/16843009);/*/128);/**/
                    //int b = (int)((p3.noise((X+1.*i/d.w)*Zoom,(Y+1.*j/d.h)*Zoom,Z)+1)*/*/16843009);/*/128);/**/
                    //int color = r+g*256+b*65536;/**/
                    //int waterlvl = 200, shorelvl = 250, snowlvl = 350;
                  /*  if(color < waterlvl) color = colormix(Color.BLUE, Color.CYAN, 1.*color/waterlvl).getRGB();
                    else if(color < shorelvl) color = colormix(Color.YELLOW.darker(), Color.GREEN.darker(), 1.*(color-waterlvl)/(shorelvl-waterlvl)).getRGB();
                    else if(color < snowlvl) color = colormix(Color.GREEN.darker(), Color.GREEN.brighter(), 1.*(color-shorelvl)/(snowlvl-shorelvl)).getRGB();
                    else color = colormix(Color.LIGHT_GRAY, Color.WHITE, 1.*(color-snowlvl)/(512-snowlvl)).getRGB();/**/
                    /*d.fillTriangle(new Vertex(i,j), new Vertex(i,j+1),
                            new Vertex(i+1,j), color);
                    d.fillTriangle(new Vertex(i,j+1), new Vertex(i+1,j),
                            new Vertex(i+1,j+1), color);*/
                    d.pixels[i+j*d.w] = color;
            }}
            g.drawImage(d.img,0,0,null);
            setTitle("FPS: "+f.refresh());
        }
    }

    Color colormix(Color A, Color B, double s){//s = scale
        int r = A.getRed(), g = A.getGreen(), b = A.getBlue();
        int r2 = B.getRed(), g2 = B.getGreen(), b2 = B.getBlue();
        r = r*r; g = g*g; b = b*b; r2 = r2*r2; g2 = g2*g2; b2 = b2*b2;
        int r3 = (int)((1-s)*r + s*r2);
        int g3 = (int)((1-s)*g + s*g2);
        int b3 = (int)((1-s)*b + s*b2);
        return new Color((int)Math.sqrt(r3),(int)Math.sqrt(g3),(int)Math.sqrt(b3));
    }


    @Override
    public void mouseDragged(MouseEvent e) {
        try{
            double prop = 0.01f;
            int dx = e.getX() - mouseX, dy = e.getY() - mouseY;
            X -= dx*prop; Y -= dy*prop;
            mouseX = e.getX(); mouseY = e.getY();
        } catch (Exception ex){mouseX = e.getX(); mouseY = e.getY();}
    }

    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if(Ctrl) Zoom += e.getPreciseWheelRotation()*0.1f;
        else Z += e.getPreciseWheelRotation()*0.01f;
    }

    public void mouseClicked(MouseEvent e) {

    }

    public void mousePressed(MouseEvent e) {
        mouseX = e.getX(); mouseY = e.getY();
    }

    public void mouseReleased(MouseEvent e) {

    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }

    public void keyTyped(KeyEvent e) {

    }

    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_CONTROL) Ctrl = true;
    }

    public void keyReleased(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_CONTROL) Ctrl = false;
    }
}

class Perlin {
    private long seed;

    public Perlin(){setSeed((long)((Math.random()-1)/2*Long.MAX_VALUE));}
    public Perlin(long seed){setSeed(seed);}
    public void setSeed(long seed){
        this.seed = seed;
        Random r = new Random(seed);
        for(int i = 0; i < permutation.length; i++) {permutation[i] = (int)(r.nextDouble()*255);}
        for (int i=0; i < 256 ; i++) p[256+i] = p[i] = permutation[i];
    }

    public double noise(double x, double y, double z) {
        int X = (int)Math.floor(x) & 255,                  // FIND UNIT CUBE THAT
                Y = (int)Math.floor(y) & 255,                  // CONTAINS POINT.
                Z = (int)Math.floor(z) & 255;
        x -= Math.floor(x);                                // FIND RELATIVE X,Y,Z
        y -= Math.floor(y);                                // OF POINT IN CUBE.
        z -= Math.floor(z);
        double u = fade(x),                                // COMPUTE FADE CURVES
                v = fade(y),                                // FOR EACH OF X,Y,Z.
                w = fade(z);
        int A = p[X  ]+Y, AA = p[A]+Z, AB = p[A+1]+Z,      // HASH COORDINATES OF
            B = p[X+1]+Y, BA = p[B]+Z, BB = p[B+1]+Z;      // THE 8 CUBE CORNERS,

        return lerp(w, lerp(v, lerp(u, grad(p[AA  ], x  , y  , z   ),  // AND ADD
                grad(p[BA  ], x-1, y  , z   )), // BLENDED
                lerp(u, grad(p[AB  ], x  , y-1, z   ),  // RESULTS
                        grad(p[BB  ], x-1, y-1, z   ))),// FROM  8
                lerp(v, lerp(u, grad(p[AA+1], x  , y  , z-1 ),  // CORNERS
                        grad(p[BA+1], x-1, y  , z-1 )), // OF CUBE
                        lerp(u, grad(p[AB+1], x  , y-1, z-1 ),
                                grad(p[BB+1], x-1, y-1, z-1 ))));
    }/**/
    static double fade(double t) {
        return (3-2*t)*t*t;
        //return t * t * t * (t * (t * 6 - 15) + 10);
    }
    static double lerp(double t, double a, double b) { return a + t * (b - a); }
    static double grad(int hash, double x, double y, double z) {
        int h = hash & 15;                      // CONVERT LO 4 BITS OF HASH CODE
        double u = h<8 ? x : y,                 // INTO 12 GRADIENT DIRECTIONS.
                v = h<4 ? y : h==12||h==14 ? x : z;
        return ((h&1) == 0 ? u : -u) + ((h&2) == 0 ? v : -v);
    }
    private int p[] = new int[512], permutation[] = { 151,160,137,91,90,15,
            131,13,201,95,96,53,194,233,7,225,140,36,103,30,69,142,8,99,37,240,21,10,23,
            190, 6,148,247,120,234,75,0,26,197,62,94,252,219,203,117,35,11,32,57,177,33,
            88,237,149,56,87,174,20,125,136,171,168, 68,175,74,165,71,134,139,48,27,166,
            77,146,158,231,83,111,229,122,60,211,133,230,220,105,92,41,55,46,245,40,244,
            102,143,54, 65,25,63,161, 1,216,80,73,209,76,132,187,208, 89,18,169,200,196,
            135,130,116,188,159,86,164,100,109,198,173,186, 3,64,52,217,226,250,124,123,
            5,202,38,147,118,126,255,82,85,212,207,206,59,227,47,16,58,17,182,189,28,42,
            223,183,170,213,119,248,152, 2,44,154,163, 70,221,153,101,155,167, 43,172,9,
            129,22,39,253, 19,98,108,110,79,113,224,232,178,185, 112,104,218,246,97,228,
            251,34,242,193,238,210,144,12,191,179,162,241, 81,51,145,235,249,14,239,107,
            49,192,214, 31,181,199,106,157,184, 84,204,176,115,121,50,45,127, 4,150,254,
            138,236,205,93,222,114,67,29,24,72,243,141,128,195,78,66,215,61,156,180
    };
}

