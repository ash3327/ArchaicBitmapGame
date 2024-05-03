package project;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;

import javax.swing.JComponent;

public class Display extends JComponent {
	private static final long serialVersionUID = 1L;
	public BufferedImage img;	//image to be displayed in the canvas
	int[] pixels;				//pixel data of img
	int colour = 0x000000;		//colour: 0xRRGGBB, each 1 hexadecimal digit
	int j0, j1;					//temperary storage of numbers
	public int w, h;
	public int mergeCol = -1;
	int dex0 = -1, dey0, dex1, dey1, Dex0 = -1, Dey0, Dex1, Dey1;
	int clrx0 = -1, clry0, clrx1, clry1; //Location of boundaries that the display region does not clear.
	boolean HFlip = false, VFlip = false, rot = false;
	int px0 = -1, py0, px1, py1;
	public String fontstr = "KaiTi", fonteng = "Centaur";
	
	public Display(int wid, int hei){
		w = wid; h = hei;
		img = new BufferedImage(wid, hei, BufferedImage.TYPE_INT_RGB);
		pixels = ((DataBufferInt)img.getRaster().getDataBuffer()).getData();
	}
	
	public void drawLine(Vertex a, Vertex b, int col, int stroke){
		if(stroke%2 == 0)	{j0 = -stroke/2; j1 = stroke/2;}
		else				{j0 = (-stroke+1)/2; j1 = (stroke+1)/2;}
		for(int j = j0; j < j1; j++){//x = a.x+i; y = a.y+i*a.m(b)
			if(a == b){
				drawLine(
					new Vertex(a.x+j, a.y),
					new Vertex(b.x+j, b.y),
					col
				);
			} else if(a.mv(b)){
				drawLine(
					new Vertex(a.x, a.y+j),
					new Vertex(b.x, b.y+j),
					col
				);
			} else {
				drawLine(
					new Vertex(a.x+j, a.y),
					new Vertex(b.x+j, b.y),
					col
				);
			}
		}
	}
	
	private void drawLine(Vertex a, Vertex b, int col){
		if(a == b){
			pixels[(a.x)+(a.y)] = col;
		} else if(a.mv(b)){
			if(a.x>b.x) a.swap(b);
			for(int i = 0; i < a.dx(b); i++){
				if((a.x+i>=0)&(a.x+i<w)&(a.y+(int)(i*a.m(b))>=0)&(a.y+(int)(i*a.m(b))<h)){
					pixels[(a.x+i)+(a.y+(int)(i*a.m(b)))*w] = col;
				}
			}
		} else {
			if(a.y>b.y) a.swap(b);
			for(int i = 0; i < a.dy(b); i++){
				if((a.x+(int)(i/a.m(b))>0)&(a.x+(int)(i/a.m(b))<w)&(a.y+i>0)&(a.y+i<h)){
					pixels[(a.x+(int)(i/a.m(b)))+(a.y+i)*w] = col;
				}
			}
		}
	}
	
	public void setClearRegion(int x0, int y0, int x1, int y1){
		px0 = x0; py0 = y0; px1 = x1; py1 = y1;
	}
	public void resetClearRegion(){
		px0 = -1; py0 = -1; px1 = w+1; py1 = h+1;
	}
	
	public void clearScreen(int col){clearScreen(col, false);}
	public void clearScreen(int col, boolean careful){
		if(careful&(clrx0!=-1)){
			for(int i = 0; i < w; i++){
				if(inRange(i, px0, px1)){
					if(!inRange(i, clrx0, clrx1))
						for(int j = Math.max(py0, 0); j < Math.min(py1, h); j++)
							pixels[i+j*w] = col;
					else {
						for(int j = Math.max(py0, 0); j < clry0; j++)
							pixels[i+j*w] = col;
						for(int j = clry1; j < Math.min(py1, h); j++)
							pixels[i+j*w] = col;
					}
				}
			}
		} else {
			Arrays.fill(pixels,col);
		}
	}
	public void clearScreen(int[] image, int widt, int heit, boolean careful){
		if(careful&(clrx0!=-1)){
			for(int i = 0; i < w; i++){
				if(inRange(i, px0, px1)){
					if(!inRange(i, clrx0, clrx1))
						for(int j = Math.max(py0, 0); j < Math.min(py1, h); j++)
							pixels[i+j*w] = getColor(image, i, j, w, h, widt, heit);
					else{
						for(int j = Math.max(py0, 0); j < clry0; j++)
							pixels[i+j*w] = getColor(image, i, j, w, h, widt, heit);
						for(int j = clry1; j < Math.min(py1, h); j++)
							pixels[i+j*w] = getColor(image, i, j, w, h, widt, heit);
					}
				}
			}
		} else {
			for(int i = 0; i < pixels.length; i++){
				pixels[i] = getColor(image, i%w, i/w, w, h, widt, heit);
			}
		}
	}
	public int getColor(int[] image, int i, int j, int w0, int h0, int w1, int h1){
		//0: what we want, 1: what we have
		float scale;
		if(1f*w1/h1 >= 1f*w0/h0){
			scale = 1f*h1/h0;
		} else {
			scale = 1f*w1/w0;
		}
		return image[(int)(i*scale) + (int)(j*scale)*w1];
	}
	
