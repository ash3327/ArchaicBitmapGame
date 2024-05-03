package project;

import java.util.ArrayList;

public class DataArrays {
	private Object[] records = new Object[0];
	public int numrecord = 0;
	
	public void addRecords(Object... record){
		records = Arrays.append(records, (Object)record);
		numrecord ++;
	}
	public void addRecord(Object[] record){
		records = Arrays.append(records, (Object)record);
		numrecord ++;
	}
	
	public Object[] getRecord(int ind){
		return (Object[]) records[ind];
	}
	
	public void deleteRecord(int ind){
		records = Arrays.delete(records, ind);
		numrecord --;
	}

	public String toString(){
		String out = "[";
		for(Object e : records) out += "["+e+"],";
		out = out.substring(0,out.length()-1);
		out += "]";
		return out;
	}
}
