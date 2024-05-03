package project.Engines;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.font.FontRenderContext;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import project.*;
import project.Sprite.*;
import project.Sprites.*;

public class Engine {
	Display d, d2, disp;
	Games game = Games.Null;
	public String title = "Bitmap Games";
	String fontstr = "KaiTi", fonteng = "Centaur"; //FZShuTi, STKaiTi
	String[] TextureList, SpriteList, ItemList;
	String[] choice = {
			"res/icons",
			"0 mapcreate/Pic.png", "0 mapcreate/Word.png",
			"1 dungeon/Pic.png", "1 dungeon/Word.png"
	};
	public static int timeMax = 600, timeT = 100; //timeT: transition time
	public int time, chosen = 0, nchosen = 0, w, h, ddh;
	int k = 0, tt = 0, flr = 0, clock = 0, clockset = 1, walkPeriod = 16, mouseX, mouseY, tim;
	int brightness = 100;
	//Left and Right Tool Bars: selectlooking [0 Tiles, 1 Items, 2 Special, 3 Mobs], others = margins
	int selectlooking = 0, sx, sty = 160, sdy = 352, sry = 160, cexcept = -1;
	int enc = 48;//encoder default = 48 (for designer use)
	Image[] Choice;
	Image logo = Toolkit.getDefaultToolkit().getImage("res/opening/nebulaWingsLogo-SHORT.png");
	Image Logo = Toolkit.getDefaultToolkit().getImage("res/opening/NebulaWingsLogo-recolor.png");
	Image word = Toolkit.getDefaultToolkit().getImage("res/opening/nebulaNameShort.png");
	Image presents = Toolkit.getDefaultToolkit().getImage("res/opening/presents.png");
	Image cross = Toolkit.getDefaultToolkit().getImage("res/opening/cross.png");
	Map m, mLowe, mOver, mItem, mStat, mLight; Camera cm, target = new Camera(CameraType.Dim2); BitmapGame btg; MapType mt;
	Texture tabs, bg, bar; //Tabs in LTB
	String texloc = "dungeon.zip";
	Camera navCm;//Navigation Camera
	Tiles tiles; SpecialObjects spobj; Mobs mobs; Items items; 
	public float sizeEnlarged = 1;
	float spobjaspeed = 0.4f, spobjbspeed = 0.075f; //speed of animation/movement triggered of the special objects
	public boolean Ctrl = false, mouseDown; boolean info = false;

	String[] commandStack; String pre = ""; Texture edge; int mouse = 0;
	Zip z; Object[] extraObj = new Object[0];
	DataArrays dispwords = new DataArrays();
	/* Display of words in the scene:
	 * Record: 
	 * [0] String: String displayed
	 * [1] Int: Time counter
	 * [2] String: Command counter
	 * [3] Int: x location on screen
	 * [4] Int: y location on screen
	 * [5] Int: Transparency
	 * [6] Int: Size of word
	 * [7] Int: Colour (0x000000-0xFFFFFF)
	 * [8] Int: State (0 none, 1 with border)
	 */
	DataArrays effects = new DataArrays();
	/* Effects in the game, e.g. those caused by potions or mobs, etc.
	 * Record:
	 * [0] String: Name of effect
	 * [1] Int: Time counter - how long does this effect last?
	 * [2] Int: Strength of the effect, e.g. Power 2 VS Power 3
	 * */
	boolean paused = false, dialogue = false; int notifed = 0;
	Texture border;
	Point startdialogue = new Point(0,0), enddialogue = new Point(0,0);
	/* drawPassage function: "+/+" is next line, "+" is space.
	 * */
	
	class Camera {
		int x, y, z, stat;
		float xf, yf, zf;
		CameraType ct;
		
		Camera(CameraType c){
			ct = c;
			if(ct == CameraType.Dim2){
				zf = (float) (0.0/0.0);
			}
		}
		//init() - Sets the initial position of the camera
		void init() throws UnhandledException{
			xf = -1; yf = -1; 
			x = Math.round(xf); y = Math.round(yf);
			if(ct == CameraType.Dim2){
				zf = (float) (0.0/0.0);
			} else {zf = -1;}
			z = Math.round(zf);
		}
		void init(float xx, float yy) throws UnhandledException{
			if(ct == CameraType.Dim2){ xf = xx; yf = yy; x = Math.round(xf); y = Math.round(yf);} 
			else {throw new UnhandledException(0x2, 
					"Camera Type "+ct+" is not applicatable for void function init(x, y)");}
		}
		void init(float xx, float yy, float zz) throws UnhandledException{
			if(ct == CameraType.Dim3){ xf = xx; yf = yy; zf = zz;
				x = Math.round(xf); y = Math.round(yf); z = Math.round(zf);} 
			else {throw new UnhandledException(0x2, 
					"Camera Type "+ct+" is not applicatable for void function init(x, y, z)");}
		}
		//move() - Changes the position of the camera
		void move(float dx, float dy) throws UnhandledException{
			if(ct == CameraType.Dim2){ xf += dx; yf += dy; x = Math.round(xf); y = Math.round(yf);}
			else {throw new UnhandledException(0x2, 
					"Camera Type "+ct+" is not applicatable for void function move(dx, dy)");}
		}
		void move(float dx, float dy, float dz) throws UnhandledException{
			if(ct == CameraType.Dim3){ xf += dx; yf += dy; zf += dz; 
			x = Math.round(xf); y = Math.round(yf); z = Math.round(zf);} 
			else {throw new UnhandledException(0x2, 
					"Camera Type "+ct+" is not applicatable for void function move(dx, dy, dz)");}
		}
		//setStatus() - sets variable stat
		void setStatus(int st){stat = st;}
	}
	enum CameraType {
		Dim2, Dim3;
	}
	
	public Engine(BitmapGame b, Display dis) throws UnhandledException{
		time = timeMax;
		btg = b;
		d = dis;
		Choice = new Image[choice.length-1];
		for(int i = 0; i < choice.length-1; i++){
			Choice[i] = Toolkit.getDefaultToolkit().getImage(choice[0] + "/" + choice[i+1]);
		}
	}
	public Engine(BitmapGame b, Display dis, int t) throws UnhandledException{
		time = timeMax;
		btg = b;
		d = dis;
		d2 = new Display(d.w, d.h);
		disp = new Display(d.w, d.h);
		time = t;
		Choice = new Image[choice.length-1];
		for(int i = 0; i < choice.length-1; i++){
			Choice[i] = Toolkit.getDefaultToolkit().getImage(choice[0] + "/" + choice[i+1]);
		}
	}
	public Engine(BitmapGame b, Display dis, Games gm) throws UnhandledException{
		btg = b;
		d = dis;
		d2 = new Display(d.w, d.h);
		disp = new Display(d.w, d.h);
		game = gm;
		clockset = 4;
		commandStack = new String[0];
		bar = new Texture("res/texture/bar.png", d);
	}
	
	public void render(Graphics g) throws UnhandledException{
		clock = (clock+1)%(16*clockset);
		if(time > 0){
			startPage(g);
		} else if (time == 0){
			sInit();
			Init();
		} else {
			try{
				sRun(g);
			} catch (Exception e){
				e.printStackTrace();
			}
		}
	}
	
	protected void startPage(Graphics g) throws UnhandledException{
		GraphicsF g2 = new GraphicsF((Graphics2D) g, d);
		if(time >= timeT){
			d.clearScreen(0xF0F0F0);
			g2.drawImage(d.img, 0, 0, d.img.getWidth(), d.img.getHeight(), 1);
			if(time >= timeMax-timeT){
				d.clearScreen(0x000000);
				g2.drawImage(d.img, 0, 0, d.img.getWidth(), d.img.getHeight(), 1.0f*(timeMax-time)/timeT);
			} else if(time >= timeT){
				d.clearScreen(0x000000);
				g2.drawImage(d.img, 0, 0, d.img.getWidth(), d.img.getHeight(), 1);
			} else {
				d.clearScreen(0x000000);
				g2.drawImage(d.img, 0, 0, d.img.getWidth(), d.img.getHeight(), 1.0f*time/timeT);
			}
			if (time >= timeMax-timeT){
				g2.drawImage(Logo, d.w/2-200, d.h/2-200, 400, 400);
				g2.drawImage(word, d.w/2-112, d.h/2-43, 224, 86);
			} else {
				float ratio = 1.0f*(time-timeT) / (timeMax - 2*timeT);
				float rati;
				if(ratio > 2/3f){
					rati = (ratio-2/3f)*3;
					int size = (int)(200+200*rati);
					g2.drawImage(Logo, (int)(d.w/2-size/2-100*(1-rati)), d.h/2-size/2, size, size);
					g2.drawImage(word, (int)(d.w/2-112*rati), d.h/2-43, 224, 86);
				} else if (ratio > 0.5f){
					rati = (ratio - 0.5f)*6;
					g2.drawImage(Logo, d.w/2-200, d.h/2-100, 200, 200, 1);
					g2.drawImage(word, d.w/2, d.h/2-43, 224, 86);
					g2.drawImage(presents, d.w/2+70, d.h/2+70, 114, 29, 1-rati);
				} else if (ratio > 1/6f) {
					rati = (ratio - 1/6f)*3;
					g2.drawImage(Logo, d.w/2-200, d.h/2-100, 200, 200);
					g2.drawImage(word, d.w/2, d.h/2-43, 224, 86);
					g2.drawImage(presents, d.w/2+70, d.h/2+70, 114, 29);
				} else {
					rati = (ratio)*6;
					g2.drawImage(Logo, d.w/2-200, d.h/2-100, 200, 200, rati);
					g2.drawImage(word, d.w/2, d.h/2-43, 224, 86);
					g2.drawImage(presents, d.w/2+70, d.h/2+70, 114, 29);
					g2.drawImage(logo, d.w-158, d.h-50, 158, 50, 1-rati);
				}
			}
		}
		else {
			d.clearScreen(0x000000);
			g2.drawImage(d.img, 0, 0, d.img.getWidth(), d.img.getHeight(), 1);
			switch(game){
			case MapCreate: btg.engine = new Engine_MapCreate(btg, d); break;
			case Dungeon: btg.engine = new Engine_Dungeon(btg, d); break;
			default: time++;
			}
			choosing(g2, Choice);
			g2.drawImage(logo, d.w-158, d.h-50, 158, 50, 1);
		}
	}
	
	protected void choosing(GraphicsF g2, Image[] Choice){
		if(chosen == nchosen){
			tt = 0;
			while (chosen<0){chosen+=Choice.length/2;}
			while (chosen>=Choice.length/2){chosen-=Choice.length/2;}
			nchosen = chosen;
		} 
		else if (tt<50){tt++;} 
		else {tt = 0; chosen = nchosen;}
		for(int i = -2; i < 3; i++){
			k = chosen+i; 
			while (k<0){k+=Choice.length/2;} while (k>=Choice.length/2){k-=Choice.length/2;}
			float ii = (i+(tt)/50f*(chosen-nchosen)); int y = (int)(d.h/2-50+ii*180);
			g2.drawImage(Choice[2*k], (int)(d.w/2-50-ii*ii*20), y, 100, 100, 1-(Math.abs(ii))/3);
			g2.drawImage(Choice[2*k+1], (int)(d.w/2-56+150-ii*ii*20), y+26, 113, 47, 1-(Math.abs(ii))/3);
		}
		g2.setAlpha(1);
		g2.g.setColor(Color.WHITE);
		drawString("Choose Game:", d.w/8, d.h/2-10, 0, 40, 0, 0, g2.g);
		drawString("Press up and down key to select", d.w/8, d.h/2+20, 2, 20, 0, 0, g2.g);
		drawString("Press right arrow key to proceed", d.w/8, d.h/2+45, 2, 20, 0, 0, g2.g);
	}
	protected void choosing(GraphicsF g2, Image img, String desc, int except){
		cexcept = except;
		if(chosen == nchosen){
			tt = 0;
			while (chosen<0){chosen+=choice.length;}
			while (chosen>=choice.length){chosen-=choice.length;}
			nchosen = chosen;
		} 
		else if (tt<50){tt++;} 
		else {tt = 0; chosen = nchosen;}
		for(int i = -2; i < 3; i++){
			k = chosen+i; 
			while (k<0){k+=choice.length;} while (k>=choice.length){k-=choice.length;}
			float ii = (i+(tt)/50f*(chosen-nchosen)); int y = (int)(d.h/2-50+ii*180);
			g2.drawImage(img, (int)(d.w/2-50-ii*ii*20), y, 100, 100, 1-(Math.abs(ii))/3);
			if(!((k == except)|(except == -2))){
				g2.drawImage(cross, (int)(d.w*3/4+50-ii*ii*20), y+25, 50, 50);
			}
			g2.g.setColor(Color.WHITE);
			drawString(choice[k], (int)(d.w/2-56+150-ii*ii*20), y+50+15, 0, 40, 0, 0, g2.g);
			//g2.drawImage(img, (int)(d.w/2-56+150-ii*ii*20), y+26, 113, 47, 1-(Math.abs(ii))/3);
		}
		g2.setAlpha(1);
		g2.g.setColor(Color.WHITE);
		drawString(desc+":", d.w/8, d.h/2-10, 0, 40, 0, 0, g2.g);
		g2.g.setFont(new Font(fontstr, 2, 20));
		drawString("Press up and down key to select", d.w/8, d.h/2+20, 2, 20, 0, 0, g2.g);
		drawString("Press right arrow key to proceed", d.w/8, d.h/2+45, 2, 20, 0, 0, g2.g);
		drawString("Press left arrow key to go back", d.w/8, d.h/2+70, 2, 20, 0, 0, g2.g);
	}
	
