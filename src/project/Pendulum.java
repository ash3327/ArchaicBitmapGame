package project;

import java.awt.BasicStroke;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Queue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.plaf.SliderUI;

public class Pendulum extends JFrame implements MouseListener {
    private BufferedImage img;
    private int[] pixels;
    private BufferStrategy bs;
    private int w = 600, h = 600, W, H;
    private FPS fps = new FPS(), fps2 = new FPS();
    private boolean slow;
    private double zoom; private JSlider slid; private JPanel p;

    Pendulum(){
        setTitle("Pendulum");
        setSize(w + 400,h);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        p = new JPanel(){
            public void paintComponent(Graphics g){
                super.paintComponent(g);
                g.drawImage(img, 0, 0, null);
            }
        };
        p.setSize(this.getSize()); p.setLayout(null);
        Insets insets = getInsets();
        slid = new JSlider(-100,100);
        slid.setOrientation(1);
        Dimension size = slid.getPreferredSize();
        slid.setBounds(0, 0, size.width, size.height);
        slid.setLocation(w, (h - slid.getHeight())/2);
        slid.setOpaque(false);
        p.add(slid);
        add(p);
        setVisible(true);
        addMouseListener(this);

        run();
    }

    public void run(){
        W = getWidth(); H = getHeight();
        img = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB);
        pixels = ((DataBufferInt)img.getRaster().getDataBuffer()).getData();
        fps.start(); fps2.start();
        pend[] a = new pend[100]; for(int i = 0; i < a.length; i++) a[i] = new pend(100, 50);
        pend[] b = new pend[a.length]; for(int i = 0; i < a.length; i++) b[i] = new pend(100, 50);
        double ang;
        ang = 90*Math.PI/180; for(int i = 0; i < a.length; i++) a[i].init(new Vector(Math.sin(ang), Math.cos(ang)));
        double dang = .00001;
        ang = 90*Math.PI/180-dang/a.length/2; for(int i = 0; i < a.length; i++) b[i].init(new Vector(Math.sin(ang+dang*i), Math.cos(ang+dang*i)));
        for(int i = 0; i < a.length; i++) a[i].setPivot(new Vector(w/2,h/2));
        for(int i = 0; i < a.length; i++) {b[i].setPivot(a[i]); }//b[i].trail = true;}
        long t = System.nanoTime(), t2 = System.nanoTime(), now, gametick = 50000000; //50000000
        int mar = 20, ddh = 20;

        stata st = new stata(new Vector(w + mar, .75*H - mar), new Vector(W-w-2*mar, 2*mar+ddh-.5*H));
        while(true){
            for(int i = 0; i < a.length; i++) {b[i].update(); a[i].update();}
            if(System.nanoTime() > t){
                Arrays.fill(pixels, 0x000000);
                for(int i = 0; i < a.length; i++) {a[i].draw(img); b[i].draw(img);}
                //st.sc = stat.sc0*Math.exp(slid.getValue()/10.);
                //st.draw(img, a, b);
                p.repaint();
                setTitle("pendulum     Screen FPS: "+fps.refresh()+"    Update FPS: "+fps2.reliablefps+"    Slow Mode: "+slow);
                now = System.nanoTime();
                t = now - (now - t)%gametick + gametick;
            }
            if(slow) pend.dt = 0.000001; //0.0000003 | 0.0000001
            else pend.dt = 0.0001; //0.000003 | 0.000001
            fps2.refresh();
        }

    }

    //public static void main(String... args){new Pendulum(); }

    public void mouseClicked(MouseEvent e){}

    public void mousePressed(MouseEvent e) { slow = true; }

    public void mouseReleased(MouseEvent e) { slow = false; }

    public void mouseEntered(MouseEvent e) {}

    public void mouseExited(MouseEvent e) {}
}

class pend {
    Vector s0, s, s1, v, a, g, T;
    final Vector G = new Vector(0, 9.81);
    static double dt = 0.000003, mu = 0; final static double maxtsize = 1000;
    static int maxint = 2000; //0.006/dt
    boolean trail;
    // x0, y0: position of pivot of rod,
    // x1, y1: position of end of rod
    // pend is a ball of mass m suspended by a massless inextensible string.
    // angley: orientation with respect to anticlockwise -y axis in degrees.
   double len, m; pend parent, offs; boolean hasPivot, hasOffs; int it = 0;
    LinkedList<Vector> traj = new LinkedList<Vector>();
    LinkedList<Double> KE = new LinkedList<Double>();
    LinkedList<Double> PE = new LinkedList<Double>();
    LinkedList<Double> Ten = new LinkedList<Double>();

