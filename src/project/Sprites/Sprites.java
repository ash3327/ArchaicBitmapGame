package project.Sprites;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import project.Sprite.Sprite;
import project.Sprite.Tile;
import project.*;

public class Sprites {
	public Texture tex, blend; public Sprite[] items = {}; public String[] names = {}; 
	public int length;
	
	Sprites(String URLData, Display d) throws UnhandledException{
		BufferedReader br;
		try {
			int pos = URLData.lastIndexOf("/");
			String zafter = URLData.substring(pos+1);
			String zpath = URLData.substring(0, pos);
			if(zpath.contains(".")){
				Zip zp = new Zip(zpath);
				br = zp.readFile(zafter);
			} else {
				br = new BufferedReader(new FileReader(URLData));
			}
			String str = "";
			str = br.readLine();
			str = trim(str);
			if(str.startsWith("/")){
				str = zpath + str;
			}
			tex = new Texture(str, d); 
			str = br.readLine();
			str = trim(str);
			blend = new Texture(str, d);
			readItemFromFile(br);
			br.close();
		} catch (FileNotFoundException e) {
			throw new UnhandledException(101, "File "+URLData+" is not found at class Items, " +
					"constructor Items(URLTexture, URLBlendColImg, URLData)");
		} catch (IOException e) {
			throw new UnhandledException(103, "Error occured when reading file "+URLData+" at class Items, " +
					"constructor Items(URLTexture, URLBlendColImg, URLData)");
		}
		length = items.length;
	}
	
	protected void readItemFromFile(BufferedReader br) throws IOException, UnhandledException{
		String str = ""; String[] strs;
		while (str != null){
			str = br.readLine();
			if(str != null){
				str = trim(str);
				if(str != ""){
					strs = str.split(",");
					names = Arrays.append(names, strs[0]);
					items = Arrays.append(items, 
							new Sprite(strs[0], Integer.parseInt(strs[1]), Integer.parseInt(strs[2]), 
									Integer.parseInt(strs[3]), Integer.parseInt(strs[4]),
									strs[5]));
				}
			} else {break;}
		}
	}
	
	public Sprite getItem(int ind) throws UnhandledException{
		if(ind == -1) {
			Tile s = new Tile();
			s.overlaid = new ConnectDir("Null");
			s.passable = false;
			return s;
		}
		try{return items[ind];} catch(Exception e){return items[0];}
	}
	public Sprite getItem(String name) throws UnhandledException{
		int ind = getItemInd(name);
		return getItem(ind);
	}
	public boolean hasItem(String name) throws UnhandledException {
		return getItemInd(name) != names.length;
	}
	public int getItemInd(String name) throws UnhandledException{
		int ind = 0;
		while(ind < names.length){
			if(names[ind].contentEquals(name)){break;}
			ind++;
		}
		return ind;
	}
	
	public static String trim(String str){
		str = str.split("#")[0];
		str = str.replaceAll(" ", "");
		str = str.replaceAll("\t", "");
		return str;
	}
}