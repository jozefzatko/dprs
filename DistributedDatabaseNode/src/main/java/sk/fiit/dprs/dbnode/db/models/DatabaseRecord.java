package sk.fiit.dprs.dbnode.db.models;

import java.util.Date;

import sk.fiit.dprs.dbnode.exceptions.InvalidVectorClockFormatException;
import sk.fiit.dprs.dbnode.models.VectorClock;

/**
 * Model of DB record (value, vClock, created, updated)
 * 
 * @author Jozef Zatko
 */
public class DatabaseRecord {

	private String value;
	
	private VectorClock vClock;
	
	private Date created;
	private Date updated;
	
	
	public DatabaseRecord(String value, String vclockDefinition) {
		
		this.value = value;
		
		try {
			this.vClock = new VectorClock(vclockDefinition);
		} catch (InvalidVectorClockFormatException e) {
			e.printStackTrace();
		}
		
		this.created = new Date();
		this.updated = new Date();
	}
	
	@Override
	public String toString() {
		
		return this.value + ";" + this.vClock.toString();
	}

	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	public VectorClock getVectorClock() {
		return vClock;
	}
	
	
	public VectorClock getvClock() {
		return vClock;
	}

	public void setvClock(VectorClock vClock) {
		this.vClock = vClock;
	}

	public Date getCreated() {
		return created;
	}

	public Date getUpdated() {
		return updated;
	}

}
