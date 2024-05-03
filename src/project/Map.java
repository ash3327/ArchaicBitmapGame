package project;

public class Map {
	public MapType m;
	public Object map;
	public int w, h, t;// width, height and tallness
	public String type;
	
	public Map(MapType mt, int ww, int hh, int tt, String Type) throws UnhandledException{
		m = mt;
		w = ww; h = hh; t = tt; //23 or 15
		type = Type;
		switch (mt){
		case Flat:
			map = new Object[w][h];  
			break;
		case Leveled:	
			map = new Object[t][w][h];  
			break;
		}
	}
	
/*	public Map(MapType mt, String strs) throws UnhandledException{
		m = mt;
		
		t = strs.split("/").length;
		h = (strs.split("-").length + t - 1)/t;
		w = (strs.split("\\.").length + h*t - 1)/(h*t);
		
		switch (mt){
		case Flat: 		map = new int[w][h]; break;
		case Leveled:	map = new int[t][w][h]; break;
		} 
		for(int k = 0; k < t; k++){
		for(int j = 0; j < h; j++){
		for(int i = 0; i < w; i++){
			if(t == 1){
				setTile(i, j, Integer.parseInt(strs.split("-")[j].split("\\.")[i]));
			} else {
				setTile(i, j, k, Integer.parseInt(strs.split("/")[k].split("-")[j].split("\\.")[i]));
			}
		}}}
	}//*/
	
	public void init(Object value) throws UnhandledException{
		for(int k = 0; k < t; k++){
			for(int j = 0; j < h; j++){
			for(int i = 0; i < w; i++){
				if(m == MapType.Flat){
					setTile(i, j, value);
				} else {
					setTile(i, j, k, value);
				}
			}}}
	}
	
	public int getTile(int x, int y) throws UnhandledException{
		if(type != "int") throw new UnhandledException(0x2, "Data Type "+type+" does not support int function getTile(x, y, z)");
		if(m==MapType.Flat)	{return (int)((Object[][])map)[x][y];}
		else				{throw new UnhandledException(0x2, "Map Type "+m+" does not support int function getTile(x, y)");}
	}
	
	public int getTile(int x, int y, int z) throws UnhandledException{
		if(type != "int") throw new UnhandledException(0x2, "Data Type "+type+" does not support int function getTile(x, y, z)");
		if(m==MapType.Leveled)	{return (int)((Object[][])((Object[])map)[z])[x][y];}
		else if (m==MapType.Flat) {return getTile(x, y);}
		else					{throw new UnhandledException(0x2, "Map Type "+m+" does not support int function getTile(x, y, z)");}
	}
	
	public String getTileStr(int x, int y) throws UnhandledException{
		if(type != "String") throw new UnhandledException(0x2, "Data Type "+type+" does not support Str function getTileStr(x, y, z)");
		if(m==MapType.Flat)	{return (String)((Object[][])map)[x][y];}
		else				{throw new UnhandledException(0x2, "Map Type "+m+" does not support Str function getTileStr(x, y)");}
	}
	
	public String getTileStr(int x, int y, int z) throws UnhandledException{
		if(type != "String") throw new UnhandledException(0x2, "Data Type "+type+" does not support Str function getTileStr(x, y, z)");
		if(m==MapType.Leveled)	{return (String)((Object[][])((Object[])map)[z])[x][y];}
		else					{throw new UnhandledException(0x2, "Map Type "+m+" does not support Str function getTileStr(x, y, z)");}
	}
	
	public void setTile(int x, int y, Object value) throws UnhandledException{
		try{
			if((int)value < -1) throw new UnhandledException(0x3, "Value "+value+" out of range (value >= 0) at void function setTile(x, y, value)");
		} catch(Exception e){}
		if(m==MapType.Flat)	{((Object[][])map)[x][y] = value;}
		else				{throw new UnhandledException(0x2, "Map Type "+m+" does not support void function setTile(x, y, value)");}
	}
	
	public void setTile(int x, int y, int z, Object value) throws UnhandledException {
		try {
			if ((int) value < -1)
				throw new UnhandledException(0x3, "Value " + value + " out of range (value >= 0) at void function setTile(x, y, z, value)");
		} catch (Exception e) {
		}
		try {
			if (m == MapType.Leveled) {
				((Object[][]) ((Object[]) map)[z])[x][y] = value;
			} else if (m == MapType.Flat) {
				setTile(x, y, value);
			} else {
				throw new UnhandledException(0x2, "Map Type " + m + " does not support void function setTile(x, y, z, value)");
			}

		} catch (Exception e) {
		}
	}
}