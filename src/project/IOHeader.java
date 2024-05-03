package project;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class IOHeader{
	public MapType Dim; public int W, H, T, enc, brightness = 100; public String texloc = "dungeon.zip"; String line; String[] args;
	public IOHeader(MapType dim, int w, int h, int t, int Enc, String Texloc, int brig){
		Dim = dim; W = w; H = h; T = t; enc = Enc; texloc = Texloc; brightness = brig;
	}
	public IOHeader(){}
	public void loadIOHeader(String URL) throws UnhandledException{
		try {
			BufferedReader br = new BufferedReader(new FileReader(URL));
			loadIOHeader(br);
			br.close();
		} catch (FileNotFoundException e) {
			throw new UnhandledException(101, "File \""+URL+"\" not found at " +
					"void loadIOHeader(URL) in class IOHeader");
		} catch (IOException e) {
			throw new UnhandledException(103, "File Input Exception at " +
					"void loadIOHeader(URL) in class IOHeader");
		} 
	}
	public void loadIOHeader(Zip z, String path) throws UnhandledException{
		try {
			BufferedReader br = z.readFile(path);
			loadIOHeader(br);
			br.close();
		} catch (FileNotFoundException e) {
			throw new UnhandledException(101, "Zip File Path \""+path+"\" not found at " +
					"void loadIOHeader(URL) in class IOHeader");
		} catch (IOException e) {
			throw new UnhandledException(103, "File Input Exception at " +
					"void loadIOHeader(URL) in class IOHeader");
		} 
	}
	private void loadIOHeader(BufferedReader br) throws UnhandledException, IOException{
		line = br.readLine();
		args = line.split(",");
		switch(args[0]){
		case "Flat": 	Dim = MapType.Flat; 	break;
		case "Leveled": Dim = MapType.Leveled; 	break;
		default: 		System.err.println("Unprocessed MapType "+args[0]+" in " +
				"void loadIOHeader(URL) in class IOHeader");
		}
		W = Integer.parseInt(args[1]);
		H = Integer.parseInt(args[2]);
		if(Dim == MapType.Flat){
			T = (int) (0.0/0.0);
		} else {
			T = Integer.parseInt(args[3]);
		}
		enc = Integer.parseInt(args[4]);
		try{texloc = args[5];} catch(Exception e){}
		try{brightness = Integer.parseInt(args[6]);} catch(Exception e){}
	}
	public void saveIOHeader(String URL) throws UnhandledException{
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(URL));
			args = new String[7];
			args[0] = Dim.toString();
			args[1] = W+"";
			args[2] = H+"";
			if(Dim == MapType.Flat){
				args[3] = "/";
			} else {
				args[3] = T+"";
			}
			args[4] = enc+"";
			args[5] = texloc;
			args[6] = brightness+"";
			line = args[0];
			for(int i = 1; i < args.length; i++){
				line = line + "," + args[i]; 
			}
			bw.write(line);
			bw.close();
		} catch (IOException e){
			throw new UnhandledException(104, "File Output Exception at " +
					"void saveIOHeader(URL) in class IOHeader");
		}
	}
	
	public byte[] getIOHeader() throws UnhandledException{
		String URL = "res/maps/0maps/temp.mph";
		saveIOHeader(URL);
		FileInputStream fis;
		try {
			fis = new FileInputStream(URL);
			return Zip.getContent(fis);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}