package sk.fiit.dprs.dbnode.db.models;

import java.util.HashMap;

/**
 * Model of one Data node
 * my data / first replica / second replica
 * 
 * @author Jozef Zatko
 */
public class DataNode {

	private HashMap<String, DatabaseRecord> data;
	
	public DataNode() {
		
		this.data = new HashMap<String, DatabaseRecord>();
	}
	
	/*
	 * GET
	 */
	public DatabaseRecord get(String key) {
		
		if(data.containsKey(key)) {
			
			return data.get(key);
		}
		return null;
	}
	
	/*
	 * CREATE
	 */
	public void create(String key, String value, String vClockDefinition) {
		
		DatabaseRecord record = new DatabaseRecord(value, vClockDefinition);
		
		data.put(key, record);
	}
	
	/*
	 * UPDATE
	 */
	public void update(String key, String value, String vClockDefinition) {
		
		DatabaseRecord record = data.get(key);
		
		record.setValue(value);
		// TODO: zmena vClocku
		
		data.put(key, record);
	}
	
	/*
	 * DELETE
	 */
	public void delete(String key) {
		
		if(data.containsKey(key)) {
			
			data.remove(key);
			return;
		}
	}
	
	/**
	 * Seed data into hash table from string input
	 */
	public void seed(String newData) {
		
		newData = newData.substring(1, newData.length()-1);
		
		String[] newDatArr = newData.split(", ");
		
		for (int i=0; i<newDatArr.length; i++) {
			
			String key = newDatArr[i].split("=")[0];
			String value = newDatArr[i].split("=")[1];
			
			String data = value.split(";")[0];
			String vClock = value.split(";")[1];
			
			create(key, data, vClock);
		}
	}
	
	public HashMap<String, DatabaseRecord> getData() {
		return data;
	}
}
