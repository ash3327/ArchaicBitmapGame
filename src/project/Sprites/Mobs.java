package project.Sprites;

import java.io.BufferedReader;
import java.io.IOException;

import project.Sprite.Mob;
import project.Arrays;
import project.Display;
import project.UnhandledException;

public class Mobs extends Sprites{
//	public int wid, hei; //Width and Height of a single mob texture
	
	public Mobs(String URLData, Display d) throws UnhandledException{
		super(URLData, d);
	}
	
	protected void readItemFromFile(BufferedReader br) throws IOException{
		String str = ""; String[] strs;
		while (str != null){
			str = br.readLine();
			if(str != null){
				if(!str.startsWith("#")){
					str = trim(str);
					strs = str.split(",");
					names = Arrays.append(names, strs[0]);
					items = Arrays.append(items, 
							new Mob(strs[0], Integer.parseInt(strs[1]), Integer.parseInt(strs[2]), 
									Integer.parseInt(strs[3]), Integer.parseInt(strs[4]),
									Integer.parseInt(strs[5]), Integer.parseInt(strs[6]),
									Integer.parseInt(strs[7]), Integer.parseInt(strs[8]),
									Integer.parseInt(strs[9]), Integer.parseInt(strs[10]), 
									strs[11], strs[12]));
				}
			} else {break;}
		}
	}
}