	protected void sInit() throws UnhandledException{}
	
	private void Init() throws UnhandledException{}
	
	protected Texture[] getTextureList(String[] nameList) throws UnhandledException{
		Texture[] Ts = new Texture[nameList.length-1];
		String source = nameList[0];
		for(int i = 1; i < Ts.length+1; i++){
			Ts[i-1] = new Texture(source+"/"+nameList[i], d);
		}
		return Ts;
	}
	
	/* Preoccupied Indices:
	 * -1 end of file (default)
	 * 3 start of file
	 * 4 end of level
	 * 5 end of horizontal
	 * 6 end of item (for String)
	 * */
	protected Map loadMap(String path, String type) throws UnhandledException{
		MapType dim; int w, h, t; Map mpp;
		IOHeader ioh = new IOHeader();
		ioh.loadIOHeader(z, "header.h");
		dim = ioh.Dim; w = ioh.W; h = ioh.H; enc = ioh.enc;
		mt = dim;
		if(dim != MapType.Flat){t = ioh.T;}
		else {t = 0;}
		mpp = new Map(dim, w, h, t, type);
		itma:try{
			BufferedReader io = z.readFile(path);
			int line = 0;
			try{
				while(!((line == 3)|(line == -1))){
					line = io.read();
				}
			} catch(Exception e){}
			if(line == -1){
				throw new UnhandledException(103, "The starting character is not found in file \""+path+"\"");
			}//*/
			if(mt == MapType.Flat){
				for(int j = 0; j < h; j++){io.read();
				for(int i = 0; i < w; i++){
					if(type == "int") {
						mpp.setTile(i, j, k, byteToInt(io.read(), io.read(), io.read(), io.read(), enc));
					}
					else if (type == "String"){
						int re = 0; int[] bt = {};
						while(re != 6){
							re = io.read();
							if(re != 6) bt = Arrays.append(bt, re);
						}
						mpp.setTile(i, j, getString(bt));
					}
				}}
			} else {
				for(int k = 0; k < t; k++){io.read();
				for(int j = 0; j < h; j++){io.read();
				for(int i = 0; i < w; i++){
					if(type == "int") {
						mpp.setTile(i, j, k, byteToInt(io.read(), io.read(), io.read(), io.read(), enc));
					}
					else if (type == "String"){
						int re = 0; int[] bt = {};
						while(re != 6 && re != -1){
							re = io.read();
							if(re != 6) bt = Arrays.append(bt, re);
						} if(re == -1) break itma;
						mpp.setTile(i, j, k, getString(bt));
					}
				}}}
			}
		} catch (FileNotFoundException e) {
			throw new UnhandledException(101, "File "+ path +" is not found");
		} catch (IOException e){
			throw new UnhandledException(103, "File IO read error");
		}
		return mpp;
	}
	
	protected void saveMap(String URL, Map mpp) throws UnhandledException{
		try{
			byte[] content = new byte[0];
			content = Arrays.append(content, 3);
			String type = mpp.type;
			if(mpp.m == MapType.Flat){
				for(int j = 0; j < mpp.h; j++){
					content = Arrays.append(content, 5);
				for(int i = 0; i < mpp.w; i++){
					if(type == "int") {
						byte[] bytes = intToByte(mpp.getTile(i, j), enc);
						for(int s = 0; s < 4; s++) content = Arrays.append(content, bytes[s]);
					}
					else if(type == "String"){
						byte[] b = mpp.getTileStr(i, j).getBytes();
						content = Arrays.append(content, b);
						content = Arrays.append(content, 6);
					}
				}}
			} else {
				for(int k = 0; k < mpp.t; k++){content = Arrays.append(content, 4);
				for(int j = 0; j < mpp.h; j++){content = Arrays.append(content, 5);
				for(int i = 0; i < mpp.w; i++){
					if(type == "int") {
						byte[] bytes = intToByte(mpp.getTile(i, j, k), enc);
						for(int s = 0; s < 4; s++) content = Arrays.append(content, bytes[s]);
					}
					else if(type == "String"){
						content = Arrays.append(content, mpp.getTileStr(i, j, k).getBytes());
						content = Arrays.append(content, 6);
					}
				}}}
			}
		//	BUG.debug(URL);
			z.writeFile(URL, content);
		/*	FileOutputStream fos = new FileOutputStream(URL);
			fos.write(3);
			if(mpp.m == MapType.Flat){
				for(int j = 0; j < mpp.h; j++){fos.write(5);
				for(int i = 0; i < mpp.w; i++){
					fos.write(enc+mpp.getTile(i, j));
				}}
			} else {
				for(int k = 0; k < mpp.t; k++){fos.write(4);
				for(int j = 0; j < mpp.h; j++){fos.write(5);
				for(int i = 0; i < mpp.w; i++){
					fos.write(enc+mpp.getTile(i, j, k));
				}}}
			}//*/
		} catch (Exception e){e.printStackTrace();
			throw new UnhandledException(104, "File Output Exception at void saveMap(URL)");
		}
	}

	protected static byte[] intToByte(int in, int enc){
		return new byte[]{(byte)(enc+(in>>24)),(byte)(enc+(in>>16)),(byte)(enc+(in>>8)),(byte)(enc+in)};
	}

	protected static int byteToInt(int b1, int b2, int b3, int b4, int enc){
		return ((b1-enc+256)%256<<24)+((b2-enc+256)%256<<16)+((b3-enc+256)%256<<8)+((b4-enc+256)%256);
	}
	
	protected void appendFile(String URL, String content) throws IOException{
		BufferedReader fr = new BufferedReader(new FileReader(URL));
		String r = fr.readLine();
		BufferedWriter bw = new BufferedWriter(new FileWriter(URL));
		if(r == null){bw.write(content);} 
		else {
			String rt = r + "," + content;
			rt = rt.trim().replace(",,", ",");
			if(rt.endsWith(",")) rt = rt.substring(0, rt.length()-1);
			bw.write(rt);
		}
		fr.close(); bw.close();
	}
	
	protected void saveData(String URL, String[] data) throws UnhandledException{
		try{
			BufferedWriter bw = new BufferedWriter(new FileWriter(URL));
			for(int i = 0; i < data.length; i++){
				bw.write(data[i]);
				bw.write(",");
			}
		} catch (Exception e){
			throw new UnhandledException(104, "File Output Exception at void saveData(URL, data)");
		}
	}
	
	protected void sRun(Graphics g) throws UnhandledException{}
	
	protected void selected(){
		switch(chosen){
		case 0: game = Games.MapCreate; break;
		case 1: game = Games.Dungeon; break;
		default: 
			System.err.println("The index of chosen:"+chosen+" is not in the switch case"); 
			break;
		}
	}
	
	//processKeyEvent(KeyEvent k) - processes key event input from the game
	protected void processKeyEventG(KeyEvent k) throws UnhandledException{
		if((time <= timeT)&(time > 0)){
			if(nchosen==chosen){
				switch(k.getKeyCode()){
				case 39: //Key Right
				case 68: //Key D
					selected();
					break;
				}
			} 
		}
	}
	public void processKeyEvent(KeyEvent k) throws UnhandledException{
		processKeyEventG(k);
		switch(k.getKeyCode()){
		case 32: //Space Bar
			if(time > timeT+1/6f*(timeMax-2*timeT)){time = (int) (timeT+1/6f*(timeMax-2*timeT));}
			break;
		}
		if((time <= timeT)&(time > 0)){
			if(nchosen==chosen){
				switch(k.getKeyCode()){
				case 38: //Key Up
				case 87: //Key W
					nchosen--;
					break;
				case 40: //Key Down
				case 83: //Key S
					nchosen++;
					break;
				case 37: //Key Left
				case 65: //Key A
					back();
					break;
				}
			}
		}
	}
	
	public void processMouseEventR(MouseEvent m, int MouseState) throws UnhandledException{}
	public void processMouseEventL(MouseEvent m, int MouseState) throws UnhandledException{}
	public void processMouseEventM(MouseEvent m) throws UnhandledException{}
	public void processMouseEvent(MouseEvent m, int MouseState) throws UnhandledException{
		/**ms: mouse state*/
		// m.getModifiersEx(): 1024 Left Press, 4096 Right Press (only 1, 2 of ms is triggered)
		mouseX = m.getX(); mouseY = m.getY();
		if(MouseState == 1) mouse = m.getModifiersEx();
		switch(mouse){
		case 0: 
			if(MouseState == 4) processMouseEventM(m);
			break;
		case 1024: 
			switch(MouseState){
			case 0://Clicked
				if((time <= timeT)&(time > 0)){
					if(nchosen == chosen){
						int dif = m.getY() - d.h/2;
						//g2.drawImage(cross, (int)(d.w*3/4+50-ii*ii*20), y+25, 50, 50);
						int i = (int)Math.floor((m.getY()-25-d.h/2+50)/180f - tt/50f*(chosen-nchosen));
						float ii = (i+(tt)/50f*(chosen-nchosen));
						if(inRange(m.getX(), d.w*3/4+50-ii*ii*20, d.w*3/4+50-ii*ii*20 + 50)&
								inRange(m.getY(), d.h/2 - 25 + ii*180, d.h/2 + 25 + ii*180)){
							if(game != Games.Null){
								if(((i + chosen + choice.length) % choice.length != cexcept)&(cexcept != -2)){
									deleteMap((i + chosen + choice.length) % choice.length);
									break;
								}
							}
						} else if(inRange(m.getX(), 0, d.w/2-50-ii*ii*20)) {
							back(); break;
						}
						if(Math.abs(dif) <= 100/2){
							selected();
						} else if (dif > 0){
							nchosen++;
						} else {
							nchosen--;
						}
					}
				} else if(time > timeT+1/6f*(timeMax-2*timeT)){time = (int) (timeT+1/6f*(timeMax-2*timeT));}
				break;
			case 1://Pressed
				break;
			case 2://Released
				break;
			case 3://Dragged
				break;
			case 4://Moved
				break;
			}
			processMouseEventL(m, MouseState);
			break;
		case 4096: 
			processMouseEventR(m, MouseState);
			break;
		}
		if(MouseState == 0) mouse = 0;
	}
	
	public void processMouseWheelEventG(MouseWheelEvent mw) throws UnhandledException{}
	public void processMouseWheelEvent(MouseWheelEvent mw) throws UnhandledException{
		if((time <= timeT)&(time > 0)){
			if(nchosen==chosen){
				if(mw.getWheelRotation() > 0){
					nchosen++;
				} else {
					nchosen--;
				}
			}
		}
		processMouseWheelEventG(mw);
	}
	
	public void deleteMap(int mapNum) throws UnhandledException{
		String[] del = {".map"};
		File file;
		for(int i = 0; i < del.length; i++){
			file = new File("res/maps/0maps/"+pre+choice[mapNum]+del[i]);
			file.delete();
		}
		String[] ch = choice;
		deleteFromFile("res/data/gameprogress.dt", pre+ch[mapNum]);
		deleteFromFile("res/data/maps.dt", pre+ch[mapNum]);
	}
	
