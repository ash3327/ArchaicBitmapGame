package project.Engines;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import project.*;
import project.GameData.*;
import project.Sprite.*;
import project.Sprites.*;

public class Engine_Dungeon extends Engine {
	public static int mapSize = 17, mapHeight = 50;
	Image icon = Toolkit.getDefaultToolkit().getImage("res/icons/1 dungeon/Pic.png");
	Image word = Toolkit.getDefaultToolkit().getImage("res/icons/1 dungeon/Word.png");
	int tileSize = 32, cflr; String loc = "null";
	Mobs player;
	Camera playerCm, cmm;
	GameData_Dungeon gdd; Stat stat; Buttons buttontex;
	int[][] inventory;//name, number
	float scale, scale2, memSizeEnlarged; //scale of the tiles in left toolbar
	int navLTBy = 0, cM = -1, seperation = 10, turn = -1, lastClock, mmy;
	String[] buttons, chs; String loc2 = "null", chos;
	boolean triggered = false, infight = false, used = false, walking = false;
	int dead = -1;
	final float faststep = 0.1f/2, normalstep = 0.1f/3;
	float step = normalstep, fighttime = 8; int fightin = 0; float lasthp = 0; long v = 0;

	//	Texture border;
	//The moving speed of the player (step), duration between fights in terms of clock cycle
	String dialoguebtncmd = ""; align dialoguebtnalign = align.horizontal; int dialoguebtn = -1;

	Engine_Dungeon(BitmapGame b, Display dis) throws UnhandledException {
		super(b, dis, Games.Dungeon);
		d = dis;
		timeMax = 500; timeT = 100;
		time = timeMax;
		cm = new Camera(CameraType.Dim2);
		navCm = new Camera(CameraType.Dim2);
		playerCm = new Camera(CameraType.Dim2);
		cmm = new Camera(CameraType.Dim2);
		title = "Pixel Dungeon ¹³¯À¦a¤U«°";
		tabs = new Texture("res/texture/tabs.png", d);
		bg = new Texture("res/texture/bg.jpg", d);
		scale = 48f/tileSize;
		scale2 = 36f/tileSize;
		sx = d.w/4-136-96;
	}

