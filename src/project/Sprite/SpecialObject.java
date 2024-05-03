package project.Sprite;

import project.ConnectDir;

public class SpecialObject extends Tile {
	public String actionCreate, actionInteract; public int numFrameTriggered;
	public String activated; public boolean pressureActivate;
	
	public SpecialObject(String Name, int indexColour, int texX, int texY, int NumFrameAnimate, int NumFrameTriggered, int NumEntity, 
			ConnectDir Overlaid, boolean Passable, boolean PressureActivate, String desc, String ActionCreate, String ActionInteract,
						 String ActivatedSound, String AlwaysSound, int bright){
		//, desc
		super(Name, indexColour, texX, texY, NumFrameAnimate, NumEntity, -1, -1, Overlaid, Passable, desc, "none", AlwaysSound, bright);
		actionCreate = ActionCreate; actionInteract = ActionInteract;
		numFrameTriggered = NumFrameTriggered;
		activated = ActivatedSound;
		pressureActivate = PressureActivate;
	}
}
