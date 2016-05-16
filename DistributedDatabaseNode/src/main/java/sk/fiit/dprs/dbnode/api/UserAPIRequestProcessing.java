package sk.fiit.dprs.dbnode.api;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import com.google.gson.Gson;

import sk.fiit.dprs.dbnode.api.services.PingRequestor;
import sk.fiit.dprs.dbnode.api.services.RESTRequestor;
import sk.fiit.dprs.dbnode.consulkv.NodeTableRecord;
import sk.fiit.dprs.dbnode.consulkv.NodeTableService;
import sk.fiit.dprs.dbnode.db.DBMock;
import sk.fiit.dprs.dbnode.db.models.Database;
import sk.fiit.dprs.dbnode.db.models.DatabaseRecord;
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
	
	static Logger log = Logger.getLogger(UserAPIRequestProcessing.class.getName());
	
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
		
		long hashKey = Hash.get(key);
		DatabaseRecord record = null;
		
		if (isMyData(key)) {
			record = Database.getinstance().getMyData().get(hashKey);
		}
		
		if (isDataOfFirstReplica(key)) {
			record = Database.getinstance().getFirstReplica().get(hashKey);
		}
		
		if (isDataOfSecondReplica(key)) {
			record = Database.getinstance().getSecondReplica().get(hashKey);
		}
		
		if(quorum != null) {
			new Quorum(quorum);
		}
		
		return new Gson().toJson(record) ;
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
	public static String createOrUpdate(String key, String value, String quorum, String vectorClock, String clientIP) throws InvalidFormatException {
		
		
		log.info("createOrUpdate "+key+" value: "+value+" quorum "+quorum+ " vectorClock "+vectorClock+ " clientIP "+clientIP);
		long HashKey2 = Hash.get(key);
		log.info("HASH KLUCA "+HashKey2);
		
		String httpRequest = "";
		httpRequest = ":4567/data/"+key+"/"+value;
		
		if(quorum!=null || vectorClock!=null){
			httpRequest = httpRequest+"?";
		}
		
		if(quorum!=null && vectorClock !=null){
			httpRequest = httpRequest+"quorum="+quorum+"&vclock="+vectorClock; 
		}else if(quorum!=null && vectorClock ==null){
			httpRequest = httpRequest+"quorum="+quorum; 
		}else if(quorum==null && vectorClock !=null){
			httpRequest = httpRequest+"vclock="+vectorClock; 
		}
		
		if(quorum != null) {
			new Quorum(quorum);
		}
		
		if(vectorClock != null) {
			new VectorClock(vectorClock);			
		}
		
		if (isMyData(key)) {

			log.info("createOrUpdate isMyData");
			DBMock.getInstance().createOrUpdate(key, value);	
			
			
			
			String myIp = "";
			try {
				myIp = InetAddress.getLocalHost().getHostAddress();
			} catch (UnknownHostException e1) {
				
				e1.printStackTrace();
			}
			NodeTableRecord record = service.getRecord(myIp);
			String firstReplica = record.getFirstReplicaId();
			String secondReplica = record.getSecondReplicaId();
			
			boolean isFromMyReplicaOrMaster = false;
			
			if(clientIP.equals(firstReplica) || clientIP.equals(secondReplica)){
				isFromMyReplicaOrMaster = true;
			}
			
			if(!isFromMyReplicaOrMaster){
				
				long hashKey=Hash.get(key);
				DatabaseRecord data = Database.getinstance().getMyData().get(hashKey);
				if(data==null){
					VectorClock vClockDefinition =  new VectorClock("[1,0,0]");
					Database.getinstance().getMyData().create(hashKey, value, vClockDefinition);
				}else{
					VectorClock vClockDefinition =  data.getVectorClock();
					int originalValue = vClockDefinition.getOriginalValue();
					vClockDefinition.setOriginalValue(originalValue+1);
					
					
					Database.getinstance().getMyData().update(hashKey, value, vClockDefinition);
				}
				
				log.info("DATA FROM MASTER "+myIp+" TO REPLICAS: "+record.getFirstReplicaId()+" "+record.getSecondReplicaId()+ " clientIP "+clientIP);
				try {
					
					new RESTRequestor("POST", "http://" + firstReplica + httpRequest).request();
					new RESTRequestor("POST", "http://" + secondReplica + httpRequest).request();
				} catch (IOException e) {
					log.info("FAILED TO REPLICATE DATA FROM MASTER "+myIp+" TO REPLICAS: "+record.getFirstReplicaId()+" "+record.getSecondReplicaId()+ " clientIP "+clientIP);
					e.printStackTrace();
				}
			}
		}else	if (isFirstReplicatedData(key)) {
			
			log.info("createOrUpdate isFirstReplicatedData");
			DBMock.getInstance().createOrUpdate(key, value);	
			
			String myIp = "";
			try {
				myIp = InetAddress.getLocalHost().getHostAddress();
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			}
			
			String nextNode = "";
			nextNode = service.getNext(myIp);
			NodeTableRecord next = service.getRecord(nextNode); // this is master
			
			String previousNode = "";
			previousNode = next.getSecondReplicaId();
			
			boolean isFromMyReplicaOrMaster = false;
			
			if(clientIP.equals(nextNode) || clientIP.equals(previousNode)){
				isFromMyReplicaOrMaster = true;
			}
			
			if(!isFromMyReplicaOrMaster){
				
				long hashKey=Hash.get(key);
				DatabaseRecord data = Database.getinstance().getFirstReplica().get(hashKey);
				if(data==null){
					VectorClock vClockDefinition =  new VectorClock("[0,1,0]");
					Database.getinstance().getFirstReplica().create(hashKey, value, vClockDefinition);
				}else{
					VectorClock vClockDefinition =  data.getVectorClock();
					int firstReplicaValue = vClockDefinition.getFirstReplica();
					vClockDefinition.setFirstReplica(firstReplicaValue+1);
					Database.getinstance().getFirstReplica().update(hashKey, value, vClockDefinition);
				}
				
				log.info(" DATA FROM 1st replica "+myIp+" TO master: "+nextNode+" and 2nd replica: "+previousNode+ " clientIP "+clientIP);
				try {
					
					new RESTRequestor("POST", "http://" + nextNode+ httpRequest).request();
					new RESTRequestor("POST", "http://" + previousNode + httpRequest).request();
				} catch (IOException e) {
					log.info("FAILED TO REPLICATE DATA FROM 1st replica "+myIp+" TO master: "+nextNode+" and 2nd replica: "+previousNode+ " clientIP "+clientIP);
					e.printStackTrace();
				}
			}
			
		}else if (isSecondReplicatedData(key)) {
			log.info("createOrUpdate isSecondReplicatedData");
			
			DBMock.getInstance().createOrUpdate(key, value);	
			
			String myIp = "";
			try {
				myIp = InetAddress.getLocalHost().getHostAddress();
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			}
			
			String nextNode = "";
			nextNode = service.getNext(myIp);
			nextNode = service.getNext(nextNode);
			NodeTableRecord next = service.getRecord(nextNode); // this is master
			
			String secondNode = "";
			secondNode = next.getFirstReplicaId();
			
			boolean isFromMyReplicaOrMaster = false;
			
			if(clientIP.equals(nextNode) || clientIP.equals(secondNode)){
				isFromMyReplicaOrMaster = true;
			}
			
			if(!isFromMyReplicaOrMaster){
					
				long hashKey=Hash.get(key);
				DatabaseRecord data = Database.getinstance().getSecondReplica().get(hashKey);
				if(data==null){
					VectorClock vClockDefinition =  new VectorClock("[0,0,1]");
					Database.getinstance().getSecondReplica().create(hashKey, value, vClockDefinition);
				}else{
					VectorClock vClockDefinition =  data.getVectorClock();
					int secondReplicaValue = vClockDefinition.getSecondReplica();
					vClockDefinition.setSecondReplica(secondReplicaValue+1);
					Database.getinstance().getSecondReplica().update(hashKey, value, vClockDefinition);
				}
				
				log.info("data patrie mastrovi DATA FROM 2nd replica "+myIp+" TO master: "+nextNode+" and 1st replica: "+secondNode+ " clientIP "+clientIP);
				try {
					
					new RESTRequestor("POST", "http://" + nextNode+ httpRequest).request();
					new RESTRequestor("POST", "http://" + secondNode + httpRequest).request();
				} catch (IOException e) {
					log.info("FAILED TO REPLICATE DATA FROM 2nd replica "+myIp+" TO master: "+nextNode+" and 1st replica: "+secondNode);
					e.printStackTrace();
				}
			}
		}else {
			
						
			log.info("Else ");
			long HashKey = Hash.get(key);
			
			String myIp = "";
			try {
				myIp = InetAddress.getLocalHost().getHostAddress();
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			}
			NodeTableRecord record = service.getRecord(myIp);
			
			String nextNode = "";
			nextNode = service.findNodeByHash(HashKey);
			NodeTableRecord next = service.getRecord(nextNode); 
			
			log.info("data nepatria ziadnej replike DATA OTHER FROM NODE "+myIp+" TO NODE: "+nextNode);
			try {
				
				new RESTRequestor("POST", "http://" + next+ ":4567/data/"+key+"/"+value+"?quorum="+quorum+"&vclock="+vectorClock).request();
			} catch (IOException e) {
				log.info("FAILED TO Sent DATA OTHER FROM NODE "+myIp+" TO NODE: "+nextNode);
				e.printStackTrace();
			}
			
		}
		
		
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
		
		log.info("POROVANNIE HASHU Hash: "+hash+" FROM: "+Database.getinstance().getMyDataHashFrom()+" TO: "+Database.getinstance().getMyDataHashTo() );
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
	
	private static boolean isFirstReplicatedData(String key) {
		
		long hash = Hash.get(key);
		
		String nextNode = "";
		try {
			nextNode = service.getNext(InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		NodeTableRecord next = service.getRecord(nextNode);
		long from = next.getHashFrom();
		long to = next.getHashTo();
		
		if (hash >= from && hash <= to) {
			
			return true;
		}
		
		return false;
	}
	
	private static boolean isSecondReplicatedData(String key) {
		
		long hash = Hash.get(key);
		
		String nextNode = "";
		try {
			nextNode = service.getNext(InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		nextNode = service.getNext(nextNode);
		
		NodeTableRecord next = service.getRecord(nextNode);
		long from = next.getHashFrom();
		long to = next.getHashTo();
		
		if (hash >= from && hash <= to) {
			
			return true;
		}
		
		return false;
	}
}