package sk.fiit.dprs.dbnode.models;

import java.util.Date;

/**
 * Model of DB record
 * 
 * @author Jozef Zatko
 */
public class DataModel {

	private String key;
	private String value;
	
	private Date created;
	private Date updated;
	
	
	public DataModel() {
		
		this.created = new Date();
		this.updated = new Date();
	}
	
	public DataModel(String key, String value) {
		
		this.key = key;
		this.value = value;
		
		this.created = new Date();
		this.updated = new Date();
	}
	
	public String getKey() {
		return key;
	}
	
	public String getValue() {
		return value;
	}

	public Date getCreated() {
		return created;
	}

	public Date getUpdated() {
		return updated;
	}
}