	public int rand(int mul){return (int)(Math.random()*mul);}
	
	public void sketTriangle(Vertex a, Vertex b, Vertex c, int col, int stroke){
		drawLine(a, b, col, stroke);
		drawLine(a, c, col, stroke);
		drawLine(b, c, col, stroke);
	}
	
	public void fillTriangle(Vertex a, Vertex b, Vertex c, int col/*/, int trans/**/){
		//Ordering: a < b < c
		a.compareX(b, false);
		a.compareX(c, false);
		c.compareX(b, true);
		Vertex d = new Vertex(b.x, (int)(a.y+a.dx(b)*a.m(c)));
		//Fill
		if(a.dy(b)>a.dx(b)*a.m(c))	{j0 = +1;} 
		else 						{j0 = -1;}
		for(int i = 0; i < a.dx(b); i++){
			int j = 0;
			while((i*(a.m(b)-a.m(c)) - j)*j0 >= 0){
				if((a.x+i>=0)&(a.x+i<w)&(a.y+j+(int)(i*a.m(c))>=0)&(a.y+j+(int)(i*a.m(c))<h)){
					pixels[(a.x+i)+(a.y+j+(int)(i*a.m(c)))*w] = col;
				}
				j+=j0;
			}
		}
		if(b.dy(c)>b.dx(c)*d.m(c))	{j0 = -1;} 
		else 						{j0 = +1;}
		for(int i = 0; i < b.dx(c); i++){
			int j = 0;
			while(((b.dx(c)-i)*(d.m(c)-b.m(c)) - j)*j0 >= 0){
				if((d.x+i>=0)&(d.x+i<w)&(d.y+j+(int)(i*d.m(c))>=0)&(d.y+j+(int)(i*d.m(c))<h)){
					pixels[(d.x+i)+(d.y+j+(int)(i*d.m(c)))*w] = col;
				}
				j+=j0;
			}
		}
	}

	public int[] getSizeOfString(String str, int fontStyle, int fontSize, Graphics g){
		if(Character.valueOf(str.charAt(0))>256){
			g.setFont(new Font(fontstr, fontStyle, fontSize+5));
		} else {
			g.setFont(new Font(fonteng, fontStyle, fontSize));
		}
		Font font = g.getFont();
		FontRenderContext frc = ((Graphics2D) g).getFontRenderContext();
		int[] result = {(int) font.getStringBounds(str, frc).getWidth(),
				(int) font.getStringBounds(str, frc).getHeight()};
		return result;
	}
	public void dispMsg(String str, int xS, int yS, Texture bgt, int bgani, int bgfra, int tileW, int tileH){
		//displayStr, xScreenPos, yScreenPos, bgtexture, bganimation, bgframeno
		String[] strs = str.split("\n");
		int len = -1, hei = -1; int[] sizeStr;
		for(int i = 0; i < strs.length; i++){
			sizeStr = getSizeOfString(strs[i],0,32, img.getGraphics());
			len = Math.max(len, sizeStr[0]);
			hei = Math.max(hei, sizeStr[1]);
		}
		int x0 = xS - len>>1, y0 = yS - hei>>1, dx = len/tileW, dy = hei/tileH;
		for(int i = 0; i < dx; i++){
			for(int j = 0; j < dy; j++){
				placeSprite(bgt, x0+i*tileW, y0+j*tileH, 1, tileW, tileH, bgfra, bgani,-1);
			}
			placeSprite(bgt, x0+i*tileW, y0+hei-tileH, 1, tileW, tileH, bgfra, bgani,-1);
		}
		for(int j = 0; j < dy; j++){
			placeSprite(bgt, x0+len-tileW, y0+j*tileH, 1, tileW, tileH, bgfra, bgani,-1);
		}

	}
	
	public void setMergeCol(Texture t, int ind){
		int col = t.p[ind];
		if(col != 0x000000){
			mergeCol = col;
		} else {mergeCol = -1;}
	}
	public boolean inRange(float var, float lowerbound, float upperbound){
		return (var>lowerbound)&(var<upperbound);
	}
	
