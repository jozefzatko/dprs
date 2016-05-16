package sk.fiit.dprs.dbnode.db.models;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Database model
 * 
 * @author Jozef Zatko
 */
public class Database {

	private static Database instance = null;
	
	private String myIP;
	
	private long myDataHashFrom;
	private long myDataHashTo;
	
	private DataNode myData;
	private DataNode firstReplica;
	private DataNode secondReplica;
	
	
	private Database() {
	
		try {
			this.myIP = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		this.myData = new DataNode();
		this.firstReplica = new DataNode();
		this.secondReplica = new DataNode();
	}

	public static Database getinstance() {
		
		if (instance == null) {
			
			instance = new Database();
		}
		
		return instance;
	}
	
	public String getMyIP() {
		return myIP;
	}
	
	public long getMyDataHashFrom() {
		return myDataHashFrom;
	}
	public void setMyDataHashFrom(long myDataHashFrom) {
		this.myDataHashFrom = myDataHashFrom;
	}

	public long getMyDataHashTo() {
		return myDataHashTo;
	}
	public void setMyDataHashTo(long myDataHashTo) {
		this.myDataHashTo = myDataHashTo;
	}

	public DataNode getMyData() {
		return myData;
	}

	public DataNode getFirstReplica() {
		return firstReplica;
	}

	public DataNode getSecondReplica() {
		return secondReplica;
	}
	
}