	protected boolean deleteFromFile(String URL, String content) throws UnhandledException{
		try{
			BufferedReader fr = new BufferedReader(new FileReader(URL));
			String r = fr.readLine();
			BufferedWriter bw = new BufferedWriter(new FileWriter(URL));
			if(r == null){fr.close(); bw.close(); return false;} 
			else {
				String[] rs = r.replace(" ", "").split(",");
				int i = 0; r = "";
				while(i < rs.length){
					if (!rs[i].contentEquals(content)){
						r += rs[i];
						if (i != rs.length - 1){
							r += ",";
						}
					}
					i++;
				}
				bw.write(r);
				fr.close(); bw.close();
				if(i >= rs.length - 1){
					return false;
				} else {
					return true;
				}
			}
		} catch (IOException e){
			throw new UnhandledException(100, "File IO Exception");
		}
	}

	protected void lightup(int i, int j, int brightness) throws UnhandledException {
		for(int I = -5; I <= +5; I++)
			for(int J = -5; J <= +5; J++)
				try{
					mLight.setTile(i+I, j+J, flr, Math.max(Math.min(100, mLight.getTile(i+I, j+J, flr) + brightness/(I*I+J*J+1)),0));
				} catch(Exception e){}
	}

	protected void printLight2D(Tiles tiles, Items items, SpecialObjects spobj, Mobs mobs, Camera navCm) throws UnhandledException {
		mLight.init(brightness);
		int Imin = (int) Math.max(0, Math.floor(navCm.xf)-5);
		int Jmin = (int) Math.max(0, Math.floor(navCm.yf)-5);
		int Imax = (int) Math.min(m.w, Math.ceil(544f/w/sizeEnlarged+navCm.xf+1)+5);
		int Jmax = (int) Math.min(m.h, Math.ceil(544f/h/sizeEnlarged+navCm.yf+1)+5);
		for(int j = Jmin; j < Jmax; j++){
			for(int i = Imin; i < Imax; i++){
				try{lightup(i, j, getTileItem(i, j, m).brightness);}catch(Exception e){e.printStackTrace();}
				//try{lightup(i, j, getTileItem(i, j, mLowe).brightness);}catch(Exception e){}
				try{lightup(i, j, getTileItem(i, j, mOver).brightness);}catch(Exception e){}
				//try{lightup(i, j, getTileItem(i, j, mItem).brightness);}catch(Exception e){}
			}
		}
	}
	protected void printScreen2D(Tiles tiles, Items items, SpecialObjects spobj, Mobs mobs, Camera navCm) throws UnhandledException{
		int Imin = (int) Math.max(0, Math.floor(navCm.xf));
		int Jmin = (int) Math.max(0, Math.floor(navCm.yf));
		int Imax = (int) Math.min(m.w, Math.ceil(544f/w/sizeEnlarged+navCm.xf+1));
		int Jmax = (int) Math.min(m.h, Math.ceil(544f/h/sizeEnlarged+navCm.yf+1));
		for(int layer = 0; layer <= 3; layer++){
			for(int j = Jmin; j < Jmax; j++){
				for(int i = Imin; i < Imax; i++){
					switch(mt){
					case Flat:
					case Leveled: 
						switch(layer){
						case 0: 
							printTile2D(getTile(i, j, m), tiles, i, j, -1, navCm, m);
							Audio.playlist(getTileItem(i, j, m).always);
							break;
						case 1:
							printTile2D(getTile(i, j, mLowe), tiles, i, j, -1, navCm, mLowe);
							break;
						case 2:
							if (getTile(i, j, mOver) == -1) break;
							if(getTile(i, j, mOver)%2 == 0)
								printTile2D(getTile(i, j, mOver)/2, tiles, i, j, -1, navCm, mOver);
							break;
						case 3:
							printObject2D(getTile(i, j, mItem), items, mobs, i, j, navCm);
							st:{
								if (getTile(i, j, mOver) == -1) break st;
								if(getTile(i, j, mOver)%2 == 1)
									printTile2D(getTile(i, j, mOver)/2, spobj, i, j, -1, navCm, mOver);
							}
							for(int s = 0; s < extraObj.length; s++){
								Object[] xobjitm = (Object[])extraObj[s];
								if(Math.round((Float)xobjitm[3]) == j) {
									if (Math.round((Float)xobjitm[2]) == i) {
										printMob2D((Integer) xobjitm[0], (Mobs) xobjitm[1], (Float) xobjitm[2],
												(Float) xobjitm[3], (int) xobjitm[4], (int)xobjitm[6], (Camera) xobjitm[5]);
										extraObj = Arrays.delete(extraObj, s);
									}
								}
							}
							break;
						}
						break;
					}
				}
			}
		}
	}

	protected int getTile(int x, int y, Map mp) throws UnhandledException{
		if(inRange(x, 0, m.w-1)&inRange(y, 0, m.h-1)){
			switch(mp.m){
			case Flat:
				return mp.getTile(x, y);
			case Leveled:
				return mp.getTile(x, y, flr);
			}
		}
		return -1;
	}
	protected String getTileStr(int x, int y, Map mp) throws UnhandledException{
		if(inRange(x, 0, m.w-1)&inRange(y, 0, m.h-1)){
			switch(mp.m){
			case Flat:
				return mp.getTileStr(x, y);
			case Leveled:
				return mp.getTileStr(x, y, flr);
			}
		}
		return null;
	}
	
	protected Map setTile(int x, int y, Object value, Map mp) throws UnhandledException{
		switch(mp.m){
		case Flat:
			mp.setTile(x, y, value); break;
		case Leveled:
			mp.setTile(x, y, flr, value); break;
		}
		return mp;
	}
	
	protected Tile getTileItem(int x, int y, Map mp) throws UnhandledException{
		int indd = getTile(x, y, mp);
		if(mp == mOver) {
			if(indd%2 == 0){
				return (Tile)tiles.getItem(indd/2);
			} else {
				return (Tile)spobj.getItem(indd/2);
			}
		} else {
			return (Tile)tiles.getItem(indd);
		}
	}

	protected Item getItemItem(int x, int y, Map mp) throws UnhandledException{
		int indd = getTile(x, y, mp);
		if(mp == mItem) {
			return (Item)items.getItem(indd/2);
		} else {
			return null;
		}
	}

	protected Mob getMobItem(int x, int y, Map mp) throws UnhandledException{
		int indd = getTile(x, y, mp);
		if(mp == mItem) {
			return (Mob)mobs.getItem(indd/2);
		} else {
			return null;
		}
	}

	protected Texture getTileTexture(int x, int y, Map mp) throws UnhandledException{
		int indd = getTile(x, y, mp);
		if(mp == mOver) {
			if(indd%2 == 0){
				return tiles.tex;
			} else {
				return spobj.tex;
			}
		} else {
			return tiles.tex;
		}
	}
	protected int[] getNeibourDirs(int itemInd, int x, int y, Tiles tils, Map mp) throws UnhandledException{
		if((((Tile)tils.getItem(itemInd)).overlaid.dir == ConnectDir.DirType.Wall)|
				(((Tile)tils.getItem(itemInd)).overlaid.dir == ConnectDir.DirType.Door)){
			return Arrays.addEachItemIn(getNeibourDirs(ConnectDir.DirType.Wall, x, y, tils, mp), 
					getNeibourDirs(ConnectDir.DirType.Door, x, y, tils, mp));
		} else {
			return getNeibourDirs(itemInd, x, y, mp);
		}
	}
	protected int[] getNeibourDirs(int itemInd, int x, int y, Map mp) throws UnhandledException{
		int div = 1;
		if(mp == mOver) div = 2;
		int[] dirs = new int[8]; int i = 0;
		for(int j = 0; j < 8; j++){
			dirs[j] = 0;
		}
		if(y>0){		if(getTile(x, y-1, mp)/div==itemInd)	{dirs[i]=1;}}i++;
		if(x<mp.w-1){
			if(y>0){	if(getTile(x+1, y-1, mp)/div==itemInd){dirs[i]=2;}}i++;
						if(getTile(x+1, y, mp)/div==itemInd)	{dirs[i]=3;}i++;
			if(y<mp.h-1){if(getTile(x+1, y+1, mp)/div==itemInd){dirs[i]=4;}}i++;
		}
		if(y<mp.h-1){	if(getTile(x, y+1, mp)/div==itemInd)	{dirs[i]=5;}}i++;
		if(x>0){
			if(y<mp.h-1){if(getTile(x-1, y+1, mp)/div==itemInd){dirs[i]=6;}}i++;
						if(getTile(x-1, y, mp)/div==itemInd)	{dirs[i]=7;}i++;
			if(y>0){	if(getTile(x-1, y-1, mp)/div==itemInd){dirs[i]=8;}}i++;
		}
		return dirs;
	}
	protected int[] getNeibourDirs(ConnectDir.DirType drt, int x, int y, Tiles tils, Map mp) throws UnhandledException{
		int[] dirs = new int[8]; int i = 0;
		for(int j = 0; j < 8; j++){
			dirs[j] = 0;
		}
		if(y>0){		if(getTileItem(x, y-1, mp).overlaid.dir==drt)	{dirs[i]=1;}}i++;
		if(x<mp.w-1){
			if(y>0){	if(getTileItem(x+1, y-1, mp).overlaid.dir==drt){dirs[i]=2;}}i++;
						if(getTileItem(x+1, y, mp).overlaid.dir==drt)	{dirs[i]=3;}i++;
			if(y<mp.h-1){if(getTileItem(x+1, y+1, mp).overlaid.dir==drt){dirs[i]=4;}}i++;
		}
		if(y<mp.h-1){	if(getTileItem(x, y+1, mp).overlaid.dir==drt)	{dirs[i]=5;}}i++;
		if(x>0){
			if(y<mp.h-1){if(getTileItem(x-1, y+1, mp).overlaid.dir==drt){dirs[i]=6;}}i++;
						if(getTileItem(x-1, y, mp).overlaid.dir==drt)	{dirs[i]=7;}i++;
			if(y>0){	if(getTileItem(x-1, y-1, mp).overlaid.dir==drt){dirs[i]=8;}}i++;
		}
		return dirs;
	}
	
