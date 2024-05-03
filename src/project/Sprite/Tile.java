package project.Sprite;

import project.ConnectDir;
import project.Sprite.Sprite;

public class Tile extends Sprite{
	public int numEntity, sideX, sideY, brightness; public boolean passable;//texX2 and Y2 are textures of the side of the tile
	public ConnectDir overlaid; public String walkedon, always;
	
	public Tile(String name, int indexColour, int texX, int texY, int NumFrame, int NumEntity, int texX2, int texY2,
				ConnectDir Overlaid, boolean Pass, String desc, String WalkedonSound, String AlwaysSound, int bright){
		super(name, indexColour, texX, texY, NumFrame, desc);
		numEntity = NumEntity;
		sideX = texX2; sideY = texY2;
		overlaid = Overlaid;
		passable = Pass;
		walkedon = WalkedonSound; always = AlwaysSound;
		brightness = bright;
	}
	
	public Tile(){
		super();
	}
}