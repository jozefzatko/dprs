package sk.fiit.dprs.dbnode.api;

import com.google.gson.Gson;

import sk.fiit.dprs.dbnode.api.services.PingRequestor;
import sk.fiit.dprs.dbnode.db.DBMock;
import sk.fiit.dprs.dbnode.exceptions.CannotPingNodeException;
import sk.fiit.dprs.dbnode.exceptions.InvalidFormatException;
import sk.fiit.dprs.dbnode.exceptions.MissingKeyException;
import sk.fiit.dprs.dbnode.models.Quorum;
import sk.fiit.dprs.dbnode.models.VectorClock;

/**
 * Java methods behind User-to-Node API calls
 * 
 * @author Jozef Zatko
 */
public class UserAPIRequestProcessing {
	
	/**
	 * Process READ from database
	 * 
	 * @param key
	 * @param quorum
	 * @return found object in JSON format
	 * @throws InvalidFormatException wrong quorum format
	 * @throws MissingKeyException wrong key
	 */
	public static String get(String key, String quorum) throws InvalidFormatException, MissingKeyException {
		
		if(quorum != null) {
			new Quorum(quorum);
		}
		
		return new Gson().toJson(DBMock.getInstance().get(key)) ;
	}
	
	/**
	 * CREATE or UPDATE database data
	 * 
	 * @param key
	 * @param value
	 * @param quorum
	 * @param vectorClock
	 * @return acknowledgment
	 * @throws InvalidFormatException wrong quorum or vector clock format
	 */
	public static String createOrUpdate(String key, String value, String quorum, String vectorClock) throws InvalidFormatException {
		
		if(quorum != null) {
			new Quorum(quorum);
		}
		
		if(vectorClock != null) {
			new VectorClock(vectorClock);			
		}
			
		DBMock.getInstance().createOrUpdate(key, value);
		
		return "Successfuly added [" + key + "," + value + "] to database.";
	}
	
	/**
	 * DELETE data from database
	 * 
	 * @param key
	 * @param quorum
	 * @param vectorClock
	 * @return acknowledgment
	 * @throws InvalidFormatException wrong quorum or vector clock format
	 * @throws MissingKeyException wrong key
	 */
	public static String delete(String key, String quorum, String vectorClock) throws InvalidFormatException, MissingKeyException {
		
		if(quorum != null) {
			new Quorum(quorum);
		}
		
		if(vectorClock != null) {
			new VectorClock(vectorClock);
		}
		
		DBMock.getInstance().delete(key);
		
		return "Successfuly deleted [" + key + "] from database.";
	}
	
	/**
	 * Ping a specific node of distributed database
	 * 
	 * @param adress url to ping
	 * @return ping info
	 * @throws CannotPingNodeException any fail during ping
	 */
	public static String pingNode(String adress) throws CannotPingNodeException {
		
		try {
			return new PingRequestor().ping("http://" + adress, "/ping");
		} catch (Exception e) {
			throw new CannotPingNodeException();
		}
	}
	
	/**
	 * Ping all nodes of distributed database
	 * 
	 * @return ping info
	 * @throws CannotPingNodeException any fail during ping
	 */
	public static String pingAllNodes() throws CannotPingNodeException {
		
		try {
			return new PingRequestor().pingAllNodes();
		} catch (Exception e) {
			throw new CannotPingNodeException();
		}
	}
}