	protected void startPage(Graphics g) throws UnhandledException{
		Graphics2D g2 = (Graphics2D) g;
		if(time >= timeMax - timeT){
			d.clearScreen(0x000000);
			g.drawImage(d.img, 0, 0, d.img.getWidth(), d.img.getHeight(), d);
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
					restrict(1.0f*(timeMax-time)/timeT, 0, 1)));
			g2.drawImage(icon, d.w/2-100, d.h/2-100, 200, 200, d);
			g.drawImage(word, (int)(d.w/2-113), d.h/2-47, 226, 94, d);
		} else if(time >= timeT){
			float ratio = 1.0f*(time-timeT)/(timeMax-2*timeT); float rati;
			d.clearScreen(0x000000);
			g.drawImage(d.img, 0, 0, d.img.getWidth(), d.img.getHeight(), d);
			if(ratio > 2/3f){
				rati = (ratio-2/3f)*3;
				g.drawImage(icon, (int)(d.w/2-100*(2-rati)), d.h/2-100, 200, 200, d);
				g.drawImage(word, (int)(d.w/2-113*rati), d.h/2-47, 226, 94, d);
			} else if (ratio > 1/3f){
				rati = (ratio-1/3f)*3;
				g.drawImage(icon, d.w/2-200, d.h/2-100, 200, 200, d);
				g.drawImage(word, d.w/2, d.h/2-47, 226, 94, d);
			} else if (ratio > 0) {
				rati = (ratio)*3;
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
			if(loc == "null"){
				loc2 = "null";
				try {
					BufferedReader br = new BufferedReader(new FileReader("res/data/gameprogress.dt"));
					try{
						String ch = br.readLine();
						ch = ch.replace(",,", ",");
						if(ch.endsWith(",")) ch = ch.substring(0, ch.length()-1);
						chs = ch.split(",");
						String[] chss = new String[chs.length];
						int j = 0;
						for(int i = 0; i < chs.length; i++){
							if(chs[i].contains("-")) continue;
							chss[j] = chs[i];
							j++;
						}
						choice = new String[j];
						for(int i = 0; i < j; i++) choice[i] = chss[i];
					} catch (NullPointerException e){
					}
				} catch (FileNotFoundException e) {
					throw new UnhandledException(101, "File \"res/data/gameprogress.dt\" is not found");
				} catch (IOException e){
					throw new UnhandledException(103, "File IO read error");
				}
				super.choice = choice;
				pre = "";
				choosing(new GraphicsF(g2, d), icon, "Load Map", -2);
				time++;
			} else if (loc2 == "null"){
				try {
					BufferedReader br = new BufferedReader(new FileReader("res/data/gameprogress.dt"));
					try{
						String ch = br.readLine();
						ch = ch.replace(",,", ",");
						if(ch.endsWith(",")) ch = ch.substring(0, ch.length()-1);
						chs = ch.split(",");
					} catch (NullPointerException e){
					}
				} catch (FileNotFoundException e) {
					throw new UnhandledException(101, "File \"res/data/gameprogress.dt\" is not found");
				} catch (IOException e){
					throw new UnhandledException(103, "File IO read error");
				}
				choice = new String[0];
				choice = Arrays.append(choice, "New Game");
				for(int i = 0; i < chs.length; i++){
					if((chs[i].split("-")[0].contentEquals(chos))&(chs[i].split("-").length == 2)){
						choice = Arrays.append(choice, chs[i].split("-")[1]);
					}
				}
				pre = loc+"-";
				choosing(new GraphicsF(g2, d), icon, "Load Progress", 0);
				time++;
			}
		}//*/
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
		g2.drawImage(logo, d.w-158, d.h-50, 158, 50, d);
	}

	protected void sInit() throws UnhandledException{
		dead = -1;
		buttontex = new Buttons("res/textureData/buttons.tx", d);
		border = new Texture("res/texture/border.png", d);
		if(loc == "new"){
			z = new Zip("res/maps/0maps/temp.map");
			z.createZipFile("header.h", "floor.lr", "lower.lr", "over.lr", "item.lr");
		} else if (loc2 == ""){
			z = new Zip("res/maps/0maps/"+loc+".map");
		} else {
			z = new Zip("res/maps/0maps/"+loc+"-"+loc2+".map");
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

		IOHeader ioh = new IOHeader();
		ioh.loadIOHeader(z,"header.h");
		texloc = ioh.texloc;
		brightness = ioh.brightness;

		tileSize = 32;
		tiles = new Tiles(getTextureDataURL("res/textureData/"+texloc, "tile"), d);
		items = new Items(getTextureDataURL("res/textureData/"+texloc, "item"), d);
		spobj = new SpecialObjects(getTextureDataURL("res/textureData/"+texloc, "sobject"), d);
		player = new Mobs(getTextureDataURL("res/textureData/"+texloc, "player"), d);
		mobs = new Mobs(getTextureDataURL("res/textureData/"+texloc, "mob"), d);

		mLight = new Map(MapType.Flat, ioh.W, ioh.H, ioh.T, "int");
		mLight.init(brightness);
		m 		= loadMap("floor.lr", "int");
		mOver 	= loadMap("over.lr", "int");
		mItem 	= loadMap("item.lr", "int");
		mLowe 	= loadMap("lower.lr", "int");
		mStat	= loadMap("stat.dt", "String");

		navCm.init(0, 0);
		String[] btns = {"Load Game", "Save Game", "Help", "Back"};
		buttons = btns;
		playerCm.init(gdd.x0, gdd.y0);
		playerCm.stat = gdd.dir;
		flr = gdd.z0;
		stat = gdd.stat;
		inventory = gdd.inventory;
		sizeEnlarged = 1.6f; //Default = 1.6f
		memSizeEnlarged = -1;
		float ttS = 544f/tileSize/sizeEnlarged;
		if(m.w > ttS){navCm.xf = playerCm.xf-ttS/2;}
		if(m.h > ttS){navCm.yf = playerCm.yf-ttS/2;}
		if(navCm.xf<0){navCm.xf=0;}
		if(navCm.yf<0){navCm.yf=0;}
		if(navCm.xf+ttS>m.w)
		{navCm.xf=m.w-ttS;}
		if(navCm.yf+ttS>m.h)
		{navCm.yf=m.h-ttS;}
		d.resetClearRegion();
		cflr = flr;
	}

	protected void sRun(Graphics g) throws UnhandledException{
		//	BUG.debug(commandStack);
		if(dead == 0){try{
			Thread.sleep(8000);
			Audio.stopAll();
			btg.engine = new Engine(btg, d, 2);
		}catch(Exception e){}}
		else if (dead != -1) dead--;

		setPrintingDetails(tileSize, tileSize, 10);

		//Clear Screen
		if((memSizeEnlarged != sizeEnlarged)|(cflr != flr)){
			if((m.w*tileSize*sizeEnlarged>544)|(m.h*tileSize*sizeEnlarged>544)){
				d.setDefaultProtectedRegion(d.w/2-272, d.h/2-272+ddh, d.w/2+272, d.h/2+272+ddh);
				d.defaultProtectedRegion();
			} else {
				float tS = tileSize*sizeEnlarged/2;//semi tile size with enlarge
				d.setDefaultProtectedRegion((int)(d.w/2-m.w*tS), (int)(d.h/2-m.h*tS+ddh),
						(int)(d.w/2+m.w*tS), (int)(d.h/2+m.h*tS+ddh));
				d.defaultProtectedRegion();
			}
			d.resetClearRegion();
			d.clearScreen(bg.p, bg.w, bg.h, true);
			d2.clearScreen(-2);
			d.img.getGraphics().drawImage(logo, d.w-158, d.h-50, 158, 50, d);
			memSizeEnlarged = sizeEnlarged;
			cflr = flr;
		}
		d.setClearRegion(d.w/2-280, 0, d.w/2+280, d.h/2-272);
		d.clearScreen(bg.p, bg.w, bg.h, true);

		//Main Screen
		extraObj = Arrays.append(extraObj, new Object[]{0, player, playerCm.xf, playerCm.yf, playerCm.stat, navCm, (fightin == 0)?-1:fightin});
		printLight2D(tiles, items, spobj, mobs, navCm);
		printScreen2D(tiles, items, spobj, mobs, navCm);
		printHP(mobs, navCm);
		Mob pp = (Mob)(player.getItem(0));
		printHPBars(bar, playerCm.xf-navCm.xf, playerCm.yf-navCm.yf, pp.xoff, pp.yoff-10, pp.wid,
				pp.hei, stat.getValueOf("HP"), lasthp, 100);
		printDispWords(d.img.getGraphics());
		//if(v > 0) v--;
		//else Audio.playlist2("none", "player");
		walking = false;
		move();
		if(!walking) Audio.playlist2("none", "player");
		if(clock %8 == 0) lasthp = stat.getValueOf("HP");
		executeCommandStack();

		//Left Tool Bar
		leftToolBar();

		//Right Button Bar
		rightButtonBar(buttontex, buttons, seperation, scale);

		//Others
		disp.clearScreen(0);
		disp.combine(d);
		disp.combine(d2);
		g.drawImage(disp.img, 0, 0, d.img.getWidth(), d.img.getHeight(), disp);

		//Button Reaction
		if (cM != -1){//Choose Command Region (e.g. save map)
			int option;
			switch(buttons[cM]){
				case "Load Game": //choose and load map again
					String[] content0 = {"If you continue, the current progress " +
							"from your last save of the game progress will be discarded.",
							"You are advised to save your game progress now if you have not.",
							"Are you sure that you want to proceed?"};
					option = JOptionPane.showConfirmDialog(null, content0,
							"Load Another Map and Discard Changes", JOptionPane.YES_NO_OPTION);
					if(option == JOptionPane.YES_OPTION){
						Audio.stopAll();
						loc = "null"; time = timeT;
					}
					break;
				case "Save Game": //save map
					JTextField Name = new JTextField(5);
					JRadioButton rb = new JRadioButton("Default: "+loc2);
					if(loc2 == ""){
						rb.setEnabled(false);
						rb.setText("Default: None Applicatable");
					}
					Object[] content1 = {"Current Map: "+loc, "Name of the game progress to be saved:", Name, rb};
					option = JOptionPane.showConfirmDialog(null, content1,
							"Save Game Progress", JOptionPane.OK_CANCEL_OPTION);
					if(option == JOptionPane.OK_OPTION){
						if(rb.isSelected()){
							Name.setText(loc2);
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
						saveGame(loc+"-"+Name.getText());
					}
					break;
				case "Help":
					help();
					break;
				case "Back":
					String[] content2 = {"If you continue, the current progress " +
							"from your last save of the map will be discarded.",
							"You are advised to save your map now if you have not.",
							"Are you sure that you want to proceed?"};
					option = JOptionPane.showConfirmDialog(null, content2,
							"Exit to Main Screen and Discard Changes", JOptionPane.YES_NO_OPTION);
					if(option == JOptionPane.YES_OPTION){
						Audio.stopAll();
						btg.engine = new Engine(btg, d, 2);
					}
					break;
			}
			cM = -1;
			mouseDown = false;
		}
	}

	protected void saveGame(String filename) throws UnhandledException{
		gdd.x0 = playerCm.x;
		gdd.y0 = playerCm.y;
		gdd.z0 = flr;
		gdd.dir = playerCm.stat;
		gdd.stat = stat;
		gdd.inventory = inventory;
		String URL = "res/maps/0maps/"+filename+".map";
		z = new Zip(URL);
		if(!new File(URL).exists()){
			try {
				appendFile("res/data/gameprogress.dt", filename);
			} catch (IOException e) {throw new UnhandledException(104, "File Output " +
					"Exception at void sRun()");}
			z.createZipFile("header.h", "floor.lr", "lower.lr", "over.lr", "item.lr");
		}
		z.prepareFile();
		z.writeFile("header.h", new IOHeader(mt, m.w, m.h, mapHeight, enc, texloc, brightness).getIOHeader());
		saveMap("floor.lr", m);
		saveMap("over.lr", mOver);
		saveMap("item.lr", mItem);
		saveMap("lower.lr", mLowe);
		saveMap("stat.dt", mStat);
		z.writeFile("gamedata.gm", gdd.getContents());
		z.writeFile();
		chosen = 0; nchosen = 0;
		JOptionPane.showMessageDialog(null, "Game Progress Saved Successfully!");
	}

	protected void help() throws UnhandledException{
		info = true;
		Object[] obj = {};
		String rooturl = "res/info/1 dungeon/";
		String url = "d?.png";
		for(int i = 1; i <= 6; i++){
			Object[] page = {new ImageIcon(rooturl + url.replace("?", i+""))};
			obj = Arrays.append(obj, page);
		}
		Engine_Info.showMessageDialog("Help", obj, false);
		info = false;
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
				case "none":
					return 1;
				case "haved":
					int cmdr = 0; //command result
					for(int i = 0; i < inventory[0].length; i++){
						int index = items.getItemInd(args[0]);
						if(inventory[0][i] == index) {
							inventory[1][i]--;
							if(inventory[1][i] <= 0){
								inventory = Arrays.deleteItem(inventory, i);
								notif("You used "+items.names[index], 1000);
							}
							cmdr = 1;
							break;
						}
					}
					if(cmdr == 0) notif("You need the item ["+args[0].replace('+',' ')+"].",1000);
					return cmdr;
				case "have":
					cmdr = 0; //command result
					for(int i = 0; i < inventory[0].length; i++){
						if(inventory[0][i] == items.getItemInd(args[0])) {cmdr = 1; break;}
					}
					return cmdr;
				case "delete":
					int xx = Integer.parseInt(args[0]), yy = Integer.parseInt(args[1]);
					if(args[0].startsWith("+") || args[0].startsWith("-"))
						xx += cm.x;
					if(args[1].startsWith("+") || args[1].startsWith("-"))
						yy += cm.y;
					if(getTile(xx, yy, mItem) != -1){
						fightin = 0;
						infight = false;
					}
					setTile(xx, yy, -1, mItem);
					setTile(xx, yy, -1, mOver);
					setTile(xx, yy, "", mStat);
					return 1;
				case "tpup":
					if(flr < m.t - 1) flr++;
					break;
				case "tpdown":
					if(flr > 0) flr--;
					break;
				case "animate":
					String content = getParameter(getTileStr(cm.x, cm.y, mStat), "<animate>");
					mStat = setTile(cm.x, cm.y, addTagTo(content, 1+"", "<animate>"), mStat);
					return - (cm.x + cm.y*m.w);
				case "animate?":
					try{
						content = getParameter(getTileStr(cm.x, cm.y, mStat), "<animate>");
					} catch(Exception e){return 2;}
					String vall = getContent(getTileStr(cm.x, cm.y, mStat), "<animate>");
					if(vall == "") vall = 0+"";
					int val = Integer.parseInt(vall);
					int XX = Integer.parseInt(args[0]), YY = Integer.parseInt(args[1]);
					if(val*spobjaspeed <= ((SpecialObject)getTileItem(XX, YY, mOver)).numFrameTriggered){
						mStat = setTile(XX, YY, addTagTo(content, (val+1)+"", "<animate>"), mStat);
						return 0;
					}
					else {
						mStat = setTile(XX, YY, addTagTo(content, (val+1)+"", "<animate>"), mStat);
						return 1;
					}
				case "pushable":
					int dx = 0, dy = 0;
					switch(playerCm.stat){
						case 0: dy = -1; break;
						case 1: dx = +1; break;
						case 2: dy = +1; break;
						case 3: dx = -1; break;
					}
					if(checkLocPassable(cm.x + dx, cm.y + dy)){
						return 1;
					}
					return 0;
				case "pushed":
					if(!getContent(getTileStr(cm.x, cm.y, mStat),"<push>").equals("")) return 2;
					content = getParameter(getParameter(getTileStr(cm.x, cm.y, mStat), "<push>"), "<pushdir>");
					mStat = setTile(cm.x, cm.y,
							addTagTo(addTagTo(content, 1+"", "<push>"), playerCm.stat+"", "<pushdir>"), mStat);
					return - (cm.x + cm.y*m.w);
				case "pushed?":
					String statdat = getTileStr(Integer.parseInt(args[0]), Integer.parseInt(args[1]), mStat);
					content = getParameter(statdat, "<push>");
					vall = getContent(statdat, "<push>");
					if(vall == "") vall = 0+"";
					val = Integer.parseInt(vall);
					XX = Integer.parseInt(args[0]); YY = Integer.parseInt(args[1]);
					if(!haveTag(content, "<passable>")) content = addTagTo(content, "TRUE", "<passable>");
					spobjbspeed = step;
					if(val*spobjbspeed <= 1){
						if((playerCm.xf == playerCm.x)&(playerCm.yf == playerCm.y))
							move(Integer.parseInt(getContent(statdat, "<pushdir>")));
						mStat = setTile(XX, YY, addTagTo(content, (val+1)+"", "<push>"), mStat);
						return 0;
					}
					else {
						dx = 0; dy = 0;
						switch(Integer.parseInt(getContent(statdat, "<pushdir>"))){
							case 0: dy = -1; break;
							case 1: dx = +1; break;
							case 2: dy = +1; break;
							case 3: dx = -1; break;
						}
						mOver = setTile(XX+dx, YY+dy, getTile(XX, YY, mOver), mOver);
						mItem = setTile(XX+dx, YY+dy, getTile(XX, YY, mItem), mItem);
					}
					break;
				case "duplicate"://duplicate(int xorig, int yorig, int xdup, int ydup), dup: coordinate for the copy
					XX = Integer.parseInt(args[0]); YY = Integer.parseInt(args[1]);
					int XX2 = Integer.parseInt(args[2]), YY2 = Integer.parseInt(args[3]);
					if(args[2].startsWith("+") || args[2].startsWith("-"))
						XX2 += XX;
					if(args[3].startsWith("+") || args[3].startsWith("-"))
						YY2 += YY;
					mOver = setTile(XX2, YY2, getTile(XX, YY, mOver), mOver);
					mItem = setTile(XX2, YY2, getTile(XX, YY, mItem), mItem);
					break;
				case "fight":
					fightin++;
					infight = true;
					changeMobStat("fight", 1, cm.x, cm.y);
					return - (cm.x + cm.y*m.w);
				case "fight?":
					//dispwords.addRecords("-1", 200, "x-1;a-1", d.w/2, d.h/2+ddh, 100, 32, 0x0000FF, 1);
					if(!infight) {
						fightin = 0;
						setTile(cm.x, cm.y, getParameter(getTileStr(cm.x, cm.y, mStat), "<fight>"), mStat);
						return 2;
					}
					if(clock % fighttime != 0) return 0;
					int mobx = Integer.parseInt(args[0]), moby = Integer.parseInt(args[1]);
					if(!haveTag(getTileStr(mobx, moby, mStat), "fight")) return 2;
					float selfhp = stat.getValueOf("HP");
					float enemyhp = getMobStat("hp", mobx, moby);
					changeMobStat("lasthp", enemyhp, mobx, moby);
					float selfded, selfmded, enemyded, enemymded;
					selfded = getMobStat("atk", mobx, moby) - stat.getValueOf("Dfs");
					enemyded = stat.getValueOf("Atk") - getMobStat("dfs", mobx, moby);
					selfmded = getMobStat("matk", mobx, moby) - stat.getValueOf("M+Dfs");
					enemymded = stat.getValueOf("M+Atk") - getMobStat("mdfs", mobx, moby);
					selfhp -= Math.max(0, selfded) + Math.max(0, selfmded);
					enemyhp -= Math.max(0, enemyded) + Math.max(0, enemymded);
					Mob mbb = (Mob)(player.getItem(0));
					Mob Mbb = (Mob)(mobs.getItem(getTile(mobx, moby, mItem)/2));
					int[] scors = mapToScreen(playerCm.xf-navCm.xf, playerCm.yf-navCm.yf,
							mbb.xoff, mbb.yoff-20, mbb.wid, mbb.hei, false, 1);
					int[] scorm = mapToScreen(mobx-navCm.xf, moby-navCm.yf,
							Mbb.xoff, Mbb.yoff-10, Mbb.wid, Mbb.hei, false, 1);
					if(Math.max(0, selfded)+Math.max(0, selfmded) != 0)
						dispwords.addRecords(-(Math.max(0, selfded)+Math.max(0, selfmded))+"",
								200, "y-1;a-4", scors[0], scors[1], 100, 24, 0x0000FF, 1);
					if(Math.max(0, enemyded)+Math.max(0, enemymded) != 0)
						dispwords.addRecords(-(Math.max(0, enemyded)+Math.max(0, enemymded))+"",
								200, "y-1;a-4", scorm[0], scorm[1], 100, 24, 0xAA0000, 1);
					stat.setValueOf("HP", selfhp);
					if(selfhp < 0) {
						notif("You're Dead", 4000);
						if(dead == -1) dead = 1;
					} else if (selfhp < 30) {
						if(selfded <= 0 && selfmded <= 0)
							notif("Your HP is Low. You may want to heal yourself.", 1000);
						else notif("Your HP is Low. You may want to run away.", 1000);
					}
					if(enemyded <= 0 && enemymded <= 0)
						notif("Your attacks are too weak for "+getMobItem(mobx, moby, mItem).name+". Strengthen yourself first.", 1000);
					if(enemyhp > 0){
						changeMobStat("hp", enemyhp, mobx, moby);
						fightin++;
						infight = true;
						return 0;
					} else {
						cm.init(mobx, moby);
					}
					break;
				case "pindist":
					break;
				case "exec"://exec(String command)
					String cmmd = args[0];
					String[] itash = cmmd.split(">");
					float ans = stat.getValueOf(itash[0]);//answer
					for(int i = 1; i < itash.length; i++){
						float oper = Float.parseFloat(itash[i].substring(1)); //operand
						switch(itash[i].charAt(0)){
							case '+': ans += oper; break;
							case '-': ans -= oper; break;
							case '*': ans *= oper; break;
							case '/': ans /= oper; break;
						}
					}
					if(itash[0].contentEquals("hp")){
						String wd = "+"+(ans-stat.getValueOf(itash[0]));
						wd.replace("+-", "-");
						mbb = (Mob)(player.getItem(0));
						scors = mapToScreen(playerCm.xf-navCm.xf, playerCm.yf-navCm.yf,
								mbb.xoff, mbb.yoff-20, mbb.wid, mbb.hei, false, 1);
						dispwords.addRecords(wd, 200, "y-1;a-4", scors[0], scors[1], 100, 24, 0x008000, 1);
					}
					stat.setValueOf(itash[0], ans);
					break;
				case "print"://print(String content)
					dispwords.addRecords(args[0], 200, "y-1;a-4", d.w/2, d.h/2+ddh, 100, 32, 0xFFC90D, 1);
					break;
				case "say"://say(String content, Icon icon)
					dialoguebtn = -1;
					if(args.length > 0)
						setTile(cm.x, cm.y,
							addTagTo(getParameter(getTileStr(cm.x, cm.y, mStat),"<say>"),args[0],"<say>")
							, mStat);
					if(args.length > 1)
						setTile(cm.x, cm.y,
								addTagTo(getParameter(getTileStr(cm.x, cm.y, mStat),"<icon>"),args[1],"<icon>")
								, mStat);
					BUG.debug(getTileStr(cm.x, cm.y, mStat));
					return - (cm.x + cm.y*m.w);
				case "say?":
					int tehx = Integer.parseInt(args[0]);
					int tehy = Integer.parseInt(args[1]);
					String data = getTileStr(tehx, tehy, mStat);
					dialoguebtncmd = getContent(data, "<buttons>");
					dialoguebtnalign = getContent(data, "<btnalign>").equals("Vertical")?align.vertical:align.horizontal;
					if(dialoguebtncmd.equals("")){
						int ind = -1; type tp = null;
						if(args.length > 2){
							if(mobs.hasItem(args[2]))	   {ind = mobs.getItemInd(args[2]); tp = type.mob;}
							else if(items.hasItem(args[2])){ind = items.getItemInd(args[2]); tp = type.item;}
							else						   {ind = spobj.getItemInd(args[2]); tp = type.spobj;}
						} else if (!getContent(data, "<icon>").equals("")) {
							String itm = getContent(data, "<icon>");
							if(mobs.hasItem(itm))	   {ind = mobs.getItemInd(itm);  tp = type.mob;}
							else if(items.hasItem(itm)){ind = items.getItemInd(itm); tp = type.item;}
							else					   {ind = spobj.getItemInd(itm); tp = type.spobj;}
						} else {
							int overt = getTile(tehx, tehy, mOver);
							if((overt %2 == 1)&(overt != -1)){
								ind = overt/2; tp = type.spobj;
							} else {
								overt = getTile(tehx, tehy, mItem);
								if((overt %2 == 1)&(overt != -1)){
									ind = overt/2; tp = type.mob;
								}
							}
						}
						if(tp != null) {
							diag(getContent(data, "<say>"), ind, tp, 4000);
						} return 1;}
					if(dialoguebtn != -1){
						String[] sd = dialoguebtncmd.split(";");
						executeCommands(sd[dialoguebtn].split("]:")[1]);
						paused = false; dialogue = false;
						return 1;
					}
					dispDialogue(border, getContent(data, "<say>").replace(" ", "+").replace("\n", "+/+"), tehx,
							tehy, 400, 200, dialoguebtncmd, dialoguebtnalign, d.img.getGraphics());
					return 0;
				case "close":
					return 1;
				case "give":
					addInventory(args[0]);
					notif("You received "+args[0], 1000);
					return 1;
				case "nomob":
					String[] list = new String[0];
					if(haveTag(getTileStr(cm.x, cm.y, mStat),"<"+args[0]+">"))
						list = Arrays.append(list,getContent(getTileStr(cm.x, cm.y, mStat),"<"+args[0]+">").split("::"));
					for(int i = 0; i < list.length; i++){
						String[] args2 = list[i].replace("(","").replace(")","").split(",");
						int ind =  mItem.getTile(Integer.parseInt(args2[1]),Integer.parseInt(args2[2]),Integer.parseInt(args2[0]));
						if((ind != -1)&(ind%2 == 1))if(mobs.getItem(ind/2).name.equals(args2[3])) return 0;
					}
					return 1;
				case "loot":
					String[] loots = getContent(getTileStr(cm.x, cm.y, mStat),"<loot>").split(";");
					for(String loot : loots) {
						if (loot.equals("")) continue;
						addInventory(loot);
						notif("You picked up "+loot, 1000);
					}
					return 1;
				case "setblock":
					XX = Integer.parseInt(args[0]); YY = Integer.parseInt(args[1]);
					if(args[0].startsWith("+") || args[0].startsWith("-"))
						XX += cm.x;
					if(args[1].startsWith("+") || args[1].startsWith("-"))
						YY += cm.y;
					String n = args[2];
					setTile(XX, YY, "", mStat);
					if(tiles.hasItem(n)) editTile(XX, YY, tiles.getItemInd(n),0);
					else if(spobj.hasItem(n)) editTile(XX, YY, spobj.getItemInd(n), 2);
					else if(items.hasItem(n)) editTile(XX, YY, items.getItemInd(n), 1);
					else if(mobs.hasItem(n)) editTile(XX, YY, mobs.getItemInd(n), 3);
					return 1;
				case "action":
					executeCommands(getContent(getTileStr(cm.x, cm.y, mStat).
							replace(",","::"),"<action>"));
					return 1;
			}
			return 1;
		} catch(Exception e){e.printStackTrace();}
		return 0;
	}

	protected void changeMobStat(String stat, float newValue, int mobx, int moby) throws UnhandledException{
		String tag = "<"+stat+">";
		setTile(mobx, moby, addTagTo(getParameter(getTileStr(mobx, moby, mStat), tag), newValue, tag), mStat);
	}

	protected void trigger() throws UnhandledException{
		if(getTileItem(playerCm.x, playerCm.y, mOver).passable){
			int indexTile = getTile(playerCm.x, playerCm.y, mOver);
			if(indexTile != -1){
				switch(indexTile%2){
					case 0:
						break;
					case 1:
						cm.init(playerCm.x, playerCm.y);
						String cont = getTileStr(cm.x, cm.y, mStat);

						setTile(cm.x, cm.y, addTagTo(getParameter(cont, "<triggered>"),"T", "<triggered>"), mStat);
						if(!executeCommands(((SpecialObject)getTileItem(cm.x, cm.y, mOver)).actionInteract)){
							setTile(cm.x, cm.y, getParameter(cont, "<triggered>"), mStat);
						}
						break;
				}
			}
		}
	}

	protected void selected(){
		if(loc == "null"){
			chos = choice[chosen];
			loc = chos.split("-")[0];
		} else if (loc2 == "null"){
			if(choice[chosen] == "New Game") loc2 = "";
			else loc2 = choice[chosen];
		}
		nchosen = 0;
		chosen = 0;
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
		} else if (time < 0){
			if((Math.abs(playerCm.xf-playerCm.x)<0.01f)&(Math.abs(playerCm.yf-playerCm.y)<0.01f)){
				if(turn == -1){
					try{Thread.sleep(10);}catch(Exception e){}
					switch(k.getKeyCode()){
						case 38: case 87: move(0); break;
						case 39: case 68: move(1); break;
						case 40: case 83: move(2); break;
						case 37: case 65: move(3); break;
						case KeyEvent.VK_SPACE: {
							if(dialogue) {
								String btns = dialoguebtncmd;
								String[] sd = btns.split(";");
								int ind = -1;
								if(sd.length == 1) ind = 0;
								dialoguebtn = ind;
							}
							else trigger();
						} break;
						case KeyEvent.VK_I: selectlooking = 1; break;
						case KeyEvent.VK_T: selectlooking = 0; break;
						default:
							if(dialogue){
								String btns = dialoguebtncmd;
								String[] sd = btns.split(";");
								int ind = -1;
								if(Character.isDigit(k.getKeyChar()))
									ind = Integer.parseInt(k.getKeyChar()+"");
								if(!inRange(ind, 0, sd.length-1)) ind = -1;
								dialoguebtn = ind;
							}
					}
					/* int doffs = 15;
							String btns = dialoguebtncmd;
							String[] sd = btns.split(";");
							int ind = -1, wid = enddialogue.x-startdialogue.x, hei = enddialogue.y-startdialogue.y, px = 8;
							d.fontstr = fontstr; d.fonteng = fonteng;
							switch(dialoguebtnalign){
								case horizontal:
									ind = (int) (Math.round(1f*(mouseX-startdialogue.x)*(sd.length+1)/wid)-1);
									if(!inRange(ind, 0, sd.length-1)){
										ind = -1;
									} else if(!inDist(mouseY, startdialogue.y + hei - doffs - 32/2, 32/2)){
										ind = -1;
									} else if(!inDist(mouseX, startdialogue.x + 1f*wid/(sd.length+1)*(ind+1),
											Math.max(d.getSizeOfString(sd[ind].split("\\[")[1].split("\\]:")[0], 0, 20,
													d.img.getGraphics())[0]+2*px, 48)/2)){
										ind = -1;
									}
									break;
								case vertical:
									ind = (int) (Math.round(1f*(mouseY-startdialogue.y)*(sd.length+1)/hei)-1);
									if(!inRange(ind, 0, sd.length-1)){
										ind = -1;
									} else if (!inDist(mouseY, startdialogue.y + 1f*hei/(sd.length+1)*(ind+1), 32/2)){
										ind = -1;
									} else if(!inRange(mouseX, startdialogue.x + wid -
											Math.max(d.getSizeOfString(sd[ind].split("\\[")[1].split("\\]:")[0], 0, 20,
													d.img.getGraphics())[0]+2*px, 48) - offs, startdialogue.x + wid - offs)){
										ind = -1;
									}
									break;
							}
							dialoguebtn = ind;
						}
					* */
				}
			}
		}
	}

	public void processMouseEventL(MouseEvent me, int ms) throws UnhandledException{/**ms: mouse state*/
		int offs = 5; float siz = 64; //Universal variable, have to be same as LTB desc.
		if(time < 0){
			switch(ms){
				case 0://Clicked
				case 1://Pressed
					mmy = mouseY;
					if (inSelTileRegion(mouseX, mouseY)){
						cmm.init((float)Math.floor((me.getX()-sx)/(w*scale)),
								(float)Math.floor((me.getY()-(sty+ddh-navLTBy))/(h*scale)));
					} else {
						if(!inTileDescRegion(mouseX, mouseY)) cmm.init();
						if (inLTBTabRegion(mouseX, mouseY)&((mouseX-sx)/48 < 2)){
							selectlooking = (mouseX-sx)/48; navLTBy = 0;
						} else if(inSelButtonRegion(mouseX, mouseY)){
							if((mouseY-(sry+ddh+seperation)+32+seperation)%(32+seperation)<=32){
								cM = (int)Math.floor((mouseY-(sry+ddh+seperation))/(32+seperation));
							} else cM = -1;
						} else if ((dialogue)&inRect(mouseX, mouseY, startdialogue.x, startdialogue.y, enddialogue.x,
								enddialogue.y)){
							int doffs = 15;
							String btns = dialoguebtncmd;
							String[] sd = btns.split(";");
							int ind = -1, wid = enddialogue.x-startdialogue.x, hei = enddialogue.y-startdialogue.y, px = 8;
							d.fontstr = fontstr; d.fonteng = fonteng;
							switch(dialoguebtnalign){
								case horizontal:
									ind = (int) (Math.round(1f*(mouseX-startdialogue.x)*(sd.length+1)/wid)-1);
									if(!inRange(ind, 0, sd.length-1)){
										ind = -1;
									} else if(!inDist(mouseY, startdialogue.y + hei - doffs - 32/2, 32/2)){
										ind = -1;
									} else if(!inDist(mouseX, startdialogue.x + 1f*wid/(sd.length+1)*(ind+1),
											Math.max(d.getSizeOfString(sd[ind].split("\\[")[1].split("\\]:")[0], 0, 20,
													d.img.getGraphics())[0]+2*px, 48)/2)){
										ind = -1;
									}
									break;
								case vertical:
									ind = (int) (Math.round(1f*(mouseY-startdialogue.y)*(sd.length+1)/hei)-1);
									if(!inRange(ind, 0, sd.length-1)){
										ind = -1;
									} else if (!inDist(mouseY, startdialogue.y + 1f*hei/(sd.length+1)*(ind+1), 32/2)){
										ind = -1;
									} else if(!inRange(mouseX, startdialogue.x + wid -
											Math.max(d.getSizeOfString(sd[ind].split("\\[")[1].split("\\]:")[0], 0, 20,
													d.img.getGraphics())[0]+2*px, 48) - offs, startdialogue.x + wid - offs)){
										ind = -1;
									}
									break;
							}
							dialoguebtn = ind;
						}
					} break;
				case 2://Released
					mmy = -1;
					if(inRange(mouseX, sx+offs+24-16, sx+offs+24+16)&inRange(mouseY, sdy+offs+ddh+siz, sdy+offs+ddh+siz+48)){
						used = true;
						/* d.placeSprite(tabs, sx + offs + 24 - 16,
						sdy + offs + ddh + siz, 1, 48, 32, 0, 6, 0);
						 * */
					}
					break;
				case 3://Dragged
					if (inSelTileRegion(mouseX, mouseY)){
						int ddy = mmy - mouseY;
						navLTBy += ddy;
						if (navLTBy > (inventory.length/4+1)*48+sty-sdy){navLTBy = (inventory.length/4+1)*48+sty-sdy;}
						if (navLTBy < 0){navLTBy = 0;}
					}
					mmy = mouseY;
					break;
			}
		}
	}

	protected void leftToolBar() throws UnhandledException{
		d.setClearRegion(0, d.h/2-290, d.w/2-280, d.h/2+290);
		d.clearScreen(bg.p, bg.w, bg.h, true);
		for(int i = 0; i < 4; i++){//Tabs in Left Tool Bar (T:LTB)
			if(selectlooking == i){
				d.placeSprite(tabs, sx+i*48, sty-31+ddh, 1, 48, 32, 0, i + 4, 0);
			} else {
				d.placeSprite(tabs, sx+i*48, sty-31+ddh, 1, 48, 32, 1, i + 4, 0);
			}
		}//*/
		int ii = 0;
		switch(selectlooking){
			case 0:
				drawEnclosedRegion(sx, sty + ddh, sx+48*4, d.h/2+272 + ddh, 48, 48);
				break;
			case 1:
				d.setProtectedRegion(sx, sty + ddh, sx+48*4, sdy + ddh);
				while(ii < (inventory[0]).length){//Null Pointer Exception will occur if case 2, 3 is not done
					if(h * scale * (ii / 4) + sty - navLTBy > sdy){
						break;
					} else if (h * scale * (ii / 4) - navLTBy + 48 >= 0){
						int cc = 0;
						if(ii == cmm.x + cmm.y*4){cc = 1;}
						d.placeSprite(
								tabs, sx + w * scale * (ii % 4),
								h * scale * (ii / 4) + sty - navLTBy + ddh, 1,
								48, 48, 4/3f, cc, -1, true
						);
						Sprite til = items.getItem(inventory[0][ii]);
						d.setMergeCol(items.blend, til.indCol);
						d.placeSprite(
								items.tex, sx + w * (scale * (ii % 4) + (scale - scale2)/2),
								h * (scale * (ii / 4) + (scale - scale2)/2) + sty - navLTBy + ddh,
								scale2, w, h, til.y, til.x, 0, true
						);
					}
					ii++;
				}
				while (h * scale * (ii / 4) + sty - navLTBy < sdy){
					d.placeSprite(
							tabs, sx + w * scale * (ii % 4), h * scale * (ii / 4) + sty - navLTBy + ddh,
							1, 48, 48, 4/3f, 0, -1, true
					);
					ii++;
				} while (h * scale * (ii / 4) + sty - navLTBy < sdy){
				d.placeSprite(
						tabs, sx + w * scale * (ii % 4), h * scale * (ii / 4) + sty - navLTBy + ddh, 1,
						48, 48, 4/3f, 2, -1, true
				);
				ii++;
			}
				drawEnclosedRegion(sx, sdy + ddh, sx+48*4, d.h/2+272 + ddh, 48, 48);
		}
		d.defaultProtectedRegion();//*/

		//Description region
		int offs = 5; float siz = 64;
		if(selectlooking == 1){
			int pos = cmm.x+cmm.y*4;
			if(pos >= inventory[0].length){
				cmm.init();
			}
			d.placeSprite(
					tabs, sx + offs,
					sdy + offs + ddh,
					siz/48, 48, 48, 4/3f, 0, 0, false
			);
			if((cmm.x != -1)&(pos < inventory[0].length)){
				Sprite til = items.getItem(inventory[0][pos]);
				d.setMergeCol(items.blend, til.indCol);
				//Image of selected item enlarged
				d.placeSprite(
						items.tex, sx + offs + siz/6f,
						sdy + offs + siz/6f + ddh,
						siz*32/48/tileSize, tileSize, tileSize, til.y, til.x+til.offset*(clock/clockset %16)/16, 0, false
				);
				//USE button
				String com = ((Item)items.getItem(inventory[0][pos])).cmdOnUse;
				int u = 0;
				if((!com.contentEquals("none"))){
					if(used){
						u = 1;
						if(executeCommand(com) > 0){
							inventory[1][pos] -= 1;
							if(inventory[1][pos] == 0){
								inventory = Arrays.deleteItem(inventory, pos);
							}
						}
						used = false;
					}
					d.placeSprite(tabs, sx + offs + 24 - 16,
							sdy + offs + ddh + siz, 1, 48, 32, 0, 6+u, 0);
				}
			}
		}//*/
		Graphics gg = d.img.getGraphics();
		gg.setColor(Color.WHITE);
		drawString(loc, d.w/4-136, ddh+70, 1, 28, 1, 0, gg);
		if (mt == MapType.Leveled){
			drawString("Floor "+flr, d.w/4-136, ddh+110, 1, 24, 1, 0, gg);
		}
		gg.setColor(Color.WHITE);
		if (selectlooking == 0){
			for(int i = 0; i < stat.keys.length; i++){
				drawString(stat.names[i], sx + offs, sty + offs + 24 + i*48, 1, 20, 0, 2, gg);
				drawString((int)stat.values[i]+"", sx + 4*48 - offs, sty + offs + 24 + i*48, 1, 20, 2, 2, gg);
			}
		} else if (selectlooking == 1){
			ii = 0;
			while(ii < (inventory[0]).length){//Null Pointer Exception will occur if case 2, 3 is not done
				if(h * scale * (ii / 4) + sty - navLTBy > sdy){
					break;
				} else if (h * scale * (ii / 4) - navLTBy + 48 >= 0){
					gg.setColor(Color.WHITE);
					if(inventory[1][ii] > 1){
						drawString(inventory[1][ii]+"", (int)(sx + w * scale * (ii % 4 + 1) - 10),
								(int)(h * scale * (ii / 4 + 1) + sty - navLTBy + ddh - 5), 1, 16, 1, 0, gg);
					}
				}
				ii++;
			}
			if((cmm.x != -1)&(cmm.x+cmm.y*4 < inventory[0].length)){
				Sprite til = items.getItem(inventory[0][cmm.x+cmm.y*4]);
				drawPassage(til.name, (int) (sx + offs + siz/6f + siz),
						(int) (sdy + siz/8f + ddh), sx + 48*4, d.w/2+272 + ddh, 0, 20, gg);
				drawPassage(til.description, (int) (sx + offs + siz/6f),
						(int) (sdy + siz + siz/8f + 32 + ddh), sx + 48*4, d.w/2+272 + ddh, 0, 18, gg);
			}
		}//*/
	}

	protected void mouseMove(float x, float y) throws UnhandledException{
		if(inMapRegion(x, y)&mouseDown&
				!((Math.abs(playerCm.xf-playerCm.x)>0.01f)|(Math.abs(playerCm.yf-playerCm.y)>0.01f))){
			float distx = 0, disty = 0, sE = tileSize*sizeEnlarged;
			float xr = playerCm.xf - navCm.xf, yr = playerCm.yf - navCm.yf;
			if((w*m.w*sizeEnlarged > 544)|(h*m.h*sizeEnlarged > 544)){//32 * 17
				distx = x-(d.w/2f-272+(w*xr)*sizeEnlarged)-sE/2;
				disty = y-(d.h/2f-272+(h*yr)*sizeEnlarged+ddh)-sE/2;
			} else {
				distx = x-(d.w/2f-(w*m.w*sizeEnlarged)/2f+(w*xr)*sizeEnlarged)-sE/2;
				disty = y-(d.h/2f-(h*m.h*sizeEnlarged)/2f+(h*yr)*sizeEnlarged+ddh)-sE/2;
			}
			if(inRange(distx, -sE/2, sE/2)&inRange(disty, -sE/2, sE/2)){
				if(!triggered) {
					//trigger();
					triggered = true;
				}
			} else {
				triggered = false;
				if(Math.abs(disty) > Math.abs(distx)){//Up/down
					if(disty < 0) move(0);
					else move(2);
				} else {//Left/right
					if(distx > 0) move(1);
					else move(3);
				}
			}
		}
	}

	protected void move() throws UnhandledException{
		if((Math.abs(playerCm.xf-playerCm.x)>0.01f)|(Math.abs(playerCm.yf-playerCm.y)>0.01f)){
			move(playerCm.stat); return;
		} else {
			playerCm.xf = playerCm.x;
			playerCm.yf = playerCm.y;
			if(turn != -1){
				boolean inTime = false;
				if((lastClock + 8)%(16*clockset)<(lastClock + 16)%(16*clockset)){
					inTime = inRange(clock, (lastClock + 8)%(16*clockset), (lastClock + 16)%(16*clockset));
				} else {
					inTime = !inRange(clock, (lastClock + 16)%(16*clockset), (lastClock + 8)%(16*clockset));
				}
				if(inTime) move(turn);
				return;
			}
		}
		mouseMove(mouseX, mouseY);
	}
	protected void move(int dir) throws UnhandledException{
		if (paused) return;
		if(playerCm.stat != dir){
			if(Math.abs(playerCm.stat - dir)%2 == 1){
				playerCm.stat = dir;
				turn = -1;
			} else {
				playerCm.stat += (Math.round(Math.random()))*2+3;
				playerCm.stat %= 4;
				turn = dir;
				lastClock = clock;
			}
		} else {
			stat.changeValueOf("EP", -0.005f);
			if(dir%2 == 0){
				move(0, dir - 1);
			} else {
				move(2 - dir, 0);
			}
		}
	}
	private void move(int dx, int dy) throws UnhandledException{
		walking = true;
		infight = false;
		try{
			if((Math.abs(playerCm.xf-playerCm.x)<0.01f)&(Math.abs(playerCm.yf-playerCm.y)<0.01f)&
					inRange(playerCm.x + dx, 0, m.w-1)&inRange(playerCm.yf + dy, 0, m.h-1)){
				if(!getTileItem(playerCm.x + dx, playerCm.y + dy, mOver).passable){
					int indexTile = getTile(playerCm.x + dx, playerCm.y + dy, mOver);
					if(indexTile != -1){
						switch(indexTile%2){
							case 0:
								break;
							case 1:
								cm.init(playerCm.x + dx, playerCm.y + dy);
								String cont = getTileStr(cm.x, cm.y, mStat);
								String act = ((SpecialObject)getTileItem(cm.x, cm.y, mOver)).actionInteract;

							/*	Because "pushed" internal command requires update per frame,
								which will be limited by the <triggered> tag that aims to prevent
								players from triggering an object multiple times. */
								if(act.contains("pushed")) executeCommands(act);
								else {
									if(!getContent(cont,"<triggered>").equals("T")){
										setTile(cm.x, cm.y, addTagTo(getParameter(cont, "<triggered>"),"T", "<triggered>"), mStat);
										if(!executeCommands(act)){
											setTile(cm.x, cm.y, getParameter(cont, "<triggered>"), mStat);
										}
									}
								}
								break;
						}
					}
				} else {
					int val = getTile(playerCm.x + dx, playerCm.y + dy, mItem);
					if ((val%2 == 1)&(val != -1)){
						cm.init(playerCm.x + dx, playerCm.y + dy);
						if(!haveTag(getTileStr(cm.x, cm.y, mStat), "<fight>")){
							infight = true;
							executeCommands("fight-delete([this])");
						}
					}
				}
			}
			if((Math.abs(playerCm.xf-playerCm.x)>0.01f)|(Math.abs(playerCm.yf-playerCm.y)>0.01f)){
				playerCm.move(dx*step, dy*step);
			} else if (checkLocPassable(playerCm.x+dx, playerCm.y+dy)){
				playerCm.move(dx*step, dy*step);
			}
			v = 10;
			Audio.playlist2(getTileItem(playerCm.x, playerCm.y, m).walkedon, "player");
			int indexItem = getTile(playerCm.x, playerCm.y, mItem);
			if(indexItem != -1){
				switch(indexItem%2){
					case 0:
						Item itm = getItemItem(playerCm.x, playerCm.y, mItem);
						if(itm.useOnTouch){
							notif("You equipped yourself with "+itm.name, 1000);
							executeCommands(itm.cmdOnUse);
						} else {
							notif("You picked up "+itm.name, 1000);
							addInventory(indexItem/2);
						}
						mItem = setTile(playerCm.x, playerCm.y, -1, mItem);
						break;
					case 1: //Code unused
						break;
				}
			} else{
				indexItem = getTile(playerCm.x, playerCm.y, mOver);
				if(indexItem != -1 && indexItem % 2 == 1){
					SpecialObject sp = (SpecialObject)getTileItem(playerCm.x, playerCm.y, mOver);
					if(sp.pressureActivate
							&& Math.abs(playerCm.xf - playerCm.x) < 1E-3
							&& Math.abs(playerCm.yf - playerCm.y) < 1E-3){
						cm.init(playerCm.x, playerCm.y);
						executeCommands(sp.actionInteract);
					}
				}
			}
			float ttS = 544f/tileSize/sizeEnlarged;
			if(m.w > ttS){navCm.xf = playerCm.xf-ttS/2;}
			if(m.h > ttS){navCm.yf = playerCm.yf-ttS/2;}
			if(navCm.xf<0){navCm.xf=0;}
			if(navCm.yf<0){navCm.yf=0;}
			if(navCm.xf+ttS>m.w)
			{navCm.xf=m.w-ttS;}
			if(navCm.yf+ttS>m.h)
			{navCm.yf=m.h-ttS;}
		} catch (Exception e){e.printStackTrace();}
	}

	protected void addInventory(String itemName) throws UnhandledException{
		addInventory(items.getItemInd(itemName));
	}
	protected void addInventory(int indexItem){
		boolean out = false;
		int i;
		for(i = 0; i < inventory[0].length; i++){
			if(inventory[0][i] == indexItem){out = true; break;}
		}
		if(out){
			inventory[1][i]++;
		} else {
			int[][] inf = {Arrays.append(inventory[0], indexItem), Arrays.append(inventory[1], 1)};
			inventory = inf;
		}
	}

	protected void back() throws UnhandledException{
		if(loc == "null") btg.engine = new Engine(btg, d, 2);
		else loc = "null";
	}

	protected boolean inSelTileRegion(int x, int y){
		return (x>sx)&(x<sx+w*scale*4)&(y>sty+ddh)&(y<sdy+ddh);}
	protected boolean inTileDescRegion(int x, int y){
		return (x>sx)&(x<sx+w*scale*4)&(y>sdy+ddh)&(y<d.h/2+272+ddh);
	}
	protected boolean inLTBTabRegion(int x, int y){
		return (x>sx)&(x<sx+w*scale*4)&(y>sty-32+ddh)&(y<sty+ddh);}
	protected boolean inSelButtonRegion(int x, int y){
		return (x>d.w-sx-144-24)&(x<d.w-sx)&(y>sry+ddh)&(y<sry+seperation+(32+seperation)*buttons.length);
	}
}
