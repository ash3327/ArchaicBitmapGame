package project.Sprites;

import java.io.BufferedReader;
import java.io.IOException;

import project.Arrays;
import project.Display;
import project.UnhandledException;
import project.Sprite.Button;

public class Buttons extends Sprites{
	public int texW, texH;

	public Buttons(String URLData, Display d) throws UnhandledException {
		super(URLData, d);
	}
	
	protected void readItemFromFile(BufferedReader br) throws IOException{
		String str = ""; String[] strs;
		str = br.readLine(); str = trim(str);
		strs = str.split(",");
		texW = Integer.parseInt(strs[0]); texH = Integer.parseInt(strs[1]);
		while (str != null){
			str = br.readLine();
			if(str != null){
				if(!str.startsWith("#")){
					str = trim(str);
					strs = str.split(",");
					strs[0] = strs[0].replace("+", " ");
					names = Arrays.append(names, strs[0]);
					items = Arrays.append(items, 
							new Button(strs[0], Integer.parseInt(strs[1]), Integer.parseInt(strs[2]), 
									Integer.parseInt(strs[3])));
				}
			} else {break;}
		}
	}
}
