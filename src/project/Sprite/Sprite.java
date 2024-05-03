package project.Sprite;

public class Sprite {
	public int indCol, x, y, offset; public String description, name;
	
	public Sprite(String Name, int indexColour, int texX, int texY, int NumFrame, String desc){
		indCol = indexColour; x = texX; y = texY; offset = NumFrame; description = desc;
		name = Name;
	}
	
	public Sprite(){}
}
