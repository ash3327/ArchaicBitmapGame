package project.GameData;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import project.UnhandledException;
import project.Zip;

public class GameData {
	
	GameData(String URL) throws UnhandledException{
		init();
		try {
			BufferedReader br = new BufferedReader(new FileReader(URL));
			readFromFile(br);
			br.close();
		} catch (FileNotFoundException e) {
			throw new UnhandledException(101, "File "+URL+" is not found in class Game");
		} catch (IOException e) {
			throw new UnhandledException(103, "Exception on reading file "+URL+" in class Game");
		}
	}
	GameData(Zip z, String path) throws UnhandledException{
		init();
		try {
			BufferedReader br = z.readFile(path);
			readFromFile(br);
			br.close();
		} catch (FileNotFoundException e) {
			throw new UnhandledException(101, "Zip path "+path+" is not found in class Game");
		} catch (IOException e) {
			throw new UnhandledException(103, "Exception on reading file "+path+" in class Game");
		}
	}
	GameData(){
		init();
	}
	
	protected void init(){}
	protected void readFromFile(BufferedReader br) throws UnhandledException, IOException{}
	
	static String trim(String str){
		str = str.split("#")[0];
		str = str.replaceAll(" ", "");
		str = str.replaceAll("\t", "");
		return str;
	}
}
