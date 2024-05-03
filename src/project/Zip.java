package project;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.*;

import javax.imageio.ImageIO;

public class Zip {
	String filePath; ZipOutputStream zos;
	Object[] ze = new ZipEntry[0];
	Object[] conts = new Object[0];
	
	public Zip(String zipFilePath){
		filePath = zipFilePath;
	}
	
	public ZipInputStream getFile(String path) throws UnhandledException{
		try {
			ZipInputStream zis;
			int pos = filePath.lastIndexOf("/");
			String zafter = filePath.substring(pos+1);
			String zpath = filePath.substring(0, pos);
			if(zpath.contains(".")) {
				Zip zz = new Zip(zpath);
				zis = new ZipInputStream(zz.getFile(zafter));
			} else {
				zis = new ZipInputStream(new FileInputStream(filePath));
			}
			ZipEntry ze;
			while(true){
				ze = zis.getNextEntry();
				if(ze.getName().contentEquals(path)){
					break;
				}
			}
			return zis;
		} catch (FileNotFoundException e) {
			throw new UnhandledException(101, e.getMessage());
		} catch (IOException e) {
			throw new UnhandledException(103, e.getMessage());
		}
	}
	public BufferedReader readFile(String path) throws UnhandledException{
		BufferedReader br = new BufferedReader(new InputStreamReader(getFile(path)));
		return br;
	}
	public BufferedImage getImage(String path) throws UnhandledException{
		try {
			ZipInputStream f = getFile(path);
			BufferedImage bi = ImageIO.read(f);
			f.close();
			return bi;
		} catch (IOException e) {
			throw new UnhandledException(103, e.getMessage());
		}
	}
	
	public void createZipFile(String... fileNames) throws UnhandledException{
		try {
			FileOutputStream fos = new FileOutputStream(filePath);
			ZipOutputStream zos = new ZipOutputStream(fos);
			for(String aFile : fileNames){
				zos.putNextEntry(new ZipEntry(aFile));
				zos.write(new byte[0], 0, 0);
				zos.closeEntry();
			}
			zos.close();
		} catch (FileNotFoundException e) {
			throw new UnhandledException(101, e.getMessage());
		} catch (IOException e) {
			throw new UnhandledException(104, e.getMessage());
		}
	}
	
	/* Process: 
	 * prepareFile() - load the data in the zip file into the processing system.
	 * writeFile(Str name, byte[] content) - write the data into the processing system.
	 * writeFile() - load the data in processing system back into the zip file.
	 * */
	public void prepareFile(){
		try{
			ZipInputStream zis = new ZipInputStream(new FileInputStream(filePath));
			ze = new ZipEntry[0];
			conts = new Object[0];
			while(true){
				ZipEntry zz = zis.getNextEntry();
				if(zz == null) break;
				ze = Arrays.append(ze, zz);
				byte[] cont = new byte[0];
				while(true){
					int s = zis.read();
					if(s == -1) break;
					cont = Arrays.append(cont, s);
				}
				conts = Arrays.append(conts, cont);
			}
			zis.close();
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	public void writeFile(String path, int[] content){
		byte c[] = new byte[content.length*4];
		for(int i = 0; i < content.length; i++){
			c[i*4] = (byte) (content[i]>>24);
			c[i*4+1] = (byte) (content[i]>>16);
			c[i*4+2] = (byte) (content[i]>>8);
			c[i*4+3] = (byte) content[i];
		}
		writeFile(path, c);
	}
	public void writeFile(String path, byte[] content){
		boolean met = false;
		for(int i = 0; i < ze.length; i++){
			try{
				ZipEntry zz = (ZipEntry)ze[i];
				if(zz.getName().contentEquals(path)){
					conts[i] = content;
					met = true;
					break;
				}
			} catch (Exception e){e.printStackTrace();}
		}
		if(!met){
			ze = Arrays.append(ze, new ZipEntry(path));
			conts = Arrays.append(conts, content);
		}
	}
	public void writeFile() throws UnhandledException{//TODO not done 
		try {
			zos = new ZipOutputStream(new FileOutputStream(filePath));
			for(int i = 0; i < ze.length; i++){
				try{
					zos.putNextEntry(new ZipEntry(((ZipEntry)ze[i]).getName()));
					zos.write((byte[])conts[i]);
					zos.closeEntry();
				} catch (Exception e){e.printStackTrace();}
			}
			zos.close();
		}/*/ catch (FileNotFoundException e) {
			throw new UnhandledException(101, e.getMessage());
		} catch (IOException e) {e.printStackTrace();
			throw new UnhandledException(103, e.getMessage());
		}/*/ catch (Exception e){e.printStackTrace();}//*/
	}
	
	public static byte[] getContent(InputStream fis) throws IOException{
		byte[] cont = new byte[0];
		while(true){
			int s = fis.read();
			if(s == -1) break;
			cont = Arrays.append(cont, s);
		}
		return cont;
	}
	
/*	public static void main(String[] args){
		try {
		/*	Zip z = new Zip("res/test.zip");
			z.createZipFile("hi.txt", "bi.txt");
			byte[] h = {5, 9, 8, 11, 13, 59, 52};
			z.writeFile("bi.txt", h);
			z.writeFile("hi.txt", h);
			Zip z2 = new Zip("res/test2.zip");
			z2.createZipFile("hello.zip");
			z2.writeFile("hello.zip", getContent(new FileInputStream("res/test.zip")));//
			Zip z2 = new Zip("res/test2.zip/hello.zip");
			BUG.debug(Zip.getContent(z2.getFile("hi.txt")));
		} catch (Exception e){
			e.printStackTrace();
		}
	}//*/
}