    pend(double len, double mass){//assume even distribution of mass
        this.len = len; this.m = mass;
    }

    public void init(Vector s){
        this.s = s.scale(len/s.len());
        g = G.copy().scale(m);
        v = Vector.nul.copy(); T = Vector.nul.copy();
    }

    public void setPivot(Vector s0){
        this.s0 = s0;
    }
    public void setPivot(pend parent){
        this.parent = parent;
        hasPivot = true;
        parent.setOffs(this);
        this.s0 = parent.s0.copy().add(parent.s, 1);
    }
    public void setOffs(pend offs){ this.offs = offs; hasOffs = true;}

    public void update(){
        pend.maxint = (int)(0.006/pend.dt);
        a = g.copy(); T = g.component(s);
        s1 = s0.copy().add(s, 1);

        Vector temp = Vector.nul.copy(); if(hasOffs) temp = offs.T.copy();

        if(hasPivot) {
            this.s0 = parent.s0.copy().add(parent.s, 1);
            s = s1.copy().add(s0, -1);
        }   T.add(s, m*(1-len/s.len())/(dt*dt));
        if(hasOffs) {
            //T.add(temp.component(s), 1);
            a.add(temp, 1/m);
        }

        v.add(a, dt);

        T.add(v.component(s), m/dt);

        v.add(T, -dt/m);
        v.add(v, -mu*dt);
        s.add(v, dt);
        s1 = s0.copy().add(s, 1);
        if(it > maxint){
            traj.add(s0.copy().add(s, 1));
            KE.add(.5*m*v.dot(v));
            PE.add(-s1.dot(g)*m);
            Ten.add(T.len());
            if(traj.size()>maxtsize) {
                traj.removeFirst();
                KE.removeFirst();
                PE.removeFirst();
                Ten.removeFirst();
            }
            it = 0;
        } else it++;
    }

    public void draw(BufferedImage img){
        Graphics2D g = (Graphics2D)img.getGraphics();
        g.setStroke(new BasicStroke(3));
        Vector[] k = s0.proj(s0.copy().add(s, 1));
        g.setColor(Color.WHITE);
        k[0].drawLine(g, k[1]);
        if(trail){
            for(int i = 1; i < traj.size(); i++){
                g.setStroke(new BasicStroke(i*3f/traj.size()));
                g.setColor(new Color(Color.HSBtoRGB(i*1f/traj.size(), 1, 1)));
                traj.get(i-1).drawLine(g, traj.get(i));
            }
        }
      /*  g.setColor(Color.RED);
        k[1].drawDVector(g, a.copy().scale(.01*m));
        g.setColor(Color.MAGENTA);
        k[1].drawDVector(g, T.copy().scale(-.01));
        if(hasPivot){
            g.setColor(Color.ORANGE);
            k[0].drawDVector(g, T.copy().scale(.01));
        }
        /**/
    }
}

class Vector{
    public static final Vector i = new Vector(1), j = new Vector(0,1),
            k = new Vector(0,0,1), nul = new Vector(0);
    double[] coords;

    public Vector(double... coords){
        this.coords = coords;
    }

    public Vector add(Vector b, double scale){
        if(coords.length < b.coords.length){
            double[] Coords = coords.clone();
            coords = new double[b.coords.length];
            System.arraycopy(Coords, 0, coords, 0, coords.length-1);
        }
        for(int i = 0; i < coords.length; i++)
            try{coords[i] += b.coords[i]*scale;} catch(ArrayIndexOutOfBoundsException e){}
        return this;
    }

    public Vector scale(double scale){
        for(int i = 0; i < coords.length; i++) coords[i] *= scale;
        return this;
    }

    public double len(){
        double len = 0;
        for(int i = 0; i < coords.length; i++) len += coords[i]*coords[i];
        return Math.sqrt(len);
    }

    public Vector copy(){
        return new Vector(coords.clone());
    }

    /** Returns the component of this vector on vector @param b */
    public Vector component(Vector b){
        return b.copy().scale(this.dot(b)/b.dot(b));
    }

    public double dot(Vector b){
        double ans = 0;
        try{
            for(int i = 0; i < coords.length; i++) ans += coords[i]*b.coords[i];
        } catch(ArrayIndexOutOfBoundsException e){}
        return ans;
    }

