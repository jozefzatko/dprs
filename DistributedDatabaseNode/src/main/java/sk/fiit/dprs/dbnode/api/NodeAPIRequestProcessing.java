package sk.fiit.dprs.dbnode.api;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import sk.fiit.dprs.dbnode.api.services.RESTRequestor;
import sk.fiit.dprs.dbnode.consulkv.NodeTableRecord;
import sk.fiit.dprs.dbnode.consulkv.NodeTableService;
import sk.fiit.dprs.dbnode.db.models.DataNode;
import sk.fiit.dprs.dbnode.db.models.Database;

public class NodeAPIRequestProcessing {

	static Logger log = Logger.getLogger(NodeAPIRequestProcessing.class.getName());
	
	public static String getDbNodeData() {
		
		StringBuilder response = new StringBuilder();
		
		try {
			response.append(InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		response.append("\n\n");
		response.append(Database.getinstance().getMyData().getData().toString());
		response.append("\n");
		response.append(Database.getinstance().getFirstReplica().getData().toString());
		response.append("\n");
		response.append(Database.getinstance().getSecondReplica().getData().toString());
		log.info("Returning data: "+response.toString());
		return response.toString();
	}
	
	public static String getDbNodeData(String replica, String hashFrom, String hashTo) {
		
		DataNode data = getData(replica);
		
		if (hashFrom == null || hashTo == null) {
			log.info("Returning ALL data");
			return data.getData().toString();
		}

		long from = new Long(hashFrom);
		long to = new Long(hashTo);
		log.info("Returning data from hash: "+from+" to "+to+"\nData: "+data.toString());
		return data.toString(from, to);
	}
	
	public static String postDbNodeData(String replica, String dataToPost, NodeTableService service) {
		
		getData(replica).seed(dataToPost);
		if(Integer.parseInt(replica) == 1){
			try {
				String myIp = InetAddress.getLocalHost().getHostAddress();
				NodeTableRecord record = service.getRecord(myIp);
				log.info("Sending data to 1st replica with id: "+record.getFirstReplicaId());
				new RESTRequestor("POST", "http://" + record.getFirstReplicaId() + ":4567/dbnode/2", dataToPost).request();
				log.info("Sending data to 2nd replica with id: "+record.getSecondReplicaId());
				new RESTRequestor("POST", "http://" + record.getSecondReplicaId() + ":4567/dbnode/3", dataToPost).request();
			} catch (IOException e) {
				log.info("FAILED TO REPLICATE DATA");
				e.printStackTrace();
			}
		}
		return "ack";
	}

	public static String deleteDbNodeData(String replica, String hashFrom, String hashTo, NodeTableService service) {
		
		DataNode data = getData(replica);
		
		if (hashFrom == null || hashTo == null) {
			
			
			if(Integer.parseInt(replica) == 1){
				try {
					String myIp = InetAddress.getLocalHost().getHostAddress();
					NodeTableRecord record = service.getRecord(myIp);
					log.info("Removing data from 1st replica with id: "+record.getFirstReplicaId());
					new RESTRequestor("DELETE", "http://" + record.getFirstReplicaId() + ":4567/dbnode/2").request();
					log.info("Removing data from 2nd replica with id: "+record.getSecondReplicaId());
					new RESTRequestor("DELETE", "http://" + record.getSecondReplicaId() + ":4567/dbnode/3").request();
				} catch (IOException e) {
					log.info("FAILED TO DELETE DATA ON REPLICAS");
					e.printStackTrace();
				}
			}
			
			
			data.getData().clear();
			return "ack";
		}
		
		long from = new Long(hashFrom);
		long to = new Long(hashTo);
		
		if(Integer.parseInt(replica) == 1){
			try {
				String myIp = InetAddress.getLocalHost().getHostAddress();
				NodeTableRecord record = service.getRecord(myIp);
				log.info("Removing data from 1st replica with id: "+record.getFirstReplicaId()+"with hash from "+from+" to "+to);
				new RESTRequestor("DELETE", "http://" + record.getFirstReplicaId() + ":4567/dbnode/2?from="+from+"&to="+to).request();
				log.info("Removing data from 2nd replica with id: "+record.getSecondReplicaId()+"with hash from "+from+" to "+to);
				new RESTRequestor("DELETE", "http://" + record.getSecondReplicaId() + ":4567/dbnode/3?from="+from+"&to="+to).request();
			} catch (IOException e) {
				log.info("FAILED TO DELETE DATA ON REPLICAS");
				e.printStackTrace();
			}
		}
		
		
		data.remove(from, to);
		
		return "ack";
	}
	
	
	private static DataNode getData(String replica) {
		
		DataNode data; 
		
		if ("1".equals(replica)) {
			data = Database.getinstance().getMyData();
		}
		else if ("2".equals(replica)) {
			data = Database.getinstance().getFirstReplica();
		}
		else {
			data = Database.getinstance().getSecondReplica();
		}
		return data;
	}

}
