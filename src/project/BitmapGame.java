package project;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.*;
import java.io.File;
import java.util.Calendar;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JFrame;

import project.Engines.Engine;

public class BitmapGame extends JFrame implements Runnable, KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {
	private static final long serialVersionUID = 0L;
	/**parameter args*/
	boolean runState;
	String title = "Bitmap Games";
	final String gap = " | ";
	long otime;
	public Engine engine; Display display; Graphics g; BufferStrategy bs;
	boolean debug = true; FPS f = new FPS();
	
	BitmapGame() throws UnhandledException{
		setTitle(title);
		setSize(1000,600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setFocusable(true);
		setLocationRelativeTo(null);
		
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		
		display = new Display(1000, 600);
		add(display);
		engine = new Engine(this, display);
		
		setVisible(true);
		runState = true;

		Audio.loopforever("Meditative-Space.wav");

		run();
	}

	public void run() {
		requestFocus();
		otime = Calendar.getInstance().getTimeInMillis();
		bs = getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(3);
			bs = getBufferStrategy();
		}
		g = bs.getDrawGraphics();
		f.start();
		while(runState){
			if(Calendar.getInstance().getTimeInMillis() - otime > 14){
				otime = Calendar.getInstance().getTimeInMillis();
				title = engine.title;
				if(debug) setTitle(title + gap + "FPS: " + f.refresh());
				else setTitle(title);
				
				try {engine.render(g);}
				catch (UnhandledException e) {e.printStackTrace();}
				
				bs.show();

				if(engine.time > -10000) engine.time--;
			}
		}
	}
	
	public void keyReleased(KeyEvent k) {
		if(k.getKeyCode() == 17) engine.Ctrl = false;
	}
	public void keyTyped(KeyEvent k) {}
	public void keyPressed(KeyEvent k) {
		if(k.getKeyCode() == 17) engine.Ctrl = true;
		try {engine.processKeyEvent(k);}
		catch (UnhandledException e) {e.printStackTrace();}
	}

	public void mouseClicked(MouseEvent mm) {
		try {engine.processMouseEvent(mm, 0);} 
		catch (UnhandledException e) {e.printStackTrace();}
	}
	public void mouseEntered(MouseEvent mm) {}
	public void mouseExited(MouseEvent mm) {}
	public void mousePressed(MouseEvent mm) {
		engine.mouseDown = true;
		try {engine.processMouseEvent(mm, 1);} 
		catch (UnhandledException e) {e.printStackTrace();}
	}
	public void mouseReleased(MouseEvent mm) {
		engine.mouseDown = false;
		try {engine.processMouseEvent(mm, 2);} 
		catch (UnhandledException e) {e.printStackTrace();}
	}
	public void mouseWheelMoved(MouseWheelEvent mw) {
		try {engine.processMouseWheelEvent(mw);} 
		catch (UnhandledException e) {e.printStackTrace();}
	}
	public void mouseDragged(MouseEvent mm) {
		try {engine.processMouseEvent(mm, 3);} 
		catch (UnhandledException e) {e.printStackTrace();}
	}
	public void mouseMoved(MouseEvent mm) {
		try {engine.processMouseEvent(mm, 4);} 
		catch (UnhandledException e) {e.printStackTrace();}
	}
	
	public static void main(String[] args) {
		try {new BitmapGame();}
		catch (UnhandledException e) {e.printStackTrace();}
	}
}