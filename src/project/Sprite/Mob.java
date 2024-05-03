package project.Sprite;

public class Mob extends Sprite{
	public int wid, hei, xoff, yoff; //width, height, x offset and y offset of the mob texture.
	public int numFrameWalk, numFrameAttack;
	public int maxhealth, atk, dfs, matk, mdfs;
	
	public Mob(String Name, int indexColour, int texX, int texY, int NumFrame, int NumFrameWalk, 
			int NumFrameAttack, int tilW, int tilH, 
			int Xoff, int Yoff, String stat, String desc){
		super(Name, indexColour, texX, texY, NumFrame, desc);
		numFrameWalk = NumFrameWalk;
		numFrameAttack = NumFrameAttack;
		xoff = Xoff;
		yoff = Yoff;//TODO NOT FINISHED: MOBS.JAVA & DUNGEON PLAYER.TX
		wid = tilW; hei = tilH;
		
		if(stat.equals(".")) return;
		String[] stats = stat.split("\\+");
		matk = 0; mdfs = 0;
		switch(stats.length){
		case 5:
			matk = Integer.parseInt(stats[3]);
			mdfs = Integer.parseInt(stats[4]);
		case 3:
			maxhealth = Integer.parseInt(stats[0]);
			atk = Integer.parseInt(stats[1]);
			dfs = Integer.parseInt(stats[2]);
			break;
		}
	}
}