    /**this vector = starting vector
     * @param end = ending vector
     * projection: the projection on 2D screen*/
    public Vector[] proj(Vector end){
        return new Vector[]{new Vector(coords[0], coords[1]), new Vector(end.coords[0], end.coords[1])};
    }

    public Vector leftMul(double[][] matrix){
        Vector ans = this.copy();
        for (int i = 0; i < matrix.length; i++){
            for (int j = 0; j < matrix.length; j++){
               ans.coords[i] += matrix[i][j]*this.coords[j];
            }
        }
        return ans;
    }

    public void drawDLine(Graphics g, Vector c){ drawLine(g, this.copy().add(c,1));}
    public void drawLine(Graphics g, Vector b){
        g.drawLine((int)coords[0], (int)coords[1], (int)b.coords[0], (int)b.coords[1]);
    }

    public void drawDVector(Graphics g, Vector c){ drawVector(g, this.copy().add(c,1));}
    public void drawVector(Graphics g, Vector b){
        drawLine(g, b); double ang = 30*Math.PI/180; Vector k = this.copy().add(b, -1);
        b.drawDLine(g, k.leftMul(
                new double[][]{{Math.cos(ang), -Math.sin(ang)},{Math.sin(ang), Math.cos(ang)}}).scale(5/k.len()));
        b.drawDLine(g, k.leftMul(
                new double[][]{{Math.cos(ang), Math.sin(ang)},{-Math.sin(ang), Math.cos(ang)}}).scale(5/k.len()));
    }
}

class stata{
    Vector startLoc, nulloc, spanSize, sloc, nloc;
    double sc = .0005; final static double sc0 = .0005;

    public stata(Vector startLoc, Vector spanSize){
        this.startLoc = startLoc; this.spanSize = spanSize;
        nulloc = startLoc.copy().add(spanSize.component(new Vector(0,1)), .5); //origin
    }

    public void draw(BufferedImage img, pend... P){
        Graphics2D g = (Graphics2D)img.getGraphics();
        g.setStroke(new BasicStroke(3));
        g.setColor(Color.WHITE);
        nulloc.drawDVector(g, spanSize.component(Vector.i));
        startLoc.drawDVector(g, spanSize.component(Vector.j));
        int gap = 1;
        Color r = Color.RED, y = Color.YELLOW, b = Color.BLUE, c = Color.CYAN;
        for(int j = 0; j < P.length; j++){
            sloc = nulloc.copy();
            pend p = P[j];
            for(int i = gap; i < p.traj.size(); i+=gap){
                nloc = sloc.copy().add(spanSize.component(Vector.i), 1.0*gap/p.maxtsize);
                g.setColor(y);
                sloc.copy().add(Vector.j,-p.KE.get(i-gap)*sc).drawLine(g, nloc.copy().add(Vector.j,-p.KE.get(i)*sc));
                g.setColor(b);
                sloc.copy().add(Vector.j,-p.PE.get(i-gap)*sc).drawLine(g, nloc.copy().add(Vector.j,-p.PE.get(i)*sc));
            //    g.setColor(c);
            //    sloc.copy().add(Vector.j,-p.Ten.get(i-1)*Sc).drawLine(g, nloc.copy().add(Vector.j,-p.Ten.get(i)*Sc));
                g.setColor(r);
                sloc.copy().add(Vector.j,-(p.PE.get(i-gap)+p.KE.get(i-gap))*sc).drawLine(g, nloc.copy().add(Vector.j,-(p.PE.get(i)+p.KE.get(i))*sc));
                sloc = nloc;
            }
            r = r.darker(); y = y.darker(); b = b.darker(); c = c.darker();
        }
        Vector sta, fin;
        sloc = nulloc.copy();
        for(int i = gap; i < P[0].traj.size(); i+=gap){
            nloc = sloc.copy().add(spanSize.component(Vector.i), 1.0*gap/pend.maxtsize);
            g.setColor(Color.ORANGE);
            sta = Vector.nul.copy(); fin = Vector.nul.copy();
            for(int j = 0; j < P.length; j++){
                pend p = P[j];
                sta.add(Vector.j,-(p.PE.get(i-gap)+p.KE.get(i-gap))*sc);
                fin.add(Vector.j,-(p.PE.get(i)+p.KE.get(i))*sc);
            }
            sta.scale(1./P.length); fin.scale(1./P.length);
            sta.add(sloc, 1); fin.add(nloc, 1);
            sta.drawLine(g, fin);
            sloc = nloc;
        }
    }
}
