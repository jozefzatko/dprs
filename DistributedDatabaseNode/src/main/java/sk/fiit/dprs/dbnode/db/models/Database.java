package sk.fiit.dprs.dbnode.db.models;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Database model
 * 
 * @author Jozef Zatko
 */
public class Database {

	private String myIP;
	
	private long myDataHashFrom;
	private long myDataHashTo;
	
	private DataNode myData;
	private DataNode firstReplica;
	private DataNode secondReplica;
	
	
	public Database(long from, long to) {
		
		try {
			this.myIP = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		this.myDataHashFrom = from;
		this.myDataHashTo = to;
		
		this.myData = new DataNode();
		this.firstReplica = new DataNode();
		this.secondReplica = new DataNode();
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
