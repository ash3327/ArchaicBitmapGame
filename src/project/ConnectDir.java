package project;

public class ConnectDir {//Direction of connection
	public enum DirType{Null, Fluid, Wall, Overlaid, Quadraconnect, QuadraOverlaid, Bridge, Door, NPC}
	public DirType dir;
	
	public ConnectDir(String dirtype) throws UnhandledException{
		switch(dirtype){
		case "Null": dir = DirType.Null; break;		//Regular Tile
		case "Fluid": dir = DirType.Fluid; break;	//Fluid Tile, Shows side texture of the tile above
		case "Wall": dir = DirType.Wall; break;		//Wall Tile, Shows half textures of the tile adjacent
		case "Overlaid": dir = DirType.Overlaid; break;//Overlaid Tile, Regular Tile on overlaid layer
		case "Quadraconnect": dir = DirType.Quadraconnect; break;
		/* Quadraconnect Tiles: Tiles in overlaid layer that have different texture when connected 
		 * to tile of same type in different directions and shapes.*/
		case "QuadraOverlaid": dir = DirType.QuadraOverlaid; break;
		/* QuadraOverlaid Tiles: Tiles in overlaid layer that the texture printed is dissected in four
		 * quadrants, texture of each quadrant depends on the connection to the same type of tile*/
		case "Bridge": dir = DirType.Bridge; break;
		case "Door": dir = DirType.Door; break;
		default:
			if(dirtype.startsWith("NPC")) {dir = DirType.NPC; break;}
			throw new UnhandledException(0x4, "Value "+dirtype+" is not in the enum " +
				"DirType in constructor connectDir(dirtype).");
		}
	}
	
	//Note: Inputs in dirs[] counts from upward (1) to upward-left(8) clockwisely, 0 is placeholder.
	public int returnFrame(int[] dirs){
		int frameNum = 0;
		switch(dir){
		case Overlaid:
		case Null: break;
		case Fluid:
			frameNum = 1;
			for(int i = 0; i < dirs.length; i++){
				if(dirs[i] == 1){frameNum = 0; break;}
			}
			break;
		case Wall:
			for(int i = 0; i < dirs.length; i++){
				if(dirs[i] == 1){frameNum = 2;}
				if(dirs[i] == 5){frameNum = 1;break;}
			}
			break;
		case Door:
			int num = 0;
			for(int i = 0; i < dirs.length; i++){
				if((dirs[i] == 1)|(dirs[i] == 5)){num++;}
				if((dirs[i] == 3)|(dirs[i] == 7)){num = 0; break;}
			}
			if(num == 2) frameNum = 1;
			break;
		case Quadraconnect:
			int countconnect = 0, OldDir = 0, NewDir = 0;
			for(int i = 0; i < dirs.length; i++){
				if(dirs[i]%2 == 1){
					countconnect++;
					if(OldDir == 0){OldDir = dirs[i];}
					else if (NewDir == 0){NewDir = dirs[i];}
				}
			}
			switch(countconnect){
			case 0: frameNum = 0; break;
			case 1: frameNum = 1; break;
			case 2: 
				if(NewDir - OldDir == 4){frameNum = 2;}
				else {frameNum = 3;}
				break;
			case 3: frameNum = 4; break;
			case 4: frameNum = 5; break;
			} break;
		case QuadraOverlaid:
			switch(dirs[0] + dirs[2]){
			case 0: frameNum = 0; break;
			case 1: frameNum = 1; break;
			case 2: 
				if(dirs[1] == 0){frameNum = 2;}
				else {frameNum = 3;}
				break;
			}
			break;
		case Bridge:
			if(dirs[0]+dirs[2]+dirs[4]+dirs[6] == 4){frameNum = 1;}
			break;
		}
		return frameNum;
	}
	//Note that flipping action is done before rotating
	public int returnFlip(int[] dirs, int frameNum){
		int flip = 0; boolean HFlip = false, VFlip = false;
		if(dir == DirType.Quadraconnect){
			switch (frameNum){
			case 1: 
				for(int i = 0; i < dirs.length; i++){
					if(dirs[i]%2 == 1){HFlip = ((dirs[i] != 3)&(dirs[i] != 5)); break;}
				}
				break;
			case 3: 
				VFlip = (dirs[0] == 1); HFlip = true;
				for(int i = 0; i < dirs.length; i++){
					if(dirs[i] == 3){HFlip = false; break;}
				}
				break;
			case 4: 
				int lostdir = 16;
				for(int i = 0; i < dirs.length; i++){
					if(dirs[i]%2 == 1){lostdir -= dirs[i];}
				}
				VFlip = ((lostdir == 3)|(lostdir == 5));
				break;
			}
		}
		if(HFlip){flip = 1;}
		if(VFlip){flip += 2;}
		return flip;
	}
	
	public boolean returnRot(int[] dirs, int frameNum){
		boolean rot = false;
		if(dir == DirType.Quadraconnect){
			int dirr = 0;
			for(int i = 0; i < dirs.length; i++){
				if(dirs[i]%2 == 1){dirr += dirs[i];}
			}
			switch (frameNum){
			case 1: 
				rot = (dirr == 1)|(dirr == 5);
				break;
			case 2: 
				rot = (dirr == 6);//Vertical bar = (1+5) = 6, Horizontal = 10
				break;
			case 4: 
				dirr = 16 - dirr;
				rot = (dirr == 3)|(dirr == 7);
				break;
			}
		} else if ((dir == DirType.QuadraOverlaid)&(frameNum == 1)){
			if(dirs[0] == 1){rot = true;}
		} else if ((dir == DirType.Bridge)&(frameNum == 0)){
			if((dirs[0] == 1)&(dirs[4] == 1)){rot = true;}
		}
		return rot;
	}
	
	protected static int hFlip(int dir){return (9-dir)	%8+1;}
	protected static int vFlip(int dir){return (13-dir)%8+1;}
}
