package project.Engines;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.function.Function;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JSplitPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.ScrollPaneLayout;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.html.HTMLEditorKit;

import project.*;
import project.GameData.GameData_Dungeon;
import project.Sprite.*;
import project.Sprites.*;

import static java.util.Arrays.fill;

public class Engine_MapCreate extends Engine{
	public int mapW = 17, mapH = 17, mapHeight = 50;

	private enum obj {tiles, items, spobj, mobs} //for reference only
	Image icon = Toolkit.getDefaultToolkit().getImage("res/icons/0 mapcreate/Pic.png");
	Image word = Toolkit.getDefaultToolkit().getImage("res/icons/0 mapcreate/Word.png");
	String loc = "null"; boolean sel = false; //sel : Selected? map size
	String[] choice; int tileSize = 32;
	 //flr: floor (z coordinate); size: size of tile
	Camera cmm; int cM = -1; int seperation = 10; //seperation between buttons in the button column
	int mmx, mmy;
	float memSizeEnlarged = -1, rtbintv; 
	//Select Bar Left: sw = start x-coor, sth0 = select tile start y-coor
	float scale, scale2, scaletool; //scale of the tiles in left toolbar
	Sprites spr; String[] buttons, tools, toolsname, toolsdesc; Buttons buttontex, tooltex;
	int navLTBy = 0, rtby = 0, ortby = -1; int startX, startY, endX, endY;
	int targettil = -1; boolean[] toolsenable; GameData_Dungeon gdd; boolean listselect = false;
	String[] list = new String[0];
	String[] cmdlist = new String[]{
			"none::no action",
			"have(ITEM)::only checks if the player has the item ITEM, and proceed to next command if found. Else, the process is terminated.",
			"haved(ITEM)::checks if the player has the item ITEM, and remove 1 such item if found.",
			"delete(X, Y)::for integer values X and Y, the items, specialObjects and mobs on that tile are deleted.",
			"tpup::teleports player to the exact x, y location one floor upwards.",
			"tpdown::teleports player to the exact x, y location one floor downwards.",
			"duplicate(X0, Y0, X\', Y\')::duplicates the item, mob, or specialObject on tile X0, Y0 to the tile X\', Y\'.",
			"exec(COMMAND)::executes simple operations to player's statistics, in the pattern of STAT0>+STAT1>*STAT2, will return STAT0(new) = (STAT0(old)+STAT1)*STAT2",
			"print(WORD)::prints the word",
			"give(ITEM)::grants the player the item with corresponding name or ID.",
			"setblock(X, Y, OBJ)::change the block in the specified coordinate to the specified object.",
			"say(WORD)::displays messages to the player.",
			"say(WORD, OBJ)::displays messages to the player."
	};
	
	Engine_MapCreate(BitmapGame b, Display dis) throws UnhandledException {
		super(b, dis, Games.MapCreate);
		title = "Map Create";
		timeMax = 500; timeT = 100;
		time = timeMax;
		d = dis;
		sx = d.w/4-136-96;
		scale = 48f/tileSize;
		scale2 = 36f/tileSize;
		tabs = new Texture("res/texture/tabs.png", d);
		bg = new Texture("res/texture/bg.jpg", d);
	}
	