	protected void printTile2D(int itemInd, Tiles itms, int x, int y, int entity, Camera navCm, Map mp) throws UnhandledException{
		printTile2D(itemInd, itms, x, y, entity, navCm, mp, x, y);}
	protected void printTile2D(int itemInd, Tiles itms, int xx, int yy, int entity, Camera navCm, Map mp, int x, int y) throws UnhandledException{
		printTile2D(itemInd, itms, xx, yy, entity, navCm, mp, x, y, false);}
	protected void printTile2D(int itemInd, Tiles itms, int xx, int yy, int entity, Camera navCm, Map mp, int x, int y, boolean careful) throws UnhandledException{
		printTile2D(itemInd, itms, xx, yy, entity, navCm, mp, x, y, careful, false, "A");
	}
	protected void printTile2D(int itemInd, Tiles itms, int xx, int yy, int entity, Camera navCm, Map mp, int x, int y, boolean careful, boolean bottom, String printPart) throws UnhandledException{
		/* bottom: for liquid use: state if the printing is a part of upper half printed by liquid
		 * printpart: for wall use: state if the printing is All, Left, or Right (A, L or R)*/
		int Flip = 0; boolean Rot = false;
		int animat = (int) (2.7*(xx%7)+1.2*(xx%3)+3.2*(yy%6)+7.1*(yy%3));
		if(itemInd == -1){return;}
		Tile itm = (Tile) itms.getItem(itemInd);
		if(itm.overlaid.dir == ConnectDir.DirType.QuadraOverlaid){
			printQuadrantTile2D(
					itemInd, itms, xx, yy, entity, navCm, mp, x, y, careful, bottom, printPart);
			return;
		} else if (itm.overlaid.dir == ConnectDir.DirType.Bridge){
			printBridgeTile2D(
					itemInd, itms, xx, yy, entity, navCm, mp, x, y, careful, bottom, printPart);
			return;
		}
		if(entity == -1){
			int[] dirs = getNeibourDirs(itemInd, x, y, itms, mp);
			entity = Math.min(itm.numEntity-1, itm.overlaid.returnFrame(dirs));
			Flip = itm.overlaid.returnFlip(dirs, entity);
			Rot = itm.overlaid.returnRot(dirs, entity);
			d.setFlipNRot(Flip, Rot);
		}
		if((entity < itm.numEntity)&(entity >= 0)){
			float X = xx - navCm.xf, Y = yy - navCm.yf;
			if((itm.overlaid.dir == ConnectDir.DirType.Fluid)&(entity == 1)){
				if((y>0)&(!bottom)){
					Tile ittm = (Tile) itms.getItem(getTile(xx, yy-1, mp));
					if(itemInd == getTile(xx, yy-1, mp)){
						entity = 0;
					} else {
						if(((getTileItem(xx, yy, mOver).overlaid.dir == ConnectDir.DirType.Wall)
								&(getTileItem(xx, yy, mOver).overlaid.dir == ConnectDir.DirType.Wall))|
								((getTileItem(xx, yy, mOver).overlaid.dir == ConnectDir.DirType.Door)
								&(getTileItem(xx, yy, mOver).overlaid.dir == ConnectDir.DirType.Door))){
							if(printPart == "R"){
								if(itemInd == getTile(xx+1, yy-1, mp)){entity = 1;}
							} else if (printPart == "L"){
								if(itemInd == getTile(xx-1, yy-1, mp)){entity = 1;}
							}
						}
					}
					if((entity == 1)&(ittm.sideX != -1)){
						if(printPart == "A"){
							if(((getTileItem(xx, yy-1, mOver).overlaid.dir == ConnectDir.DirType.Wall)&
									(getTileItem(xx, yy-2, mOver).overlaid.dir == ConnectDir.DirType.Wall))|
									((getTileItem(xx, yy-1, mOver).overlaid.dir == ConnectDir.DirType.Door)&
									(getTileItem(xx, yy-2, mOver).overlaid.dir == ConnectDir.DirType.Door))){
								setProtectedRegion(X, Y, X+0.5f, Y+1);
								printTile2D(getTile(xx-1, yy-1, m), itms, xx, yy, -1, navCm, m, xx, yy-1, true, true, "L");
								setProtectedRegion(X+0.5f, Y, X+1, Y+1);
								printTile2D(getTile(xx+1, yy-1, m), itms, xx, yy, -1, navCm, m, xx, yy-1, true, true, "R");
								d.defaultProtectedRegion();
								Tile til = getTileItem(xx, yy-1, m);
								Tile till = getTileItem(xx, yy-1, mOver);
								if(getTileTexture(xx, yy-1, mOver) != spobj.tex){
									printShadowSprite(
											itms.tex, getTileTexture(xx, yy-1, mOver), 
											X, Y, til.sideY, til.sideX+
											(int) (til.offset*((clock/clockset+animat)%16)/16f),
											till.sideY, till.sideX, 
											0, careful, w, h, 0, 0, false
									);
								}
							} else {
								printTile2D(getTile(xx, yy-1, mp), itms, xx, yy, -1, navCm, mp, xx, yy-1, true, true, printPart);
							}
						} else if((getTileItem(xx, yy-1, mOver).overlaid.dir != ConnectDir.DirType.Wall)&
								(getTileItem(xx, yy-1, mOver).overlaid.dir != ConnectDir.DirType.Door)){
							switch(printPart){
							case "L": 
								printTile2D(getTile(x+1, yy-1, mp), itms, xx, yy, -1, navCm, mp, xx, yy-1, true, true, printPart);
								break;
							case "R": 
								printTile2D(getTile(x-1, yy-1, mp), itms, xx, yy, -1, navCm, mp, xx, yy-1, true, true, printPart);
								break;
							}
						} else {
							printTile2D(getTile(x, yy-1, mp), itms, xx, yy, -1, navCm, mp, xx, yy-1, true, true, printPart);
						}
					}
				} else {entity = 0;}
			} else if(((getTileItem(x, y, mp).overlaid.dir == ConnectDir.DirType.Wall)|
					(getTileItem(x, y, mp).overlaid.dir == ConnectDir.DirType.Door))&(entity != 0)){
				Tile ittm;
				d.setDeProtectedRegion();
				if(printPart != "R"){
					if(x>0){
						ittm = getTileItem(x-1, y, m);
						d.setMergeCol(itms.blend, ittm.indCol);
						setProtectedRegion(X, Y, X+0.5f, Y+1);
						if(((getTileItem(x-1, y, mOver)).overlaid.dir == ConnectDir.DirType.Wall)|
								((getTileItem(x-1, y, mOver)).overlaid.dir == ConnectDir.DirType.Door)){
							printTile2D(getTile(x, y, mOver)/2, itms, xx, yy, 0, navCm, mOver, x-1, y, true, bottom, "L");
						} else if(ittm.overlaid.dir == ConnectDir.DirType.Fluid){
							printTile2D(getTile(x-1, y, m), tiles, xx, yy, 1, navCm, m, x-1, y, true, bottom, "L");
						} else {
							printTile2D(getTile(x-1, y, m), tiles, xx, yy, -1, navCm, m, x-1, y, true, bottom, "L");
						}
					} else {
						ittm = itm;
						d.setMergeCol(itms.blend, ittm.indCol);
						setProtectedRegion(X, Y, X+0.5f, Y+1);
						printTile2D(getTile(x, y, mOver)/2, tiles, xx, yy, 0, navCm, mOver, x, y, true, bottom, "L");
					}
				};
				if(printPart != "L"){
					if(x<mp.w-1){
						ittm = getTileItem(x+1, y, m);
						d.setMergeCol(itms.blend, ittm.indCol);
						setProtectedRegion(X+0.5f, Y, X+1, Y+1);
						if((getTileItem(x+1, y, mOver).overlaid.dir == ConnectDir.DirType.Wall)|
								(getTileItem(x+1, y, mOver).overlaid.dir == ConnectDir.DirType.Door)){
							printTile2D(getTile(x, y, mOver)/2, itms, xx, yy, 0, navCm, mOver, x+1, y, true, bottom, "R");
						} else if(ittm.overlaid.dir == ConnectDir.DirType.Fluid){
							printTile2D(getTile(x+1, y, m), tiles, xx, yy, 1, navCm, m, x+1, y, true, bottom, "R");
						} else {
							printTile2D(getTile(x+1, y, m), tiles, xx, yy, -1, navCm, m, x+1, y, true, bottom, "R");
						}
					} else {
						ittm = itm;
						d.setMergeCol(itms.blend, ittm.indCol);
						setProtectedRegion(X+0.5f, Y, X+1, Y+1);
						printTile2D(getTile(x, y, mOver)/2, tiles, xx, yy, 0, navCm, mOver, x, y, true, bottom, "R");
					}
				}
				d.deProtectedRegion();
				if(!bottom){d.defaultProtectedRegion();}
			} 
			d.setMergeCol(itms.blend, itm.indCol);
			if(bottom){
				printSprite(
						itms.tex, X, Y, itm.sideY, itm.sideX+(itm.offset)*entity+
						(int) (itm.offset*((clock/clockset+animat)%16)/16f), 
						itms.blend.p[itm.indCol], true, careful
				);
			} else {
				try {
					SpecialObject itmm = (SpecialObject) itm;
					String animf = getContent(getTileStr(x, y, mStat), "<animate>");
					if (animf == "") animf = 0+"";
					String ddd = getContent(getTileStr(x, y, mStat), "<push>");
					if (ddd == "") ddd = 0+"";
					String dir = getContent(getTileStr(x, y, mStat), "<pushdir>");
					float dx = 0, dy = 0;
					if (dir != ""){
						int step  = Integer.parseInt(ddd);
						switch(Integer.parseInt(dir)){
						case 0: dy = -step*spobjbspeed; break;
						case 1: dx = +step*spobjbspeed; break;
						case 2: dy = +step*spobjbspeed; break;
						case 3: dx = -step*spobjbspeed; break;
						}
					}
					SpecialObjects itmms = (SpecialObjects) itms;
					printSprite(
							itms.tex, X + dx, Y + dy, itm.y, itm.x+(itm.offset+itmm.numFrameTriggered)*entity+
							(int) (itm.offset*((clock/clockset+animat)%16)/16f) + 
							(int)(Integer.parseInt(animf)*spobjaspeed), 
							0, careful, itmms.wid, itmms.hei, w-itmms.wid, h-itmms.hei
					);
				} catch (Exception e){
					printSprite(
							itms.tex, X, Y, itm.y, itm.x+(itm.offset)*entity+
							(int) (itm.offset*((clock/clockset+animat)%16)/16f),
							0, true, careful
					);
				}
				if(itm.overlaid.dir == ConnectDir.DirType.Fluid){
					Tile till = getTileItem(xx, yy-1, mOver);
					int bool = 0;
					Texture txx = getTileTexture(xx, yy-1, mOver);
					if((getTileItem(xx, yy-2, mOver).overlaid.dir == ConnectDir.DirType.Wall)|
							(getTileItem(xx, yy-2, mOver).overlaid.dir == ConnectDir.DirType.Door)){
						bool = 1;
					} else if ((getTileItem(xx, yy, mOver).overlaid.dir == ConnectDir.DirType.Wall)|
							(getTileItem(xx, yy, mOver).overlaid.dir == ConnectDir.DirType.Door)){
						bool = 2;
					}
					if(((till.overlaid.dir == ConnectDir.DirType.Wall)|
							(till.overlaid.dir == ConnectDir.DirType.Door))&
							(bool > 0)){
						if((getTileItem(xx-1, yy-1, m) == itm)&(printPart != "R")){
							setProtectedRegion(X, Y, X+0.5f, Y+1);
							printShadowSprite(
									itms.tex, txx, X, Y, itm.y, itm.x+
									(int) (itm.offset*((clock/clockset+animat)%16)/16f),
									till.y, till.x+till.offset, 
									0, true, w, h, 0, 0, true
							);
							d.defaultProtectedRegion();
						}
						if((getTileItem(xx+1, yy-1, m) == itm)&(printPart != "L")){
							setProtectedRegion(X+0.5f, Y, X+1, Y+1);
							printShadowSprite(
									itms.tex, txx, X, Y, itm.y, itm.x+
									(int) (itm.offset*((clock/clockset+animat)%16)/16f),
									till.y, till.x+till.offset, 
									0, true, w, h, 0, 0, true
							);
							d.defaultProtectedRegion();
						}
					}
				}
			}
		} else {throw new UnhandledException(0x3, "Value of entity "+entity+" is out of range "
				+"in printTile2D(itemInd, itms, x, y, entity)");}
		d.resetFlipNRot();
	}
	protected void printBridgeTile2D(int itemInd, Tiles itms, int xx, int yy, int entity, Camera navCm, Map mp, int x, int y, boolean careful, boolean bottom, String printPart) throws UnhandledException{
		if(printPart != "A"){return;}
		if(bottom){return;}
		int Flip = 0; boolean Rot = false;
		Tile itm = (Tile) itms.getItem(itemInd);
		float X = xx - navCm.xf, Y = yy - navCm.yf;
		int[] dirs = new int[8], Dirs = new int [8];
		dirs = getNeibourDirs(itemInd, x, y, mp);
		for(int i = 0; i < itms.length; i++){
			ConnectDir.DirType dr = ((Tile)itms.getItem(i)).overlaid.dir;
			if(!((dr == ConnectDir.DirType.Fluid)|(dr == ConnectDir.DirType.Wall))){
				Dirs = getNeibourDirs(i, x, y, m);
				for(int j = 0; j < 8; j++){
					dirs[j] += Dirs[j];
				}
			}
		}
		for(int j = 0; j < 8; j++){
			dirs[j] = (int) Math.signum(dirs[j]);
		}
		entity = itm.overlaid.returnFrame(dirs);
		Rot = itm.overlaid.returnRot(dirs, entity);
		d.setFlipNRot(Flip, Rot);
		int animat = 0;
		printSprite(
				itms.tex, X, Y, itm.y, itm.x+(itm.offset)*entity+
				(int) (itm.offset*((clock/clockset+animat)%16)/16f), 
				itms.blend.p[itm.indCol], true, true
		);
		d.resetFlipNRot();
	}
	protected void printQuadrantTile2D(int itemInd, Tiles itms, int xx, int yy, int entity, Camera navCm, Map mp, int x, int y, boolean careful, boolean bottom, String printPart) throws UnhandledException{
		if(printPart != "A"){return;}
		if(bottom){return;}
		float X = xx - navCm.xf, Y = yy - navCm.yf;
		int Flip = 0; boolean Rot = false;
		Tile itm = (Tile) itms.getItem(itemInd);
		int[] Dirs = getNeibourDirs(itemInd, x, y, mp);
		int[] dirs = new int[3];
		for(int i = 0; i < 4; i++){
			d.setDeProtectedRegion();
			setProtectedRegion(X+0.5f*(i%2), Y+0.5f*(i/2), X+0.5f+0.5f*(i%2), Y+0.5f+0.5f*(i/2));
			switch (i){
			case 0: for(int j = 0; j < 3; j++){dirs[j] = (int) Math.signum(Dirs[(8-j)%8]);} break;
			case 1: for(int j = 0; j < 3; j++){dirs[j] = (int) Math.signum(Dirs[j]);} break;
			case 2: for(int j = 0; j < 3; j++){dirs[j] = (int) Math.signum(Dirs[j+4]);} break;
			case 3: for(int j = 0; j < 3; j++){dirs[j] = (int) Math.signum(Dirs[4-j]);} break;
			}
			entity = itm.overlaid.returnFrame(dirs);
			Rot = itm.overlaid.returnRot(dirs, entity);
			d.setFlipNRot(Flip, Rot);
			int animat = 0;
			printSprite(
					itms.tex, X, Y, itm.y, itm.x+(itm.offset)*entity+
					(int) (itm.offset*((clock/clockset+animat)%16)/16f), 
					itms.blend.p[itm.indCol], true, true
			);
			d.deProtectedRegion();
		}
		d.resetFlipNRot();
	}
	
