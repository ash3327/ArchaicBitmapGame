package project;

import java.io.Console;
import java.util.Scanner;

import project.Sprite.Sprite;

public class Arrays {
	public static Sprite[] append(Sprite[] itms, Sprite itm){
		Sprite[] Itms = new Sprite[itms.length+1];
		System.arraycopy(itms, 0, Itms, 0, itms.length);
		Itms[itms.length] = itm;
		return Itms;
	}
	public static String[] append(String[] itms, String itm){
		String[] Itms = new String[itms.length+1];
		System.arraycopy(itms, 0, Itms, 0, itms.length);
		Itms[itms.length] = itm;
		return Itms;
	}
	public static String[][] append(String[][] itms, String[] itm){
		String[][] Itms = new String[itms.length+1][];
		System.arraycopy(itms, 0, Itms, 0, itms.length);
		Itms[itms.length] = itm;
		return Itms;
	}
	public static String[] append(String[] itms, String[] itms2){
		String[] Itms = new String[itms.length+itms2.length];
		System.arraycopy(itms, 0, Itms, 0, itms.length);
		System.arraycopy(itms2, 0, Itms, itms.length, itms2.length);
		return Itms;
	}
	public static String[] deleteLast(String[] itms){
		String[] Itms = new String[itms.length - 1];
		if (itms.length - 1 >= 0) System.arraycopy(itms, 0, Itms, 0, itms.length - 1);
		return Itms;
	}
	public static String[] arrayAfter(String[] itms, int breakpt){
		String[] Itms = new String[itms.length - breakpt - 1];
		System.arraycopy(itms, breakpt + 1, Itms, 0, itms.length - breakpt - 1);
		return Itms;
	}
	public static String[] replaceString(String[] itms, String original, String news){
		String[] Itms = new String[itms.length];
		for(int i = 0; i < itms.length; i++) Itms[i] = itms[i].replace(original, news);
		return Itms;
	}
	public static String[] delete(String[] itms, int location){
		String[] Itms = new String[itms.length-1];
		if (location >= 0) System.arraycopy(itms, 0, Itms, 0, location);
		if (itms.length - 1 - location >= 0)
			System.arraycopy(itms, location + 1, Itms, location, itms.length - 1 - location);
		return Itms;
	}
	public static String[] insert(String[] itms, String itm, int location){
		String[] Itms = new String[itms.length+1];
		if (location >= 0) System.arraycopy(itms, 0, Itms, 0, location);
		Itms[location] = itm;
		if (itms.length - location >= 0)
			System.arraycopy(itms, location, Itms, location + 1, itms.length - location);
		return Itms;
	}
	public static boolean[] append(boolean[] itms, boolean itm){
		boolean[] Itms = new boolean[itms.length+1];
		System.arraycopy(itms, 0, Itms, 0, itms.length);
		Itms[itms.length] = itm;
		return Itms;
	}
	public static int[][] deleteItem(int[][] itms, int index){
		int[][] Itms = new int[itms.length][itms[0].length - 1];
		for(int j = 0; j < Itms.length; j++){
			if (index >= 0) System.arraycopy(itms[j], 0, Itms[j], 0, index);
			if (itms[0].length - 1 - index >= 0)
				System.arraycopy(itms[j], index + 1, Itms[j], index, itms[0].length - 1 - index);
		}
		return Itms;
	}
	public static int[] addEachItemIn(int[] itms, int[] itms2){
		int[] Itms = new int[itms.length];
		for(int i = 0; i < itms.length; i++) Itms[i] = itms[i] + itms2[i];
		return Itms;
	}
	public static int[] append(int[] itms, int itm){
		int[] Itms = new int[itms.length+1];
		System.arraycopy(itms, 0, Itms, 0, itms.length);
		Itms[itms.length] = itm;
		return Itms;
	}
	public static float[] append(float[] itms, float itm){
		float[] Itms = new float[itms.length+1];
		System.arraycopy(itms, 0, Itms, 0, itms.length);
		Itms[itms.length] = itm;
		return Itms;
	}
	public static byte[] append(byte[] itms, int itm){
		byte[] Itms = new byte[itms.length+1];
		System.arraycopy(itms, 0, Itms, 0, itms.length);
		Itms[itms.length] = (byte)itm;
		return Itms;
	}
	public static byte[] append(byte[] itms, byte[] itms2){
		byte[] Itms = new byte[itms.length+itms2.length];
		System.arraycopy(itms, 0, Itms, 0, itms.length);
		System.arraycopy(itms2, 0, Itms, itms.length + 0, itms2.length);
		return Itms;
	}
	public static Object[] append(Object[] itms, Object itm){
		Object[] Itms = new Object[itms.length+1];
		System.arraycopy(itms, 0, Itms, 0, itms.length);
		Itms[itms.length] = itm;
		return Itms;
	}
	public static Object[] insert(Object[] itms, Object itm, int location){
		Object[] Itms = new Object[itms.length+1];
		if (location >= 0) System.arraycopy(itms, 0, Itms, 0, location);
		Itms[location] = itm;
		if (itms.length - location >= 0)
			System.arraycopy(itms, location, Itms, location + 1, itms.length - location);
		return Itms;
	}
	public static Object[] delete(Object[] itms, int location){
		Object[] Itms = new Object[itms.length-1];
		if (location >= 0) System.arraycopy(itms, 0, Itms, 0, location);
		if (itms.length - 1 - location >= 0)
			System.arraycopy(itms, location + 1, Itms, location, itms.length - 1 - location);
		return Itms;
	}
}