	public void defaultProtectedRegion(){//Specify the region that would not be cleared
		clrx0 = dex0; clry0 = dey0; clrx1 = dex1; clry1 = dey1;}
	public void deProtectedRegion(){//Specify the region that would not be cleared
		clrx0 = Dex0; clry0 = Dey0; clrx1 = Dex1; clry1 = Dey1;
		Dex0 = -1; Dey0 = -1; Dex1 = -1; Dey1 = -1;}
	public void setProtectedRegion(int X0, int Y0, int X1, int Y1){//Specify the region that would not be cleared
		clrx0 = X0; clry0 = Y0; clrx1 = X1; clry1 = Y1;}
	public void overlapProtectedRegion(int X0, int Y0, int X1, int Y1){//Specify the region that would not be cleared
		clrx0 = Math.max(X0, clrx0); clry0 = Math.max(Y0, clry0); 
		clrx1 = Math.min(X1, clrx1); clry1 = Math.min(Y1, clry1);}
	public void setDefaultProtectedRegion(int X0, int Y0, int X1, int Y1){//Specify the region that would not be cleared
		dex0 = X0; dey0 = Y0; dex1 = X1; dey1 = Y1;}
	public void setDeProtectedRegion(){//Specify the region that would not be cleared
		Dex0 = clrx0; Dey0 = clry0; Dex1 = clrx1; Dey1 = clry1;}
	
	public void setFlipNRot(int Flip, boolean Rot){
		if(Flip%2 == 1){HFlip = true;}
		if(Flip/2 == 1){VFlip = true;}
		rot = Rot;
	}
	public void resetFlipNRot(){
		HFlip = false; VFlip = false; rot = false;
	}
	
	public void placeSprite(Texture t, float X, float Y, float size, int tileW, int tileH, float frameNum, float animate ,int transCol){
		placeSprite(t, X, Y, size, tileW, tileH, frameNum, animate, transCol, false);
	}
	public void placeSprite(Texture t, float X, float Y, float size, int tileW, int tileH, float frameNum, 
			float animate,int transCol, boolean restrict){
		placeShadowSprite(t, t, X, Y, size, tileW, tileH, frameNum, animate, frameNum, animate, transCol, restrict, false);
	}
	public void placeShadowSprite(Texture t, Texture tS, float X, float Y, float size, int tileW, int tileH, float frameNum,
								  float animate, float frameShadow, float animateShadow, int transCol, boolean restrict, boolean reverse){
		placeShadowSprite(t, tS, X, Y, size, tileW, tileH, frameNum,
				animate, frameShadow, animateShadow, transCol, restrict, reverse, -1, 0);
	}
	public void placeShadowSprite(Texture t, Texture tS, float X, float Y, float size, int tileW,
								  int tileH, float frameNum, float animate, float frameShadow,
								  float animateShadow, int transCol, boolean restrict, boolean reverse,
								  int tintCol, int deg){
	//	selectTile((int)X, (int)Y, (int)(size*tileW), (int) (size*tileH), 4);//*/
		int x = (int) X, y = (int) Y;
		int col, col2; boolean same = false;
		if((animateShadow == animate)&(frameShadow == frameNum)) same = true;
		float i0min = 0, i0max = tileW*size, i1min = 0, i1max = tileH*size;
		if(restrict){
			i0min = Math.max(0, clrx0-x); i0max = Math.min(tileW*size, clrx1-x);
			i0min = Math.max(i0min, -x); i0max = Math.min(i0max, w-x);
			i1min = Math.max(0, clry0-y); i1max = Math.min(tileH*size, clry1-y);
			i1min = Math.max(i1min, -y); i1max = Math.min(i1max, h-y);
		}
		int aw = (int) (animate*tileW), fh = (int) (tileH*frameNum);
		int aw2 = aw, fh2 = fh;
		if(!same){
			aw2 = (int) (animateShadow*tileW); fh2 = (int) (tileH*frameShadow);
		}
		for(int i0 = (int) i0min; i0 < i0max; i0++){
		for(int i1 = (int) i1min; i1 < i1max; i1++){
			int colX = (int)(i0/size), colY = (int)(i1/size), placeholder;
			//The rotation should be done before flipping because this part is where we take the col
			if(rot){
				placeholder = colX;
				colX = colY;
				colY = placeholder;
			}
			if(HFlip){colX = tileW - colX - 1;}
			if(VFlip){colY = tileH - colY - 1;}
			while(colX + aw >= t.w){
				animate -= 1f*t.w/tileW;
				frameNum += 1;
				aw = (int) (animate*tileW);
				fh = (int) (tileH*frameNum);
			}
			col = t.p[colX+aw+(colY+fh)*t.w];
			if(same) col2 = col;
			else col2 = tS.p[colX+aw2+(colY+fh2)*tS.w];
			if(((col!=transCol)&(col2!=transCol)&!reverse)|((col2==transCol)&reverse)){
				if(mergeCol!=-1)	col = colMerge(col, mergeCol);
				if(tintCol!=-1)		col = colChange(col, tintCol, deg, colChangeMode.Mix);//degree of tint
				pixels[x+i0+(y+i1)*w] = col;
			}
		}}
		mergeCol = -1;
	}
	
