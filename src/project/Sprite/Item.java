package project.Sprite;

public class Item extends Sprite{
	public String cmdOnUse; public boolean useOnTouch;
	public Item(String Name, int indexColour, int texX, int texY, int NumFrame, String desc, String commandOnUse, boolean useontouch){
		super(Name, indexColour, texX, texY, NumFrame, desc);
		cmdOnUse = commandOnUse;
		useOnTouch = useontouch;
	}
}