	protected void startPage(Graphics g) throws UnhandledException{
		Graphics2D g2 = (Graphics2D) g;
		if(time >= timeMax - timeT){
			d.clearScreen(0x000000);
			g2.drawImage(d.img, 0, 0, d.img.getWidth(), d.img.getHeight(), d);
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 
					restrict(1.0f*(timeMax-time)/timeT, 0, 1)));
			g2.drawImage(icon, d.w/2-100, d.h/2-100, 200, 200, d);
			g.drawImage(word, d.w/2-113, d.h/2-47, 226, 94, d);
		} else if(time >= timeT){
			float ratio = 1.0f*(time-timeT)/(timeMax-2*timeT); float rati;
			d.clearScreen(0x000000);
			g.drawImage(d.img, 0, 0, d.img.getWidth(), d.img.getHeight(), d);
			if(ratio > 2/3f){
				rati = 3*ratio-2;
				g.drawImage(icon, (int)(d.w/2-100*(2-rati)), d.h/2-100, 200, 200, d);
				g.drawImage(word, (int)(d.w/2-113*rati), d.h/2-47, 226, 94, d);
			} else if (ratio > 1/3f){
				g.drawImage(icon, d.w/2-200, d.h/2-100, 200, 200, d);
				g.drawImage(word, d.w/2, d.h/2-47, 226, 94, d);
			} else if (ratio > 0) {
				rati = ratio*3;
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, rati));
				g2.drawImage(icon, d.w/2-200, d.h/2-100, 200, 200, d);
				g2.drawImage(word, d.w/2, d.h/2-47, 226, 94, d);
			}
		} else {
			d.clearScreen(0xF0F0F0);
			g.drawImage(d.img, 0, 0, d.img.getWidth(), d.img.getHeight(), d);
			d.clearScreen(0x000000);
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f*time/timeT));
			g2.drawImage(d.img, 0, 0, d.img.getWidth(), d.img.getHeight(), d);
			pre = "";
			if(loc.equals("null")){
				try {
					BufferedReader br = new BufferedReader(new FileReader("res/data/maps.dt"));
					try{
						String ch = br.readLine();
						String[] chs = ch.split(",");
						choice = new String[chs.length+1];
						choice[0] = "New Map";
						System.arraycopy(chs, 0, choice, 1, chs.length);
					} catch (NullPointerException e){
						choice = new String[1];
						choice[0] = "New Map";
					}
				} catch (FileNotFoundException e) {
					throw new UnhandledException(101, "File \"res/data/maps.dt\" is not found");
				} catch (IOException e){
					throw new UnhandledException(103, "File IO read error");
				}
				super.choice = choice;
				choosing(new GraphicsF(g2, d), icon, "Load Map", 0);
				sel = false;
				time++;
			} else if ((loc.equals("new"))&!sel){
				//Select Map Type
				JPanel p1 = new JPanel();
				ButtonGroup gp1 = new ButtonGroup();
				JRadioButton a = new JRadioButton("Flat"); a.setActionCommand("Flat");
				JRadioButton b = new JRadioButton("Leveled"); b.setActionCommand("Leveled");
				gp1.add(a); gp1.add(b);
				p1.add(new JLabel("Select Map Type:")); p1.add(a); p1.add(b);
				int obt;
				while(gp1.getSelection() == null){
					obt = JOptionPane.showConfirmDialog(null, p1, "Map Type", JOptionPane.OK_CANCEL_OPTION);
					if((obt==JOptionPane.CANCEL_OPTION)|(obt==JOptionPane.CLOSED_OPTION))
						{loc = "null"; break;}
				}
				if(!loc.equals("null")){
					//Do Map Specifications
					JPanel p2 = new JPanel();
					JTextField Ww = new JTextField(5); JTextField Hh = new JTextField(5);
					JTextField Tt = new JTextField(5);
					p2.add(new JLabel("Width:")); 	p2.add(Ww);
					p2.add(new JLabel("Height:"));	p2.add(Hh);
					switch(gp1.getSelection().getActionCommand()){
					case "Flat": mt = MapType.Flat; break;
					case "Leveled": mt = MapType.Leveled; break;
					}
					boolean flat = (gp1.getSelection().getActionCommand().equals("Flat"));
					if(!flat){ p2.add(new JLabel("Number of Levels:")); p2.add(Tt);}
					JRadioButton c = new JRadioButton("Default"); c.setActionCommand("Default");
					Object[] Q = {new JLabel("Input the specifications of the map:"),p2, c};
					if(tileSize == 32){
						if(flat){c.setText("Default: Width:17, Height:17");}
						else	{c.setText("Default: Width:17, Height:17, Number Of Levels:1");}
					} else if(tileSize == 24){
						if(flat){c.setText("Default: Width:23, Height:23");}
						else	{c.setText("Default: Width:23, Height:23, Number Of Levels:1");}
					}
					int opt = JOptionPane.showConfirmDialog(null, Q, "Specification of Map", 
							JOptionPane.OK_CANCEL_OPTION);
					while (!sel){
						if((opt == JOptionPane.CANCEL_OPTION)|(opt == JOptionPane.CLOSED_OPTION))
							{loc = "null"; break;}
						else if(c.isSelected()){
							if(tileSize == 32)		{mapW = 17; mapH = 17;} 
							else if (tileSize == 24){mapW = 23; mapH = 23;}
							mapHeight = 1;
							sel = true;
						} else {
							try{
								mapW = Integer.parseInt(Ww.getText());
								mapH = Integer.parseInt(Hh.getText());
								if(!flat) mapHeight = Integer.parseInt(Tt.getText()); else mapHeight = 1;
								sel = true;
							} catch (Exception e){
								opt = JOptionPane.showConfirmDialog(null, Q, "Specification of Map", 
										JOptionPane.OK_CANCEL_OPTION);
							}
						}
					}
				}
				time++;
			}
		}//*/
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
		g2.drawImage(logo, d.w-158, d.h-50, 158, 50, d);
	}
	
	protected void sInit() throws UnhandledException{
		buttontex = new Buttons("res/textureData/buttons.tx", d);

		buttons = new String[]{"Load Map", "Save Map", "Help", "Back"};
		buttontex = new Buttons("res/textureData/buttons.tx", d);
		tools = new String[]{"select", "selectarea", "brush", "fill", "erase", "zoomin", "zoomout",
				"uplvl", "downlvl","deletelvl", "addlvl"};
		toolsname = new String[]{"Sole Selection Tool", "Area Selection Tool", "Brush Tool", "Fill Tool",
				"Erase Tool", "Zoom In", "Zoom Out", "Move Up A Floor", "Move Down A Floor",
				"Delete Current Floor", "Add New Floor"};
		toolsdesc = new String[]{"Select a tile", "Select rectangular area swept",
				"Change tile on the mouse path", "Fill nearby region with same base",
				"Erase any object on mouse path", "[Ctrl + Mouse Wheel Up]", "[Ctrl + Mouse Wheel Down]",
				"[Mouse Wheel Up]", "[Mouse Wheel Down]", "", ""};
		toolsenable = new boolean[tools.length];

		tooltex = new Buttons("res/textureData/tools.tx", d);
		if(loc.equals("new")){
			z = new Zip("res/maps/0maps/temp.map");
			z.createZipFile("header.h", "floor.lr", "lower.lr", "over.lr", "item.lr");
		} else {
			z = new Zip("res/maps/0maps/"+loc+".map");
		}
		boolean exists;
		try{
			z.readFile("gamedata.gm");
			exists = true;
		} catch (NullPointerException e){
			exists = false;
		}
		if(!exists){
			gdd = new GameData_Dungeon();
		} else {
			gdd = new GameData_Dungeon(z, "gamedata.gm");
		}
		gdd.exists = exists;

		if(loc.equals("new")){
			/**JPanel p = new JPanel();
			Object[] Q = {"", p, };
			JOptionPane.showOptionDialog(null, Q, "Specification of Map",
					JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, Q);*/
			int pg = -1;
			String rooturl = "res/textureData/";
			File folder = new File(rooturl);
			ArrayList<Object> obj = new ArrayList();
			ArrayList<String> fs = new ArrayList();

			for(File entry : folder.listFiles()){
				if(!entry.getName().endsWith(".zip")) continue;
				Zip z = new Zip(rooturl + entry.getName());
				fs.add(entry.getName());
				Object[] page = {new ImageIcon(z.getImage("logo.png")), entry.getName()};
				obj.add(page);
			}
			while(pg == -1)
				pg = Engine_Info.showMessageDialog
						("Choose Texture Pack", obj.toArray(new Object[0]), true);
			texloc = fs.get(pg);

			try{brightness = Integer.parseInt(JOptionPane.showInputDialog
					("Input default brightness level of your map: (in the range 0 - 100, 100 is brightest) "));}
			catch(Exception e){}

			mLight = new Map(MapType.Flat, mapW, mapH, mapHeight, "int");
			mLight.init(brightness);
			m 		= loadMap(mt, mapW, mapH, mapHeight, m, "floor.lr", 0);
			mOver 	= loadMap(mt, mapW, mapH, mapHeight, mOver, "over.lr", -1);
			mItem 	= loadMap(mt, mapW, mapH, mapHeight, mItem, "item.lr", -1);
			mLowe 	= loadMap(mt, mapW, mapH, mapHeight, mLowe, "lower.lr", -1);
			mStat = new Map(mt, m.w, m.h, m.t, "String");
			mStat.init("");
		} else {
			IOHeader ioh = new IOHeader();
			ioh.loadIOHeader(z,"header.h");
			texloc = ioh.texloc;
			brightness = ioh.brightness;

			mapW = ioh.W; mapH = ioh.H; mapHeight = ioh.T;

			mLight = new Map(MapType.Flat, ioh.W, ioh.H, ioh.T, "int");
			mLight.init(brightness);
			m 		= loadMap("floor.lr", "int");
			mOver 	= loadMap("over.lr", "int");
			mItem 	= loadMap("item.lr", "int");
			mLowe 	= loadMap("lower.lr", "int");
			mStat	= loadMap("stat.dt", "String");
			try{
				String s = z.readFile("header.h").readLine();
				switch(s.split(",")[0]){
					case "Flat": mt = MapType.Flat; break;
					case "Leveled": mt = MapType.Leveled; break;
				}
			} catch(IOException e){throw new UnhandledException(101, e.getMessage());}
		}

		tiles = new Tiles(getTextureDataURL("res/textureData/"+texloc, "tile"), d);
		items = new Items(getTextureDataURL("res/textureData/"+texloc, "item"), d);
		spobj = new SpecialObjects(getTextureDataURL("res/textureData/"+texloc, "sobject"), d);
		mobs = new Mobs(getTextureDataURL("res/textureData/"+texloc, "mob"), d);

		flr = 0;
		mapHeight = m.t;

		for(int i = 0; i < tools.length; i++){
			if(mt == MapType.Flat){
				switch(tools[i]){
					case "uplvl":
					case "downlvl":
					case "deletelvl":
					case "addlvl":
						toolsenable[i] = false;
						break;
					default: toolsenable[i] = true; break;
				}
			} else {
				toolsenable[i] = true;
			}
		}

		cm 		= new Camera(CameraType.Dim2); cm.init();
		cmm 	= new Camera(CameraType.Dim2); cmm.init();
		navCm 	= new Camera(CameraType.Dim2); navCm.init(0, 0);
		target	= new Camera(CameraType.Dim2); target.init();
		setPrintingDetails(tileSize, tileSize, 10);
		sizeEnlarged = 1f; //SIZE ENLARGE
		memSizeEnlarged = -1;
		scaletool = 1.2f;
		rtbintv = (544f - tooltex.texH*scaletool)/(tools.length - 1);
		d.resetClearRegion();
		d.clearScreen(0xF0F0F0, true);
	}
	
	protected void sRun(Graphics g) throws UnhandledException{
		setPrintingDetails(tileSize, tileSize, 10);
		
		//Clear Screen
		if(memSizeEnlarged!=sizeEnlarged){
			if((m.w*tileSize*sizeEnlarged>544)|(m.h*tileSize*sizeEnlarged>544)){
				d.setDefaultProtectedRegion(d.w/2-272, d.h/2-270+ddh, d.w/2+272, d.h/2+272+ddh);
			} else {
				float tS = tileSize*sizeEnlarged/2;//semi tile size with enlarge
				d.setDefaultProtectedRegion((int)(d.w/2-m.w*tS), (int)(d.h/2-m.h*tS+2+ddh), 
						(int)(d.w/2+m.w*tS), (int)(d.h/2+m.h*tS+ddh));
				
			} d.defaultProtectedRegion();
			d.clearScreen(bg.p, bg.w, bg.h, true);
			memSizeEnlarged = sizeEnlarged;
		}
		d.setClearRegion(d.w/2-280, 0, d.w/2+280, d.h/2-272);
		d.clearScreen(bg.p, bg.w, bg.h, true);
		if((m.w*tileSize*sizeEnlarged>544)|(m.h*tileSize*sizeEnlarged>544)){
			if(navCm.xf > m.w-544f/tileSize/sizeEnlarged)
				navCm.init(m.w-544f/tileSize/sizeEnlarged, navCm.y);
			if(navCm.yf > m.h-544f/tileSize/sizeEnlarged)
				navCm.init(navCm.x, m.h-544f/tileSize/sizeEnlarged);
		} else {
			navCm.init(0, 0);
		}
		
		int w = tileSize, h = tileSize; 
		
		//Main Screen
		printLight2D(tiles, items, spobj, mobs, navCm);
		printScreen2D(tiles, items, spobj, mobs, navCm);
		printHP(mobs, navCm);
		executeCommandStack();
		
		if(inRange(mouseX, d.w/2+250, d.w)&inRange(mouseY, d.h/2-290, d.h/2+290)){
			d.setClearRegion(d.w/2+290, d.h/2-290, d.w, d.h/2+290);
			d.clearScreen(bg.p, bg.w, bg.h, true);
		}
		d.img.getGraphics().drawImage(logo, d.w-158, d.h-50, 158, 50, d);
		
		//Left Tool Bar
		switch(selectlooking){
		case 0: //Tiles
			spr = tiles; break;
		case 1: //Items
			spr = items; break;
		case 2: 
			spr = spobj; break; //case 3 waiting
		case 3:
			spr = mobs; break;
		default: 
			spr = null; break; 
		}
		int ii = 0;
		d.setProtectedRegion(sx, sty + ddh, sx+48*4, sdy + ddh);
		while(ii < spr.length){//Null Pointer Exception will occur if case 2, 3 is not done
			if(h * scale * (ii / 4) + sty - navLTBy > sdy){
				break;
			} else if (h * scale * (ii / 4) - navLTBy + 48 >= 0){
				int cc = 0, offsx = 0, offsy = 0;
				if(ii == cmm.x + cmm.y*4){cc = 1;}
				d.placeSprite(
						tabs, sx + w * scale * (ii % 4), h * scale * (ii / 4) + sty - navLTBy + ddh, 1,
						48, 48, 4/3f, cc, -1, true
				);
				Sprite til = spr.getItem(ii);
				d.setMergeCol(spr.blend, til.indCol);
				int ww = w, hh = h;
				try{
					ww = ((Mob)til).wid;
					hh = ((Mob)til).hei;
				} catch (Exception e){}
				try{
					ww = ((SpecialObjects)spr).wid;
					hh = ((SpecialObjects)spr).hei;
					offsx = w-ww;
					offsy = h-hh;
				} catch (Exception e){}
				d.placeShadowSprite(spr.tex, spr.tex, sx + w * (scale * (ii % 4) + (scale - scale2)/2) + offsx, 
						h * (scale * (ii / 4) + (scale - scale2)/2) + sty - navLTBy + offsy + ddh, 
						scale2, ww, hh, til.y, til.x, til.y, til.x, 0, true, false);
			}
			ii++;
		} while (h * scale * (ii / 4) + sty - navLTBy < sdy){
			d.placeSprite(
					tabs, sx + w * scale * (ii % 4), h * scale * (ii / 4) + sty - navLTBy + ddh, 1,
					48, 48, 4/3f, 2, -1, true
			);
			ii++;
		} 
		drawEnclosedRegion(sx, sdy + ddh, sx+48*4, d.h/2+272 + ddh, 48, 48);
		
		for(int i = 0; i < 4; i++){//Tabs in Left Tool Bar (T:LTB)
			if(selectlooking == i){
				d.placeSprite(tabs, sx+i*48, sty-31+ddh, 1, 48, 32, 0, i, 0);
			} else {
				d.placeSprite(tabs, sx+i*48, sty-31+ddh, 1, 48, 32, 1, i, 0);
			}
		}
		
		int offs = 5; float siz = 64;
		d.placeSprite(
				tabs, sx + offs, 
				sdy + offs + ddh, 
				siz/48, 48, 48, 4/3f, 0, 0, false
		);
		if(cmm.x != -1){
			try{
				int offsx = 0, offsy = 0;
				Sprite til = spr.getItem(cmm.x+cmm.y*4);
				d.setMergeCol(spr.blend, til.indCol);
				int ww = w, hh = h;
				try{
					ww = ((Mob)til).wid;
					hh = ((Mob)til).hei;
				} catch (Exception e){}
				try{
					ww = ((SpecialObjects)spr).wid;
					hh = ((SpecialObjects)spr).hei;
					offsx = w-ww;
					offsy = h-hh;
				} catch (Exception e){}
				d.placeShadowSprite(spr.tex, spr.tex, sx + offs + siz/6f + offsx, 
						sdy + offs + siz/6f + offsy + ddh, 
						siz*32/48/tileSize, ww, hh, til.y, til.x+til.offset*(clock/clockset %16)/16, 
						til.y, til.x+til.offset*(clock/clockset %16)/16, 0, false, false);
			} catch (Exception e){cmm.init();}
		}
		
		//Right Button Bar
		rightButtonBar(buttontex, buttons, seperation, scale);
		
		//Right Tools Bar
		rightToolsBar(tooltex, tools, 1);
	
		//Select Tile
		if(toolsenable[rtby]){
			switch(tools[rtby]){
			case "selectarea": 
				int sx, sy, ex, ey;
				sx = startX; sy = startY;
				ex = Math.max(sx, endX); ey = Math.max(sy, endY);
				if(ex == sx) sx = endX;
				if(ey == sy) sy = endY;
				for(int i = sx; i <= ex; i++){
				for(int j = sy; j <= ey; j++){
					if ((cm.x != -1)&inRange(i-navCm.xf, 0, 544f/tileSize/sizeEnlarged-1)&
							inRange(j-navCm.yf, 0, 544f/tileSize/sizeEnlarged-1)){//Select Tiles Region
						selectTile(i-navCm.xf, j-navCm.yf, w, h, 4);
					}
				}}
				break;
			case "fill":
				if(cm.x != -1){
					targettil = getTile(cm.x, cm.y, m);
					if((cmm.x != -1)&(targettil != cmm.x + cmm.y*4)){
						spreadCol(cm.x, cm.y, targettil, cmm.x + cmm.y*4);
					}
					targettil = -1;
				}
			default: 
				if ((cm.x != -1)&inRange(cm.xf-navCm.xf, 0, 544f/tileSize/sizeEnlarged-1)&
						inRange(cm.yf-navCm.yf, 0, 544f/tileSize/sizeEnlarged-1)){//Select Tiles Region
					selectTile(cm.x-navCm.xf, cm.y-navCm.yf, w, h, 4);
				} else cm.init();
				break;
			}
		}
		if(listselect) for(int i = 2; i < list.length; i++){
			String[] args = list[i].replace("(","").replace(")","").split(",");
			int x = Integer.parseInt(args[1]), y = Integer.parseInt(args[2]);
			if(args[0].equals(flr+"")&(x != -1)&
					inRange(x-navCm.xf, -1.5f, 544f/tileSize/sizeEnlarged+.5f)&
					inRange(y-navCm.yf, -1.5f, 544f/tileSize/sizeEnlarged+.5f))
				selectTile(x-navCm.xf, y-navCm.yf, w, h, 6);
		}
		
		//Others
		g.drawImage(d.img, 0, 0, d.img.getWidth(), d.img.getHeight(), d);
		g.setColor(Color.BLACK);
		
		//Drawing Strings
		g.setColor(Color.WHITE);
		drawString(loc, d.w/4-136, ddh+70, 1, 28, 1, 0, g);
		if (mt == MapType.Leveled){
			drawString("Floor "+flr, d.w/4-136, ddh+110, 1, 24, 1, 0, g);
		}
		if(cmm.x != -1){
			try{
				g.setColor(Color.WHITE);
				Sprite til = spr.getItem(cmm.x+cmm.y*4);
				drawPassage(til.name, (int) (sx + offs + siz/6f + siz), 
						(int) (sdy + siz/8f + ddh), sx + 48*4, d.w/2+272 + ddh, 0, 20, g);
				String description = "";
				if(selectlooking == 3){
					description += "Max Health: "+((Mob)til).maxhealth;
					description += "+Physical Attack: "+((Mob)til).atk;
					description += "+Physical Defense: "+((Mob)til).dfs;
					description += "+Magical Attack: "+((Mob)til).matk;
					description += "+Magical Defense: "+((Mob)til).mdfs;
					description += "+/+";
				}
				description += til.description;
				drawPassage(description, (int) (sx + offs + siz/6f), 
						(int) (sdy + siz + siz/8f + ddh), sx + 48*4, d.w/2+272 + ddh, 0, 18, g);
			} catch (Exception e){cmm.init();}
		}
		
		//Button Reaction
		if (cM != -1){//Choose Command Region (e.g. save map)
			int option;
			switch(buttons[cM]){
			case "Load Map": //choose and load map again
				String[] content0 = {"If you continue, the current progress " +
						"from your last save of the map will be discarded.",
						"You are advised to save your map now if you have not.",
						"Are you sure that you want to proceed?"};
				option = JOptionPane.showConfirmDialog(null, content0,
						"Load Another Map and Discard Changes", JOptionPane.YES_NO_OPTION);
				if(option == JOptionPane.YES_OPTION){
					loc = "null"; time = timeT; 
				}
				break;
			case "Save Map": //save map
				JPanel p = new JPanel();
				JTextField Name = new JTextField(10);
				p.add(Name); p.add(new JLabel(".mp"));
				JRadioButton rb = new JRadioButton("Default: "+loc+".mp");
				if(loc.equals("new")){rb.setEnabled(false);}
				Object[] content1 = {"File name of the map to be saved:", p, rb};
				option = JOptionPane.showConfirmDialog(null, content1,
						"Save Map", JOptionPane.OK_CANCEL_OPTION);
				if(option == JOptionPane.OK_OPTION){
					if(rb.isSelected()){
						Name.setText(loc);
					} else {
						if((Name.getText() == null)|(Name.getText() == "")){
							JOptionPane.showMessageDialog(null, "Name cannot be null!", "Name Null Error", 0);
							break;
						} else if (Name.getText().contains(",")|Name.getText().contains("-")|Name.getText().contains("/")){
							JOptionPane.showMessageDialog(null, "Name cannot contain invalid characters (, or - or /)!", "Invalid Character Error", 0);
							break;
						} else if (Name.getText().contentEquals("temp")) {
							JOptionPane.showMessageDialog(null, "Name cannot be \"temp\"!", "Preoccupied Name Error", 0);
							break;
						} else if (Name.getText().contentEquals("new")) {
							JOptionPane.showMessageDialog(null, "Name cannot be \"new\"!", "Preoccupied Name Error", 0);
							break;
						}
					}
					String URL = "res/maps/0maps/"+Name.getText()+".map";
					z = new Zip(URL);
					if(!new File(URL).exists()){
						try {
							appendFile("res/data/maps.dt", Name.getText());
							appendFile("res/data/gameprogress.dt", Name.getText());
						} catch (IOException e) {throw new UnhandledException(104, "File Output " +
								"Exception at void sRun()");}
						z.createZipFile("header.h", "floor.lr", "lower.lr", "over.lr", "item.lr");
					}
					z.prepareFile();
					z.writeFile("header.h", new IOHeader(mt, mapW, mapH, mapHeight, enc, texloc, brightness).getIOHeader());
					saveMap("floor.lr", m);
					saveMap("over.lr", mOver);
					saveMap("item.lr", mItem);
					saveMap("lower.lr", mLowe);
					saveMap("stat.dt", mStat);
					try{
						byte[] gdddata = gdd.getContents();
						z.writeFile("gamedata.gm", gdddata);
					} catch (NullPointerException e){}
					z.writeFile();
					chosen = 0; nchosen = 0;
					JOptionPane.showMessageDialog(null, "Map Saved Successfully!");
				}
				break;
			case "Help":
				help();
				break;
		/*	case "Properties":
				properties();
				break;//*/
			case "Back":
				String[] content2 = {"If you continue, the current progress " +
						"from your last save of the map will be discarded.",
						"You are advised to save your map now if you have not.",
						"Are you sure that you want to proceed?"};
				option = JOptionPane.showConfirmDialog(null, content2,
						"Exit to Main Screen and Discard Changes", JOptionPane.YES_NO_OPTION);
				if(option == JOptionPane.YES_OPTION){
					btg.engine = new Engine(btg, d, 2);
				}
				break;
			}
			cM = -1;
		} 
		
		//Change Tiles
		if ((cm.x!=-1)&toolsenable[rtby]&!listselect){
			switch(tools[rtby]){
			case "select":
			case "brush":
				if(cmm.x != -1){
					editTile(cm.x, cm.y, cmm.x + cmm.y*4);
				}
				break;
			case "erase":
				if(getTile(cm.x, cm.y, mItem) != -1) mItem = setTile(cm.x, cm.y, -1, mItem);
				else if (getTile(cm.x, cm.y, mOver) != -1) mOver = setTile(cm.x, cm.y, -1, mOver);
				break;
			}
		}
		
		//perform extra actions of the Right Tool Bar
		if((!mouseDown)&toolsenable[rtby]){
			switch(tools[rtby]){
			case "selectarea": 
				if((cmm.x != -1)&!listselect){
					int sx, sy, ex, ey;
					sx = Math.min(startX, endX); sy = Math.min(startY, endY);
					ex = Math.max(startX, endX); ey = Math.max(startY, endY);
					if(selectlooking == 0){
						for(int i = sx; i <= ex; i++){
						for(int j = sy; j <= ey; j++){
							editTile(i, j, cmm.x + cmm.y*4);
						}}
					}
					cm.init();
				}
				break;
			case "zoomin": zoom(true); rtby = 0; break;
			case "zoomout": zoom(false); rtby = 0; break;
			case "uplvl": changeflr(true); rtby = 0; break;
			case "downlvl": changeflr(false); rtby = 0; break;
			case "deletelvl": 
				if(mapHeight > 1){
					m.map = Arrays.delete((Object[]) (m.map), flr);
					mOver.map = Arrays.delete((Object[]) (mOver.map), flr);
					mItem.map = Arrays.delete((Object[]) (mItem.map), flr);
					mLowe.map = Arrays.delete((Object[]) (mLowe.map), flr);
					mStat.map = Arrays.delete((Object[]) (mStat.map), flr);
					mapHeight -= 1; m.t -= 1; mOver.t -= 1; mItem.t -= 1; mLowe.t -= 1; mStat.t -= 1;
					changeflr(true); changeflr(false); 
				}
				rtby = 0;
				break;
			case "addlvl"://Note: The referencing of the map layer will affect one another
				Map mrf = new Map(MapType.Flat, m.w, m.h, 1, "int");
				mrf.init(0);
				m.map = Arrays.insert((Object[])(m.map), mrf.map, flr+1);
				mrf = new Map(MapType.Flat, m.w, m.h, 1, "int");
				mrf.init(-1);
				mOver.map = Arrays.insert((Object[])(mOver.map), mrf.map, flr+1);
				mrf = new Map(MapType.Flat, m.w, m.h, 1, "int");
				mrf.init(-1);
				mItem.map = Arrays.insert((Object[])(mItem.map), mrf.map, flr+1);
				mrf = new Map(MapType.Flat, m.w, m.h, 1, "int");
				mrf.init(-1);
				mLowe.map = Arrays.insert((Object[])(mLowe.map), mrf.map, flr+1);
				mrf = new Map(MapType.Flat, m.w, m.h, 1, "String");
				mrf.init("");
				mStat.map = Arrays.insert((Object[])(mStat.map), mrf.map, flr+1);
				mapHeight += 1; m.t += 1; mOver.t += 1; mItem.t += 1; mLowe.t += 1; mStat.t += 1;
				changeflr(true);
				rtby = 0;
				break;
			}
		}
	}
	
	protected void rightToolsBar(Buttons tooltex, String[] tools, float scale) throws UnhandledException{
		int start = d.h/2 - 272;
		for(int i = 0; i < tools.length; i++){
			int selec = 0;
			if(!toolsenable[i]){
				selec = 2;
				if(rtby == i){
					rtby = 0;
				}
			} else if(rtby == i){
				selec = 1;
			}
			d.placeSprite(
					tooltex.tex, d.w/2+272, start + rtbintv*i + ddh, scaletool, 
					tooltex.texW, tooltex.texH, 1f*tooltex.getItem(tools[i]).y/tooltex.texH,
					1f*tooltex.getItem(tools[i]).x/tooltex.texW+selec, 0
			);
			int ob = ortby;
			if(ob == i){
				Graphics ggg = d.img.getGraphics();
				d.fontstr = fontstr; d.fonteng = fonteng;
				float reg = 1/7f; int len = d.getSizeOfString(toolsname[ob], 1, 20, ggg)[0];
				ggg.setColor(Color.BLACK);
				if(toolsdesc[ob]!=""){
					reg = 0;
					len = Math.max(len, d.getSizeOfString(toolsdesc[ob], 0, 15, ggg)[0]);
				}
				ggg.fillRect((int) (d.w/2+272+tooltex.texW*scaletool+2), 
						(int) (start + rtbintv*i + ddh + tooltex.texH*scaletool*reg), 
						len + 6, (int)(tooltex.texH*scaletool*(1-2*reg)));
				ggg.setColor(Color.WHITE);
				if(toolsdesc[ob]!=""){
					drawString(toolsname[ob], (int) (d.w/2+272+tooltex.texW*scaletool+5), 
							(int) (start + rtbintv*i + ddh + tooltex.texH*scaletool/2 - 10), 1, 20, 0, 1, 
							ggg);
					drawString(toolsdesc[ob], (int) (d.w/2+272+tooltex.texW*scaletool+5), 
							(int) (start + rtbintv*i + ddh + tooltex.texH*scaletool/2 + 10), 0, 15, 0, 1, 
							ggg);
				} else {
					drawString(toolsname[ob], (int) (d.w/2+272+tooltex.texW*scaletool+5), 
							(int) (start + rtbintv*i + ddh + tooltex.texH*scaletool/2 - 5), 1, 20, 0, 1, 
							ggg);
				}
			}
		}
	}

	protected void addDocumentListener(final JTextField btncommand, final JEditorPane cmdaid){
		btncommand.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {update(e);}
			public void removeUpdate(DocumentEvent e) {update(e);}
			public void changedUpdate(DocumentEvent e) {update(e);}
			public void update(DocumentEvent e){
				String msg = "<b>Do you mean this?</b> <br>  ";
				String tip = "<br><b>In this code:</b>";
				String txt = btncommand.getText();
				boolean blue = false;
				if(txt.contains("cmds:")) txt = txt.split("cmds:")[1];
				if(txt.contains("endcmds")) txt = txt.split("endcmds")[0];
				char[] t = txt.toCharArray();
				String k = ""; String[] strl = new String[0]; int s = 0; char c = ' ';
				for(int i = 0; i < txt.length(); i++){
					if(Character.isLetterOrDigit(t[i])) k += t[i];
					else{
						if(!k.equals("")){
							strl = Arrays.append(strl, " "+s+"|"+k);
							k = "";
						}
						// use startwith, endwith to check the label!
						// test string: say(hi)-if:{have(key)}then:{give(potion)-say(bye)}-close()
						switch(t[i]){// angle brackets "<" and ">" aren't treated as brackets,
							// as they're reserved as "next" operator in argument-syntax.
							case '(': case '[': case '{':
								strl = Arrays.append(strl, " "+s+"|"+t[i]); s++; break;
							case ')': case ']': case '}':
								s--; strl = Arrays.append(strl, " "+s+"|"+t[i]); break;
							default: strl = Arrays.append(strl, " "+s+"|"+t[i]);
						}
					}
				}
				if(!k.equals("")){
					strl = Arrays.append(strl, " "+s+"|"+k);
					k = "";
				}
				for(int i = 0; i < strl.length; i++) {
					k = extract(strl[i].toLowerCase());
					s = Integer.parseInt(strl[i].substring(1, strl[i].indexOf("|")));
					c = strl[i].charAt(0);
					swit: {
						boolean at = false;
						switch (k) {
							case "if":
							case "then":
							case "else":
								if (!extract(strl[i]).equals(k))
									strl[i] = "e" + s + "|" + k;
								else strl[i] = "g" + s + "|" + k;
								try {
									int[] brace = searchbrace(strl, i, s);
									int j0 = brace[0], i1 = brace[1];
									open:{
										if (i + 2 < strl.length) {
											if (strl[i + 1].endsWith(":") & strl[i + 2].endsWith("{"))
												break open;
										}
										tip += "<br>The \"if\"-\"then\"-\"else\" structure should be " +
												"in the form of: \"if:{...}then:{...}else:{...}\"";
										for (int j = i + 1; j < j0; j++)
											strl = Arrays.delete(strl, j);
										strl = Arrays.insert(strl, "e" + s + "|:", i + 1);
										strl = Arrays.insert(strl, "e" + s + "|{", i + 2);
										i1 -= j0 - i - 3;
										j0 -= j0 - i - 3;
									}
									close:{
										if (i1 < strl.length) {
											if (strl[i1].endsWith("}")) break close;
										}
										tip += "<br>The \"if\"-\"then\"-\"else\" structure should be " +
												"in the form of: \"if:{...}then:{...}else:{...}\"";
										if (i1 < strl.length & brace[2] == 0)
											strl = Arrays.delete(strl, i1);
										strl = Arrays.insert(strl, "e" + s + "|}", i1);
									}
									next:{
										if (i1+1 < strl.length) {
											if (strl[i1+1].endsWith("}")) break next;
											String match = "";
											if(k.equals("if")) match = "then";
											else if (k.equals("then")) match = "else";
											else break next;
											if (match.contains(extract(strl[i1+1]))){
												strl = Arrays.delete(strl, i1+1);
												strl = Arrays.insert(strl, "b" + s + "|" + match, i1+1);
											}
										}
									}
								} catch (ArrayIndexOutOfBoundsException E) { }
								break swit;
							case " ": case ">":
								break swit;
							case "{": case "}": case "(": case ")":
								break swit;
						}
						boolean named = false;
						if(c != ' ') continue;
						for (String name : cmdlist) {
							if (name.split("::")[0].startsWith(k)) {
								String addtxt = name.split("::")[0].split("\\(")[0];
								String syn = name.split("::")[0];
								if(syn.split("\\(")[0].equals(k)) {
									blue = true;
									if (syn.contains("(")) {
										int[] brace = searchbrace(strl, i, s);
										int j0 = brace[0], i1 = brace[1];

										strl[i] = "b" + s + "|" + addtxt;
										open:{
											if (i + 1 < strl.length) {
												if (strl[i + 1].endsWith("("))
													break open;
											}
											for (int j = i + 1; j < j0; j++)
												strl = Arrays.delete(strl, j);
											strl = Arrays.insert(strl, "e" + s + "|(", i + 1);
											i1 -= j0 - i - 2;
											j0 -= j0 - i - 2;
										}
										close:{
											if (i1 < strl.length) {
												if (strl[i1].endsWith(")")) break close;
											}
											if (i1 < strl.length & brace[2] == 0)
												strl = Arrays.delete(strl, i1);
											strl = Arrays.insert(strl, "e" + s + "|)", i1);
										}
										next:{
											if (i1 + 1 < strl.length) {
												if (strl[i1 + 1].endsWith("}")) break next;
												if (strl[i1 + 1].endsWith("-")) break next;
												strl = Arrays.insert(strl, "e" + s + "|-", i1 + 1);
											}
										}
										then:{//***after here syn becomes the arguments only, the original function name is discarded
											syn  = syn.split("\\(")[1].split("\\)")[0];
											String[] synn = syn.replace(", ",",").split(",");
											int I = 0;
											for(String elem : synn){
												if(i+2+I < strl.length) if(strl[i+2+I].endsWith(")")|strl[i+2+I].endsWith("}")) {
													//remind the user about the format of the input function
													strl = Arrays.insert(strl, "r"+s+"|"+elem, i+2+I);
													if(!(strl[i+1+I].endsWith("(")|strl[i+1+I].endsWith(","))){
														strl = Arrays.insert(strl, "r"+s+"|,", i+2+I); I++;}
												}
												else if(i+3+I < strl.length){
													while(!(strl[i+3+I].endsWith(",")|strl[i+3+I].endsWith(")")|strl[i+3+I].endsWith("}"))) {
														//combines all strings in function brackets that should belong to the same argument
														//stops when either [(1) the bracket is closed or (2) the comma has been reached] is true.
														strl[i+2+I] += extract(strl[i+3+I]);
														strl = Arrays.delete(strl, i+3+I);
														if(i+3+I >= strl.length) break;
													}
													String s2 = strl[i+2+I].substring(1, strl[i+2+I].indexOf("|")),
															st = extract(strl[i+2+I]);

													strl[i+2+I] = match(elem, s2, st);

													if(extract(strl[i+3+I]).equals(",")) I++;
												}
												I++;
											}
										}
										break swit;
									} else {
										if (i + 1 < strl.length) {
											if (strl[i + 1].endsWith("(")) {
												strl = Arrays.delete(strl, i + 1);
												break swit;
											}
										}
									}
								}
								if (!named) {strl[i] = "b" + s + "|" + addtxt; named = true;}
								else strl[i] += "$$" + addtxt;
							}
						}
					}
				}
				txt = ""; ArrayList<String> txts = new ArrayList<String>(); int l = 0;
				for(int i = 0; i < strl.length; i++){
					String[] list; boolean cont = strl[i].contains("$$"); char ch = strl[i].charAt(0);
					list = new String[]{extract(strl[i])};
					if(cont) list = list[0].split("\\$\\$");
					switch(ch){
						//error colour (red - orange)
						case 'e': txt += "<span style=\"color:red;background-color:yellow;font-weight:bold;\">"; break;
						//error colour (greyish red - orange)
						case 'r': txt += "<span style=\"color:#917F7F;background-color:yellow;font-weight:bold;\">"; break;
						//syntax component (green)
						case 'g': txt += "<span style=\"color:green;font-weight:bold;\">"; break;
						//recognized function (blue)
						case 'b': txt += "<span style=\"color:blue;font-weight:bold;\">"; break;
						//recognized function (cyan)
						case 'c': txt += "<span style=\"color:#008B8B;font-weight:bold;\">"; break;
					}
					int maxl = -1;
					for(String elem : list) if(elem.length() > maxl) maxl = elem.length();
					txt += pad(list[0], maxl); l += maxl;
					for(int I = 1; I < list.length; I++){
						if(I <= txts.size()) txts.set(I-1, txts.get(I-1)+fpad(pad(list[I], maxl), l-txts.get(I-1).length()));
						else txts.add(I-1, fpad(pad(list[I], maxl), l));
					}
					switch(ch){
						case 'e': case 'r': case 'g': case 'b': case 'c':
							txt += "</span>";
					}
				}
				if(blue) tip += "<br> Please click \"Command List\" button to view the usage of commands.";
				if(tip.equals("<br><b>In this code:</b>")) tip += "<br>There is no syntax error.";
				msg += toHTML(txt) + "<span style=\"color:blue;font-weight:bold;\">";
				for(String subt : txts)	msg += "<br>" + toHTML(subt);
				msg += "</span>" + tip;
				cmdaid.setText("<div style=\"font-family:monospace;\">"+msg+"</div>");
				cmdaid.select(0,0);
			}
			String fpad(String in, int l){return String.format("%1$" + l + "s", in);}
			String pad(String in, int l){return String.format("%1$-" + l + "s", in);}
			String toHTML(String in){return in.replace(" ", "&nbsp;");}
			String extract(String in){return in.substring(in.indexOf("|") + 1);}
		});
	}
	
	protected int executeCommand(String str) throws UnhandledException{
		try{
			str = str.replace("[this]", cm.x+"::"+cm.y);
			String command; String[] args;
			if(str.contains("(")){
				command = str.split("\\(")[0];
				args = str.split("\\(")[1].split("\\)")[0].split("::");
			} else {
				command = str;
				args = new String[0];
			}
			switch(command){
			case "none": return 1;
			case "setspawn":
				gdd.x0 = Integer.parseInt(args[0]);
				gdd.y0 = Integer.parseInt(args[1]);
				gdd.z0 = flr;
				if(!gdd.exists){
					gdd.dir = 2;
					String[] keys = {"HP", "EP", "Atk", "Dfs", "M Atk", "M Dfs"};
					String[] name = {
							"Health Points", "Energy Points", "Physical Attack", "Physical Defense",
							"Magical Attack", "Magical Defense"};
					float[] values = {100, 100, 1, 1, 0, 0};
					gdd.stat = new Stat(keys, name, values);
					int[][] invent = {{},{}};
					gdd.inventory = invent;
				}
				return 1;
			case "delete":
				int xx = Integer.parseInt(args[0]), yy = Integer.parseInt(args[1]);
				if(args[0].startsWith("+") || args[0].startsWith("-"))
					xx += cm.x;
				if(args[1].startsWith("+") || args[1].startsWith("-"))
					yy += cm.y;
				cm.init();
				setTile(xx, yy, -1, mItem);
				setTile(xx, yy, -1, mOver);
				setTile(xx, yy, "", mStat);
				return 1;
			case "setcontent":
			{	int tehx = Integer.parseInt(args[0]);
				int tehy = Integer.parseInt(args[1]);
				String content = getTileStr(tehx, tehy, mStat);
				JTextField input = new JTextField(20);

				//retrieve data from storage
				input.setText(getContent(content, "<"+args[2]+">"));

				int opt0 = JOptionPane.showOptionDialog(null, new Object[]{"Input the content of <"+args[2]+">:", input}, "Input", 2,3, null, null, null);
				//String input = JOptionPane.showInputDialog(null, "Input the content of <"+args[2]+">:");
				if(opt0 != 0) return 3;
				else if(input.getText().equals("")) return 0;
				content = addTagTo(getParameter(content, "<"+args[2]+">"), input.getText(), "<"+args[2]+">");
				mStat = setTile(tehx, tehy, content, mStat);
				return 1;}
			case "setcontenttable":
			{	int tehx = Integer.parseInt(args[0]);
				int tehy = Integer.parseInt(args[1]);
				String content = getTileStr(tehx, tehy, mStat);
				DataArrays da = new DataArrays();

				//retrieve data from storage
				String dt = getContent(content, "<"+args[2]+">");
				for(String co : dt.split(";")){
					if (!co.contains("]:")) break;
					da.addRecords(co.substring(1,co.indexOf("]:")), co.substring(co.indexOf("]:")+2));
				}

				String[] options = {"OK", "Add", "Advanced", "Command List", "Cancel"};
				int option = 1;
				content = addTagTo(getParameter(content, "<btnalign>"), "Horizontal", "<btnalign>");

				JSplitPane jsp = new JSplitPane();
				JTextPane jtp = new JTextPane();
				jtp.setEditable(false);
				jtp.setOpaque(false);
				jtp.setFont(new Font("Times New Roman", 0, 16));
				JTextPane jtpr = new JTextPane();
				jtpr.setEditable(false);
				jtpr.setOpaque(false);
				jtpr.setFont(new Font("Times New Roman", 0, 16));
				JTextField btnname = new JTextField(5), btncommand = new JTextField(15);

				JEditorPane cmdaid = new JEditorPane();
				cmdaid.setEditable(false);
				cmdaid.setOpaque(false);
				cmdaid.setEditorKit(new HTMLEditorKit());
				cmdaid.setPreferredSize(new Dimension(200, 100));
				JScrollPane cmdp = new JScrollPane(cmdaid, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

				JPanel namep = new JPanel();
				namep.add(jtp); namep.add(btnname);
				namep.setLayout(new BoxLayout(namep, BoxLayout.Y_AXIS));
				JPanel commandp = new JPanel();
				commandp.add(jtpr); commandp.add(btncommand);
				commandp.setLayout(new BoxLayout(commandp, BoxLayout.Y_AXIS));

				jsp.setLeftComponent(namep);
				jsp.setRightComponent(commandp);
				jsp.setEnabled(false);
				jsp.setDividerSize(0);
				jsp.setResizeWeight(0.5);

				JPanel jp = new JPanel();
				jp.setLayout(new BoxLayout(jp, BoxLayout.Y_AXIS));
				jp.add(jsp);
				jp.add(cmdp); //or jp.add(cmdaid)

				addDocumentListener(btncommand, cmdaid);
				do{
					if(option == 1){
						String names = "Button Names", commands = "Button Commands";
						for(int i = 0; i < da.numrecord; i++){
							names += "\n"+da.getRecord(i)[0];
							commands += "\n"+da.getRecord(i)[1];
						}

						jtp.setText(names);
						jtpr.setText(commands);

						option = JOptionPane.showOptionDialog(null, jp, "Set Content Table", -1, -1, null, options, null);
						switch(option){
						case 0: break;
						case 1:
							da.addRecords(btnname.getText(), btncommand.getText());
							btnname.setText(""); btncommand.setText("");
							break;
						case 2: break;
						case 3: break;
						case 4: break;
						}
					} else if (option == 2){//Advanced
						String[] opts = {"Horizontal", "Vertical"};
						int opt = JOptionPane.showOptionDialog(null, "Please choose a layout for the buttons below:",
								"Set Content Table", -1, -1, null, opts, null);
						content = addTagTo(getParameter(content, "<btnalign>"), opts[opt], "<btnalign>");
						option = 1;
					} else if (option == 3){
						JPanel ma = new JPanel();
						JPanel mb = new JPanel();
						for(int i = 0; i < cmdlist.length; i++){
							ma.add(new JLabel(cmdlist[i].split("::")[0]));
							mb.add(new JLabel(cmdlist[i].split("::")[1]));
						}
						ma.setLayout(new BoxLayout(ma, BoxLayout.Y_AXIS));
						mb.setLayout(new BoxLayout(mb, BoxLayout.Y_AXIS));
						JSplitPane m = new JSplitPane();
						m.setLeftComponent(ma); m.setRightComponent(mb);
						m.setEnabled(false);
						m.setDividerSize(0);
						m.setResizeWeight(0.5);
						JOptionPane.showMessageDialog(null, m, "command list", JOptionPane.INFORMATION_MESSAGE);
						option = 1;
					}
				} while ((option != 0)&(option != 4)&(option != -1));
				switch(option){
					case 0: //OK
						String data = "";
						for(int i = 0; i < da.numrecord; i++){
							data += "["+da.getRecord(i)[0]+"]:"+da.getRecord(i)[1]+";";
						}
						if(da.numrecord == 0) return 0;
						setTile(tehx, tehy, addTagTo(getParameter(content, "<"+args[2]+">"), data, "<"+args[2]+">"), mStat);
						return 1;
					case 4: return 3;//Cancel
					case -1: return 3; //Cross button
				}}
			case "setlist":
				//r, mobs, select+guards+of+this+door:+This+door+can+only+be+opened+after+all+guards+are+killed
			{	list = new String[0];
				list = Arrays.append(Arrays.append(list, args[1]), args[0]+","+cm.x+","+cm.y);
				switch(list[0]){
					case "mobs": case "stat": break;
					default: list[0] = "mobs";
				}
				if(haveTag(getTileStr(cm.x, cm.y, mStat),"<"+args[0]+">"))
					list = Arrays.append(list,getContent(getTileStr(cm.x, cm.y, mStat),"<"+args[0]+">").split("::"));
				listselect = true;
				for(int i = 0; i < toolsenable.length; i++){
					switch(tools[i]){
						case "uplvl": case"downlvl": case"addlvl": case"deletelvl":break;
						case "fill": toolsenable[i] = false; break;
						default: toolsenable[i] = true;
					}
				}
				return 1;}
			case "setloot":
			{	int tehx = Integer.parseInt(args[0]);
				int tehy = Integer.parseInt(args[1]);
				String content = getTileStr(tehx, tehy, mStat);
				DataArrays da = new DataArrays();

				//retrieve data from storage
				String dt = getContent(content, "<loot>");
				for(String loot : dt.split(";")){
					da.addRecords(loot);
				}

				String[] options = new String[]{"OK", "Add", "Cancel"};
				int option = 1;

				JSplitPane jsp = new JSplitPane();
				JTextPane jtp = new JTextPane();
				jtp.setEditable(false);
				jtp.setOpaque(false);
				jtp.setFont(new Font("Times New Roman", 0, 16));
				JTextPane jtpr = new JTextPane();
				jtpr.setEditable(false);
				jtpr.setOpaque(false);
				jtpr.setFont(new Font("Times New Roman", 0, 16));
				JTextField btnname = new JTextField(5);
				final JTextField btncommand = new JTextField(15);

				final JEditorPane cmdaid = new JEditorPane();
				cmdaid.setEditable(false);
				cmdaid.setOpaque(false);
				cmdaid.setEditorKit(new HTMLEditorKit());
				cmdaid.setPreferredSize(new Dimension(200, 100));
				JScrollPane cmdp =
						new JScrollPane(cmdaid, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
								JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

				JPanel commandp = new JPanel();
				commandp.add(jtpr);
				commandp.add(btncommand);
				commandp.setLayout(new BoxLayout(commandp, BoxLayout.Y_AXIS));

				JPanel jp = new JPanel();
				jp.setLayout(new BoxLayout(jp, BoxLayout.Y_AXIS));
				jp.add(commandp);
				jp.add(cmdp); //or jp.add(cmdaid)

				btncommand.getDocument().addDocumentListener(new DocumentListener() {
					public void insertUpdate(DocumentEvent e) {update(e);}
					public void removeUpdate(DocumentEvent e) {update(e);}
					public void changedUpdate(DocumentEvent e) {update(e);}
					public void update(DocumentEvent e){
						String txt = match("ITEM","",btncommand.getText()); int l = 0;
						String[] list; boolean cont = txt.contains("$$");
						txt = "<span style=\"color:#008B8B;font-weight:bold;\">" + extract(txt).replace("$$","<br>") + "</span>";
						cmdaid.setText("<div style=\"font-family:monospace;\">"+toHTML(txt)+"</div>");
						cmdaid.select(0,0);
					}
					String fpad(String in, int l){return String.format("%1$" + l + "s", in);}
					String pad(String in, int l){return String.format("%1$-" + l + "s", in);}
					String toHTML(String in){return in.replace(" ", "&nbsp;");}
					String extract(String in){return in.substring(in.indexOf("|") + 1);}
				});
				do{
					if(option == 1){
						String names = "Button Names", commands = "Add Loots";
						for(int i = 0; i < da.numrecord; i++){
							commands += "\n"+da.getRecord(i)[0];
						}

						jtp.setText(names);
						jtpr.setText(commands);
						option = JOptionPane.showOptionDialog(null, jp, "Set Content Table", -1, -1, null, options, null);
						switch(option){
							case 0: break;
							case 1:
								da.addRecords(btncommand.getText());
								btnname.setText(""); btncommand.setText("");
								break;
							case 2: break;
						}
					}
				} while ((option != 0)&(option != 2)&(option != -1));
				switch(option){
					case 0: //OK
						String data = "";
						for(int i = 0; i < da.numrecord; i++){
							data += da.getRecord(i)[0]+";";
						}
						if(da.numrecord == 0) return 0;
						setTile(tehx, tehy, addTagTo(getParameter(content, "<loot>"), data, "<loot>"), mStat);
						return 1;
					case 2: return 3;//Cancel
					case -1: return 3; //Cross button
				}
				break;}
			case "setaction":
			{
				int tehx = Integer.parseInt(args[0]);
				int tehy = Integer.parseInt(args[1]);

				JTextField btncommand = new JTextField(15);
				btncommand.setText(getContent(getTileStr(tehx, tehy, mStat), "<action>"));

				JEditorPane cmdaid = new JEditorPane();
				cmdaid.setEditable(false);
				cmdaid.setOpaque(false);
				cmdaid.setEditorKit(new HTMLEditorKit());
				cmdaid.setPreferredSize(new Dimension(200, 100));
				JScrollPane cmdp =
						new JScrollPane(cmdaid, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
								JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

				JPanel jp = new JPanel();
				jp.setLayout(new BoxLayout(jp, BoxLayout.Y_AXIS));
				jp.add(btncommand);
				jp.add(cmdp); //or jp.add(cmdaid)

				addDocumentListener(btncommand, cmdaid);

				int option = JOptionPane.showOptionDialog(
						null, jp, "Set Action Command When Triggered",
						-1,-1, null,
						new String[]{"OK", "Cancel"}, null);
				if(option == 0 && btncommand.getText() != "") //OK
					setTile(tehx, tehy,
							addTagTo(getParameter(getTileStr(tehx, tehy, mStat), "<action>"),
									btncommand.getText(), "<action>"), mStat);
				return 1;}
			}
		} catch(Exception e){e.printStackTrace();}
		return 0;
	}

	/**@param elem: matching item
	 * @param s2: index
	 * @param st: content*/
	public String match(String elem, String s2, String st){
		String out = "";
		match:{
			try{
				ArrayList<Sprites> spr = new ArrayList();
				switch(elem){
					case "ITEM": spr.add(items); 	break;
					case "MOBS": spr.add(mobs); 	break;
					case "SPOBJ":spr.add(spobj); 	break;
					case "TILE": spr.add(tiles);	break;
					case "OBJ":  spr.add(tiles);
								 spr.add(items);
								 spr.add(mobs);
								 spr.add(spobj);	break;
					default: out = "c"+s2+"|"+st; break match;
				}
				if(spr.size() != 0){
					String oak = ""; boolean nmd = false;
					for(Sprites sp : spr.toArray(new Sprites[0]))
					for(String nam : sp.names){
						if(nam.contains(st)){
							if (!nmd) {oak = nam; nmd = true;}
							else oak += "$$" + nam;
						}
					}
					if(oak.equals("")) out = "r"+s2+"|"+st;
					else out = "c"+s2+"|"+oak;
				} else out = "r"+s2+"|"+st;
			} catch (Exception e5){out = "r"+s2+"|"+st;}
		}
		return out;
	}

	/**@param strl: input string array of the analyzed bracket structure
	 * @param i: starting position of checking, esp. when there are keywords like "if" or "give" (commands)
	 * @param s: indicates the "level", where inside a bracket structure this number increment by 1, with boundary (the brackets) at the lower level.
	 * @return value: <b>int[]</b>{<b>j0</b>, <b>i1</b>, <b>inclosure</b>}
	 * <br> <b>j0</b>: the first instance of an alphabet or "inside" of the bracket structure (with s increasing)
	 * <br> <b>i1</b>: the first instance of "quitting bracket structure" (with s decreasing)
	 * <br> <b>inclosure</b>: 1 if the close bracket is incomplete,
	 * e.g. if:{have<span style="font-weight:bold;color:green;background-color:#c8e6c9;">(</span>KEY<span style="font-weight:bold;color:red;background-color:yellow;">)</span>}*/
	private int[] searchbrace(String[] strl, int i, int s) {
		boolean at = false; int temp, inclosure = 0, i1 = -1, j0 = -1;
		for (int j = i + 1; j < strl.length; j++) {
			temp = Integer.parseInt(strl[j].substring(1, strl[j].indexOf("|")));
			if (!at) {
				if ((temp == s + 1) |
						Character.isLetterOrDigit(strl[j].substring(strl[j].indexOf("|") + 1).charAt(0))) {
					at = true;
					j0 = j;
					if (temp == s + 1) continue;
					else break;
				}
			}
			if (at & (temp <= s)) {
				i1 = j;
				if(temp < s) inclosure = 1;
				break;
			}
		} // at: false => no if brace structure; true: has if-open brace
		if (j0 == -1) j0 = strl.length;
		if (i1 == -1) i1 = strl.length;
		return new int[]{j0, i1, inclosure};
	}

	protected void selectTile(float x, float y, int w0, int h0, int ani){
		int[] scor = mapToScreen(x, y, 0, 0, w0, h0, true, 0);
		if ((ani == 4)&(listselect)) ani = 5;
		d.placeSprite(
				tabs, scor[0]-sizeEnlarged*2, scor[1]-sizeEnlarged*2, sizeEnlarged*.75f,
				48, 48, 4/3f, ani, 0, true
		);
	}
	
	protected Map genMap(MapType dim, int w, int h, int t, Map Mp, int value) throws UnhandledException{
		Mp = new Map(dim, w, h, t, "int");
		for(int k = 0; k < t; k++)
		for(int j = 0; j < h; j++)
		for(int i = 0; i < w; i++)
			Mp.setTile(i, j, k, value);
		return Mp;
	}
	protected Map genMap(int w, int h, Map Mp, int value) throws UnhandledException{
		Mp = new Map(MapType.Flat, w, h, 1, "int");
		for(int j = 0; j < h; j++)
		for(int i = 0; i < w; i++)
			Mp.setTile(i, j, value);
		return Mp;
	}
	
	protected Map loadMap(MapType dim, int w, int h, int t, Map Mp, String path, int value) throws UnhandledException{
		if(dim == MapType.Flat){Mp = genMap(w, h, Mp, value);}
		else{Mp = genMap(dim, w, h, t, Mp, value);}
		z.prepareFile();
		saveMap(path, Mp);
		z.writeFile(path, new IOHeader(dim, w, h, t, enc, texloc, brightness).getIOHeader());
		z.writeFile();
		return Mp;
	}
	
	protected void help(){
		info = true;
		Object[] obj = {}; 
		String rooturl = "res/info/0 mapcreate/";
		String url = "mc?.png"; 
		for(int i = 1; i <= 6; i++){
			Object[] page = {new ImageIcon(rooturl + url.replace("?", i+""))};
			obj = Arrays.append(obj, page);
		}
		Engine_Info.showMessageDialog("Help", obj, false);
		info = false;
	}
	
	protected void selected(){
		switch(chosen){
		case 0: loc = "new"; break;
		default: loc = choice[chosen]; break;
		}
	}

	public void processKeyEventG(KeyEvent k) throws UnhandledException{
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
	public void processMouseEventL(MouseEvent me, int ms) throws UnhandledException{/**ms: mouse state*/
		if(time < 0){
			int x, y;
			x = me.getX(); y = me.getY();
			switch(ms){
			case 1://Pressed
				if(inMapRegion(x, y)){
					cameraSetLoc(x, y);
					if(tools[rtby].equals("select")) listselect(true, true);
					else if (tools[rtby].equals("erase")) listselect(false, true);
				}
				if(inMapRegion(x, y)&toolsenable[rtby]){
					if(tools[rtby] == "selectarea"){
						cameraSetLoc(x, y);
						startX = cm.x; startY = cm.y;
						endX = cm.x; endY = cm.y;
					}
				}
			case 0://Clicked
				mmx = x; mmy = y;
				int w = tileSize, h = tileSize;
				if(inMapRegion(x, y)){
					cameraSetLoc(x, y);
				} else if (inSelTileRegion(x, y)&!listselect){
					float xs = (float)Math.floor((me.getX()-sx)/(w*scale));
					float ys = (float)Math.floor((me.getY()-(sty+ddh-navLTBy))/(h*scale));
					cmm.init(xs, ys);
				} else if (inRTB(x, y)){
					int distft = y - d.h/2 + 272 - ddh; //distance from top margin
					cm.init(); startX = -1; startY = -1; endX = -1; endY = -1;
					if((distft%rtbintv <= tooltex.texH*scaletool)&inRange(distft, 0, 544)){
						rtby = (int)Math.floor(distft/rtbintv);
					}
				} else {
					cmm.init(); 
					if(inSelButtonRegion(x, y)){
						cm.init();
						if((me.getY()-(sry+ddh+seperation)+32+seperation)%(32+seperation)<=32){
							cM = (int)Math.floor((me.getY()-(sry+ddh+seperation))/(32+seperation));
						} else cM = -1;
					} else if (inLTBTabRegion(x, y)&!listselect){
						selectlooking = (x-sx)/48; navLTBy = 0; rtby = 0;
						resetLooking();
					} else cm.init();
				}
				break;
			case 2://Released
				if(inMapRegion(x, y)&toolsenable[rtby]){
					if(tools[rtby] == "selectarea"){
						cameraSetLoc(x, y);
						endX = cm.x; endY = cm.y;
					}
				}
				if(listselect&
						!(inMapRegion(x, y)|inLTBTabRegion(x, y)|inSelButtonRegion(x, y)|inRTB(x, y))){
					listselect = false;
					resetLooking();
					String[] args = list[1].split(",");
					String val = "";
					for(int i = 2; i < list.length; i++){
						if(i != 2) val += "::";
						val += list[i];
					}
					if(!val.equals("")) val = addTagTo("", val, "<"+args[0]+">");
					setTile(Integer.parseInt(args[1]), Integer.parseInt(args[2]), val, mStat);
				//	list = Arrays.append(list,getContent(getTileStr(cm.x, cm.y, mStat),"<"+args[0]+">").split("::"));
				}
				mmx = -1; mmy = -1;
				break;
			case 3://Dragged
				if(inMapRegion(x, y)&toolsenable[rtby]){
					switch(tools[rtby]){
					case "selectarea": 
						cameraSetLoc(x, y);
						listselect(true, false);
						endX = cm.x; endY = cm.y;
						break;
					case "brush": listselect(true, false);
					case "erase":
						cameraSetLoc(x, y);
						listselect(false, true);
						break;
					default:
						float tS = tileSize*sizeEnlarged;
						float ddx = 1f*(mmx-x)/tS, ddy = 1f*(mmy-y)/tS;
						navCm.move(ddx, ddy);
						if(navCm.xf < 0) navCm.xf = 0;
						if(navCm.yf < 0) navCm.yf = 0;
						if(navCm.xf+544/tS>m.w) navCm.xf=m.w-544/tS;
						if(navCm.yf+544/tS>m.h) navCm.yf=m.h-544/tS;
						break;
					}
				} else if (inSelTileRegion(x, y)){
					int ddy = mmy - y;
					navLTBy += ddy;
					if (navLTBy > (spr.length/4+1)*48+sty-sdy) navLTBy = (spr.length/4+1)*48+sty-sdy;
					if (navLTBy < 0) navLTBy = 0;
				}
				mmx = x; mmy = y;
				break;
			case 4://Moved
				break;
			}
		}
	}
	public void processMouseEventM(MouseEvent me) throws UnhandledException{
		if(time < 0){
			int x, y;
			x = me.getX(); y = me.getY();
			if (inRange(x, d.w/2+272, d.w/2+272+tooltex.texW*scaletool)){
				int distft = y - d.h/2 + 272 - ddh; //distance from top margin
				if((distft%rtbintv <= tooltex.texH*scaletool)&inRange(distft, 0, 544)){
					ortby = (int)Math.floor(distft/rtbintv);
					return;
				}
			}
			ortby = -1;
		}
	}
	public void processMouseWheelEventG(MouseWheelEvent mw) throws UnhandledException{
		int wheelRot = mw.getWheelRotation();
		if(time < 0){
			if(Ctrl){
				if(wheelRot > 0) zoom(false);
				else if (wheelRot < 0) zoom(true);
			} else {
				int x = mw.getX(), y = mw.getY();
				if(inMapRegion(x, y)){
					if(wheelRot > 0) changeflr(false);
					else if (wheelRot < 0) changeflr(true);
				} else if (inSelTileRegion(x, y)){
					if((wheelRot < 0)&(navLTBy>0)) navLTBy-=6;
					else if ((wheelRot > 0)&(navLTBy<((spr.length-1)/4+1)*48+sty-sdy)) navLTBy+=6;
				} else {
				}
			}
			
		}
	}
	protected void spreadCol(int x, int y, int til, int withtil) throws UnhandledException{
		if(inRange(x, 0, m.w-1)&inRange(y, 0, m.h-1)){
			if((getTile(x, y, m) == til)&getTileItem(x, y, mOver).passable){
				editTile(x, y, withtil); //This would crash when the region chosen is too large.
				if(getTile(x, y, m) != til){
					spreadCol(x+1, y, til, withtil);
					spreadCol(x-1, y, til, withtil);
					spreadCol(x, y+1, til, withtil);
					spreadCol(x, y-1, til, withtil);
				}
			}
		}
	}
	protected void editTile(int x, int y, int withtil) throws UnhandledException{
		editTile(x, y, withtil, selectlooking);
	}
	protected void cameraSetLoc(int x, int y) throws UnhandledException{
		if((w*m.w*sizeEnlarged>544)|(h*m.h*sizeEnlarged>544)){//w/2*(m.w*sizeEnlarged)
			cm.init((float)Math.floor(1f*(x-(d.w/2-272))/w/sizeEnlarged+navCm.xf),
					(float)Math.floor(1f*(y-(d.h/2-272+ddh))/h/sizeEnlarged+navCm.yf));
		} else {
			cm.init((float)Math.floor(1f*(x-(d.w/2-(w*m.w*sizeEnlarged)/2))/w/sizeEnlarged),
					(float)Math.floor(1f*(y-(d.h/2-(h*m.h*sizeEnlarged)/2+ddh))/h/sizeEnlarged));
		}
	}
	protected void zoom(boolean in){
		float amount = 0.04f, s = tileSize*sizeEnlarged, stx = d.w/2 - 272, sy = d.h/2 - 272 + ddh;
		//scrolling amount;;starting x of game region
		if(in){
			if(s<64){sizeEnlarged+=amount;}
			else {sizeEnlarged = 64/tileSize;}
		} else{
			if(s>32){sizeEnlarged-=amount;}
			else {sizeEnlarged = 32/tileSize;}
		}
		navCm.xf += (mouseX-stx)/s; navCm.yf += (mouseY-sy)/s;
		s = tileSize*sizeEnlarged;
		navCm.xf -= (mouseX-stx)/s; navCm.yf -= (mouseY-sy)/s;
		float con = 544/s;
		if(navCm.xf<0){navCm.xf=0;}
		if(navCm.yf<0){navCm.yf=0;}
		if(navCm.xf+con>m.w)
			{navCm.xf=m.w-con;}
		if(navCm.yf+con>m.h)
			{navCm.yf=m.h-con;}
	}
	protected void changeflr(boolean up) throws UnhandledException{
		if(up) {if(flr<mapHeight-1) {flr++;cmm.init();}}
		else {if(flr > 0) {flr--;cmm.init();}}
	}
	protected void back() throws UnhandledException{
		btg.engine = new Engine(btg, d, 2);
	}
	protected void resetLooking(){
		rtby = 0;
		switch(selectlooking){
			case 0:
				for(int i = 0; i < tools.length; i++){
					if((tools[i] == "selectarea")|(tools[i] == "fill")) toolsenable[i] = true;
				}
				break;
			case 1:
			case 2:
			case 3:
				if(!listselect)
				for(int i = 0; i < tools.length; i++){
					if((tools[i] == "selectarea")|(tools[i] == "fill")) toolsenable[i] = false;
				}
				break;
		}
	}
	protected void listselect(boolean select, boolean delete) throws UnhandledException{
		if(listselect){
			boolean nquit = true;
			if(delete) for(int i = 2; i < list.length; i++){
				String[] args = list[i].replace("(","").replace(")","").split(",");
				if(!args[0].equals(flr+"")) continue;
				if((Integer.parseInt(args[1]) == cm.x)&(Integer.parseInt(args[2]) == cm.y)){
					list = Arrays.delete(list, i); nquit = false; break;}
			}
			if(nquit&select){
				switch(list[0]){
					case "mobs":
						int ind = getTile(cm.x,cm.y,mItem);
						if((ind != -1)&(ind%2 == 1))
							list = Arrays.append(list, "("+flr+","+cm.x+","+cm.y+","+mobs.getItem(ind/2).name+")");
						break;
					case "stat"://later do
						break;
				}

			}
		}
	}
	
	protected boolean inSelTileRegion(int x, int y){
		return (x>sx)&(x<sx+w*scale*4)&(y>sty+ddh)&(y<sdy+ddh);}
	protected boolean inLTBTabRegion(int x, int y){
		return (x>sx)&(x<sx+w*scale*4)&(y>sty-32+ddh)&(y<sty+ddh);}
	protected boolean inSelButtonRegion(int x, int y){
		return (x>d.w-sx-144-24)&(x<d.w-sx)&(y>sry+ddh)&(y<sry+seperation+(32+seperation)*buttons.length);
	}
	protected boolean inRTB(int x, int y){
		return inRange(x, d.w/2+272, d.w/2+272+tooltex.texW*scaletool);
	}

}