	protected void printMob2D(int itemInd, Mobs itms, float x, float y, int dir, int fightsec, Camera navCm) throws UnhandledException{
		float X = x - navCm.xf, Y = y - navCm.yf;
		Mob itm = (Mob) itms.getItem(itemInd);
		d.setMergeCol(itms.blend, itm.indCol);
		int txX = itm.x, txY = itm.y;
		switch(dir){
		case 0: txX += 2*(itm.numFrameWalk+itm.offset); break;
		case 2: break;
		case 1: d.setFlipNRot(1, false);
		case 3:
			txX += itm.numFrameWalk+itm.offset;
			break;
		}
		if(!((x == Math.floor(x))&(y == Math.floor(y)))){
			txX += (int) (itm.offset + 1f*(itm.numFrameWalk-1)*((clock/clockset)%walkPeriod)/walkPeriod) + 1;
		}
		if(fightsec != -1) {txY += 3; txX += fightsec%itm.numFrameWalk;}
		float animat = x%3 + x%8 + y%7 - y%3;
		printSprite(
				itms.tex, X, Y, 
				txY, txX + (int) (itm.offset*((clock/clockset+animat)%8)/8f), 0, false, itm.wid,
				itm.hei, itm.xoff, itm.yoff
		);
		d.resetFlipNRot();
		
	}
	protected void printHP(Mobs itms, Camera navCm) throws UnhandledException{
		for(int y = 0; y < m.h; y++){
			for(int x = 0; x < m.w; x++){
				float X = x - navCm.xf, Y = y - navCm.yf;
				if((getTile(x, y, mItem) == -1)|(getTile(x, y, mItem) %2 == 0)) continue; 
				Mob itm = (Mob) itms.getItem(getTile(x, y, mItem)/2);
				printHPBars(bar, X, Y, itm.xoff, itm.yoff, itm.wid,	itm.hei, 
						getMobStat("hp", (int)x, (int)y), getMobStat("lasthp", (int)x, (int)y), itm.maxhealth);
		}}
	}
	protected void printHPBars(Texture t, float x, float y, float xoff, float yoff, int tilW, int tilH, 
			float hp, float lasthp, float fullhp){
		int[] scor = mapToScreen(x, y, xoff, yoff, tilW, tilH, false, 1); //screen coordinates
		printHPBars(t, scor[0], scor[1], hp, lasthp, fullhp);
	}
	private void printHPBars(Texture t, int x, int y, float hp, float lasthp, float fullhp){
		/* printHPBar
		 * 0 grey: no HP there, space filling up to fullHP
		 * 1 green: basic HP (no repetition)
		 * 2 orange
		 * 3 yellow: HP near to death
		 * 4 red: HP deducted in this round
		 * length of HP bar depends on the average HP of the level.
		 * the second bar: for bosses, also longer bars
		 * */
		int state = 1; float se = 1f, yoff = 0, see = 0.6f*sizeEnlarged;
		float enl = 1.0f; int state2 = 0;
		hp *= enl; fullhp *= enl; lasthp *= enl;
		int num = 1; int minhp = (int) (100);
		int len = (int) (fullhp*se); int eka = 0;
		while(len > Math.pow(minhp, num)){
			len -= Math.pow(minhp, num);
			eka += Math.pow(minhp, num);
			num ++;
		}
		len -= Math.pow(minhp, num);
		eka += Math.pow(minhp, num);
		int quo = 0, rem = eka - (int) (hp*se), quo2 = 0, rem2 = eka - (int) (lasthp*se);//quotient, remainder = complement
		yoff += (num-1)*-15; state = num; int n = num;
		while(num > 0){
			num --;
			int pow = (int) Math.pow(minhp, num);
			quo = minhp - rem/pow; quo2 = minhp - rem2/pow; 
			rem = rem%pow; rem2 = rem2%pow; 
			int tec = (int) (minhp*see);
			if(num == n-1) tec = (int) ((minhp - (eka - (int) (fullhp*se))/pow)*see);
			if(inRange(x - tec/2 - 7*se, d.w/2-272, d.w/2+272, tec + 14*se)&
					inRange(y, d.h/2-272, d.h/2+272, 11*se)){
				d.placeSprite(t, x - tec/2 - 7*se, y + yoff, se, 7, 11, state, 0, -1, false);
				state2 = 0;
				for(int i = 0; i < quo*see; i++){
					d.placeSprite(t, x - tec/2 + i*se, y + yoff, se, 1, 11, state, 7+i%34, -1, false);
				}
				boolean den = false;
				state2 = 2;
				for(int i = (int) (quo*see); i < quo2*see; i++){
					d.placeSprite(t, x - tec/2 + i*se, y + yoff, se, 1, 11, state, 7+i%34+48*state2, -1, false);
					den = true;
				}
				if(!den&!(lasthp == 0)){
					state2 = 3;
					for(int i = (int) (quo2*see); i < quo*see; i++){
						d.placeSprite(t, x - tec/2 + i*se, y + yoff, se, 1, 11, state, 7+i%34+48*state2, -1, false);
					}
				}
				if(!den) quo2 = quo;
				if(num != n-1) state2 = 1;
				else state = 0;
				for(int i = (int) (quo2*see); i < tec; i++){
					d.placeSprite(t, x - tec/2 + i*se, y + yoff, se, 1, 11, state, 7+i%34+48*state2, -1, false);
				}
				d.placeSprite(t, x + tec/2, y + yoff, se, 7, 11, state, 41/7f+48/7f*state2, -1, false);
			}
			state = num;
			yoff	+= 15;
		}
	}
	
	protected void printObject2D(int itemInd, Items itms, Mobs mobs, int x, int y, Camera navCm) throws UnhandledException{
		if(itemInd == -1) return;
		switch(itemInd%2){
		case 0: //Items
			printItem2D(itemInd/2, itms, x, y, navCm);
			break;
		case 1: //Mobs
			String dirr = getContent(getTileStr(x, y, mStat), "<dir>");
			if(dirr == "") dirr = 0+"";
			String cont = getTileStr(x, y, mStat);
			printMob2D(itemInd/2, mobs, x, y, Integer.parseInt(dirr)+2,
					(haveTag(cont,"<fighting>"))?
							Integer.parseInt(getContent(cont,"<fighting>")):-1,
					navCm);
			break;
		}
	}
	
	protected void printItem2D(int itemInd, Items itms, int x, int y, Camera navCm) throws UnhandledException{
		if(itemInd == -1) return;
		float X = x - navCm.xf, Y = y - navCm.yf;
		Item itm = (Item) itms.getItem(itemInd);
		d.setMergeCol(itms.blend, itm.indCol);
		printSprite(
				itms.tex, X, (float) (-0.1*Math.pow(Math.sin(((clock/clockset)%16)/16f*Math.PI),2)+Y), 
				itm.y, (int) (itm.x+itm.offset*(((clock/clockset)%16)/16f)), 
				0, true, 32, 32, 0, -10
		);
	}

	protected void setPrintingDetails(int ww, int hh, int ddhh){
		w = ww; h = hh; ddh = ddhh;
	}

	protected void printSprite(Texture[] t, int ind, float x, float y, int vert, int horiz, 
			int transCol, boolean realFrameNum) throws UnhandledException{
		/**t, ind		: the texture array and the index for the array 
		 * x, y			: x, y Coordinate of Sprite printed on screen, relative to tile (0, 0) at top left corner
		 * w, h			: the width and height of the tile
		 * ddh			: the offset of the display of tiles due to the horizontal bar in Windows
		 * vert, horiz	: specifies the coordinate that the pixels value read from, counted in tiles
		 * transCol		: set the colour to be transparent
		 * realFrameNum	: boolean, specifies if vert is the real vertical frame number or not, the direction of sprite*/
		printSprite(t[ind], x, y, vert, horiz, transCol, realFrameNum);
	}
	protected void printSprite(Texture t, float x, float y, int vert, int horiz, int transCol, 
			boolean realFrameNum) throws UnhandledException{
		printSprite(t, x, y, vert, horiz, transCol, false);
	}
	protected void printSprite(Texture t, float x, float y, int vert, int horiz, int transCol, 
			boolean realFrameNum, boolean careful) throws UnhandledException{
		printSprite(t, x, y, vert, horiz, transCol, careful, w, h, 0, 0);
	}
	protected void printSprite(Texture t, float x, float y, int vert, int horiz, int transCol, 
			boolean careful, int tilW, int tilH, int xoff, int yoff) throws UnhandledException{
		printShadowSprite(t, t, x, y, vert, horiz, vert, horiz, transCol, careful, tilW, tilH, xoff, yoff, false);
	}
	protected void printShadowSprite(Texture t, Texture tS, float x, float y, int vert, int horiz, int vertS, int horizS,
			int transCol, boolean careful, int tilW, int tilH, int xoff, int yoff, boolean reverse) throws UnhandledException{
		int[] scor = mapToScreen(x, y, xoff, yoff, tilW, tilH, careful, 0);
		boolean bo = false;
		if(scor[2] == 1) bo = true;
		int bright = 100;
		try{bright = mLight.getTile((int)(x+navCm.xf), (int)(y+navCm.yf), flr);}
		catch(Exception e){BUG.debug("int+"+x+","+y+","+flr);
		e.printStackTrace();}
		d.placeShadowSprite(t, tS, scor[0], scor[1],
				sizeEnlarged, tilW, tilH, vert, horiz, vertS, horizS, transCol, bo, reverse, 0x000000, 100-bright);
	}
	
	protected void setProtectedRegion(float X0, float Y0, float X1, float Y1){
		if((w*m.w*sizeEnlarged >= 544)|(h*m.h*sizeEnlarged >= 544)){//32 * 17
			if((w*X0*sizeEnlarged>0)&(w*X1*sizeEnlarged<544)
					&(h*Y0*sizeEnlarged>0)&(h*Y1*sizeEnlarged<544)){
				d.setProtectedRegion((int)(d.w/2-272+w*X0*sizeEnlarged)-1, (int)(d.h/2-272+h*Y0*sizeEnlarged+ddh)-1,
						(int)(d.w/2-272+w*X1*sizeEnlarged)+1, (int)(d.h/2-272+h*Y1*sizeEnlarged+ddh)+1);
			} else {
				d.defaultProtectedRegion();
				d.overlapProtectedRegion((int)(d.w/2-272+w*X0*sizeEnlarged)-1, (int)(d.h/2-272+h*Y0*sizeEnlarged+ddh)-1,
						(int)(d.w/2-272+w*X1*sizeEnlarged)+1, (int)(d.h/2-272+h*Y1*sizeEnlarged+ddh)+1);
			}
		} else {
			d.setProtectedRegion((int)(d.w/2-(w*m.w*sizeEnlarged)/2f+w*X0*sizeEnlarged)-1, (int)(d.h/2-(h*m.h*sizeEnlarged)/2f+h*Y0*sizeEnlarged+ddh)-1,
					(int)(d.w/2-(w*m.w*sizeEnlarged)/2f+w*X1*sizeEnlarged)+1, (int)(d.h/2-(h*m.h*sizeEnlarged)/2f+h*Y1*sizeEnlarged+ddh)+1);
		}
	}
	
	protected void selectTile(float x, float y, int w0, int h0){selectTile(x, y, w0, h0, 4);}
	protected void selectTile(float x, float y, int w0, int h0, int ani){
		if((w*m.w*sizeEnlarged > 544)|(h*m.h*sizeEnlarged > 544)){//544 = 32 * 17, 272 = 544/2
			d.selectTile((int)(d.w/2-272+w*x*sizeEnlarged), (int)(d.h/2-272+h*y*sizeEnlarged+ddh), 
					(int)(w0*sizeEnlarged), (int)(h0*sizeEnlarged), 4);
		} else {
			d.selectTile((int)(d.w/2-w/2*(m.w*sizeEnlarged)+w*x*sizeEnlarged), (int)(d.h/2-h/2*(m.h*sizeEnlarged)+h*y*sizeEnlarged+ddh), 
					(int)(w0*sizeEnlarged), (int)(h0*sizeEnlarged), 4);
		}
		
	}
	
