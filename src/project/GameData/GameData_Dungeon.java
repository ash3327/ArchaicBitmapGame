package project.GameData;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import project.*;
import project.Stat;

public class GameData_Dungeon extends GameData{
	public int x0, y0, z0, dir; //Position of Character
	public Stat stat; public int[][] inventory; public boolean exists;
	
	public GameData_Dungeon(String URL) throws UnhandledException{
		super(URL);
	}
	public GameData_Dungeon(Zip z, String path) throws UnhandledException{
		super(z, path);
	}
	public GameData_Dungeon(){
		super();
	}
	
	protected void init(){
		inventory = new int[2][0];
	}
	
	protected void readFromFile(BufferedReader br) throws UnhandledException, IOException{
		String[] pos = trim(br.readLine()).split(",");
		x0 = Integer.parseInt(pos[0]);
		y0 = Integer.parseInt(pos[1]);
		if(pos.length > 3){
			z0 = Integer.parseInt(pos[2]);
			dir = Integer.parseInt(pos[3]);
		} else {
			z0 = 0;
			dir = Integer.parseInt(pos[2]);
		}
		String[] keys = {}, names = {}; float[] values = {};
		String re = ""; String[] ret; boolean after = false;
		while(re != null){
			re = br.readLine();
			if(re != null){
				re = trim(re);
				if(re != ""){
					if(!after){
						if(re.contentEquals("Inventory")){
							after = true;
						} else {
							ret = re.split(",");
							keys = Arrays.append(keys, ret[0]);
							names = Arrays.append(names, ret[1].replace("+", " "));
							values = Arrays.append(values, Float.parseFloat(ret[2]));
						}
					} else {
						ret = re.split(",");
						if(inventory[0].length == 0){
							int[][] inf = {{Integer.parseInt(ret[0])}, {Integer.parseInt(ret[1])}};
							inventory = inf;
						} else {
							int[][] inf = {Arrays.append(inventory[0], Integer.parseInt(ret[0])), 
									Arrays.append(inventory[1], Integer.parseInt(ret[1]))};
							inventory = inf;
						}
					}
				}
			}
		}
		stat = new Stat(keys, names, values);
	}
	
	public void saveToFile(String URL) throws UnhandledException{
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(URL));
			bw.write(x0 + ", " + y0 + ", " + z0 + ", " + dir + " #Position of Player\n");
			for(int i = 0; i < stat.keys.length; i++){
				bw.write(stat.keys[i].replace(" ", "+") + ", " + stat.names[i].replace(" ", "+")
						+ ", " + stat.values[i]+"\n");
			}
			bw.write("Inventory");
			for(int i = 0; i < inventory[0].length; i++){
				bw.write("\n"+inventory[0][i] + ", " + inventory[1][i]);
			}
			bw.close();
		} catch (IOException e) {
			throw new UnhandledException(104, "File Output Exception at "+URL);
		}
	}
	public byte[] getContents() throws UnhandledException{
		try {
			String URL = "res/maps/0maps/temp.gm";
			try{
				FileOutputStream fos = new FileOutputStream(URL);
				fos.close();
			} catch (IOException e){}
			BufferedWriter bw = new BufferedWriter(new FileWriter(URL));
			bw.write(x0 + ", " + y0 + ", " + z0 + ", " + dir + " #Position of Player\n");
			for(int i = 0; i < stat.keys.length; i++){
				bw.write(stat.keys[i].replace(" ", "+") + ", " + stat.names[i].replace(" ", "+")
						+ ", " + stat.values[i]+"\n");
			}
			bw.write("Inventory");
			for(int i = 0; i < inventory[0].length; i++){
				bw.write("\n"+inventory[0][i] + ", " + inventory[1][i]);
			}
			bw.close();
			FileInputStream fis = new FileInputStream(URL);
			byte[] content = Zip.getContent(fis);
			fis.close();
			return content;
		} catch (IOException e) {e.printStackTrace();
			throw new UnhandledException(104, "File Output Exception at "+"res/maps/0maps/temp.gm");
		}
	}//*/
}
