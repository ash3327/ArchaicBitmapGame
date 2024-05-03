package project.Sprites;

import java.io.BufferedReader;
import java.io.IOException;

import project.Arrays;
import project.ConnectDir;
import project.Display;
import project.UnhandledException;
import project.Sprite.Tile;

public class Tiles extends Sprites{
	
	public Tiles(String URLData, Display d) throws UnhandledException{
		super(URLData, d);
	}
	
	protected void readItemFromFile(BufferedReader br) throws IOException, UnhandledException{
		String str = ""; String[] strs;
		while (str != null){
			str = br.readLine();
			if(str != null){
				if(!str.startsWith("#")){
					str = trim(str);
					strs = str.split(",");
					names = Arrays.append(names, strs[0]);
					items = Arrays.append(items, 
							new Tile(strs[0], Integer.parseInt(strs[1]), Integer.parseInt(strs[2]), 
									Integer.parseInt(strs[3]), Integer.parseInt(strs[4]),
									Integer.parseInt(strs[5]),
									Integer.parseInt(strs[6]), Integer.parseInt(strs[7]),
									new ConnectDir(strs[8]), Boolean.parseBoolean(strs[9]),
									strs[10], strs[11], strs[12], Integer.parseInt(strs[13])));
				}
			} else {break;}
		}
	}
}