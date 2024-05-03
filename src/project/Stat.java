package project;

import project.Sprites.Sprites;

public class Stat {
	public String[] keys, names; public float[] values;
	
	public Stat(String[] Keys, String[] Names, float[] Values){
		keys = Keys; names = Names; values = Values;
	}
	Stat(String[] Keys, String[] Names){
		keys = Keys; names = Names;
		values = new float[Keys.length];
	}
	Stat(String[] Keys, String[] Names, float Value){
		keys = Keys; names = Names;
		values = new float[Keys.length];
		for(int i = 0; i < values.length; i++){
			values[i] = Value;
		}
	}
	
	public void setValueOf(String key, float toValue) throws UnhandledException{
		values[searchFor(key)] = toValue;
	}
	public void changeValueOf(String key, float byValue) throws UnhandledException{
		values[searchFor(key)] += byValue;
	}
	
	public float getValueOf(String key) throws UnhandledException{
		return values[searchFor(key)];
	}
	
	private int searchFor(String key) throws UnhandledException{
		for(int i = 0; i < keys.length; i++){
			if(Sprites.trim(keys[i].toLowerCase().replace("+","")).
					contentEquals(Sprites.trim(key.toLowerCase().replace("+","")))){return i;}
		}
		throw new UnhandledException(0x4, "The key " + key + " does not exist.");
	}
}