	protected boolean inRange(float var, float lowerbound, float upperbound){
		return (var>=lowerbound)&(var<=upperbound);
	}
	protected boolean inRange(float var, float lowerbound, float upperbound, float dvar){
		if(dvar > 0){
			return (var>=lowerbound)&(var+dvar<=upperbound);
		} else {
			return (var+dvar>=lowerbound)&(var<=upperbound);
		}
	}
	protected boolean inDist(float var, float center, float dist){
		return Math.abs(var-center) <= dist;
	}
	protected boolean inMapRegion(float x, float y){
		float w2 = w*sizeEnlarged, h2 = h*sizeEnlarged;
		if((w2*m.w>544)|(h2*m.h>544)){//32*17 = 544; 
			return inRange(x, d.w/2-544/2, d.w/2+544/2)&inRange(y, d.h/2-544/2+ddh, d.h/2+544/2+ddh);
		}
		return inRange(x, d.w/2-(w2*m.w)/2, d.w/2+(w2*m.w)/2)&
				inRange(y, d.h/2-(h2*m.h)/2+ddh, d.h/2+(h2*m.h)/2+ddh);
	}

	protected void drawString(String str, int x, int y, int fontStyle, int fontSize, int alignHoriz, int alignVert, Graphics g) {
		drawString(str, x, y, fontStyle, fontSize, alignHoriz, alignVert, g, d);
	}
	protected void drawString(String str, int x, int y, int fontStyle, int fontSize, int alignHoriz, int alignVert, Graphics g, Display d){//Alignment: 0 Left, 1 Centre, 2 Right
		if(str == "") return;
		d.fontstr = fontstr; d.fonteng = fonteng;
		int[] sizes = d.getSizeOfString(str, fontStyle, fontSize, g);
		int len = sizes[0];
		int hei = sizes[1];
		int xxx = x, yyy = y;
		switch(alignHoriz){
		case 0: break;
		case 1: xxx = x - len/2; break;
		case 2: xxx = x - len; break;
		}
		switch(alignVert){
		case 0: break;
		case 1: yyy = y + hei/2; break;
		case 2: yyy = y + hei; break;
		}
		g.drawString(str, xxx, yyy);
	}

	protected void drawPassage(String strs, int x0, int y0, int x1, int y1, int fontStyle,
							   int fontSize, Graphics g){
		drawPassage(strs, x0, y0, x1, y1, fontStyle, fontSize, g, d);
	}
	protected void drawPassage(String strs, int x0, int y0, int x1, int y1, int fontStyle, 
			int fontSize, Graphics g, Display d){
		strs = strs.replace(" ", "+");
		int i = 0, len = 0, hei = 0, slen = 0, sh = 0;
		String str = ""; String[] Strs = strs.split("\\+");
		boolean isChin = (strs.equals(""))? false : Character.valueOf(strs.charAt(0))>256;
		if(isChin) {
			strs = strs.replace("+", "");
			Strs = new String[strs.toCharArray().length];
			for(int ind = 0; ind < Strs.length; ind++) Strs[ind] = strs.charAt(ind)+"";
		}
		while(i < Strs.length){
			str = Strs[i];
			if(str.contentEquals("/")){
				slen = 0;
				sh += hei + 5;
			} else {
				if(Character.valueOf(str.charAt(0))>256){
					g.setFont(new Font(fontstr, fontStyle, fontSize+5));
				} else {
					g.setFont(new Font(fonteng, fontStyle, fontSize));
				}
				Font font = g.getFont();
				FontRenderContext frc = ((Graphics2D) g).getFontRenderContext();
				if(!isChin) str += " ";
				len = (int) font.getStringBounds(str, frc).getWidth();
				hei = (int) font.getStringBounds(str, frc).getHeight();
				slen += len;
				if(slen > x1 - x0){
					slen = len;
					sh += hei;
				}
				drawString(str, x0 + slen - len, y0 + sh, fontStyle, fontSize, 0, 2, g, d);
			}
			i++;
		}
	}
	
	protected void rightButtonBar(Buttons buttontex, String[] buttons, int seperation, float scale) throws UnhandledException{
		sry = d.h/2-(seperation + (seperation+32)*buttons.length)/2;
		d.placeSprite(tabs, d.w-sx-144-24, sry-31+ddh, 1, 144, 32, 3.5f, 0, 0);
		drawEnclosedRegion(d.w-sx-144-24, sry + ddh, d.w-sx-24, d.h-sry + ddh, 48, 48);
		for(int i = 0; i < buttons.length; i++){
			d.placeSprite(
					buttontex.tex, d.w-sx-144, sry+seperation+(32+seperation)*i+ddh, 1, 
					buttontex.texW, buttontex.texH, 1f*buttontex.getItem(buttons[i]).y/buttontex.texH,
					1f*buttontex.getItem(buttons[i]).x/buttontex.texW, 0
			);
		}
	}

	protected void back() throws UnhandledException{}
	
	protected String[] getCommands(String str) throws UnhandledException{
		return getCommands(str, 0);
	}
	private String[] getCommands(String str, int layer) throws UnhandledException{
		String[] commands = {};
		String stor = "", pair = ""; int numB = 0;
		char[] charArray = str.toCharArray();
		if(str.contains(":{")){
			for(int i = 0; i < charArray.length; i++){
				stor += charArray[i];
				if(charArray[i] == '-'
						&& ((charArray.length > i + 1) ? !Character.isDigit(charArray[i + 1]) : true)){//TODO a
					commands = Arrays.append(commands, stor.substring(0, stor.length()-1));
					stor = "";
				}
				if(charArray[i] == '{'){
					if(numB == 0){
						pair = stor.substring(0, stor.length()-2);
						commands = Arrays.append(commands, stor.substring(0, stor.length()-1)+layer);
						stor = "";
					}
					numB++;
					continue;
				}
				if(charArray[i] == '}'){
					numB--;
					if(numB < 0) throw new UnhandledException(
							201, "Command Syntax Error: Close Bracket Before Open Bracket");
					else if (numB == 0) {
						commands = Arrays.append(commands,
								getCommands(stor.substring(0, stor.length()-1), layer+1));
						commands = Arrays.append(commands, "end"+pair+layer);
						stor = "";
						continue;
					}
				}
			} commands = Arrays.append(commands, getCommands(stor));
		} else {
			//commands = str.split("-");
			for(int i = 0; i < charArray.length; i++) {
				stor += charArray[i];
				if (charArray[i] == '-'
						&& ((charArray.length > i + 1) ? !Character.isDigit(charArray[i + 1]) : true)) {//TODO a
					commands = Arrays.append(commands, stor.substring(0, stor.length() - 1));
					stor = "";
				}
			} commands = Arrays.append(commands, stor);
		}
		return commands;
	}
	protected void executeCommandStack() throws UnhandledException{
		/*if(commandStack.length > 0){
			BUG.debug("stack",1);
			BUG.debug(commandStack);
			BUG.debug("end stack",1);
		}*/
		try{
			int len = 0, starti = -1;
			String[] commandst = new String[0];
			String[][] commandstac = new String[0][0];
			for(int i = 0; i < commandStack.length; i++){
				switch (commandStack[i]){
				case "cmds:":
					len = 0; starti = i + 1;
					commandst = new String[0];
					break;
				case "endcmds":
					boolean abk = executeCommandStackItem(starti, len);
					if(!abk) commandstac = Arrays.append(commandstac, commandst);
					break;
				default:
					len++;
					commandst = Arrays.append(commandst, commandStack[i]);
					break;
				}
			}
			commandStack = new String[0];
			for(int i = 0; i < commandstac.length; i++){
				commandStack = Arrays.append(commandStack, "cmds:");
				for(int j = 0; j < commandstac[i].length; j++)
					commandStack = Arrays.append(commandStack, commandstac[i][j]);
				commandStack = Arrays.append(commandStack, "endcmds");
			}
	//	BUG.debug(commandStack);//*/
		} catch(Exception e){e.printStackTrace();}
	}
	protected boolean executeCommandStackItem(int starti, int len) throws UnhandledException{
		String[] commands = new String[len];
		for(int i = 0; i < len; i++){
			commands[i] = commandStack[starti + i];
		}
		return executeCommands(commands, true);
	}
	protected boolean executeCommands(String str) throws UnhandledException{
		return executeCommands(str, false);
	}
	protected boolean executeCommands(String str, boolean stack) throws UnhandledException{
		String[] commands = getCommands(str);
		return executeCommands(commands, stack);
	}
	protected boolean executeCommands(String[] commands, boolean stack) throws UnhandledException{
		/*BUG.debug("st");
		BUG.debug(commands);
		BUG.debug("end st");/**/
		boolean finished = true, fin = true;
		String[] exit = {};
		String command;
		for(int i = 0; i < commands.length; i++){
			command = commands[i];
			if(command.contains("if:")){
				i++;
				int res = executeCommand(commands[i]);
				if((res <= 0)|(res == 2)|(res == 3)){
					if(res <= 0) fin = false;
					do{
						i++;
					} while(!commands[i].contentEquals("endthen"+command.split("if:")[1]));
					if(i+1 < commands.length){
						if(commands[i+1].contains("else:")){
							fin = true;
							exit = Arrays.append(exit, "end"+command.replace(":",""));
							i++;
							continue;
						}
					}
					finished = fin&finished;
				} else {
					i++;
				}
				continue;
			} else if (command.contains("then:")) {
				exit = Arrays.append(exit, "end"+command.replace(":",""));
				continue;
			} else if (command.contains("else:")) {
				do{
					i++;
				} while(!commands[i].contentEquals("endelse"+command.split("else:")[1]));
			} else if (command.contains("end")){
				exit = Arrays.deleteLast(exit);
				continue;
			} else {
				int result = executeCommand(command);
				if(result <= 0) finished = false;
				else if(result == 2) break;
				else if(result == 3) {finished = false; break;}
				if((result <= 0)) {//there used to be: &(!stack) in the line
					commandSaveToStack(commands, i, command, result); return true;}
			}
		}
		return finished;
	}
	protected int executeCommand(String str) throws UnhandledException{return -1;}
	
	protected void commandSaveToStack(String[] commands, int breakpt, String command, int result) throws UnhandledException{
		if(command.contains("(")){
			command = command.split("\\(")[0];
		}
		String[] cmd = getCommands("if:{"+command+"?("+(-result)%m.w+"::"+(-result)/m.w+")}then:{");
		String[] arr = Arrays.arrayAfter(commands, breakpt);
		if(arr.length > 0)
		while((arr[arr.length-1].contains("endthen"))|(arr[arr.length-1].contentEquals(""))){
			arr = Arrays.deleteLast(arr);
		}
		cmd = Arrays.append(cmd, arr);
		cmd = Arrays.append(cmd, "endthen0");
		cmd = Arrays.replaceString(cmd, "[this]", cm.x+"::"+cm.y);
		commandStack = Arrays.append(commandStack, "cmds:");
		commandStack = Arrays.append(commandStack, cmd);
		commandStack = Arrays.append(commandStack, "endcmds");
	}

	protected String getParameter(String content, String tag){
		if(content.contains(tag)){
			String[] ar = content.split(tag);
			String ans = content.split(tag)[0];
			if(ar.length == 3) ans += content.split(tag)[2];
			return ans;
		}
		else return content;
	}
	
	protected float getMobStat(String stat, int mobx, int moby) throws UnhandledException{
		try{
			return Float.parseFloat(getContent(getTileStr(mobx, moby, mStat), "<"+stat+">"));
		} catch (Exception e){
			return 0;
		}
	}
	protected String getContent(String content, String tag){
		if(content.contains(tag)) return content.split(tag)[1];
		else return "";
	}
	protected String addTagTo(String contentExc, Object content, String tag){
		return contentExc+tag+content+tag;
	}
	protected boolean haveTag(String content, String tag){
		return content.contains(tag);
	}
	
