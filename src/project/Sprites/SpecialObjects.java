package project.Sprites;

import java.io.BufferedReader;
import java.io.IOException;

import project.Arrays;
import project.ConnectDir;
import project.Display;
import project.UnhandledException;
import project.Sprite.SpecialObject;

public class SpecialObjects extends Tiles {
	public int wid, hei;

	public SpecialObjects(String URLData, Display d) throws UnhandledException {
		super(URLData, d);
	}
	
	protected void readItemFromFile(BufferedReader br) throws IOException, UnhandledException{
		String str = ""; String[] strs;
		str = trim(br.readLine());
		wid = Integer.parseInt(str.split(",")[0]);
		hei = Integer.parseInt(str.split(",")[1]);
		while (str != null){
			str = br.readLine();
			if(str != null){
				if(!str.startsWith("#")){
					str = trim(str);
					strs = str.split(",");
					names = Arrays.append(names, strs[0]);
					items = Arrays.append(items, 
							new SpecialObject(strs[0], Integer.parseInt(strs[1]), Integer.parseInt(strs[2]), 
									Integer.parseInt(strs[3]), Integer.parseInt(strs[4]),
									Integer.parseInt(strs[5]), Integer.parseInt(strs[6]),
									new ConnectDir(strs[7]), Boolean.parseBoolean(strs[8]),
									Boolean.parseBoolean(strs[9]), strs[10], strs[11], strs[12], strs[13], strs[14],
									Integer.parseInt(strs[15])));
				}
			} else {break;}
		}
	}
}
