package project;

import project.Sprite.Tile;

public class BUG {
	public static void debug(){
		System.err.println("DEBUG");
	}
	public static void debug0(int ind){
		System.err.println(fpad("",ind*4)+"DEBUG");
	}
	public static void debug0(int ind, Object... args){
		for(Object arg : args) debug(arg,ind+1);
		debug0(ind);
	}
	public static void debug(Object... args){
		for(Object arg : args) debug(arg,1);
		debug();
	}
	public static void debug(Object arg, int ind){
		try{
			Object[] oar = (Object[]) arg;
			debug("START ARRAY:",ind);
			for(Object elem : oar) debug(elem, ind+1);
			debug("END ARRAY:",ind);
		} catch(Exception e){
			try{
				int[] oar = (int[]) arg;
				debug("START ARRAY:",ind);
				for(int elem : oar) debug(elem, ind+1);
				debug("END ARRAY:",ind);
			} catch(Exception e2){
				try{
					Tile tile = (Tile) arg;
					debug("Tile: "+tile.indCol+": "+tile.x+","+tile.y+"-"+tile.offset);
				} catch(Exception e3) {System.err.println(fpad("",ind*4)+"OBJECT:["+arg.toString()+"]");}
			}
		}
	}
	public static void throwError(String err){System.err.println("ERROR: "+err);}
	public static String fpad(String in, int len){
		return String.format("%1$"+len+"s", in);
	}
	public static String pad(String in, int len){
		return String.format("%1$-"+len+"s", in);
	}
}
