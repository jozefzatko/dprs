package sk.fiit.dprs.dbnode.db.models;

import java.util.HashMap;

import sk.fiit.dprs.dbnode.exceptions.InvalidVectorClockFormatException;
import sk.fiit.dprs.dbnode.models.VectorClock;

/**
 * Model of one Data node
 * my data / first replica / second replica
 * 
 * @author Jozef Zatko
 */
public class DataNode {

	private HashMap<Long, DatabaseRecord> data;
	
	public DataNode() {
		
		this.data = new HashMap<Long, DatabaseRecord>();
	}
	
	/*
	 * GET
	 */
	public DatabaseRecord get(Long key) {
		
		if(data.containsKey(key)) {
			
			return data.get(key);
		}
		return null;
	}
	
	/*
	 * CREATE
	 */
	public void create(Long key, String value, VectorClock vClockDefinition) {
		
		DatabaseRecord record = new DatabaseRecord(value, vClockDefinition.toString());
		
		data.put(key, record);
	}
	
	/*
	 * UPDATE
	 */
	public void update(Long key, String value, VectorClock vClockDefinition) {
		
		DatabaseRecord record = data.get(key);
		
		record.setValue(value);
		record.setvClock(vClockDefinition);
		
		
		data.put(key, record);
	}
	
	/*
	 * DELETE
	 */
	public void delete(Long key) {
		
		if(data.containsKey(key)) {
			
			data.remove(key);
			return;
		}
	}
	
	/**
	 * Seed data into hash table from string input
	 */
	public void seed(String newData) {
		
		if("".equals(newData) || "{}".equals(newData)) {
			
			return;
		}
		
		newData = newData.substring(1, newData.length()-1);
		
		String[] newDatArr = newData.split(", ");
		
		for (int i=0; i<newDatArr.length; i++) {
			
			Long key = new Long(newDatArr[i].split("=")[0]);
			String value = newDatArr[i].split("=")[1];
			
			String data = value.split(";")[0];
			String vClockDefinition = value.split(";")[1];
			
			VectorClock vClock ;
			try {
				vClock = new VectorClock(vClockDefinition);
				create(key, data, vClock);
			} catch (InvalidVectorClockFormatException e) {
				e.printStackTrace();
			}
			
			
		}
	}
	
	/**
	 * Select part of data from hash interval
	 */
	public String toString(long from, long to) {
		
		if(this.data.isEmpty()) {
			
			return "";
		}
		
		String strHashTable = this.data.toString().substring(1, this.data.toString().length()-1);
		String arrHashTable[] = strHashTable.split(", ");
		
		StringBuilder builder = new StringBuilder("");
	    
		for (int i=0; i<arrHashTable.length; i++) {
			
			long key = new Long(arrHashTable[i].split("=")[0]);
			
			if (key >= from && key<=to) {
				
				builder.append(arrHashTable[i] + ", ");
			}
		}
		
		String result = builder.toString();
		
		if ("".equals(result)) {
			
			return result;
		}
		
		result = result.substring(0, builder.length()-2);
		
		return "{" + result + "}";
	}
	
	/**
	 * Remove values from node according to hash
	 * 
	 * @param from hash range from
	 * @param to hash range to
	 */
	public void remove(long from, long to) {
		
		if(this.data.isEmpty()) {
			return;
		}
		
		String strHashTable = this.data.toString().substring(1, this.data.toString().length()-1);
		String arrHashTable[] = strHashTable.split(", ");
	    
		for (int i=0; i<arrHashTable.length; i++) {
			
			long key = new Long(arrHashTable[i].split("=")[0]);
			
			if (key >= from && key<=to) {
				
				this.data.remove(key);
			}
		}
	}
	
	public HashMap<Long, DatabaseRecord> getData() {
		return data;
	}
	
	public void clear(){
		data.clear();
	}
}
