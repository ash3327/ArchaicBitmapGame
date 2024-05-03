package project.Sprites;

import java.io.BufferedReader;
import java.io.IOException;

import project.Arrays;
import project.Display;
import project.UnhandledException;
import project.Sprite.Item;

public class Items extends Sprites{
	
	public Items(String URLData, Display d) throws UnhandledException{
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
							new Item(strs[0], Integer.parseInt(strs[1]), Integer.parseInt(strs[2]), 
									Integer.parseInt(strs[3]), Integer.parseInt(strs[4]),
									strs[5], strs[6], Boolean.parseBoolean(strs[7])));
				}
			} else {break;}
		}
	}
}