	protected void drawEnclosedRegion(int x0, int y0, int x1, int y1, int ww, int hh){
		
/**/ 	d.setProtectedRegion(x0, y0, x1, y1);
		int ii = 0, jj = 0;
		while (y0 + hh * jj < y1){
			while (x0 + ww * ii < x1){
				d.placeSprite(
						tabs, x0 + ww * ii, y0 + hh * jj, 1,
						48, 48, 4/3f, 3, -1, true
				);
				ii++;
			}
			jj++;
			ii = 0;
		} /*/ Cancelled:
		int ext = -3;
		x0 -= ext; x1 += ext; y0 -= ext; y1 += ext;
		d.setProtectedRegion(x0, y0, x1, y1);
		int ii = 0, jj = 0;
		while (y0 + hh * jj < y1){
			while (x0 + ww * ii < x1){
				d.placeSprite(
						tabs, x0 + ww * ii, y0 + hh * jj, 1,
						48, 48, 4/3f, 3, -1, true
				);
				ii++;
			}
			jj++;
			ii = 0;
		}
		ext = 11;
		x0 -= ext; x1 += ext; y0 -= ext; y1 += ext;
		d.setProtectedRegion(x0, y0, x1, y1);
		ww = 32; hh = 32; int gg = 10;
		int numgy = (int) Math.floor(1f*(y1-y0-hh-gg)/(hh+gg));
		int gy = (int) (1f*(y1-y0-(numgy+1)*(hh))/numgy);
		int numgx = (int) Math.floor(1f*(x1-x0-ww-gg)/(ww+gg));
		int gx = (int) (1f*(x1-x0-(numgx+1)*(ww))/numgx);
		d.placeSprite(
				edge, x0, y0, 1,
				32, 32, 0, 0, 0, true
		);
		d.placeSprite(
				edge, x1 - ww, y0, 1,
				32, 32, 0, 2, 0, true
		);
		d.placeSprite(
				edge, x0, y1 - hh, 1,
				32, 32, 2, 0, 0, true
		);
		d.placeSprite(
				edge, x1 - ww, y1 - hh, 1,
				32, 32, 2, 2, 0, true
		);
		for(ii = 0; ii < numgx - 1; ii++){
			d.placeSprite(
					edge, x0 + (ww + gx) * (ii + 1), y0, 1,
					32, 32, 0, 1, 0, true
			);
		}
		for(ii = 0; ii < numgx - 1; ii++){
			d.placeSprite(
					edge, x0 + (ww + gx) * (ii + 1), y1 - hh, 1,
					32, 32, 2, 1, 0, true
			);
		}
		for(jj = 0; jj < numgy - 1; jj++){
			d.placeSprite(
					edge, x0, y0 + (hh + gy) * (jj + 1), 1,
					32, 32, 1, 0, 0, true
			);
		}
		for(jj = 0; jj < numgy - 1; jj++){
			d.placeSprite(
					edge, x1 - ww, y0 + (hh + gy) * (jj + 1), 1,
					32, 32, 1, 2, 0, true
			);
		}
		//*/
		d.defaultProtectedRegion();
	}

	protected boolean checkLocPassable(int x, int y) throws UnhandledException{
		if(inRange(x, 0, m.w-1)&inRange(y, 1, m.h-1)){
			if(haveTag(getTileStr(x, y, mStat), "<passable>")) return true;
				//this is a single tag <passable>, cannot get value
			Tile t0 = getTileItem(x, y, m);
			Tile t1;
			if(getTile(x, y, mOver) >= 0){
				t1 = getTileItem(x, y, mOver);
			} else {
				t1 = new Tile(); t1.passable = true; t1.overlaid = new ConnectDir("Null");
			}
			Tile t2 = getTileItem(x, y, mLowe);
			if((t0.passable|(t2.overlaid.dir == ConnectDir.DirType.Bridge)) & t1.passable){
			} else return false;
			if((getTile(x, y, mItem) == -1)|(getTile(x, y, mItem)%2 == 0)){
				return true;
			}
		}
		return false;
	}
	
	protected String getString(int[] bytes){
		String ans = "";
		for(int i = 0; i < bytes.length; i++){
			try{
				char[] cha = Character.toChars(bytes[i]);
				ans += String.valueOf(cha);
			}
			catch(Exception e){}
		}
		return ans;
	}
	
	protected String getTextureDataURL(String texturePackURL, String name){
		return texturePackURL + "/" + name + ".tex/" + name + ".tx";
	}
	protected String getTextureImgURL(String texturePackURL, String name){
		return texturePackURL + "/" + name + ".tex/" + name + ".png";
	}
	
	protected void printDispWords(Graphics g) throws UnhandledException{
		String det = fonteng;
		fonteng = "Cooper Black";
		for(int i = 0; i < dispwords.numrecord; i++){
			Object[] record = dispwords.getRecord(i);
			GraphicsF gf = new GraphicsF((Graphics2D)g, d);
			gf.setAlpha((int)record[5]/200f);
			if((int)record[8] == 1){
				int teh = 1;
				g.setColor(Color.WHITE);
				drawString((String)record[0], (int)record[3]-teh, (int)record[4], 0, (int)record[6], 1, 1, g);
				drawString((String)record[0], (int)record[3]+teh, (int)record[4], 0, (int)record[6], 1, 1, g);
				drawString((String)record[0], (int)record[3], (int)record[4]-teh, 0, (int)record[6], 1, 1, g);
				drawString((String)record[0], (int)record[3], (int)record[4]+teh, 0, (int)record[6], 1, 1, g);
			}
			gf.setAlpha((int)record[5]/100f);
			g.setColor(new Color((int)record[7]));
			drawString((String)record[0], (int)record[3], (int)record[4], 0, (int)record[6], 1, 1, g);
			gf.setAlpha(1f);
			if((int)record[1] > 0){
				record[1] = (int)record[1]-1;
				String cmd = (String)record[2];
				String[] cmds = cmd.split(";");
				for(int j = 0; j < cmds.length; j++){
					switch(cmds[j].charAt(0)){
					case 'x':
						record[3] = changeval((int)record[3], cmds[j].substring(1));
						break;
					case 'y':
						record[4] = changeval((int)record[4], cmds[j].substring(1));
						break;
					case 'a':
						record[5] = changeval((int)record[5], cmds[j].substring(1));
						if((int)record[5] < 0){
							dispwords.deleteRecord(i);
							i--;
						}
						break;
					}
				}
			} else {
				dispwords.deleteRecord(i);
				i--;
			}
		}
		fonteng = det;
	}
	protected int changeval(int var, String code){
		String[] cmd = code.split(",");
		for(int i = 0; i < cmd.length; i++){
			switch(cmd[i].charAt(0)){
			case '+': var += Float.parseFloat(cmd[i].substring(1));
			case '-': var -= Float.parseFloat(cmd[i].substring(1));
			case '*': var *= Float.parseFloat(cmd[i].substring(1));
			case '/': var /= Float.parseFloat(cmd[i].substring(1));
			}
		}
		return var;
	}
	
	protected int[] mapToScreen(float x, float y, float xoff, float yoff, float tilW, float tilH, boolean careful, int eka){
		int xx = -1, yy = -1, bo = 1;
		if((w*m.w*sizeEnlarged > 544)|(h*m.h*sizeEnlarged > 544)){//32 * 17
			if(!careful&inRange((w*x+xoff)*sizeEnlarged, w*sizeEnlarged, 544-w*sizeEnlarged, tilW)
					&inRange((h*y+yoff)*sizeEnlarged, h*sizeEnlarged, 544-h*sizeEnlarged, tilH)){
				xx = (int)(d.w/2f-272+(w*x+xoff)*sizeEnlarged+eka*tilW*sizeEnlarged/2);
				yy = (int)(d.h/2-272+(h*y+yoff)*sizeEnlarged+ddh);
				bo = 0;
			} else if (inRange((w*x+xoff)*sizeEnlarged, -w*sizeEnlarged, 544+w*sizeEnlarged, tilW)
					&inRange((h*(y+.5f)+yoff)*sizeEnlarged, -h*sizeEnlarged, 544+h*sizeEnlarged+h, tilH)){
				xx = (int)(d.w/2f-272+(w*x+xoff)*sizeEnlarged+eka*tilW*sizeEnlarged/2);
				yy = (int)(d.h/2-272+(h*y+yoff)*sizeEnlarged+ddh);
			}
		} else {
			xx = (int)(d.w/2-(w*m.w*sizeEnlarged)/2f+(w*x+xoff)*sizeEnlarged+eka*tilW*sizeEnlarged/2);
			yy = (int)(d.h/2-(h*m.h*sizeEnlarged)/2f+(h*y+yoff)*sizeEnlarged+ddh);
		}//*/
		int[] ans = {xx, yy, bo};
		return ans;
	}
	protected float[] mapToTileIndex(int x, int y, int xoff, int yoff){
		float xx = -1, yy = -1;
		if((w*m.w*sizeEnlarged > 544)|(h*m.h*sizeEnlarged > 544)){//32 * 17
			xx = (1f*(x - d.w/2 + 272)/sizeEnlarged-xoff)/w;
			yy = (1f*(y - d.h/2 + 272 - ddh)/sizeEnlarged-yoff)/h;
		} else {
			xx = (1f*(x - d.w/2 + (w*m.w*sizeEnlarged)/2f)/sizeEnlarged - xoff)/w;
			yy = ((y - d.h/2 + (h*m.h*sizeEnlarged)/2f - ddh)/sizeEnlarged - yoff)/h;
		}//*/
		float[] ans = {xx, yy};
		return ans;
	}

	protected void notif(final String in, final int time) throws UnhandledException {
		Thread t = new Thread(){
			public void run(){
				notifed++;
				long oldt = System.nanoTime();
				Font font = new Font(fonteng, 0, 20);
				FontRenderContext frc = ((Graphics2D) d.getGraphics()).getFontRenderContext();
				int len = (int) font.getStringBounds(in, frc).getWidth();
				int hei = (int) font.getStringBounds(in, frc).getHeight();
				int offs = 15;
				int px = 8;
				int x = d.w/2, y = d.h*2/3+(notifed-1)*(hei+40), w = Math.min(len, d.w*2/3)+40, h = hei*(3*(len/d.w+1)/2)+40;

				startdialogue = new Point(x - w / 2, y - h / 2);
				enddialogue = new Point(x + w / 2, y + h / 2);
				drawDialogue(border, px, 1.5f, x, y, w, h, 1, 1, d2);
				x -= w / 2;
				d2.img.getGraphics().setColor(Color.WHITE);
				drawPassage(in, x + offs, y - h / 2 + offs, x + w - offs, y + h / 2 - offs, 0, 20, d2.img.getGraphics(), d2);
				try {
					sleep(time);
				} catch (InterruptedException e) {}

				d2.clearScreen(-2);
				notifed = 0;
			}
		};
		t.start();
	}

	protected enum type{mob, spobj, item};
	protected void diag(final String in, final int ind, final type tp, final int time) throws UnhandledException {
		Thread t = new Thread(){
			public void run(){
				long oldt = System.nanoTime();
				Font font = new Font(fonteng, 0, 20);
				FontRenderContext frc = ((Graphics2D) d.getGraphics()).getFontRenderContext();
				int len = (int) font.getStringBounds(in, frc).getWidth();
				int hei = (int) font.getStringBounds(in, frc).getHeight();
				int offs = 15;
				int px = 8;
				int w = d.w*2/3, h = (int)(hei*(3*(1f*len/d.w+1)/2)+40), x = d.w/2, y = d.h-h/2-offs;

				startdialogue = new Point(x - w / 2, y - h / 2);
				enddialogue = new Point(x + w / 2, y + h / 2);
				drawDialogue(border, px, 1.5f, x, y, w, h, 1, 1, d2);
				x -= w / 2;
				d2.img.getGraphics().setColor(Color.WHITE);
				drawPassage(in,
						x + offs + 72, y - h / 2 + offs,
						x + w - offs, y + h / 2 - offs, 0, 20,
						d2.img.getGraphics());
				try {
					int xx = d.w*11/60, yy = y - 72;
					float siz = 1.6f;
					if(tp == type.spobj){
						SpecialObject tils = null;
						tils = (SpecialObject) spobj.getItem(ind);
						d2.placeSprite(
								spobj.tex, xx, yy, siz, spobj.wid, spobj.hei, tils.y,
								tils.x+tils.offset*(clock/clockset %16)/16, 0, false
						);
					} else if(tp == type.item){
						Item tils = null;
						tils = (Item) items.getItem(ind);
						d2.placeSprite(
								items.tex, xx, yy, siz, 32, 32, tils.y,
								tils.x+tils.offset*(clock/clockset %16)/16, 0, false
						);
					} else {
						if(tp == type.mob){
							Mob tilm = null;
							tilm = (Mob) mobs.getItem(ind);
							d2.placeSprite(
									mobs.tex, xx, yy, siz, tilm.wid, tilm.hei, tilm.y,
									tilm.x+tilm.offset*(clock/clockset %16)/16, 0, false
							);
						}
					}
				} catch(UnhandledException e){}

				try {
					sleep(time);
				} catch (InterruptedException e) {}

				d2.clearScreen(-2);
			}
		};
		t.start();
	}

