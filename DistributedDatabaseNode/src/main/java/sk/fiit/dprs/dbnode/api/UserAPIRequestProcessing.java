package sk.fiit.dprs.dbnode.api;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.google.gson.Gson;

import sk.fiit.dprs.dbnode.api.services.PingRequestor;
import sk.fiit.dprs.dbnode.consulkv.NodeTableRecord;
import sk.fiit.dprs.dbnode.consulkv.NodeTableService;
import sk.fiit.dprs.dbnode.db.DBMock;
import sk.fiit.dprs.dbnode.db.models.Database;
import sk.fiit.dprs.dbnode.exceptions.CannotPingNodeException;
import sk.fiit.dprs.dbnode.exceptions.InvalidFormatException;
import sk.fiit.dprs.dbnode.exceptions.MissingKeyException;
import sk.fiit.dprs.dbnode.models.Quorum;
import sk.fiit.dprs.dbnode.models.VectorClock;
import sk.fiit.dprs.dbnode.utils.Hash;

/**
 * Java methods behind User-to-Node API calls
 * 
 * @author Jozef Zatko
 */
public class UserAPIRequestProcessing {
	
	public static NodeTableService service = null;
	
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
		
		if (isMyData(key)) {
			
		}
		
		if (isDataOfFirstReplica(key)) {
			
		}
		
		if (isDataOfSecondReplica(key)) {
			
		}
		
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
		} catch(Exception e) {
			throw new CannotPingNodeException(e);
		}
	}
	
	/**
	 * Ping all nodes of distributed database
	 * 
	 * @return ping info
	 * @throws CannotPingNodeException any fail during ping
	 */
	public static String pingAllNodes(String consulURL) throws Exception {
		
		try {
			return new PingRequestor().pingAllNodes(consulURL);
		} catch(Exception e) {
			throw new CannotPingNodeException(e);
		}
	}
	
	/**
	 * Ping all healthy nodes of distributed database
	 * 
	 * @return ping info
	 * @throws CannotPingNodeException any fail during ping
	 */
	public static String pingHealthyNodes(String consulURL) throws Exception {
		
		try {
			return new PingRequestor().pingHealthyNodes(consulURL);
		} catch(Exception e) {
			throw new CannotPingNodeException(e);
		}
	}

	public static String getNodeInfo(String id) throws UnknownHostException {
		
		String ip = InetAddress.getLocalHost().getHostAddress();
		
		return "[{ipAdress:" + ip + "},{customID:" + id + "}]";
	}
	
	private static boolean isMyData(String key) {
		
		long hash = Hash.get(key);
		
		if (hash < Database.getinstance().getMyDataHashFrom() || hash > Database.getinstance().getMyDataHashTo()) {
			
			return false;
		}
		return true;
	}
	
	private static boolean isDataOfFirstReplica(String key) {
		
		long hash = Hash.get(key);
		
		String previousNode = "";
		try {
			previousNode = service.getPrevious(InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		NodeTableRecord previous = service.getRecord(previousNode);
		long from = previous.getHashFrom();
		long to = previous.getHashTo();
		
		if (hash >= from && hash <= to) {
			
			return true;
		}
		
		return false;
	}
	
	private static boolean isDataOfSecondReplica(String key) {
		
		long hash = Hash.get(key);
		
		String previousNode = "";
		try {
			previousNode = service.getPrevious(InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		previousNode = service.getPrevious(previousNode);
		
		NodeTableRecord previous = service.getRecord(previousNode);
		long from = previous.getHashFrom();
		long to = previous.getHashTo();
		
		if (hash >= from && hash <= to) {
			
			return true;
		}
		
		return false;
	}
}