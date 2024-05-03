package project;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Texture{
	String f;
	BufferedImage image;
	BufferedImage img;
	public int[] p;
	public int w, h;
	
	public Texture(String file, Display d) throws UnhandledException{
		f = file;
		try {
			int pos = file.lastIndexOf("/");
			String zafter = file.substring(pos+1);
			String zpath = file.substring(0, pos);
			if(!zpath.contains(".")) img = ImageIO.read(new File(f));
			else {
				Zip z = new Zip(zpath);
				img = z.getImage(zafter);
			}
			image = new BufferedImage(img.getWidth(),img.getHeight(),BufferedImage.TYPE_INT_RGB);
			image.createGraphics().drawImage(img, 0, 0, img.getWidth(), img.getHeight(), null);
		} catch (IOException e) {
			throw new UnhandledException(102, "IOException on retrieving image: " + f);
		}
		w = image.getWidth();
		h = image.getHeight();
		p = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
	}
}
