package sk.fiit.dprs.dbnode.db;

import java.util.HashMap;

import sk.fiit.dprs.dbnode.exceptions.MissingKeyException;
import sk.fiit.dprs.dbnode.models.DataModel;

/**
 * Temporary mock for DB node
 * 
 * @pattern Singleton
 * @author Jozef Zatko 
 */
public class DBMock {

	private static DBMock instance = null;
	
	private static HashMap<String, String> data;
	
	private DBMock() {}
	
	public static DBMock getInstance() {
		
		if(instance == null) {
			
			instance = new DBMock();
			data = new HashMap<String, String>();
			
			return instance;
		}
		return instance;
	}
	
	/*
	 * READ
	 */
	public DataModel get(String key) throws MissingKeyException {
		
		if(data.containsKey(key)) {
			
			return new DataModel(key,data.get(key));
		}
		throw new MissingKeyException(key);
	}
	
	/*
	 * CREATE or UPDATE
	 */
	public void createOrUpdate(String key, String value) {
		
		data.put(key, value);
	}
	
	/*
	 * DELETE
	 */
	public void delete(String key) throws MissingKeyException {
		
		if(data.containsKey(key)) {
			
			data.remove(key);
			return;
		}
		throw new MissingKeyException(key);
	}
}