	protected void drawDialogue(Texture tx, int px, float size, int x, int y, int w, int h, int alignx, int aligny){
		drawDialogue(tx, px, size, x, y, w, h, alignx, aligny, d);
	}
	protected void drawDialogue(Texture tx, int px, float size, int x, int y, int w, int h, int alignx, int aligny, Display d){
		/* drawDialogue(border, 8, 1.5f, d.w/2, d.h/2+ddh, 400, 300, 1, 1);
		 * */
		switch(alignx){
		case 0: break;
		case 1: x -= w/2; break;
		case 2: x -= w; break;
		}
		switch(aligny){
		case 0: break;
		case 1: y -= h/2; break;
		case 2: y -= h; break;
		}
		
		for(float i = x+px*size; i < x+w-px*size; i+=size){
			for(float j = y+px*size; j < y+h-px*size; j+=size){
				d.placeSprite(tx, i, j, size, 1, 1, px+1f*(j-y)/size%(1f*tx.h-2*px),
						px+1f*(i-x)/size%(1f*tx.w-2*px), -1, false);
			}
		}
		for(float i = x+px*size; i < x+w-px*size; i+=size){
			d.placeSprite(tx, i, y, size, 1, px, 0, px+1f*(i-x)/size%(1f*tx.w-2*px), -1, false);
		}
		for(float i = x+px*size; i < x+w-px*size; i+=size){
			d.placeSprite(tx, i, y+h-px*size, size, 1, px, 1f*(tx.h-px)/px, px+1f*(i-x)/size%(1f*tx.w-2*px), 
					-1, false);
		}
		for(float j = y+px*size; j < y+h-px*size; j+=size){
			d.placeSprite(tx, x, j, size, px, 1, px+1f*(j-y)/size%(1f*tx.h-2*px), 0, -1, false);
		}
		for(float j = y+px*size; j < y+h-px*size; j+=size){
			d.placeSprite(tx, x+w-px*size, j, size, px, 1, px+1f*(j-y)/size%(1f*tx.h-2*px), 1f*(tx.w-px)/px, -1, false);
		}
		d.placeSprite(tx, x, y, size, px, px, 0, 0, -1, false);
		d.placeSprite(tx, x+w-px*size, y, size, px, px, 0, 1f*(tx.w-px)/px, -1, false);
		d.placeSprite(tx, x, y+h-px*size, size, px, px, 1f*(tx.h-px)/px, 0, -1, false);
		d.placeSprite(tx, x+w-px*size, y+h-px*size, size, px, px, 1f*(tx.h-px)/px, 1f*(tx.w-px)/px, -1, false);
	}
	protected enum align{horizontal, vertical};
	protected void dispDialogue(Texture tx, String strs, int xmob, int ymob, int w, int h, String btns, align alignment, Graphics g) throws UnhandledException{
		/* dispDialogue(("The Wise Elderly:" +
				" / You brave warrior, if your aim in this trip is to save the princess, " +
				"then you would have to know something about the monsters, here is a book of everything. " +
				"You would certainly need it afterwards.").replace(" ", "+"), 2, 6, 400, 200, d.img.getGraphics());
		 * */
		dialogue = true;
		int offs = 15;
		int x = d.w/2, y = d.h/2+ddh;
		int px = 8;
		
		startdialogue = new Point(x - w/2, y - h/2); enddialogue = new Point(x + w/2, y + h/2);
		drawDialogue(tx, px, 1.5f, x, y, w, h, 1, 1);
		x -= w/2; y -= h/2;

		drawPassage(strs, x + 48 + 2*offs, y + offs, x+w - offs - ((alignment.equals(align.vertical))?100:0), y+h - offs, 0, 20, g);

		int xx = x + offs, yy = y + offs;
		float siz = 1.6f;
		int overt = getTile(xmob, ymob, mOver);
		if((overt %2 == 1)&(overt != -1)){
			SpecialObject tils = (SpecialObject) spobj.getItem(overt/2);
			d.placeSprite(
					spobj.tex, xx, yy, siz, spobj.wid, spobj.hei, tils.y,
					tils.x+tils.offset*(clock/clockset %16)/16, 0, false
			);
		} else {
			overt = getTile(xmob, ymob, mItem);
			if((overt %2 == 1)&(overt != -1)){
				Mob tilm = (Mob) mobs.getItem(overt/2);
				d.placeSprite(
						mobs.tex, xx, yy, siz, tilm.wid, tilm.hei, tilm.y,
						tilm.x+tilm.offset*(clock/clockset %16)/16, 0, false
				);
			}
		}
		
		int btnw = 48, btnh = 32;
		if(btns.equals("")) {dialogue = false; return;}
		String[] sd = btns.split(";");
		switch(alignment){
		case horizontal:
			for(int i = 0; i < sd.length; i++){
				int btnwl = btnw, btnhl = btnh;
				String cont = sd[i].split("\\[")[1].split("\\]:")[0];
				d.fontstr = fontstr; d.fonteng = fonteng;
				btnwl = Math.max(d.getSizeOfString(cont, 0, 20, g)[0]+2*px, btnwl);
				drawDialogue(tx, 8, 1, (int) (x + 1f*w/(sd.length+1)*(i+1)), y + h - offs, btnwl, btnhl, 1, 2);
				drawString(cont, (int) (x + 1f*w/(sd.length+1)*(i+1)), y + h - offs - btnhl/2, 
						0, 20, 1, 1, g);
			}
			break;
		case vertical:
			for(int i = 0; i < sd.length; i++){
				int btnwl = btnw, btnhl = btnh;
				String cont = sd[i].split("\\[")[1].split("\\]:")[0];
				d.fontstr = fontstr; d.fonteng = fonteng;
				btnwl = Math.max(d.getSizeOfString(cont, 0, 20, g)[0]+2*px, btnwl);
				drawDialogue(tx, 8, 1, x + w - btnwl/2 - offs, (int) (y + 1f*h/(sd.length+1)*(i+1)), btnwl, btnhl, 1, 1);
				drawString(cont, x + w - btnwl/2 - offs, (int) (y + 1f*h/(sd.length+1)*(i+1)), 
						0, 20, 1, 1, g);
			}
			break;
		}
		//command sd[i].split("]:")[1]
	}
	
	protected boolean inRect(int x, int y, int xlow, int ylow, int xup, int yup){
		return inRange(x, xlow, xup)&inRange(y, ylow, yup);
	}
	
	protected float restrict(float input, float lowbound, float upbound){
		if(lowbound > upbound){
			float t = lowbound;
			lowbound = upbound;
			upbound = t;
		} 
		return Math.max(Math.min(input, upbound), lowbound);
	}

	protected void editTile(int x, int y, int withtil, int sl) throws UnhandledException{
		if(inRange(cm.x, 0, m.w-1)&inRange(cm.y, 0, m.h-1)){
			switch(sl){
				case 0: //Tile
					Tile til, tilBase;
					til = (Tile) tiles.getItem(withtil);
					if(til.overlaid.dir == ConnectDir.DirType.Bridge){
						if(getTile(x, y, mItem)!=-1){
							mItem = setTile(x, y, -1, mItem);
							mStat = setTile(x, y, "", mStat);
						}
						tilBase = (Tile) tiles.getItem(getTile(x, y, m));
						if(tilBase.overlaid.dir == ConnectDir.DirType.Fluid){
							if((getTile(x, y, mOver) != withtil) & (withtil<tiles.length)){
								mStat = setTile(x, y, "", mStat);
								mLowe = setTile(x, y, withtil, mLowe);
							}
						}
					} else if((til.overlaid.dir == ConnectDir.DirType.Overlaid)|
							(til.overlaid.dir == ConnectDir.DirType.QuadraOverlaid)|
							(til.overlaid.dir == ConnectDir.DirType.Quadraconnect)|
							(til.overlaid.dir == ConnectDir.DirType.Wall)){
						if(getTile(x, y, mItem)!=-1){
							mItem = setTile(x, y, -1, mItem);
							mStat = setTile(x, y, "", mStat);
						}
						tilBase = (Tile) tiles.getItem(getTile(x, y, m));
						if((tilBase.overlaid.dir != ConnectDir.DirType.Fluid)){
							if(til.passable){
								if((getTile(x, y, mLowe) != withtil) & (withtil<tiles.length)){
									mStat = setTile(x, y, "", mStat);
									mLowe = setTile(x, y, withtil, mLowe);
								}
							} else {
								if((getTile(x, y, mOver)/2 != withtil) & (withtil<tiles.length)){
									mStat = setTile(x, y, "", mStat);
									mOver = setTile(x, y, withtil*2, mOver);
								}
							}
						}
					} else {
						if(getTile(x, y, mItem)!=-1){
							mItem = setTile(x, y, -1, mItem);}
						if(getTile(x, y, mOver)!=-1){
							mStat = setTile(x, y, "", mStat);
							mOver = setTile(x, y, -1, mOver);}
						if(getTile(x, y, mLowe)!=-1){mLowe = setTile(x, y, -1, mLowe);}
						if((getTile(x, y, m) != withtil) & (withtil<tiles.length)){
							mStat = setTile(x, y, "", mStat);
							m = setTile(x, y, withtil, m);
						}
					}
					break;
				case 1://Items
					if(getTileItem(x, y, m).passable){
						if(getTile(x, y, mOver)==-1){}
						else if(getTileItem(x, y, mOver).passable){}
						else break;
					} else {
						if(getTile(x, y, mOver)==-1) break;
						else if(getTileItem(x, y, mLowe).overlaid.dir == ConnectDir.DirType.Bridge){}
						else break;
					}
					mStat = setTile(x, y, "", mStat);
					mItem = setTile(x, y, withtil*2, mItem);
					break;
				case 2: //Special Tiles
					til = (Tile) spobj.getItem(withtil);
					if(getTileItem(x, y, m).overlaid.dir != ConnectDir.DirType.Fluid){
						if(getTile(x, y, mItem)!=-1){
							mItem = setTile(x, y, -1, mItem);
							mStat = setTile(x, y, "", mStat);
						}

						target.init(x, y);
						BUG.debug(x + "," + y + "," + withtil + "," + spobj.getItem(withtil).name);
						mOver = setTile(x, y, withtil * 2 + 1, mOver);
						if(!executeCommands(((SpecialObject)spobj.getItem(withtil)).actionCreate)) {
							BUG.debug(x + "," + y + "," + withtil + "," + spobj.getItem(withtil).name);
							mOver = setTile(x, y, -1, mOver);
						}

						cm.init();
					}
					break;
				case 3: //Mobs
					if(getTileItem(x, y, m).passable){
						if(getTile(x, y, mOver)==-1){}
						else if(getTileItem(x, y, mOver).passable){}
						else break;
					} else {
						if(getTile(x, y, mOver)==-1) break;
						else if(getTileItem(x, y, mOver).overlaid.dir == ConnectDir.DirType.Bridge){}
						else break;
					}
					mStat = setTile(x, y, "", mStat);
					mItem = setTile(x, y, withtil*2+1, mItem);
					Mob mb = (Mob) mobs.items[withtil];

					String mobdata = "";
					mobdata = addTagTo(mobdata, mb.maxhealth, "<hp>");
					mobdata = addTagTo(mobdata, mb.atk, "<atk>");
					mobdata = addTagTo(mobdata, mb.dfs, "<dfs>");
					mobdata = addTagTo(mobdata, mb.matk, "<matk>");
					mobdata = addTagTo(mobdata, mb.mdfs, "<mdfs>");
					mStat = setTile(x, y, mobdata, mStat);
					break;
			}
		}
	}
}	

enum Games {
	Null, MapCreate, Dungeon;
}

class GraphicsF {
	Graphics2D g;
	Display d;
	GraphicsF(Graphics2D G, Display d){
		g = G;
	}
	void setAlpha(float alpha){
		try{
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
		} catch (IllegalArgumentException e){}
	}
	void drawImage(Image img, int x, int y, int w, int h, float alpha){
		try{
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
			drawImage(img, x, y, w, h);
		} catch (IllegalArgumentException e){}
	}
	void drawImage(Image img, int x, int y, int w, int h){
		g.drawImage(img, x, y, w, h, d);
	}
	void dispose(){
		g.dispose();
	}
}