	public void selectTile(int x, int y, int w0, int h0, int stroke){
		drawLine(new Vertex(x, y), new Vertex(x+w0/3, y), colour, stroke);
		drawLine(new Vertex(x, y), new Vertex(x, y+h0/3), colour, stroke);

		drawLine(new Vertex(x+w0, y), new Vertex(x+w0*2/3, y), colour, stroke);
		drawLine(new Vertex(x+w0, y), new Vertex(x+w0, y+h0/3), colour, stroke);
		
		drawLine(new Vertex(x, y+h0), new Vertex(x+w0/3, y+h0), colour, stroke);
		drawLine(new Vertex(x, y+h0), new Vertex(x, y+h0*2/3), colour, stroke);

		drawLine(new Vertex(x+w0, y+h0), new Vertex(x+w0*2/3, y+h0), colour, stroke);
		drawLine(new Vertex(x+w0, y+h0), new Vertex(x+w0, y+h0*2/3), colour, stroke);
	}
	
	private int colMerge(int col, int mergeCol){
		Color c = new Color(col);
		float[] fs0 = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
		c = new Color(mergeCol);
		float[] fs1 = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
		fs0[0] = fs1[0]; fs0[1] *= fs1[1];
		return Color.HSBtoRGB(fs0[0], fs0[1], fs0[2]);
	}

	public void combine(Display d2){
		for(int i = 0; i < pixels.length; i++)
			if(d2.pixels[i] != -2)
				pixels[i] = d2.pixels[i];
	}

	private int colChange(int col, int col2, int deg, colChangeMode ccm){
		//if(1 == 1) return col;
		int R = (col>>16)-(col>>24<<8), G = (col>>8)-(col>>16<<8), B = col-(col>>8<<8);
		int R2 = col2>>16, G2 = (col2>>8)-(col2>>16<<8), B2 = col2-(col2>>8<<8);
		R *= R; G *= G; B *= B; R2 *= R2; G2 *= G2; B2 *= B2;
		switch(ccm){
		case Mix: 		R = (R*(100-deg)+R2*deg)/100; 	G = (G*(100-deg)+G2*deg)/100; 	B = (B*(100-deg)+B2*deg)/100; break;
		case Add: 		R = R+R2*deg/100; 				G = G+G2*deg/100;		 		B = B+B2*deg/100; 			break;
		case Subtract: 	R = R-R2*deg/100; 				G = G-G2*deg/100; 				B = B-B2*deg/100; 			break;
		case Recolour:
			if((R == G)&(G == B)){
				int Max = Math.max(Math.max(R2, G2), B2), Min = Math.min(Math.min(R2, G2), B2);
				float mul = 1f*R/(Max+Min)*2;
				R = (int) (R2*mul); G = (int) (G2*mul); B = (int) (B2*mul);
			}
			break;
		}
		R = (int)Math.sqrt(R); G = (int)Math.sqrt(G); B = (int)Math.sqrt(B);
		R = Math.max(Math.min(R, 255), 0); G = Math.max(Math.min(G, 255), 0); B = Math.max(Math.min(B, 255), 0);
		col = (R<<16) + (G<<8) + B;
		return col;
	}
	private enum colChangeMode{Mix, Add, Subtract, Recolour}
}

class Vertex {
	int x, y;
	Vertex(int x0, int y0){x = x0; y = y0;}
	float m(Vertex b){return 1f*dy(b)/dx(b);}	/*get slope of the line*/
	float am(Vertex b){return -1f*dx(b)/dy(b);}
	boolean mv(Vertex b){return (Math.abs(dx(b))>(Math.abs(dy(b))));}
		/*true only if the absolute slope of the line ab is less than 1, regardless of direction of slope*/
	int dx(Vertex b){return (b.x-x);}			/*get X distance between 2 points*/
	int dy(Vertex b){return (b.y-y);}
	void swap(Vertex b){
		int xc = b.x, yc = b.y;
		b.x = x; b.y = y;
		x = xc; y = yc;
	}
	void compareX(Vertex b, boolean L){
		boolean result;
		if(L)	{result = (x==Math.max(x, b.x));} 
		else	{result = (x==Math.min(x, b.x));}
		if(!result){swap(b);}
	}
}
